package com.example.demo.project.boards.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.demo.project.boards.service.BoardsService;
import com.example.demo.project.boards.service.BoardsVO;
import jakarta.servlet.http.HttpSession;

@RequestMapping("/project/boards")
@Controller
public class BoardsController {
    
    @Autowired private BoardsService boardsService;
    
    @GetMapping("/register")
    public String insertForm(Model model) {
        model.addAttribute("boards", new BoardsVO());
        return "project/Boards/boardsRegister";
    }
    
    @PostMapping("/insert")
    public String insert(BoardsVO boards, HttpSession session) {
        Long projectId = (Long) session.getAttribute("currentProjectId");
        boards.setPrjId(projectId);
        boardsService.insert(boards);
        return "redirect:/project/boards/list";
    }

    @GetMapping("/list")
    public String boardsList(Model model, BoardsVO boards, HttpSession session) {
        session.setAttribute("currentMenu", "board");
        Long projectid = (Long) session.getAttribute("currentProjectId");
        boards.setPrjId(projectid);
        model.addAttribute("list", boardsService.selectAll(boards));
        return "project/Boards/boardsList";
    }

    @GetMapping("/modify")
    public String modifyForm(Model model, @RequestParam("id") Long id) {
        model.addAttribute("boards", boardsService.selectOne(id));
        return "project/Boards/boardsRegister";
    }
    
    @PostMapping("/modify")
    public String modify(BoardsVO boards) {
        boardsService.update(boards);
        return "redirect:/project/boards/list";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam("id") Long id) {
        boardsService.delete(id);
        return "redirect:/project/boards/list";
    }
}