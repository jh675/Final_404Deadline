package com.example.demo.project.history.controller;

import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.example.demo.project.history.service.HistoryListCriteria;
import com.example.demo.project.history.service.HistoryService;
import com.example.demo.project.history.service.HistoryVO;
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
        model.addAttribute("currentMenu", "history");

        return "project/history/history";
    }
}
