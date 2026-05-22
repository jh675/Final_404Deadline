package com.example.demo.management.userManage.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.login.service.UserVO;
import com.example.demo.management.userManage.service.CAUserManageService; // 🚨 CA 서비스 임포트
import com.example.demo.management.userManage.service.UserManageVO;
import com.example.demo.util.attach.service.AttachService;
import com.example.demo.util.attach.service.AttachVO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/cadmin")
public class CAUserManageController {

	private final CAUserManageService caUserManageService;
	private final AttachService attachService;
	

	private UserVO getCurrentAdmin() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return (UserVO) auth.getPrincipal();
	}
	
	@GetMapping("/userList")
	public String userList(Model model, @ModelAttribute("filter01") UserManageVO userManage) {
		// 내 기업의 직원만 보도록 bizNo 강제 세팅
		String myBizNo = getCurrentAdmin().getBizNo();
		userManage.setBizNo(myBizNo); 
		
		List<UserManageVO> list = caUserManageService.selectAll(userManage);
		model.addAttribute("userList", list);
		
		// 모달창에서 보여줄 내 기업 번호 전달
		model.addAttribute("myBizNo", myBizNo); 
		
		return "management/user/userListCA";
	}
	
	
	@PostMapping("/userInsert")
	@ResponseBody
	public UserManageVO userInsert(@RequestBody UserManageVO vo) {
		
		vo.setBizNo(getCurrentAdmin().getBizNo()); 
		vo.setAdminCd("03ROLE");                   
		
		caUserManageService.insertUser(vo);
	    return vo;
	}
	
	@PutMapping("/userUpdate")
	@ResponseBody
	public Map<String, String> userUpdate(@RequestBody UserManageVO vo) {
		

	    int updateCnt = caUserManageService.updateUser(vo);
	    Map<String, String> result = new HashMap<>();
	    if(updateCnt > 0) {
	        result.put("result", "SUCCESS");
	    } else {
	        result.put("result", "ERROR");
	    }
	    return result;
	}

	@GetMapping("/user/profile/{userId}")
	@ResponseBody
	public ResponseEntity<AttachVO> getProfileImage(@PathVariable("userId") Long userId) {
	    List<AttachVO> list = attachService.selectAttachList("09MODULE", userId);
	    if (list != null && !list.isEmpty()) {
	        return ResponseEntity.ok(list.get(list.size() - 1)); 
	    }
	    return ResponseEntity.ok().build(); 
	}

	@PostMapping("/user/profile")
	@ResponseBody
	public ResponseEntity<?> uploadProfileImage(@RequestParam("userId") Long userId, 
	                                            @RequestParam("file") MultipartFile file) {
	    List<AttachVO> existList = attachService.selectAttachList("09MODULE", userId);
	    if (existList != null) {
	        for (AttachVO attach : existList) {
	            try {
	                attachService.removeAttach(attach); 
	                attachService.deleteAttach(attach.getId()); 
	            } catch (Exception e) {
	                log.error("기존 프로필 삭제 실패: {}", e.getMessage());
	            }
	        }
	    }
	    attachService.saveAndInsertAttachments(userId, new MultipartFile[]{file}, "09MODULE", "users");
	    return ResponseEntity.ok().body("SUCCESS");
	}

	@DeleteMapping("/user/profile/{userId}")
	@ResponseBody
	public ResponseEntity<?> deleteProfileImage(@PathVariable("userId") Long userId) {
	    List<AttachVO> existList = attachService.selectAttachList("09MODULE", userId);
	    if (existList != null) {
	        for (AttachVO attach : existList) {
	            try {
	                attachService.removeAttach(attach); 
	                attachService.deleteAttach(attach.getId()); 
	            } catch (Exception e) {
	                log.error("프로필 삭제 실패: {}", e.getMessage());
	                return ResponseEntity.internalServerError().build();
	            }
	        }
	    }
	    return ResponseEntity.ok().body("SUCCESS");
	}
}