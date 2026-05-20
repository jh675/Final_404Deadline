package com.example.demo.project.member.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.example.demo.project.member.service.*;
import lombok.RequiredArgsConstructor;

/**
 * 프로젝트 멤버 관리 — {@code project/member/memberManagement.html}
 */
@Controller
@RequestMapping("/project/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    /** 구성원 목록 화면 — 검색 조건 반영 후 Thymeleaf에 rows 전달 */
    @GetMapping("/memberManagement")
    public String memberManagementPage(
    		MemberListCriteria criteria,
            Model model) {

        List<ProjectMemberRowVO> rows = memberService.selectProjectMemberList(criteria);

        model.addAttribute("rows", rows);
        model.addAttribute("prjId", criteria.getPrjId());
        model.addAttribute("memberName", criteria.getMemberName());
        model.addAttribute("grpName", criteria.getGrpName());
        model.addAttribute("prjStartFrom", criteria.getPrjStartFrom());
        model.addAttribute("prjStartTo", criteria.getPrjStartTo());
        return "project/member/memberManagement";
    }

    /**
     * 구성원 상세·수정·등록 — {@code memberManagementInfo.html}
     * <ul>
     *   <li>userId·grpId 없음 → 등록</li>
     *   <li>{@code edit=true} → 수정(투입 종료일·소속 그룹)</li>
     *   <li>그 외 → 상세 조회</li>
     * </ul>
     */
    @GetMapping("/memberManagementInfo")
    public String memberManagementInfoPage(
            @RequestParam("prjId") Long prjId,
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "grpId", required = false) Long grpId,
            @RequestParam(value = "edit", defaultValue = "false") boolean edit,
            Model model) {

        model.addAttribute("prjId", prjId);

        if (userId == null || grpId == null) {
            model.addAttribute("registerMode", true);
            model.addAttribute("editMode", false);
            model.addAttribute("viewMode", false);
            model.addAttribute("userId", null);
            model.addAttribute("grpId", null);
            model.addAttribute("memberNotFound", false);
            model.addAttribute("detail", MemberDetailVO.builder()
                    .prjId(prjId)
                    .prjName(memberService.selectProjectName(prjId))
                    .build());
            model.addAttribute("issues", List.<MemberIssueRowVO>of());
            return "project/member/memberManagementInfo";
        }

        model.addAttribute("registerMode", false);
        model.addAttribute("userId", userId);
        model.addAttribute("grpId", grpId);

        MemberDetailVO detail = memberService.selectMemberDetail(prjId, userId, grpId);
        if (detail == null) {
            model.addAttribute("memberNotFound", true);
            model.addAttribute("editMode", false);
            model.addAttribute("viewMode", false);
            model.addAttribute("userId", userId);
            model.addAttribute("grpId", grpId);
            model.addAttribute("issues", List.<MemberIssueRowVO>of());
            return "project/member/memberManagementInfo";
        }

        boolean editMode = edit;
        model.addAttribute("memberNotFound", false);
        model.addAttribute("editMode", editMode);
        model.addAttribute("viewMode", !editMode);
        model.addAttribute("detail", detail);
        model.addAttribute("pwUpdatedOnYmd", formatDateYmd(detail.getPwUpdatedOn()));
        model.addAttribute("lastLoginOnYmd", formatDateYmd(detail.getLastLoginOn()));
        model.addAttribute(
                "issues",
                editMode
                        ? List.<MemberIssueRowVO>of()
                        : memberService.selectMemberIssues(prjId, userId, grpId));
        return "project/member/memberManagementInfo";
    }

    /** 구성원 등록 모달 — 프로젝트 내 활성 그룹 목록 */
    @GetMapping("/projectGroups")
    @ResponseBody
    public Map<String, Object> projectGroups(@RequestParam("prjId") Long prjId) {
        List<MemberGroupPickRowVO> rows = memberService.selectProjectGroupsByPrjId(prjId);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("content", rows);
        body.put("prjId", prjId);
        return body;
    }

    /** 프로젝트 수행 기업 활성 사용자 목록 — 구성원 등록 모달( excludeRegistered 시 MEMBER 미등록만) */
    @GetMapping("/companyMembers")
    @ResponseBody
    public Map<String, Object> companyMembers(
            @RequestParam("prjId") Long prjId,
            @RequestParam(value = "excludeRegistered", defaultValue = "false")
                    boolean excludeRegistered) {
        List<CompanyMemberRowVO> rows =
                memberService.selectCompanyMembersByPrjId(prjId, excludeRegistered);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("content", rows);
        body.put("prjId", prjId);
        return body;
    }

    /** 구성원 목록 JSON (AJAX 검색용, 현재 HTML에서는 미사용) */
    @GetMapping("/memberManagementList")
    @ResponseBody
    public Map<String, Object> memberManagementList(
            @RequestParam("prjId") Long prjId,
            @RequestParam(required = false) String memberName,
            @RequestParam(required = false) String grpName,
            @RequestParam(required = false) String prjStartFrom,
            @RequestParam(required = false) String prjStartTo) {

        MemberListCriteria criteria = MemberListCriteria.builder()
                .prjId(prjId)
                .memberName(nullToEmpty(memberName))
                .grpName(nullToEmpty(grpName))
                .prjStartFrom(nullToEmpty(prjStartFrom))
                .prjStartTo(nullToEmpty(prjStartTo))
                .build();

        List<ProjectMemberRowVO> rows = memberService.selectProjectMemberList(criteria);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("content", rows);
        body.put("prjId", prjId);
        return body;
    }

    /** 구성원 수정 — 투입 종료일·소속 그룹 */
    @PostMapping("/updateMember")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateMember(
            @RequestBody(required = false) MemberUpdateRequest body) {
        try {
            if (body == null) {
                return badRequest("요청이 올바르지 않습니다.");
            }
            memberService.updateMember(
                    body.prjId(),
                    body.userId(),
                    body.oldGrpId(),
                    body.grpId(),
                    body.prjEndDate());
            Map<String, Object> ok = new LinkedHashMap<>();
            ok.put("ok", true);
            ok.put("prjId", body.prjId());
            ok.put("userId", body.userId());
            ok.put("grpId", body.grpId());
            return ResponseEntity.ok(ok);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("ok", false, "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("ok", false, "message", "구성원 수정 중 오류가 발생했습니다."));
        }
    }

    /** 구성원 등록 — MEMBER INSERT (mem_seq) */
    @PostMapping("/registerMember")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> registerMember(
            @RequestBody(required = false) MemberRegisterRequest body) {
        try {
            if (body == null) {
                return badRequest("요청이 올바르지 않습니다.");
            }
            Long memberId = memberService.registerMember(
                    body.prjId(),
                    body.userId(),
                    body.grpId(),
                    body.prjStartDate(),
                    body.prjEndDate());
            Map<String, Object> ok = new LinkedHashMap<>();
            ok.put("ok", true);
            ok.put("memberId", memberId);
            ok.put("userId", body.userId());
            ok.put("grpId", body.grpId());
            ok.put("prjId", body.prjId());
            return ResponseEntity.ok(ok);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("ok", false, "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("ok", false, "message", "구성원 등록 중 오류가 발생했습니다."));
        }
    }

    /** Grid에서 선택한 구성원을 프로젝트에서 제거 (MEMBER 행 삭제) */
    @PostMapping("/deleteMembers")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteMembers(
            @RequestBody(required = false) MemberDeleteRequest body) {
        Map<String, Object> ok = new LinkedHashMap<>();
        ok.put("ok", true);
        try {
            if (body == null || body.prjId() == null) {
                return badRequest("요청이 올바르지 않습니다.");
            }
            List<ProjectMemberRowVO> rows = body.members() == null
                    ? List.of()
                    : body.members().stream()
                            .map(k -> ProjectMemberRowVO.builder()
                                    .userId(k.userId())
                                    .grpId(k.grpId())
                                    .build())
                            .toList();
            memberService.deleteMembers(body.prjId(), rows);
            return ResponseEntity.ok(ok);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("ok", false, "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("ok", false, "message", "멤버 제거 중 오류가 발생했습니다."));
        }
    }

    private static ResponseEntity<Map<String, Object>> badRequest(String message) {
        return ResponseEntity.badRequest().body(Map.of("ok", false, "message", message));
    }

    /** updateMember 요청 JSON */
    public record MemberUpdateRequest(
            Long prjId,
            Long userId,
            Long oldGrpId,
            Long grpId,
            String prjEndDate) {}

    /** registerMember 요청 JSON */
    public record MemberRegisterRequest(
            Long prjId,
            Long userId,
            Long grpId,
            String prjStartDate,
            String prjEndDate) {}

    /** deleteMembers 요청 JSON */
    public record MemberDeleteRequest(Long prjId, List<MemberKey> members) {}

    /** MEMBER 복합키 — userId + grpId */
    public record MemberKey(Long userId, Long grpId) {}

    /** MyBatis 동적 SQL에서 null 대신 빈 문자열로 통일 */
    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }

    private static String formatDateYmd(LocalDateTime dt) {
        if (dt == null) {
            return "";
        }
        return dt.toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE);
    }
}
