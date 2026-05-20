package com.example.demo.project.ganttchart.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.example.demo.project.issue.service.IssueInputVO;
import com.example.demo.project.issue.service.IssueOutputVO;
import com.example.demo.project.issue.service.IssueService;

@Controller
public class Ganttchartconttoller {

	@Autowired
	IssueService issueService;

	// 간트차트 페이지 접속
	@GetMapping("/ganttchart/list")
	public String ganttchartlist() {
		return "project/issue/ganttChart";  
	}

	// 이슈목록 가져오기
	 @GetMapping("/ganttchart/listget") 
	 @ResponseBody
	 public List<IssueOutputVO> getIssue() {
		 IssueInputVO param = new IssueInputVO();
		 return issueService.selectIssueList(param);
	 }

}
