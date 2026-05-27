package com.example.demo.login.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.company.service.CompanyVO;
import com.example.demo.login.service.LoginService;
import com.example.demo.login.service.UserVO;
import com.example.demo.management.userManage.service.UserManageVO;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class LoginController {
	private final LoginService loginService;
	
	@GetMapping("/")
	public String main(Model model, HttpSession session) {
		// 프로젝트 밖으로 나왔으므로 프로젝트 관련 세션 정보만 깔끔하게 청소!
        session.removeAttribute("currentProjectId");
        session.removeAttribute("currentMenu");
        session.removeAttribute("moduleList");
        session.removeAttribute("project");
		
		// 시큐리티 컨텍스트 객체를 얻습니다.
		SecurityContext context = SecurityContextHolder.getContext();

		// 인증 객체를 얻습니다.
		Authentication authentication = context.getAuthentication();

		// 로그인한 사용자정보를 가진 객체를 얻습니다.
	    Object principal = authentication.getPrincipal();
	    
	    if(principal instanceof UserVO vo) {

	        // 권한 목록
	        List<String> roles = vo.getRole();
	        model.addAttribute("user", vo);
	        
	        // 시스템 관리자
	        if(roles.contains("ROLE_ADMIN")) {
	            return "redirect:/management/project";
	        }

	        // 기업 관리자
	        if(roles.contains("ROLE_CADMIN")) {
	            return "redirect:/management/project";
	        }

	        // 일반 사용자
	        if(roles.contains("ROLE_USER")) {
	            return "redirect:/management/project";
	        }

	        // 권한이 없는 경우
	        return "error/403";

	    } else {
	        // 비로그인 사용자
	        return "login/info";
	    }
  	
//	    userVO 꺼내서 쓰는 방법 > 회의록 밑의 개발표준 9번 확인
	    
	}
	
	@GetMapping("/login/companies/search")
	@ResponseBody
	public List<CompanyVO> searchCompanies(@RequestParam("keyword") String keyword) {
	    if (keyword == null || keyword.trim().isEmpty()) {
	        return List.of(); // 빈 리스트 반환
	    }
	    return loginService.searchActiveCompanies(keyword);
	}

		@GetMapping("/login/password-reset")
		public String showPasswordResetPage() {
			return "login/pwReset"; 
		}
	
	// 비밀번호 재설정 처리
	@PostMapping("/login/password-reset")
	@ResponseBody
	public String processPasswordReset(@RequestParam("newPassword") String newPassword, 
                                       Authentication authentication) {
        
		// 현재 로그인되어 있는 사용자 객체 가져오기
		if (authentication != null && authentication.getPrincipal() instanceof UserVO) {
			UserVO vo = (UserVO) authentication.getPrincipal();
			loginService.updatePassword(vo, newPassword);
			vo.setMcpCd("02ACTIVE");
			return "success";
		}
		
		return "fail";
	}
		
	@GetMapping("/errorTest")
	public String errorpgTest() {
		return "error/403";
	}
	
	@PostMapping("/login/company/request")
	public String requestCompanyRegistration(CompanyVO company, UserManageVO user) {
	    // 💡 서비스 호출
		loginService.requestCompanyRegistration(company, user);
	    
	    // 완료 후 다시 로그인 페이지로 보내면서 파라미터 전달 (예: alert 띄우기 용도)
	    return "redirect:/login?reqSuccess=true";
	}
}
