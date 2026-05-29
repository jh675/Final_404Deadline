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
import org.springframework.web.bind.annotation.ResponseBody;
import com.example.demo.project.group.service.*;
import com.example.demo.project.option.service.*;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

/** 프로젝트 그룹 — 세션 {@code currentProjectId} 기준 */
@Controller
@RequestMapping("/project/group")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;
    private final RoleService roleService;

    @GetMapping("/list")
    public String groupList(GroupListCriteria criteria, HttpSession session, Model model) {
        Long prjId = (Long) session.getAttribute("currentProjectId");
        if (prjId == null) {
            return "redirect:/management/project";
        }
        criteria.setPrjId(prjId);
        criteria.normalized();
        model.addAttribute("rows", groupService.selectProjectGroupList(criteria));
        model.addAttribute("grpName", criteria.getGrpName());
        model.addAttribute("createdFrom", criteria.getCreatedFrom());
        model.addAttribute("createdTo", criteria.getCreatedTo());
        session.setAttribute("currentMenu", "group");
        return "project/group/groupManagement";
    }

    @GetMapping("/info")
    public String groupInfo(GroupInfoCriteria criteria, HttpSession session, Model model) {
        Long prjId = (Long) session.getAttribute("currentProjectId");
        if (prjId == null) {
            return "redirect:/management/project";
        }
        Long grpId = criteria.getGrpId();
        if (grpId == null) {
            model.addAttribute("registerMode", true);
            model.addAttribute("groupNotFound", false);
            model.addAttribute("detail", GroupDetailVO.builder()
                    .prjId(prjId)
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

    @GetMapping("/checkGrpNameDuplicate")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkGrpNameDuplicate(
            GroupNameDuplicateCriteria criteria, HttpSession session) {
        Long prjId = (Long) session.getAttribute("currentProjectId");
        if (prjId == null) {
            return badRequest("프로젝트를 선택한 뒤 이용해 주세요.");
        }
        String name = criteria.getGrpName() == null ? "" : criteria.getGrpName().trim();
        if (name.isEmpty()) {
            return badRequest("그룹명을 입력하세요.");
        }
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("ok", true);
        body.put("duplicate", groupService.existsGroupName(prjId, name));
        return ResponseEntity.ok(body);
    }

    @GetMapping("/groupRoleMenus")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> groupRoleMenus(
            GroupRoleMenusCriteria criteria, HttpSession session) {
        Long prjId = (Long) session.getAttribute("currentProjectId");
        Long grpId = criteria.getGrpId();
        Long roleCd = criteria.getRoleCd();
        if (prjId == null) {
            return badRequest("프로젝트를 선택한 뒤 이용해 주세요.");
        }
        if (grpId == null || roleCd == null) {
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
        body.put("menuSections", roleService.selectMenuSectionsByRoleCd(prjId, roleCd));
        return ResponseEntity.ok(body);
    }

    @GetMapping("/groupMemberPickList")
    @ResponseBody
    public Object groupMemberPickList(HttpSession session) {
        Long prjId = (Long) session.getAttribute("currentProjectId");
        if (prjId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("ok", false, "message", "프로젝트를 선택한 뒤 이용해 주세요."));
        }
        return listBody(prjId, groupService.selectGroupMemberPickList(prjId));
    }

    @PostMapping("/registerGroup")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> registerGroup(
            @RequestBody(required = false) GroupInsertRequest body, HttpSession session) {
        Long prjId = (Long) session.getAttribute("currentProjectId");
        if (prjId == null) {
            return badRequest("프로젝트를 선택한 뒤 이용해 주세요.");
        }
        try {
            if (body == null) {
                return badRequest("요청이 올바르지 않습니다.");
            }
            String grpName = body.grpName() == null ? "" : body.grpName().trim();
            if (grpName.isEmpty()) {
                return badRequest("그룹명을 입력하세요.");
            }
            groupService.insertGroup(prjId, grpName, body.userIds(), body.roleCds());
            return ResponseEntity.ok(Map.of("ok", true));
        } catch (IllegalArgumentException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return serverError("그룹 등록 중 오류가 발생했습니다.");
        }
    }

    @PostMapping("/updateGroup")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateGroup(
            @RequestBody(required = false) GroupUpdateRequest body, HttpSession session) {
        Long prjId = (Long) session.getAttribute("currentProjectId");
        if (prjId == null) {
            return badRequest("프로젝트를 선택한 뒤 이용해 주세요.");
        }
        try {
            if (body == null || body.grpId() == null) {
                return badRequest("요청이 올바르지 않습니다.");
            }
            groupService.updateGroup(prjId, body.grpId(), body.userIds());
            return ResponseEntity.ok(Map.of("ok", true));
        } catch (IllegalArgumentException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return serverError("그룹 수정 중 오류가 발생했습니다.");
        }
    }

    @GetMapping("/groupRolePickList")
    @ResponseBody
    public Object groupRolePickList(HttpSession session) {
        Long prjId = (Long) session.getAttribute("currentProjectId");
        if (prjId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("ok", false, "message", "프로젝트를 선택한 뒤 이용해 주세요."));
        }
        RoleVO search = new RoleVO();
        search.setPrjId(prjId);
        return listBody(prjId, roleService.selectRoleList(search));
    }

    @PostMapping("/updateGroupRoles")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateGroupRoles(
            @RequestBody(required = false) GroupRolesUpdateRequest body, HttpSession session) {
        Long prjId = (Long) session.getAttribute("currentProjectId");
        if (prjId == null) {
            return badRequest("프로젝트를 선택한 뒤 이용해 주세요.");
        }
        try {
            if (body == null || body.grpId() == null) {
                return badRequest("요청이 올바르지 않습니다.");
            }
            groupService.updateGroupRoles(prjId, body.grpId(), body.roleCds());
            return ResponseEntity.ok(Map.of("ok", true));
        } catch (IllegalArgumentException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return serverError("그룹 권한 수정 중 오류가 발생했습니다.");
        }
    }

    @PostMapping("/deleteGroups")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteGroups(
            @RequestBody(required = false) GroupDeleteRequest body, HttpSession session) {
        Long prjId = (Long) session.getAttribute("currentProjectId");
        if (prjId == null) {
            return badRequest("프로젝트를 선택한 뒤 이용해 주세요.");
        }
        try {
            groupService.deleteGroups(
                    prjId, body == null || body.grpIds() == null ? List.of() : body.grpIds());
            return ResponseEntity.ok(Map.of("ok", true));
        } catch (IllegalArgumentException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return serverError("그룹 삭제 중 오류가 발생했습니다.");
        }
    }

    private static Map<String, Object> listBody(Long prjId, List<?> content) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("content", content);
        body.put("prjId", prjId);
        return body;
    }

    private static ResponseEntity<Map<String, Object>> badRequest(String message) {
        return ResponseEntity.badRequest().body(Map.of("ok", false, "message", message));
    }

    private static ResponseEntity<Map<String, Object>> serverError(String message) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("ok", false, "message", message));
    }

    public record GroupDeleteRequest(List<Long> grpIds) {}

    public record GroupInsertRequest(String grpName, List<Long> userIds, List<Long> roleCds) {}

    public record GroupUpdateRequest(Long grpId, List<Long> userIds) {}

    public record GroupRolesUpdateRequest(Long grpId, List<Long> roleCds) {}
}
