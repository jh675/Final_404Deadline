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
		
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                        			  .getRequest();

        String bizNo = request.getParameter("bizNo");
        
        // null 방지 + 공백 제거
        userId = userId == null ? "" : userId.trim();
        bizNo = bizNo == null ? "" : bizNo.trim();


        // 숫자만 추출
        bizNo = bizNo.replaceAll("[^0-9]", "");
        
        // 길이 체크
        if (bizNo.length() != 10) {
            throw new UsernameNotFoundException("사업자번호 형식 오류");
        }
        
        // 하이픈 형식으로 변환
        bizNo = bizNo.substring(0, 3) + "-" +
                bizNo.substring(3, 5) + "-" +
                bizNo.substring(5);
        
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
