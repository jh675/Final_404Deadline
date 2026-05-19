package com.example.demo.management.userManage.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.company.service.CompanyVO;
import com.example.demo.management.userManage.service.UserManageService;
import com.example.demo.management.userManage.service.UserManageVO;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class UserManageController {
	
	private final UserManageService userManageService;
	
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
}
