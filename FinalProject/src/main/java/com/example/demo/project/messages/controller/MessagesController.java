package com.example.demo.project.messages.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.project.boards.service.BoardsService;
import com.example.demo.project.messages.service.MessagesService;
import com.example.demo.project.messages.service.MessagesVO;
import com.example.demo.util.attach.service.AttachService;

import jakarta.servlet.http.HttpSession;

@RequestMapping("/project")
@Controller
public class MessagesController {
    
    @Autowired
    private MessagesService messagesService;
    
    @Autowired
    private BoardsService boardsService;
    
    @Autowired
    private AttachService attachService;
    
    //메시지 등록화면
    @GetMapping("/messages/register")
    public String insertForm(Model model, 
                             @RequestParam("boardId") Long boardId,
                             @RequestParam(value = "parentId", required = false) Long parentId) {
        MessagesVO message = new MessagesVO();
        message.setBoardId(boardId); 
        
        if (parentId != null) {
            MessagesVO parentMessage = messagesService.selectOne(parentId);
            if (parentMessage != null) {
                message.setTitle("[RE] " + parentMessage.getTitle());
            }
            model.addAttribute("parentId", parentId);
        }
        
        model.addAttribute("boardId", boardId);
        model.addAttribute("message", message);
        return "project/messages/messagesRegister";
    }
    
    //메시지 실제 등록처리 (리다이렉트 주소 수정완료)
    @PostMapping("/messages/insert")
    public String insert(MessagesVO messages, 
            @RequestPart(value="attachments", required = false) MultipartFile[] attachments) {
        
        messagesService.insert(messages);
        
        boolean hasFiles = attachService.hasAttachmentFiles(attachments);
        if (hasFiles && messages.getId() != null) {
            attachService.saveAndInsertAttachments((long)messages.getId(), attachments, "01MODULE", "notice");
        }
        
        try {
            com.example.demo.project.boards.service.BoardsVO board = boardsService.selectOne(messages.getBoardId());
            if (board != null) {
                Long currentCount = board.getTopicsCount();
                
                if (currentCount == null) {
                    currentCount = 0L;
                }
                
                board.setTopicsCount(currentCount + 1L);
                boardsService.update(board);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // redirect: 경로 뒤에 /project를 명시해 줍니다.
        return "redirect:/project/messages/list?boardId=" + messages.getBoardId();
    }
    
    // 메시지 목록 조회 및 하단 상세 내용/댓글 로드 매핑
    @GetMapping("/messages/list")
    public String messagesList(Model model, 
                               @RequestParam("boardId") Long boardId, 
                               @RequestParam(value = "id", required = false) Long id, 
                               MessagesVO messages , 
                               HttpSession session) {
        session.setAttribute("currentMenu", "messages");
        messages.setBoardId(boardId);
        
        model.addAttribute("list", messagesService.selectAll(messages));
        model.addAttribute("boardId", boardId);
        model.addAttribute("board", boardsService.selectOne(boardId));
        
        if (id != null) {
            model.addAttribute("message", messagesService.selectOne(id)); 
        }
        
        return "project/messages/messagesList";
    }
    
    // 메시지 상세보기 화면 이동
    @GetMapping("/messages/detail")
    public String messagesDetail(Model model, @RequestParam("id") Long id) {
        model.addAttribute("message", messagesService.selectOne(id));
        return "project/messages/messagesDetail";
    }
    
    // 메시지 수정화면 이동
    @GetMapping("/messages/modify")
    public String modifyForm(Model model, @RequestParam("id") Long id) {
        MessagesVO message = messagesService.selectOne(id);
        
        model.addAttribute("message", message);
        model.addAttribute("boardId", message.getBoardId());
        
        return "project/messages/messagesRegister"; 
    }
    
    // 메세지 수정 (리다이렉트 주소 수정완료)
    @PostMapping("/messages/update")
    public String modify(MessagesVO messages) {
        messagesService.update(messages);
        // redirect: 경로 뒤에 /project를 명시해 줍니다.
        return "redirect:/project/messages/list?boardId=" + messages.getBoardId();
    }

    // 메시지 삭제 (리다이렉트 주소 수정완료)
    @GetMapping("/messages/delete")
    public String delete(@RequestParam("id") Long id, @RequestParam("boardId") Long boardId) {
        
        messagesService.delete(id);
        
        try {
            com.example.demo.project.boards.service.BoardsVO board = boardsService.selectOne(boardId);
            if (board != null) {
                Long currentCount = board.getTopicsCount();
                
                if (currentCount != null && currentCount > 0L) {
                    board.setTopicsCount(currentCount - 1L);
                } else {
                    board.setTopicsCount(0L);
                }
                boardsService.update(board);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // redirect: 경로 뒤에 /project를 명시해 줍니다.
        return "redirect:/project/messages/list?boardId=" + boardId;
    }
    
    private Long parentId; 

    public Long getParentId() {
        return parentId;
    }
    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
}