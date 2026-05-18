package com.example.demo.management.userManage.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

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
}
