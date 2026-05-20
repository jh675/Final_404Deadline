package com.example.demo.management.userManage.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.company.service.CompanyVO;
import com.example.demo.management.userManage.service.UserManageService;
import com.example.demo.management.userManage.service.UserManageVO;
import com.example.demo.util.attach.service.AttachService;
import com.example.demo.util.attach.service.AttachVO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admin")
public class UserManageController {
	
	private final UserManageService userManageService;
	private final AttachService attachService;
	
	@GetMapping("/userList")
	public String userList(Model model, @ModelAttribute("filter01") UserManageVO userManage) {
		List<UserManageVO> list = userManageService.selectAll(userManage);
		model.addAttribute("userList", list);
		return "management/user/userList";
	}
	
	@GetMapping("/companyList")
	@ResponseBody
	public List<CompanyVO> companyList() {
	    return userManageService.selectCompanyList();
	}
	
	@PostMapping("/userInsert")
	@ResponseBody
	public UserManageVO userInsert(@RequestBody UserManageVO vo) {
		userManageService.insertUser(vo);
	    return vo;
	}
	
	@PutMapping("/userUpdate")
	@ResponseBody
	public Map<String, String> userUpdate(@RequestBody UserManageVO vo) {
	    int updateCnt = userManageService.updateUser(vo);
	    Map<String, String> result = new HashMap<>();
	    if(updateCnt > 0) {
	        result.put("result", "SUCCESS");
	    } else {
	        result.put("result", "ERROR");
	    }
	    return result;
	}

	// 1. 프로필 이미지 조회 (최신 1건의 정보만 반환)
	@GetMapping("/user/profile/{userId}")
	@ResponseBody
	public ResponseEntity<AttachVO> getProfileImage(@PathVariable("userId") Long userId) {
	    // 조건에 맞춰 검색
	    List<AttachVO> list = attachService.selectAttachList("09MODULE", userId);
	    
	    if (list != null && !list.isEmpty()) {
	        // 가장 최근에 등록된 이미지를 가져옴 (또는 리스트의 마지막 값)
	        return ResponseEntity.ok(list.get(list.size() - 1)); 
	    }
	    // 이미지가 없으면 빈 상태 반환
	    return ResponseEntity.ok().build(); 
	}

	// 2. 프로필 이미지 업로드
	@PostMapping("/user/profile")
	@ResponseBody
	public ResponseEntity<?> uploadProfileImage(@RequestParam("userId") Long userId, 
	                                            @RequestParam("file") MultipartFile file) {
	    
	    // 1단계: 기존 프로필 이미지가 있다면 물리적 파일과 DB 데이터 삭제
	    List<AttachVO> existList = attachService.selectAttachList("09MODULE", userId);
	    if (existList != null) {
	        for (AttachVO attach : existList) {
	            try {
	                attachService.removeAttach(attach); // 디스크 물리 파일 삭제
	                attachService.deleteAttach(attach.getId()); // DB 삭제
	            } catch (Exception e) {
	                log.error("기존 프로필 삭제 실패: {}", e.getMessage());
	            }
	        }
	    }

	    // 2단계: 새 프로필 이미지 저장 (DB 등록까지)
	    attachService.saveAndInsertAttachments(userId, new MultipartFile[]{file}, "09MODULE", "users");
	    
	    return ResponseEntity.ok().body("SUCCESS");
	}

	// 3. 프로필 이미지 삭제
	@DeleteMapping("/user/profile/{userId}")
	@ResponseBody
	public ResponseEntity<?> deleteProfileImage(@PathVariable("userId") Long userId) {
	    
	    List<AttachVO> existList = attachService.selectAttachList("09MODULE", userId);
	    if (existList != null) {
	        for (AttachVO attach : existList) {
	            try {
	                attachService.removeAttach(attach); // 디스크 물리 파일 삭제
	                attachService.deleteAttach(attach.getId()); // DB 삭제
	            } catch (Exception e) {
	                log.error("프로필 삭제 실패: {}", e.getMessage());
	                return ResponseEntity.internalServerError().build();
	            }
	        }
	    }
	    
	    return ResponseEntity.ok().body("SUCCESS");
	}
}
