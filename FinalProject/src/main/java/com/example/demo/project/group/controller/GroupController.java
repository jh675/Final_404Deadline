package com.example.demo.project.group.controller;

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
import com.example.demo.project.group.service.*;
import com.example.demo.project.option.service.RoleService;
import com.example.demo.project.option.service.RoleVO;
import lombok.RequiredArgsConstructor;

/**
 * 프로젝트 그룹 관리 — {@code project/group/groupManagement.html}
 */
@Controller
@RequestMapping("/project/group")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;
    private final RoleService roleService;

    /** 그룹 목록 화면 — 검색 조건 반영 후 Thymeleaf에 rows 전달 */
    @GetMapping("/groupManagement")
    public String groupManagementPage(
            GroupListCriteria criteria,
            Model model) {

     

        List<ProjectGroupRowVO> rows = groupService.selectProjectGroupList(criteria);

        model.addAttribute("rows", rows);
        model.addAttribute("prjId", criteria.getPrjId());
        model.addAttribute("grpName", criteria.getGrpName());
        model.addAttribute("createdFrom", criteria.getCreatedFrom());
        model.addAttribute("createdTo", criteria.getCreatedTo());
        return "project/group/groupManagement";
    }

    /** 그룹 상세·등록 — {@code project/group/groupManagementInfo.html} (grpId 없으면 등록) */
    @GetMapping("/groupManagementInfo")
    public String groupManagementInfoPage(
            @RequestParam("prjId") Long prjId,
            @RequestParam(value = "grpId", required = false) Long grpId,
            Model model) {

        model.addAttribute("prjId", prjId);

        if (grpId == null) {
            model.addAttribute("registerMode", true);
            model.addAttribute("groupNotFound", false);
            model.addAttribute("detail", GroupDetailVO.builder()
                    .prjId(prjId)
                    .prjName(groupService.selectProjectName(prjId))
                    .grpName("")
                    .build());
            model.addAttribute("members", List.<GroupMemberDetailRowVO>of());
            model.addAttribute("roles", List.<GroupRoleDetailRowVO>of());
            return "project/group/groupManagementInfo";
        }

        model.addAttribute("registerMode", false);
        model.addAttribute("grpId", grpId);

        GroupDetailVO detail = groupService.selectGroupDetail(prjId, grpId);
        if (detail == null) {
            model.addAttribute("groupNotFound", true);
            model.addAttribute("members", List.<GroupMemberDetailRowVO>of());
            model.addAttribute("roles", List.<GroupRoleDetailRowVO>of());
            return "project/group/groupManagementInfo";
        }

        model.addAttribute("groupNotFound", false);
        model.addAttribute("detail", detail);
        model.addAttribute("members", groupService.selectGroupMembers(prjId, grpId));
        model.addAttribute("roles", groupService.selectGroupRoles(prjId, grpId));
        return "project/group/groupManagementInfo";
    }

    /** 그룹명 중복 확인 — GRP.PRJ_ID + GRP.NAME */
    @GetMapping("/checkGrpNameDuplicate")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkGrpNameDuplicate(
            @RequestParam("prjId") Long prjId,
            @RequestParam("grpName") String grpName) {
        if (prjId == null) {
            return badRequest("프로젝트 ID가 필요합니다.");
        }
        String name = grpName == null ? "" : grpName.trim();
        if (name.isEmpty()) {
            return badRequest("그룹명을 입력하세요.");
        }
        boolean duplicate = groupService.existsGroupName(prjId, name);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("ok", true);
        body.put("duplicate", duplicate);
        return ResponseEntity.ok(body);
    }

    /** 그룹 목록 JSON (AJAX 검색용, 현재 HTML에서는 미사용) */
    @GetMapping("/groupManagementList")
    @ResponseBody
    public Map<String, Object> groupManagementList(
            @RequestParam("prjId") Long prjId,
            @RequestParam(required = false) String grpName,
            @RequestParam(required = false) String createdFrom,
            @RequestParam(required = false) String createdTo) {

        GroupListCriteria criteria = GroupListCriteria.builder()
                .prjId(prjId)
                .grpName(nullToEmpty(grpName))
                .createdFrom(nullToEmpty(createdFrom))
                .createdTo(nullToEmpty(createdTo))
                .build();

        List<ProjectGroupRowVO> rows = groupService.selectProjectGroupList(criteria);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("content", rows);
        body.put("prjId", prjId);
        return body;
    }

    /**
     * 그룹 보유 권한 — 역할에 연결된 메뉴명 목록.
     * 해당 그룹에 {@code GRP_ROLE}로 부여된 역할만 조회 가능합니다.
     */
    @GetMapping("/groupRoleMenus")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> groupRoleMenus(
            @RequestParam("prjId") Long prjId,
            @RequestParam("grpId") Long grpId,
            @RequestParam("roleCd") Long roleCd) {
        if (prjId == null || grpId == null || roleCd == null) {
            return badRequest("요청이 올바르지 않습니다.");
        }
        if (groupService.selectGroupDetail(prjId, grpId) == null) {
            return badRequest("그룹을 찾을 수 없습니다.");
        }
        if (!groupService.isRoleAssignedToGroup(prjId, grpId, roleCd)) {
            return badRequest("이 그룹에 부여된 권한이 아닙니다.");
        }
        RoleVO role = roleService.selectRoleByPrjAndCd(prjId, roleCd);
        if (role == null) {
            return badRequest("권한을 찾을 수 없습니다.");
        }
        List<String> menus = roleService.selectMenuNamesByRoleCd(prjId, roleCd);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("ok", true);
        body.put("roleName", role.getRoleName());
        body.put("menus", menus == null ? List.of() : menus);
        return ResponseEntity.ok(body);
    }

    /** 그룹 등록 모달 — 프로젝트 구성원 선택 목록 JSON */
    @GetMapping("/groupMemberPickList")
    @ResponseBody
    public Map<String, Object> groupMemberPickList(@RequestParam("prjId") Long prjId) {
        List<GroupMemberPickRowVO> rows = groupService.selectGroupMemberPickList(prjId);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("content", rows);
        body.put("prjId", prjId);
        return body;
    }

    /** 그룹 등록 — DB {@code PROC_GRP_INSERT} (userIds 없으면 그룹만 생성) */
    @PostMapping("/registerGroup")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> registerGroup(
            @RequestBody(required = false) GroupInsertRequest body) {
        try {
            if (body == null || body.prjId() == null) {
                return badRequest("요청이 올바르지 않습니다.");
            }
            String grpName = body.grpName() == null ? "" : body.grpName().trim();
            if (grpName.isEmpty()) {
                return badRequest("그룹명을 입력하세요.");
            }
            List<Long> userIds = body.userIds();
            groupService.insertGroup(body.prjId(), grpName, userIds);
            Map<String, Object> ok = new LinkedHashMap<>();
            ok.put("ok", true);
            return ResponseEntity.ok(ok);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("ok", false, "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("ok", false, "message", "그룹 등록 중 오류가 발생했습니다."));
        }
    }

    /** 그룹 수정 — DB {@code PROC_GRP_UPDATE} (userIds 없으면 구성원 변경 없음) */
    @PostMapping("/updateGroup")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateGroup(
            @RequestBody(required = false) GroupUpdateRequest body) {
        try {
            if (body == null || body.prjId() == null || body.grpId() == null) {
                return badRequest("요청이 올바르지 않습니다.");
            }
            groupService.updateGroup(body.prjId(), body.grpId(), body.userIds());
            return ResponseEntity.ok(Map.of("ok", true));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("ok", false, "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("ok", false, "message", "그룹 수정 중 오류가 발생했습니다."));
        }
    }

    /** 선택 그룹 삭제 — DB {@code PROC_GRP_DELETE} 호출 */
    @PostMapping("/deleteGroups")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteGroups(
            @RequestBody(required = false) GroupDeleteRequest body) {
        Map<String, Object> ok = new LinkedHashMap<>();
        ok.put("ok", true);
        try {
            if (body == null || body.prjId() == null) {
                return badRequest("요청이 올바르지 않습니다.");
            }
            groupService.deleteGroups(body.prjId(), body.grpIds() == null ? List.of() : body.grpIds());
            return ResponseEntity.ok(ok);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("ok", false, "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("ok", false, "message", "그룹 삭제 중 오류가 발생했습니다."));
        }
    }

    private static ResponseEntity<Map<String, Object>> badRequest(String message) {
        return ResponseEntity.badRequest().body(Map.of("ok", false, "message", message));
    }

    /** deleteGroups 요청 JSON */
    public record GroupDeleteRequest(Long prjId, List<Long> grpIds) {}

    /** registerGroup 요청 JSON — userIds 키는 구성원이 있을 때만 전송 */
    public record GroupInsertRequest(Long prjId, String grpName, List<Long> userIds) {}

    /** updateGroup 요청 JSON */
    public record GroupUpdateRequest(Long prjId, Long grpId, List<Long> userIds) {}

    /** MyBatis 동적 SQL에서 null 대신 빈 문자열로 통일 */
    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }
}
