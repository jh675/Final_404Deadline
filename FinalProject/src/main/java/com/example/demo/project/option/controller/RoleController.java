package com.example.demo.project.option.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.example.demo.project.option.service.RoleGroupRowVO;
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
@RequestMapping("/project/option/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping("/roleManagement")
    public String roleManagementPage(
            @RequestParam("prjId") Long prjId,
            @RequestParam(required = false) String permissionKey,
            @RequestParam(required = false) String permissionName,
            @RequestParam(required = false) String createdFrom,
            @RequestParam(required = false) String createdTo,
            Model model) {

        RoleVO search = RoleVO.builder()
                .prjId(prjId)
                .permissionKey(nullToEmpty(permissionKey))
                .permissionName(nullToEmpty(permissionName))
                .createdFrom(nullToEmpty(createdFrom))
                .createdTo(nullToEmpty(createdTo))
                .build();

        List<RoleVO> rows = roleService.selectRoleList(search);

        model.addAttribute("rows", rows);
        model.addAttribute("prjId", prjId);
        model.addAttribute("permissionKey", search.getPermissionKey());
        model.addAttribute("permissionName", search.getPermissionName());
        model.addAttribute("createdFrom", search.getCreatedFrom());
        model.addAttribute("createdTo", search.getCreatedTo());
        return "project/role/roleManagement";
    }

    /**
     * 역할 상세 — {@code project/role/roleManagementInfo.html}
     * (프로젝트 {@code ROLE} + {@code ROLE_MENU}으로 연결된 {@code MENU} 권한 체크 표시)
     */
    @GetMapping("/roleManagementInfo")
    public String roleManagementInfoPage(
            @RequestParam("prjId") Long prjId,
            @RequestParam("roleCd") Long roleCd,
            Model model) {

        model.addAttribute("prjId", prjId);
        model.addAttribute("roleCd", roleCd);

        RoleVO currentRole = roleService.selectRoleByPrjAndCd(prjId, roleCd);
        if (currentRole == null) {
            model.addAttribute("roleNotFound", true);
            model.addAttribute("menuSections", List.of());
            return "project/role/roleManagementInfo";
        }

        model.addAttribute("roleNotFound", false);
        model.addAttribute("currentRole", currentRole);
        model.addAttribute("roleCreatedOnYmd", formatRoleDateYmd(currentRole.getCreatedOn()));

        List<RoleVO> allMenus = roleService.selectAllMenus();
        Set<String> linked = new HashSet<>(roleService.selectMenuRoleIdsByRoleCd(roleCd));
        model.addAttribute("menuSections", buildMenuSections(allMenus, linked));
        return "project/role/roleManagementInfo";
    }

    @GetMapping("/roleManagementList")
    @ResponseBody
    public Map<String, Object> roleManagementList(
            @RequestParam("prjId") Long prjId,
            @RequestParam(required = false) String permissionKey,
            @RequestParam(required = false) String permissionName,
            @RequestParam(required = false) String createdFrom,
            @RequestParam(required = false) String createdTo) {

        RoleVO search = RoleVO.builder()
                .prjId(prjId)
                .permissionKey(nullToEmpty(permissionKey))
                .permissionName(nullToEmpty(permissionName))
                .createdFrom(nullToEmpty(createdFrom))
                .createdTo(nullToEmpty(createdTo))
                .build();

        List<RoleVO> rows = roleService.selectRoleList(search);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("content", rows);
        body.put("prjId", prjId);
        return body;
    }

    /**
     * 역할 목록에서 선택 후 「제거」 — DB {@code PROC_ROLE_DELETE} 호출.
     */
    @PostMapping("/deleteRoles")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteRoles(
            @RequestBody(required = false) RoleDeleteRequest body) {
        Map<String, Object> ok = new LinkedHashMap<>();
        ok.put("ok", true);
        try {
            if (body == null) {
                return badRequest("요청 본문이 비어 있습니다.");
            }
            roleService.deleteRolesForProject(body.prjId(), body.roleCds());
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
    public Map<String, Object> roleGroups(
            @RequestParam("prjId") Long prjId,
            @RequestParam(value = "roleCd", required = false) Long roleCd) {

        List<RoleGroupRowVO> list =
                roleCd == null ? List.of() : roleService.selectRoleGroupsList(prjId, roleCd);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("content", list);
        body.put("prjId", prjId);
        return body;
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }

    private static String formatRoleDateYmd(LocalDateTime dt) {
        if (dt == null) {
            return "";
        }
        return dt.toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

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

            List<LabelSlot> fullSlots = fullRows.stream()
                    .map(m -> new LabelSlot(
                            m.getRoleName() != null ? m.getRoleName() : "전체 관리",
                            safeLinked.contains(m.getRoleId())))
                    .collect(Collectors.toList());

            String[] mths = {"GET", "POST", "PUT", "DELETE"};
            String[] labels = {"조회", "등록", "수정", "삭제"};
            List<CrudSlot> crud = new ArrayList<>(4);
            for (int i = 0; i < mths.length; i++) {
                RoleVO row = findFirstByMth(nonFullRows, mths[i]);
                boolean checked = anyFullLinked
                        || (row != null && safeLinked.contains(row.getRoleId()));
                crud.add(new CrudSlot(labels[i], checked));
            }

            List<LabelSlot> extras = nonFullRows.stream()
                    .filter(m -> !isCrudMth(m.getRoleMth()))
                    .map(m -> new LabelSlot(
                            m.getRoleName() != null ? m.getRoleName() : m.getRoleId(),
                            safeLinked.contains(m.getRoleId())))
                    .collect(Collectors.toList());

            sections.add(new RoleMenuSectionVO(e.getKey(), fullSlots, crud, extras));
        }
        return sections;
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
    public static record RoleDeleteRequest(Long prjId, List<Long> roleCds) {}
}
