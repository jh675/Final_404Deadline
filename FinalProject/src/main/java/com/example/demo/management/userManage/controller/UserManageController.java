package com.example.demo.management.userManage.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

@Controller
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

	@PostMapping("/user/profile")
	@ResponseBody
	public ResponseEntity<?> uploadProfile(
	        @RequestParam Long userId,
	        @RequestParam MultipartFile[] file) {
		attachService.saveAndInsertAttachments(userId, file,"09MODULE" , "user");
//	    attachService.saveUserProfile(userId, file);

	    return ResponseEntity.ok().build();
	}
	
	@GetMapping("/user/profile/{userId}")
	@ResponseBody
	public AttachVO getProfile(@PathVariable Long userId) {
	    List<AttachVO> list =
	        attachService.selectAttachList("09MODULE", userId);

	    return list.isEmpty() ? null : list.get(0);
	}
}
