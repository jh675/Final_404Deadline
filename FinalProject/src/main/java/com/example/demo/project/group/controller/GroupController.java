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
import com.example.demo.project.group.service.GroupListCriteria;
import com.example.demo.project.group.service.GroupService;
import com.example.demo.project.group.service.ProjectGroupRowVO;
import lombok.RequiredArgsConstructor;

/**
 * 프로젝트 그룹 관리 — {@code project/group/groupManagement.html}
 */
@Controller
@RequestMapping("/project/group")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    /** 그룹 목록 화면 — 검색 조건 반영 후 Thymeleaf에 rows 전달 */
    @GetMapping("/groupManagement")
    public String groupManagementPage(
            @RequestParam("prjId") Long prjId,
            @RequestParam(required = false) String grpName,
            @RequestParam(required = false) String createdFrom,
            @RequestParam(required = false) String createdTo,
            Model model) {

        GroupListCriteria criteria = GroupListCriteria.builder()
                .prjId(prjId)
                .grpName(nullToEmpty(grpName))
                .createdFrom(nullToEmpty(createdFrom))
                .createdTo(nullToEmpty(createdTo))
                .build();

        List<ProjectGroupRowVO> rows = groupService.selectProjectGroupList(criteria);

        model.addAttribute("rows", rows);
        model.addAttribute("prjId", prjId);
        model.addAttribute("grpName", criteria.getGrpName());
        model.addAttribute("createdFrom", criteria.getCreatedFrom());
        model.addAttribute("createdTo", criteria.getCreatedTo());
        return "project/group/groupManagement";
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

    /** 선택 그룹 삭제 — 소속 MEMBER·GRP_ROLE 정리 후 GRP 삭제 */
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

    /** MyBatis 동적 SQL에서 null 대신 빈 문자열로 통일 */
    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }
}
