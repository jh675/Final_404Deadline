package com.example.demo.project.boards.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.project.boards.service.BoardsService;
import com.example.demo.project.boards.service.BoardsVO;

@Controller
public class BoardsController {
	
	@Autowired
	private BoardsService boardsService;
	    
	// 등록 페이지 이동
	@GetMapping("/boards/register")
	public String insertForm(Model model) {
		model.addAttribute("boards", new BoardsVO());
		return "project/boards/boardsRegister"; 
	}
	
	// 등록 처리
	@PostMapping("/boards/insert")
	public String insert(BoardsVO boards) {
		boardsService.insert(boards);
		return "redirect:/boards/list";
	}
    
	// 목록 페이지
	@GetMapping("/boards/list")
	public String boardsList(Model model, BoardsVO boards) {
		boards.setPrjId(4);
		model.addAttribute("list", boardsService.selectAll(boards));
		return "project/boards/boardsList";
	}
    
	// 수정 페이지 이동
	@GetMapping("/boards/modify")
	public String modifyForm(Model model, @RequestParam("id") int id) {
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
	public String delete(@RequestParam("id") int id) {
		boardsService.delete(id);
		return "redirect:/boards/list";
	}
}