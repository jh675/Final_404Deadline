package com.example.demo.project.messages.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.demo.project.boards.service.BoardsService;
import com.example.demo.project.messages.service.MessagesService;
import com.example.demo.project.messages.service.MessagesVO;
import com.example.demo.util.attach.service.AttachService;
import jakarta.servlet.http.HttpSession;

@RequestMapping("/project/messages")
@Controller
public class MessagesController {
    
    @Autowired private MessagesService messagesService;
    @Autowired private BoardsService boardsService;
    @Autowired private AttachService attachService;

    //목록 조회
    @GetMapping("/list")
    public String list(Model model, @RequestParam("boardId") Long boardId, 
                       @RequestParam(value = "id", required = false) Long id, 
                       MessagesVO messages, HttpSession session) {
        session.setAttribute("currentMenu", "messages");
        messages.setBoardId(boardId);
        model.addAttribute("list", messagesService.selectAll(messages));
        model.addAttribute("boardId", boardId);
        model.addAttribute("board", boardsService.selectOne(boardId));
        if (id != null) model.addAttribute("message", messagesService.selectOne(id));
        return "project/messages/messagesList";
    }

    //등록 폼
    @GetMapping("/register")
    public String registerForm(Model model, @RequestParam("boardId") Long boardId,
                               @RequestParam(value = "parentId", required = false) Long parentId) {
        MessagesVO message = new MessagesVO();
        message.setBoardId(boardId);
        if (parentId != null) model.addAttribute("parentId", parentId);
        model.addAttribute("boardId", boardId);
        model.addAttribute("message", message);
        return "project/messages/messagesRegister";
    }

    // 3. 등록 처리
    @PostMapping("/insert")
    public String insert(@ModelAttribute MessagesVO messages, 
                         @RequestPart(value="attachments", required = false) MultipartFile[] attachments) {
        messagesService.insert(messages);
        if (attachService.hasAttachmentFiles(attachments) && messages.getId() != null) {
            attachService.saveAndInsertAttachments(messages.getId(), attachments, "01MODULE", "notice");
        }
        return "redirect:/project/messages/list?boardId=" + messages.getBoardId();
    }

    // 4. 수정 폼
    @GetMapping("/modify")
    public String modifyForm(Model model, @RequestParam("id") Long id) {
        MessagesVO message = messagesService.selectOne(id);
        model.addAttribute("message", message);
        model.addAttribute("boardId", message.getBoardId());
        return "project/messages/messagesRegister";
    }

    //수정 처리
    @PostMapping({"/modify", "/update"})
    public String modify(@ModelAttribute MessagesVO messages) {
        messagesService.update(messages);
        return "redirect:/project/messages/list?boardId=" + messages.getBoardId();
    }

    //삭제 처리
    @GetMapping("/delete")
    public String delete(@RequestParam("id") Long id, @RequestParam("boardId") Long boardId) {
        messagesService.delete(id);
        return "redirect:/project/messages/list?boardId=" + boardId;
    }
    
    
}