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
	
	@GetMapping("/boards/register")
	public String insertForm(Model model) {
		model.addAttribute("boards", new BoardsVO());
		return "project/Boards/boardsRegister";
	}
	
	@PostMapping("/boards/insert")
	public String insert(BoardsVO boards, HttpSession session) {
		Long projectId = (Long) session.getAttribute("currentProjectId");
		boards.setPrjId(projectId);

		boardsService.insert(boards);
		// 경로 수정 완료
		return "redirect:/project/boards/list";
	}

	@GetMapping("/boards/list")
	public String boardsList(Model model, BoardsVO boards, HttpSession session) {
		session.setAttribute("currentMenu", "board");
		Long projectid = (Long) session.getAttribute("currentProjectId");
		boards.setPrjId(projectid);
		model.addAttribute("list", boardsService.selectAll(boards));
		return "project/Boards/boardsList";
	}

	@GetMapping("/boards/modify")
	public String modifyForm(Model model, @RequestParam("id") Long id) {
		model.addAttribute("boards", boardsService.selectOne(id));
		return "project/boards/boardsRegister";
	}
	
	@PostMapping("/boards/modify")
	public String modify(BoardsVO boards) {
		boardsService.update(boards);
		// 경로 수정 완료
		return "redirect:/project/boards/list";
	}

	@GetMapping("/boards/delete")
	public String delete(@RequestParam("id") Long id) {
		boardsService.delete(id);
		// 경로 수정 완료
		return "redirect:/project/boards/list";
	}
}