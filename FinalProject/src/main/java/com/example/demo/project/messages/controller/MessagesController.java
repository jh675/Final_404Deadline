package com.example.demo.project.messages.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.project.messages.service.MessagesService;
import com.example.demo.project.messages.service.MessagesVO;

@Controller
public class MessagesController {
    
    @Autowired
    private MessagesService messagesService;
    
    // 등록 페이지 이동
    @GetMapping("/messages/register")
    public String insertForm(Model model, @RequestParam("boardId") int boardId) {
        MessagesVO message = new MessagesVO();
        message.setBoardId(boardId);
        
        model.addAttribute("messages", message);
        return "project/messages/messagesRegister"; 
    }
    
    // 등록 처리
    @PostMapping("/messages/insert")
    public String insert(MessagesVO messages) {
        messagesService.insert(messages);
        return "redirect:/messages/list?boardId=" + messages.getBoardId();
    }
    
    // 목록 페이지
    @GetMapping("/messages/list")
    public String messagesList(Model model, @RequestParam("boardId") int boardId, MessagesVO messages) {
        messages.setBoardId(boardId);
        
        model.addAttribute("list", messagesService.selectAll(messages));
        model.addAttribute("boardId", boardId);
        return "project/messages/messagesList";
    }
    
    // 상세 조회 페이지
    @GetMapping("/messages/detail")
    public String messagesDetail(Model model, @RequestParam("id") int id) {
        model.addAttribute("message", messagesService.selectOne(id));
        return "project/messages/messagesDetail";
    }
    
    // 수정 페이지 이동
    @GetMapping("/messages/modify")
    public String modifyForm(Model model, @RequestParam("id") int id) {
        model.addAttribute("messages", messagesService.selectOne(id));
        return "project/messages/messagesRegister"; 
    }
    
    // 수정 처리
    @PostMapping("/messages/modify")
    public String modify(MessagesVO messages) {
        messagesService.update(messages);
        return "redirect:/messages/list?boardId=" + messages.getBoardId();
    }

    // 삭제 처리
    @GetMapping("/messages/delete")
    public String delete(@RequestParam("id") int id, @RequestParam("boardId") int boardId) {
        messagesService.delete(id);
        return "redirect:/messages/list?boardId=" + boardId;
    }
}