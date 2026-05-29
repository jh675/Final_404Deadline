package com.example.demo.project.member.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.example.demo.project.member.service.*;
import com.example.demo.util.attach.service.AttachService;
import com.example.demo.util.attach.service.AttachVO;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

/** 프로젝트 구성원 — 세션 {@code currentProjectId} 기준 ({@link com.example.demo.project.history.controller.HistoryController}와 동일) */
@Controller
@RequestMapping("/project/member")
@RequiredArgsConstructor
public class MemberController {

    private static final Logger log = LoggerFactory.getLogger(MemberController.class);

    private final MemberService memberService;
    private final AttachService attachService;

    @GetMapping("/list")
    public String memberList(MemberListCriteria criteria, HttpSession session, Model model) {
        Long prjId = (Long) session.getAttribute("currentProjectId");
        if (prjId == null) {
            return "redirect:/management/project";
        }
        criteria.setPrjId(prjId);
        criteria.normalized();
        model.addAttribute("rows", memberService.selectProjectMemberList(criteria));
        model.addAttribute("memberName", criteria.getMemberName());
        model.addAttribute("grpName", criteria.getGrpName());
        session.setAttribute("currentMenu", "member");
        return "project/member/memberManagement";
    }

    @GetMapping("/info")
    public String memberInfo(MemberInfoCriteria criteria, HttpSession session, Model model) {
        Long prjId = (Long) session.getAttribute("currentProjectId");
        if (prjId == null) {
            return "redirect:/management/project";
        }
        Long userId = criteria.getUserId();
        Long grpId = criteria.getGrpId();
        model.addAttribute("prjId", prjId);
        model.addAttribute("currentMenu", "member");

        if (userId == null || grpId == null) {
            return "redirect:/project/member/list";
        }

        model.addAttribute("registerMode", false);
        model.addAttribute("userId", userId);
        model.addAttribute("grpId", grpId);

        MemberDetailVO detail = memberService.selectMemberDetail(prjId, userId, grpId);
        if (detail == null) {
            model.addAttribute("memberNotFound", true);
            model.addAttribute("editMode", false);
            model.addAttribute("viewMode", false);
            model.addAttribute("issues", List.<MemberIssueRowVO>of());
            return "project/member/memberManagementInfo";
        }

        boolean editMode = criteria.isEdit();
        model.addAttribute("memberNotFound", false);
        model.addAttribute("editMode", editMode);
        model.addAttribute("viewMode", !editMode);
        model.addAttribute("detail", detail);
        List<AttachVO> profileImages = attachService.selectAttachListByContainer("users", userId);
        Long profileImageAttachId =
                profileImages.isEmpty() ? null : profileImages.get(profileImages.size() - 1).getId();
        model.addAttribute("profileImageAttachId", profileImageAttachId);
        model.addAttribute("pwUpdatedOnYmd", formatDateYmd(detail.getPwUpdatedOn()));
        model.addAttribute("lastLoginOnYmd", formatDateYmd(detail.getLastLoginOn()));
        model.addAttribute(
                "issues",
                editMode
                        ? List.<MemberIssueRowVO>of()
                        : memberService.selectMemberIssues(prjId, userId, grpId));
        return "project/member/memberManagementInfo";
    }

    /** 구성원 등록 화면 — TUI Grid 기반 (memberJoin.html) */
    @GetMapping("/join")
    public String memberJoin(HttpSession session) {
        if (session.getAttribute("currentProjectId") == null) {
            return "redirect:/management/project";
        }
        return "project/member/memberJoin";
    }

    @GetMapping("/projectGroups")
    @ResponseBody
    public Object projectGroups(HttpSession session) {
        Long prjId = (Long) session.getAttribute("currentProjectId");
        if (prjId == null) {
            return redirectRequired();
        }
        return listBody(prjId, memberService.selectProjectGroupsByPrjId(prjId));
    }

    @GetMapping("/companyMembers")
    @ResponseBody
    public Object companyMembers(MemberCompanyMembersCriteria criteria, HttpSession session) {
        Long prjId = (Long) session.getAttribute("currentProjectId");
        if (prjId == null) {
            return redirectRequired();
        }
        return listBody(
                prjId,
                memberService.selectCompanyMembersByPrjId(prjId, criteria.isExcludeRegistered()));
    }

    @PostMapping("/updateMember")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateMember(
            @RequestBody(required = false) MemberUpdateRequest body, HttpSession session) {
        Long prjId = (Long) session.getAttribute("currentProjectId");
        if (prjId == null) {
            return badRequest("프로젝트를 선택한 뒤 이용해 주세요.");
        }
        try {
            if (body == null) {
                return badRequest("요청이 올바르지 않습니다.");
            }
            memberService.updateMember(prjId, body.userId(), body.oldGrpId(), body.grpId());
            Map<String, Object> ok = new LinkedHashMap<>();
            ok.put("ok", true);
            ok.put("prjId", prjId);
            ok.put("userId", body.userId());
            ok.put("grpId", body.grpId());
            return ResponseEntity.ok(ok);
        } catch (IllegalArgumentException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return serverError("구성원 수정 중 오류가 발생했습니다.");
        }
    }

    /** 다중 직원 일괄 등록 */
    @PostMapping("/registerMembers")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> registerMembers(
            @RequestBody(required = false) MemberBulkRegisterRequest body, HttpSession session) {
        Long prjId = (Long) session.getAttribute("currentProjectId");
        if (prjId == null) {
            return badRequest("프로젝트를 선택한 뒤 이용해 주세요.");
        }
        try {
            if (body == null) {
                return badRequest("요청이 올바르지 않습니다.");
            }
            int registered = memberService.registerMembers(prjId, body.userIds(), body.grpId());
            Map<String, Object> ok = new LinkedHashMap<>();
            ok.put("ok", true);
            ok.put("registered", registered);
            ok.put("grpId", body.grpId());
            ok.put("prjId", prjId);
            return ResponseEntity.ok(ok);
        } catch (IllegalArgumentException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("registerMembers failed - prjId={}, body={}", prjId, body, e);
            return serverError("구성원 등록 중 오류가 발생했습니다.");
        }
    }

    @PostMapping("/deleteMembers")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteMembers(
            @RequestBody(required = false) MemberDeleteRequest body, HttpSession session) {
        Long prjId = (Long) session.getAttribute("currentProjectId");
        if (prjId == null) {
            return badRequest("프로젝트를 선택한 뒤 이용해 주세요.");
        }
        try {
            List<ProjectMemberRowVO> rows = body == null || body.members() == null
                    ? List.of()
                    : body.members().stream()
                            .map(k -> ProjectMemberRowVO.builder()
                                    .userId(k.userId())
                                    .grpId(k.grpId())
                                    .build())
                            .toList();
            memberService.deleteMembers(prjId, rows);
            return ResponseEntity.ok(Map.of("ok", true));
        } catch (IllegalArgumentException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return serverError("멤버 제거 중 오류가 발생했습니다.");
        }
    }

    private static Map<String, Object> listBody(Long prjId, List<?> content) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("content", content);
        body.put("prjId", prjId);
        return body;
    }

    private static ResponseEntity<Map<String, Object>> redirectRequired() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("ok", false, "message", "프로젝트를 선택한 뒤 이용해 주세요."));
    }

    private static ResponseEntity<Map<String, Object>> badRequest(String message) {
        return ResponseEntity.badRequest().body(Map.of("ok", false, "message", message));
    }

    private static ResponseEntity<Map<String, Object>> serverError(String message) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("ok", false, "message", message));
    }

    private static String formatDateYmd(Date dt) {
        if (dt == null) {
            return "";
        }
        return new SimpleDateFormat("yyyy-MM-dd").format(dt);
    }

    public record MemberUpdateRequest(Long userId, Long oldGrpId, Long grpId) {}

    public record MemberBulkRegisterRequest(List<Long> userIds, Long grpId) {}

    public record MemberDeleteRequest(List<MemberKey> members) {}

    public record MemberKey(Long userId, Long grpId) {}
}
