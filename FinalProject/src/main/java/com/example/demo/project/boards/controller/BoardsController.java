package com.example.demo.project.boards.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.project.boards.service.BoardsService;
import com.example.demo.project.boards.service.BoardsVO;

import jakarta.servlet.http.HttpSession;

@RequestMapping("/project")
@Controller
public class BoardsController {
	
	@Autowired
	private BoardsService boardsService;
	
	// 등록 페이지 이동
	@GetMapping("/boards/register")
	public String insertForm(Model model) {
		model.addAttribute("boards", new BoardsVO());
		return "project/Boards/boardsRegister";
	}
	
	// 등록 처리
	@PostMapping("/boards/insert")
	public String insert(BoardsVO boards, HttpSession session) {
		// 세션에서 프로젝트 ID를 꺼냅니다.
		Long projectId = (Long) session.getAttribute("currentProjectId");
		boards.setPrjId(projectId);


		// 게시글 등록 실행
		boardsService.insert(boards);
		return "redirect:/boards/list";
	}

	// 목록 페이지
	@GetMapping("/boards/list")
	public String boardsList(Model model, BoardsVO boards, HttpSession session) {
		session.setAttribute("currentMenu", "board");
		Long projectid = (Long) session.getAttribute("currentProjectId");
		boards.setPrjId(projectid);
		model.addAttribute("list", boardsService.selectAll(boards));
		return "project/Boards/boardsList";
	}

	// 수정 페이지 이동
	@GetMapping("/boards/modify")
	public String modifyForm(Model model, @RequestParam("id") Long id) {
		model.addAttribute("boards", boardsService.selectOne(id));
		return "project/boards/boardsRegister";
	}
	
	// 수정 처리
	@PostMapping("/boards/modify")
	public String modify(BoardsVO boards) {
		boardsService.update(boards);
		return "redirect:/boards/list";
	}

	// 삭제 처리
	@GetMapping("/boards/delete")
	public String delete(@RequestParam("id") Long id) {
		boardsService.delete(id);
		return "redirect:/boards/list";
	}
}