package com.example.demo.project.option.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
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
import com.example.demo.project.option.service.RoleMenuSectionVO;
import com.example.demo.project.option.service.RoleMenuSectionVO.CrudSlot;
import com.example.demo.project.option.service.RoleMenuSectionVO.LabelSlot;
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
            model.addAttribute("menuSections", buildMenuSections(allMenus, Set.of()));
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
        model.addAttribute("menuSections", buildMenuSections(allMenus, linked));
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

    /** MENU 전체 + ROLE_MENU 연결 여부 → 상세 화면 체크박스 섹션 DTO로 변환 */
    private static List<RoleMenuSectionVO> buildMenuSections(List<RoleVO> allMenus, Set<String> linked) {
        Set<String> safeLinked = linked == null ? Set.of() : linked;

        Map<String, List<RoleVO>> byTp = allMenus.stream()
                .collect(Collectors.groupingBy(
                        m -> (m.getRoleTp() != null && !m.getRoleTp().isBlank())
                                ? m.getRoleTp()
                                : "기타",
                        LinkedHashMap::new,
                        Collectors.toList()));

        List<RoleMenuSectionVO> sections = new ArrayList<>();
        for (Map.Entry<String, List<RoleVO>> e : byTp.entrySet()) {
            List<RoleVO> items = e.getValue();
            List<RoleVO> fullRows = new ArrayList<>();
            List<RoleVO> nonFullRows = new ArrayList<>();
            for (RoleVO m : items) {
                if (isAllMth(m.getRoleMth())) {
                    fullRows.add(m);
                } else {
                    nonFullRows.add(m);
                }
            }

            boolean anyFullLinked =
                    fullRows.stream().anyMatch(m -> safeLinked.contains(m.getRoleId()));

            boolean viewOnlySection = isViewOnlySection(e.getKey());

            String[] mths = {"GET", "POST", "PUT", "DELETE"};
            String[] labels = {"조회", "등록", "수정", "삭제"};
            List<CrudSlot> crud = new ArrayList<>(4);
            for (int i = 0; i < mths.length; i++) {
                if (viewOnlySection && !"GET".equals(mths[i])) {
                    continue;
                }
                RoleVO row = findFirstByMth(nonFullRows, mths[i]);
                boolean checked = anyFullLinked
                        || (row != null && safeLinked.contains(row.getRoleId()));
                String menuRoleId = row != null ? row.getRoleId() : null;
                crud.add(new CrudSlot(labels[i], checked, menuRoleId));
            }

            boolean allCrudChecked =
                    crud.size() == mths.length
                            && crud.stream().allMatch(CrudSlot::isChecked);

            List<LabelSlot> fullSlots;
            if (viewOnlySection) {
                fullSlots = List.of();
            } else if (fullRows.isEmpty()) {
                fullSlots =
                        crud.isEmpty()
                                ? List.of()
                                : List.of(new LabelSlot("전체 관리", allCrudChecked, null));
            } else {
                fullSlots = fullRows.stream()
                        .map(m -> new LabelSlot(
                                m.getRoleName() != null ? m.getRoleName() : "전체 관리",
                                safeLinked.contains(m.getRoleId()) || allCrudChecked,
                                m.getRoleId()))
                        .collect(Collectors.toList());
            }

            List<LabelSlot> extras =
                    viewOnlySection
                            ? List.of()
                            : nonFullRows.stream()
                                    .filter(m -> !isCrudMth(m.getRoleMth()))
                                    .map(m -> new LabelSlot(
                                            m.getRoleName() != null
                                                    ? m.getRoleName()
                                                    : m.getRoleId(),
                                            safeLinked.contains(m.getRoleId()),
                                            m.getRoleId()))
                                    .collect(Collectors.toList());

            sections.add(new RoleMenuSectionVO(e.getKey(), fullSlots, crud, extras));
        }
        return sections;
    }

    /** HISTORY 구역은 조회(GET) 체크박스만 노출 */
    private static boolean isViewOnlySection(String sectionTp) {
        return sectionTp != null && "HISTORY".equalsIgnoreCase(sectionTp.trim());
    }

    private static boolean isAllMth(String roleMth) {
        if (roleMth == null || roleMth.isBlank()) {
            return false;
        }
        String u = roleMth.trim().toUpperCase(Locale.ROOT);
        return "ALL".equals(u) || "전체".equals(roleMth.trim());
    }

    private static boolean isCrudMth(String roleMth) {
        if (roleMth == null || roleMth.isBlank()) {
            return false;
        }
        String u = roleMth.trim().toUpperCase(Locale.ROOT);
        return "GET".equals(u)
                || "POST".equals(u)
                || "PUT".equals(u)
                || "DELETE".equals(u)
                || "조회".equals(roleMth.trim())
                || "등록".equals(roleMth.trim())
                || "수정".equals(roleMth.trim())
                || "삭제".equals(roleMth.trim());
    }

    private static RoleVO findFirstByMth(List<RoleVO> rows, String code) {
        for (RoleVO m : rows) {
            if (mthMatches(m.getRoleMth(), code)) {
                return m;
            }
        }
        return null;
    }

    private static boolean mthMatches(String raw, String code) {
        if (raw == null || raw.isBlank()) {
            return false;
        }
        String t = raw.trim();
        String u = t.toUpperCase(Locale.ROOT);
        if (u.equals(code)) {
            return true;
        }
        return switch (code) {
            case "GET" -> "조회".equals(t);
            case "POST" -> "등록".equals(t);
            case "PUT" -> "수정".equals(t);
            case "DELETE" -> "삭제".equals(t);
            default -> false;
        };
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
