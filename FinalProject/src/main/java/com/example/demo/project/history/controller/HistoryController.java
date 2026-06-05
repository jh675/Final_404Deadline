package com.example.demo.project.history.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.example.demo.project.history.service.*;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/project/history")
@RequiredArgsConstructor
public class HistoryController {

    private final HistoryService historyService;

    /** 프로젝트 작업내역 — 세션 {@code currentProjectId} 기준 */
    @GetMapping("/list")
    public String historyList(HistoryListCriteria criteria, HttpSession session, Model model) {
        Long projectId = (Long) session.getAttribute("currentProjectId");
        if (projectId == null) {
            return "redirect:/management/project";
        }

        if (criteria == null) {
            criteria = new HistoryListCriteria();
        }
        criteria.setPrjId(projectId);

        List<HistoryVO> rows = historyService.selectHistoryList(criteria);

        model.addAttribute("rows", rows);
        model.addAttribute("prjId", projectId);
        model.addAttribute("updatedFrom", criteria.getUpdatedFrom());
        model.addAttribute("updatedTo", criteria.getUpdatedTo());
        model.addAttribute("modifierName", criteria.getModifierName());
        model.addAttribute("tableName", criteria.getTableName());
        model.addAttribute("detail", criteria.getDetail());
        session.setAttribute("currentMenu", "history");

        return "project/history/history";
    }

    /** 히스토리 변경 필드 상세 — 모달 AJAX ({@code HISTORY_DETAIL.HISTORY_ID}) */
    @GetMapping(value = "/detail", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> historyDetail(
            @RequestParam(value = "historyId", required = false) Long historyId) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("historyId", historyId);
        if (historyId == null) {
            body.put("ok", false);
            body.put("message", "historyId가 필요합니다.");
            body.put("content", List.of());
            return ResponseEntity.badRequest().body(body);
        }
        List<HistoryDetailVO> content = historyService.selectHistoryDetailList(historyId);
        body.put("ok", true);
        body.put("content", content);
        return ResponseEntity.ok(body);
    }
}
