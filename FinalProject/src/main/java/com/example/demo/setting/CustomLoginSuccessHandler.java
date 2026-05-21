package com.example.demo.setting;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.example.demo.login.service.LoginService;
import com.example.demo.login.service.UserVO;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler{

	private final LoginService loginService;
		
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		
		UserVO vo = (UserVO) authentication.getPrincipal();
		boolean needsPasswordReset = false;
		
		if ("01ACTIVE".equals(vo.getMcpCd())) {
			needsPasswordReset = true;
		} else {
			// 2. 90일 경과 여부 체크
			LocalDate pwUpdatedDate = null;
			
			// Date 타입을 LocalDate로 변환하여 계산
			if (vo.getPwUpdatedOn() != null) {
				pwUpdatedDate = vo.getPwUpdatedOn().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			} else if (vo.getCreatedOn() != null) {
				pwUpdatedDate = vo.getCreatedOn().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			}

			if (pwUpdatedDate != null) {
				long daysBetween = ChronoUnit.DAYS.between(pwUpdatedDate, LocalDate.now());
				
				// 90일이 지났다면
				if (daysBetween >= 90) {
					needsPasswordReset = true;
					// DB 상태를 '01ACTIVE'로 갱신
					vo.setMcpCd("01ACTIVE");
					loginService.updateMcpCd(vo); 
				}
			}
		}

		// 3. 상태에 따른 분기 처리
		if (needsPasswordReset) {
			// 비밀번호 변경 페이지로 강제 이동 
			response.sendRedirect("/login/password-reset"); 
		} else {
			// 정상 로그인인 경우 루트 경로로 이동하여 LoginController의 "/"에서 권한별 처리
			response.sendRedirect("/");
		}
	}
}
