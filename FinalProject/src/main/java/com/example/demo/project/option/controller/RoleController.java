package com.example.demo.project.option.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.example.demo.project.group.service.GroupListCriteria;
import jakarta.servlet.http.HttpSession;
import com.example.demo.project.group.service.GroupService;
import com.example.demo.project.option.service.RoleGroupsCriteria;
import com.example.demo.project.option.service.RoleInfoCriteria;
import com.example.demo.project.option.service.RoleListCriteria;
import com.example.demo.project.option.service.RoleRevokeResultVO;
import com.example.demo.project.option.service.RoleService;
import com.example.demo.project.option.service.RoleVO;
import lombok.RequiredArgsConstructor;

/**
 * 권한 관리 — {@code project/role/roleManagement.html} (TOAST UI Grid, {@code ROLE} 목록).
 */
@Controller
@RequestMapping("/project/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;
    private final GroupService groupService;

    @GetMapping("/list")
    public String roleList(RoleListCriteria criteria, HttpSession session, Model model) {
        Long prjId = (Long) session.getAttribute("currentProjectId");
        if (prjId == null) {
            return "redirect:/management/project";
        }
        criteria.setPrjId(prjId);
        RoleVO search = criteria.toSearchVo();
        model.addAttribute("rows", roleService.selectRoleList(search));
        model.addAttribute("prjId", prjId);
        model.addAttribute("permissionKey", search.getPermissionKey());
        model.addAttribute("permissionName", search.getPermissionName());
        model.addAttribute("createdFrom", search.getCreatedFrom());
        model.addAttribute("createdTo", search.getCreatedTo());
        model.addAttribute("currentMenu", "role");
        return "project/role/roleManagement";
    }

    /**
     * 역할 상세 — {@code project/role/roleManagementInfo.html}
     * (프로젝트 {@code ROLE} + {@code ROLE_MENU}으로 연결된 {@code MENU} 권한 체크 표시)
     */
    @GetMapping("/info")
    public String roleInfo(RoleInfoCriteria criteria, HttpSession session, Model model) {
        Long prjId = (Long) session.getAttribute("currentProjectId");
        if (prjId == null) {
            return "redirect:/management/project";
        }
        Long roleCd = criteria.getRoleCd();
        model.addAttribute("prjId", prjId);
        model.addAttribute("currentMenu", "role");
        List<RoleVO> allMenus = roleService.selectAllMenus();

        if (roleCd == null) {
            model.addAttribute("registerMode", true);
            model.addAttribute("roleNotFound", false);
            model.addAttribute("roleCd", null);
            model.addAttribute("prjName", groupService.selectProjectName(prjId));
            model.addAttribute("menuSections", roleService.buildMenuSections(allMenus, Set.of()));
            return "project/role/roleManagementInfo";
        }

        model.addAttribute("registerMode", false);
        model.addAttribute("roleCd", roleCd);

        RoleVO currentRole = roleService.selectRoleByPrjAndCd(prjId, roleCd);
        if (currentRole == null) {
            model.addAttribute("registerMode", false);
            model.addAttribute("roleNotFound", true);
            model.addAttribute("roleCd", roleCd);
            model.addAttribute("menuSections", List.of());
            return "project/role/roleManagementInfo";
        }

        model.addAttribute("roleNotFound", false);
        model.addAttribute("currentRole", currentRole);
        model.addAttribute("roleCreatedOnYmd", formatRoleDateYmd(currentRole.getCreatedOn()));

        Set<String> linked = new HashSet<>(roleService.selectMenuRoleIdsByRoleCd(roleCd));
        model.addAttribute("menuSections", roleService.buildMenuSections(allMenus, linked));
        return "project/role/roleManagementInfo";
    }


    /**
     * 역할 목록에서 선택 후 「제거」 — DB {@code PROC_ROLE_DELETE} 호출.
     */
    @PostMapping("/deleteRoles")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteRoles(
            @RequestBody(required = false) RoleDeleteRequest body, HttpSession session) {
        Long prjId = (Long) session.getAttribute("currentProjectId");
        if (prjId == null) {
            return badRequest("프로젝트를 선택한 뒤 이용해 주세요.");
        }
        Map<String, Object> ok = new LinkedHashMap<>();
        ok.put("ok", true);
        try {
            if (body == null) {
                return badRequest("요청 본문이 비어 있습니다.");
            }
            roleService.deleteRolesForProject(prjId, body.roleCds());
            return ResponseEntity.ok(ok);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("ok", false, "message", e.getMessage()));
        } catch (Exception e) {
            Map<String, Object> err = new LinkedHashMap<>();
            err.put("ok", false);
            err.put("message", "역할 제거 중 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
        }
    }

    private static ResponseEntity<Map<String, Object>> badRequest(String message) {
        return ResponseEntity.badRequest().body(Map.of("ok", false, "message", message));
    }

    /** 역할 상세 — 이 {@code ROLE_CD}를 보유한 그룹 목록(TOAST Grid 데이터). */
    @GetMapping("/roleGroups")
    @ResponseBody
    public Map<String, Object> roleGroups(RoleGroupsCriteria criteria, HttpSession session) {
        Long prjId = (Long) session.getAttribute("currentProjectId");
        Long roleCd = criteria.getRoleCd();
        if (prjId == null) {
            return Map.of("ok", false, "message", "프로젝트를 선택한 뒤 이용해 주세요.");
        }
        Map<String, Object> body = listBody(
                prjId,
                roleCd == null ? List.of() : roleService.selectRoleGroupsList(prjId, roleCd));
        body.put("grpRoleCount", roleCd == null ? 0 : roleService.countGroupsWithRole(roleCd));
        return body;
    }

    /** 역할 등록 — {@code PROC_ROLE_CREATE} */
    @PostMapping("/registerRole")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> registerRole(
            @RequestBody(required = false) RoleCreateRequest body, HttpSession session) {
        Long prjId = (Long) session.getAttribute("currentProjectId");
        if (prjId == null) {
            return badRequest("프로젝트를 선택한 뒤 이용해 주세요.");
        }
        Map<String, Object> resp = new LinkedHashMap<>();
        try {
            if (body == null) {
                return badRequest("요청이 올바르지 않습니다.");
            }
            String roleName = body.roleName() == null ? "" : body.roleName().trim();
            if (roleName.isEmpty()) {
                return badRequest("역할명을 입력하세요.");
            }
            RoleRevokeResultVO result =
                    roleService.createRole(
                            prjId, roleName, body.menuRoleIds(), body.grpIds());
            resp.put("resultStatus", result.getResultStatus());
            resp.put("resultMessage", result.getResultMsg());
            resp.put("ok", result.isOk());
            if (!result.isOk()) {
                return ResponseEntity.badRequest().body(resp);
            }
            return ResponseEntity.ok(resp);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("ok", false, "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("ok", false, "message", "권한 등록 중 오류가 발생했습니다."));
        }
    }

    /** 역할 상세 그룹 관리 모달 — 프로젝트 그룹 선택 목록 */
    @GetMapping("/roleGroupPickList")
    @ResponseBody
    public Map<String, Object> roleGroupPickList(HttpSession session) {
        Long prjId = (Long) session.getAttribute("currentProjectId");
        if (prjId == null) {
            return Map.of("ok", false, "message", "프로젝트를 선택한 뒤 이용해 주세요.");
        }
        return listBody(
                prjId,
                groupService.selectProjectGroupList(GroupListCriteria.forPrjId(prjId)));
    }

    /** 역할 상세 — 메뉴 권한 수정 ({@code PROC_ROLE_UPDATE}) */
    @PostMapping("/updateRole")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateRole(
            @RequestBody(required = false) RoleUpdateRequest body, HttpSession session) {
        Long prjId = (Long) session.getAttribute("currentProjectId");
        if (prjId == null) {
            return badRequest("프로젝트를 선택한 뒤 이용해 주세요.");
        }
        Map<String, Object> resp = new LinkedHashMap<>();
        try {
            if (body == null || body.roleCd() == null) {
                return badRequest("요청이 올바르지 않습니다.");
            }
            RoleRevokeResultVO result =
                    roleService.updateRole(
                            prjId,
                            body.roleCd(),
                            body.menuRoleIds(),
                            body.grpIds());
            resp.put("resultStatus", result.getResultStatus());
            resp.put("resultMessage", result.getResultMsg());
            resp.put("ok", result.isOk());
            if (!result.isOk()) {
                return ResponseEntity.badRequest().body(resp);
            }
            return ResponseEntity.ok(resp);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("ok", false, "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("ok", false, "message", "권한 수정 중 오류가 발생했습니다."));
        }
    }

    /** 역할 상세 — 그룹에서 권한 회수 ({@code PROC_GRP_ROLE_DELETE}) */
    @PostMapping("/revokeRoleFromGroup")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> revokeRoleFromGroup(
            @RequestBody(required = false) RoleRevokeRequest body) {
        Map<String, Object> resp = new LinkedHashMap<>();
        try {
            if (body == null || body.roleCd() == null || body.grpId() == null) {
                return badRequest("요청이 올바르지 않습니다.");
            }
            RoleRevokeResultVO result =
                    roleService.revokeRoleFromGroup(
                            body.roleCd(),
                            body.grpId(),
                            Boolean.TRUE.equals(body.deleteRoleIfUnused()));
            resp.put("resultStatus", result.getResultStatus());
            resp.put("resultMessage", result.getResultMsg());
            resp.put("ok", result.isOk());
            if (!result.isOk()) {
                return ResponseEntity.badRequest().body(resp);
            }
            return ResponseEntity.ok(resp);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("ok", false, "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("ok", false, "message", "권한 회수 중 오류가 발생했습니다."));
        }
    }

    private static Map<String, Object> listBody(Long prjId, List<?> content) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("content", content);
        body.put("prjId", prjId);
        return body;
    }

    /** ROLE.CREATED_ON → yyyy-MM-dd (상세 화면 표시용) */
    private static String formatRoleDateYmd(Date dt) {
        if (dt == null) {
            return "";
        }
        return new SimpleDateFormat("yyyy-MM-dd").format(dt);
    }

    /** {@link #deleteRoles} JSON 본문 */
    public static record RoleDeleteRequest(List<Long> roleCds) {}

    public static record RoleCreateRequest(
            String roleName, List<String> menuRoleIds, List<Long> grpIds) {}

    public static record RoleUpdateRequest(
            Long roleCd, List<String> menuRoleIds, List<Long> grpIds) {}

    /** {@link #revokeRoleFromGroup} JSON 본문 */
    public static record RoleRevokeRequest(
            Long roleCd, Long grpId, Boolean deleteRoleIfUnused) {}
}
