package com.example.demo.project.option.controller;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.example.demo.project.option.service.RoleService;
import com.example.demo.project.option.service.RoleVO;
import lombok.RequiredArgsConstructor;

/**
 * 권한 관리 — {@code project/role/roleManagement.html} (TOAST UI Grid, {@code rows}, {@code prjId}).
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
     * 권한 상세 — {@code project/role/roleManagementInfo.html}
     */
    @GetMapping("/roleManagementInfo")
    public String roleManagementInfoPage(
            @RequestParam("prjId") Long prjId,
            @RequestParam("roleId") String roleId,
            Model model) {

        RoleVO search = RoleVO.builder()
                .prjId(prjId)
                .permissionKey("")
                .permissionName("")
                .createdFrom("")
                .createdTo("")
                .build();

        List<RoleVO> rows = roleService.selectRoleList(search);

        model.addAttribute("rows", rows);
        addPermSection(model, "issue", rows, "ROLE_ISSUE_");
        addPermSection(model, "member", rows, "ROLE_MEMBER_");
        addPermSection(model, "group", rows, "ROLE_GROUP_");
        addPermSection(model, "history", rows, "ROLE_HISTORY_");
        model.addAttribute("prjId", prjId);
        model.addAttribute("roleId", nullToEmpty(roleId));
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

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }

    /** 메뉴 권한 코드 접두사별 목록 (상세 화면 체크박스용), ROLE_ID 오름차순 */
    private static List<RoleVO> permsByPrefix(List<RoleVO> rows, String prefix) {
        if (rows == null || prefix == null) {
            return List.of();
        }
        return rows.stream()
                .filter(r -> r.getRoleId() != null && r.getRoleId().startsWith(prefix))
                .sorted(Comparator.comparing(RoleVO::getRoleId))
                .toList();
    }

    private static void addPermSection(Model model, String key, List<RoleVO> rows, String prefix) {
        List<RoleVO> all = permsByPrefix(rows, prefix);
        model.addAttribute(key + "Perms", all);
        model.addAttribute(key + "PermAll", findAllMenuPerm(all));
        model.addAttribute(key + "PermDetails", detailPermsSorted(all));
    }

    /** 접두사 묶음 안의 {@code *_ALL} 한 건 (전체 관리) */
    private static RoleVO findAllMenuPerm(List<RoleVO> perms) {
        if (perms == null) {
            return null;
        }
        return perms.stream()
                .filter(p -> p.getRoleId() != null && p.getRoleId().endsWith("_ALL"))
                .findFirst()
                .orElse(null);
    }

    /** 전체 관리 제외 — 조회·등록·수정·삭제 등 세부 권한 (표시 순: 조회 → 등록 → 수정 → 삭제) */
    private static List<RoleVO> detailPermsSorted(List<RoleVO> perms) {
        if (perms == null) {
            return List.of();
        }
        return perms.stream()
                .filter(p -> p.getRoleId() == null || !p.getRoleId().endsWith("_ALL"))
                .sorted(Comparator.comparingInt(RoleController::crudSortKey).thenComparing(RoleVO::getRoleId))
                .toList();
    }

    /** {@code ROLE_*_READ} 등을 조회(0)~삭제(3) 순으로 정렬 */
    private static int crudSortKey(RoleVO p) {
        String id = p.getRoleId();
        if (id == null) {
            return 99;
        }
        String u = id.toUpperCase();
        if (u.endsWith("_READ") || u.endsWith("_SELECT") || u.endsWith("_VIEW") || u.endsWith("_LIST")) {
            return 0;
        }
        if (u.endsWith("_CREATE") || u.endsWith("_INSERT") || u.endsWith("_ADD") || u.endsWith("_REG")) {
            return 1;
        }
        if (u.endsWith("_UPDATE") || u.endsWith("_MODIFY") || u.endsWith("_EDIT")) {
            return 2;
        }
        if (u.endsWith("_DELETE") || u.endsWith("_REMOVE")) {
            return 3;
        }
        return 50;
    }
}
