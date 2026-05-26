package com.example.demo.project.messages.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.project.boards.service.BoardsService;
import com.example.demo.project.messages.service.MessagesService;
import com.example.demo.project.messages.service.MessagesVO;
import com.example.demo.util.attach.service.AttachService;

@Controller
public class MessagesController {
    
    @Autowired
    private MessagesService messagesService; // 메시지 관리 서비스
    
    @Autowired
    private BoardsService boardsService;     // 게시판 관리 서비스 
    
    @Autowired
    private AttachService attachService;     // 첨부파일 관리 서비스
    
    /**
     * 1. 메시지 등록 화면 이동
     */
    @GetMapping("/messages/register")
    public String insertForm(Model model, @RequestParam("boardId") Long boardId) {
        MessagesVO message = new MessagesVO();
        message.setBoardId(boardId); 
        
        model.addAttribute("boardId", boardId);
        model.addAttribute("message", message);
        return "project/messages/messagesRegister";
    }
    
    //메시지 실제 등록처리
    @PostMapping("/messages/insert")
    public String insert(MessagesVO messages, 
            @RequestPart(value="attachments", required = false) MultipartFile[] attachments) {
        
        // 새 메시지 db에 등록
        messagesService.insert(messages);
        
        // 첨부파일 db에 등록
        boolean hasFiles = attachService.hasAttachmentFiles(attachments);
        if (hasFiles && messages.getId() != null) {
            attachService.saveAndInsertAttachments((long)messages.getId(), attachments, "01MODULE", "notice");
        }
        
        // 토픽수 증가
        try {
            // 현재 메시지가 등록된 게시판 정보 조회
            com.example.demo.project.boards.service.BoardsVO board = boardsService.selectOne(messages.getBoardId());
            if (board != null) {
                Long currentCount = board.getTopicsCount(); // 기존 토픽 수 가져오기
                
                if (currentCount == null) {
                    currentCount = 0L; // 값이 없으면 0으로 세팅
                }
                
                board.setTopicsCount(currentCount + 1L); // 기존수의 +1
                boardsService.update(board);             // 변경된 정보 db저장
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return "redirect:/messages/list?boardId=" + messages.getBoardId();
    }
    
    // 메시지 목록 조회 및 하단 상세 내용/댓글 로드 매핑 (오류 수정 완료)
    @GetMapping("/messages/list")
    public String messagesList(Model model, 
                               @RequestParam("boardId") Long boardId, 
                               @RequestParam(value = "id", required = false) Long id, 
                               MessagesVO messages) {
        messages.setBoardId(boardId);
        
        model.addAttribute("list", messagesService.selectAll(messages)); // 메시지 목록 데이터
        model.addAttribute("boardId", boardId);
        model.addAttribute("board", boardsService.selectOne(boardId));   // 게시판 정보 
        
        // [상세] 버튼이나 제목을 눌러 id가 들어온 경우 단건 데이터를 정상적으로 바인딩합니다.
        if (id != null) {
            model.addAttribute("message", messagesService.selectOne(id)); 
        }
        
        // return 문이 if문 밖으로 완전히 빠져나와 정상적으로 모든 상황에서 화면을 그려줍니다.
        return "project/messages/messagesList";
    }
    
    // 메시지 상세보기 화면 이동
    @GetMapping("/messages/detail")
    public String messagesDetail(Model model, @RequestParam("id") Long id) {
        model.addAttribute("message", messagesService.selectOne(id)); // 특정메시지 조회
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
    
    // 메세지 수정
    @PostMapping("/messages/update")
    public String modify(MessagesVO messages) {
        messagesService.update(messages); // 메시지 데이터 내용 수정
        return "redirect:/messages/list?boardId=" + messages.getBoardId();
    }

    // 메시지 삭제
    @GetMapping("/messages/delete")
    public String delete(@RequestParam("id") Long id, @RequestParam("boardId") Long boardId) {
        
        // 데이터를 db에서 삭제
        messagesService.delete(id);
        
        try {
            // 메시지가 삭제된 게시판 정보 조회
            com.example.demo.project.boards.service.BoardsVO board = boardsService.selectOne(boardId);
            if (board != null) {
                Long currentCount = board.getTopicsCount(); // 기존 토픽 수 가져오기
                
                if (currentCount != null && currentCount > 0L) {
                    board.setTopicsCount(currentCount - 1L); // 기존 수에서 -1
                } else {
                    board.setTopicsCount(0L);
                }
                boardsService.update(board);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/messages/list?boardId=" + boardId;
    }
}