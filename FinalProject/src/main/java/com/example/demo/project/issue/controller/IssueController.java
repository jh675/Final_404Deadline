package com.example.demo.project.issue.controller;

import java.io.Console;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.project.issue.service.IssueInputVO;
import com.example.demo.project.issue.service.IssueOutputVO;
import com.example.demo.project.issue.service.IssueService;

@Controller
public class IssueController {

    @Autowired
    private IssueService issueService;

    @GetMapping("/issue/list")
    public String issueList(Model model,@ModelAttribute("filter")IssueInputVO issueVO ) {
        List<IssueOutputVO> issueList = issueService.selectIssueList(issueVO);
        System.out.println(issueList);
        model.addAttribute("issueList", issueList);
        return "project/issue/issueList";
    }

    @GetMapping("/issue/detail")
    public String issueDetail(Model model, @RequestParam("id") Long id) {
    	IssueOutputVO issue = issueService.selectIssue(id);
        model.addAttribute("issue", issue);
        return "project/issue/issueDetail";
    }

    @PostMapping("/issue/insert")
    public String issueInsert(Model model, @RequestBody IssueInputVO issueVO) {
        issueService.insertIssue(issueVO);
        return "redirect:/issue/list";
    }

    @DeleteMapping("/issue/update")
    public String issueUpdate(Model model, @RequestBody IssueInputVO issueVO) {
        issueService.updateIssue(issueVO);
        return "redirect:/issue/list";

    }
}
