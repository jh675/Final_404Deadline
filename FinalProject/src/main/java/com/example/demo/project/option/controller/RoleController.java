package com.example.demo.project.option.controller;

import java.util.Collections;
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
 * 권한 관리 목록 — DB {@code menu} 마스터 + 프로젝트별 {@code grp} 집계.
 */
@Controller
@RequestMapping("/project/option/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping("/roleManagement")
    public String roleManagementPage(
            @RequestParam("projectId") Long projectId,
            @RequestParam(required = false) String permissionKey,
            @RequestParam(required = false) String permissionName,
            @RequestParam(required = false) String createdFrom,
            @RequestParam(required = false) String createdTo,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int pageSize,
            Model model) {

        RoleVO search = RoleVO.builder()
                .projectId(projectId)
                .permissionKey(nullToEmpty(permissionKey))
                .permissionName(nullToEmpty(permissionName))
                .createdFrom(nullToEmpty(createdFrom))
                .createdTo(nullToEmpty(createdTo))
                .page(page)
                .pageSize(pageSize)
                .build();

        long totalElements = roleService.countRoleList(search);
        List<RoleVO> rows =
                totalElements == 0 ? Collections.emptyList() : roleService.selectRoleList(search);
        int totalPages = totalPages(totalElements, search.getPageSize());

        model.addAttribute("rows", rows);
        model.addAttribute("totalElements", totalElements);
        model.addAttribute("page", search.getPage());
        model.addAttribute("pageSize", search.getPageSize());
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("projectId", projectId);
        model.addAttribute("permissionKey", nullToEmpty(permissionKey));
        model.addAttribute("permissionName", nullToEmpty(permissionName));
        model.addAttribute("createdFrom", nullToEmpty(createdFrom));
        model.addAttribute("createdTo", nullToEmpty(createdTo));
        return "project/role/RoleManagement";
    }

    @GetMapping("/roleManagementList")
    @ResponseBody
    public Map<String, Object> roleManagementList(
            @RequestParam("projectId") Long projectId,
            @RequestParam(required = false) String permissionKey,
            @RequestParam(required = false) String permissionName,
            @RequestParam(required = false) String createdFrom,
            @RequestParam(required = false) String createdTo,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int pageSize) {

        RoleVO search = RoleVO.builder()
                .projectId(projectId)
                .permissionKey(nullToEmpty(permissionKey))
                .permissionName(nullToEmpty(permissionName))
                .createdFrom(nullToEmpty(createdFrom))
                .createdTo(nullToEmpty(createdTo))
                .page(page)
                .pageSize(pageSize)
                .build();

        long totalElements = roleService.countRoleList(search);
        List<RoleVO> rows =
                totalElements == 0 ? Collections.emptyList() : roleService.selectRoleList(search);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("content", rows);
        body.put("totalElements", totalElements);
        body.put("page", search.getPage());
        body.put("pageSize", search.getPageSize());
        body.put("totalPages", totalPages(totalElements, search.getPageSize()));
        return body;
    }

    private static int totalPages(long totalElements, int pageSize) {
        if (pageSize <= 0) {
            return 0;
        }
        return (int) Math.ceil((double) totalElements / pageSize);
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }
}
