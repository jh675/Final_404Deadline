package com.example.demo.login.service.impl;

import java.util.List;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.example.demo.company.service.CompanyVO;
import com.example.demo.login.mapper.LoginMapper;
import com.example.demo.login.service.LoginService;
import com.example.demo.login.service.UserVO;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService, UserDetailsService {

	private final LoginMapper loginMapper;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		String bizNo = null;
		String loginId = null;

		// 자동 로그인(Remember-Me)으로 접근한 경우
		// username 변수에 구분자 '_' 가 포함됨 (예: 123-45-67890_cadmin)
		if (username != null && username.contains("_")) {
			String[] parts = username.split("_", 2);
			bizNo = parts[0];   // 앞부분은 기업번호
			loginId = parts[1]; // 뒷부분은 순수 아이디
		} 
		//  사용자가 직접 로그인 폼에서 [로그인] 버튼을 누른 경우
		//  username은 순수 아이디이므로, 기업번호는 기존처럼 Request 파라미터에서 직접 꺼냅니다.
		else {
			loginId = username;
			
			ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
			if (attributes != null) {
				HttpServletRequest request = attributes.getRequest();
				bizNo = request.getParameter("bizNo");
			}
		}
		
		// null 방지 및 공백 제거
		loginId = (loginId == null) ? "" : loginId.trim();
		bizNo = (bizNo == null) ? "" : bizNo.trim();

		// 숫자만 추출
		bizNo = bizNo.replaceAll("[^0-9]", "");
		
		// 기업번호가 올바르게 들어왔는지 체크 (일반 로그인 시 필수)
		if (bizNo.length() != 10) {
			throw new UsernameNotFoundException("소속 기업 정보가 누락되었거나 사업자번호 형식이 올바르지 않습니다.");
		}
		
		// 하이픈 형식으로 변환 (기존 DB 매칭용 포맷 유지)
		bizNo = bizNo.substring(0, 3) + "-" +
				bizNo.substring(3, 5) + "-" +
				bizNo.substring(5);
		
		// DB 조회용 파라미터 세팅
		UserVO param = new UserVO();
		param.setBizNo(bizNo);
		param.setLogin(loginId);
		
		UserVO vo = loginMapper.selectOne(param);
		
		if (vo == null) {
			throw new UsernameNotFoundException("해당 기업에 등록된 회원 정보를 찾을 수 없습니다.");
		}

		// DB 권한을 Security 권한 규격으로 변환
		String role = switch (vo.getAdminNm()) {
			case "시스템관리자" -> "ROLE_ADMIN";
			case "기업관리자" -> "ROLE_CADMIN";
			default -> "ROLE_USER";
		};

		vo.setRole(List.of(role));
		return vo;
	}
	
	@Override
	public List<CompanyVO> searchActiveCompanies(String keyword) {
		return loginMapper.searchActiveCompanies(keyword);
	}
}