package com.example.demo.login.service.impl;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

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
	public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
		
		HttpServletRequest request =
                ((ServletRequestAttributes)
                        RequestContextHolder.currentRequestAttributes())
                        .getRequest();

        String bizNo = request.getParameter("bizNo");
        
        UserVO param = new UserVO();
        
        param.setBizNo(bizNo);
        param.setLogin(userId);
		
        UserVO vo = loginMapper.selectOne(param);

		String role = switch (vo.getAdminNm()) {
		// db에 있는 권한을 security에서 쓰이는 형태의 문구로 변환
		// Nm 필드는 함수를 사용해 이름을 가져오는 걸로 함
			case "시스템관리자" -> "ROLE_ADMIN";
			case "기업관리자" -> "ROLE_CADMIN";
			default -> "ROLE_USER";
		};

		vo.setRole(List.of(role));
		return vo;
	}
}
