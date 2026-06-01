package com.example.demo.login.service.impl;

import java.util.List;

import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.example.demo.company.mapper.CompanyMapper;
import com.example.demo.company.service.CompanyVO;
import com.example.demo.login.mapper.LoginMapper;
import com.example.demo.login.service.LoginService;
import com.example.demo.login.service.UserVO;
import com.example.demo.management.userManage.mapper.UserManageMapper;
import com.example.demo.management.userManage.service.UserManageVO;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService, UserDetailsService {

	private final LoginMapper loginMapper;
	private final PasswordEncoder passwordEncoder;
	private final CompanyMapper companyMapper;
	private final UserManageMapper userManageMapper;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		String bizNo = null;
		String loginId = null;
		String loginType = "USER";

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
				
				// 로그인 타입 받기
				String reqType = request.getParameter("loginType");
				if (reqType != null) loginType = reqType;
			}
		}
		
		// null 방지 및 공백 제거
		loginId = (loginId == null) ? "" : loginId.trim();
		UserVO vo = null;
		
		if ("ADMIN".equals(loginType)) {
			// 시스템 관리자 로그인
			vo = loginMapper.selectSystemAdmin(loginId);
			if (vo == null) {
				throw new UsernameNotFoundException("시스템 관리자 계정을 찾을 수 없거나 권한이 없습니다.");
			}
		} else {
			// 기업관리자, 회원 로그인 로직
			bizNo = (bizNo == null) ? "" : bizNo.trim();
			bizNo = bizNo.replaceAll("[^0-9]", "");
			
			if (bizNo.length() != 10) {
				throw new UsernameNotFoundException("소속 기업 정보가 누락되었거나 사업자번호 형식이 올바르지 않습니다.");
			}
			bizNo = bizNo.substring(0, 3) + "-" + bizNo.substring(3, 5) + "-" + bizNo.substring(5);
			
			String companyStatus = loginMapper.selectCompanyStatus(bizNo);
			
			if (companyStatus == null) {
				throw new UsernameNotFoundException("등록되지 않은 기업입니다.");
			}
			if (!"01ACTIVE".equals(companyStatus)) {
				throw new DisabledException("소속 기업이 활성 되어있지 않아 로그인이 제한되었습니다. 관리자에게 문의하세요.");
			}
			
			UserVO param = new UserVO();
			param.setBizNo(bizNo);
			param.setLogin(loginId);
			
			vo = loginMapper.selectOne(param);
			
			if (vo == null) {
				throw new UsernameNotFoundException("해당 기업에 등록된 회원 정보를 찾을 수 없습니다.");
			}
		}
		
		if ("02ACTIVE".equals(vo.getStatusCd())) {
	        throw new DisabledException("비활성화된 계정입니다. 관리자에게 문의하세요.");
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
	
	@Override
	public void updateMcpCd(UserVO vo) {
	    loginMapper.updateMcpCd(vo);
	}
	
	@Override
	public void updatePassword(UserVO vo, String newPassword) {
		// 1. 평문 비밀번호를 BCrypt로 암호화
		String encodedPassword = passwordEncoder.encode(newPassword);
		
		// 2. 암호화된 비밀번호를 vo에 셋팅
		vo.setPassword(encodedPassword);
		
		// 3. MyBatis Mapper 호출 (앞서 수정하신 updatePassword 쿼리 실행)
		loginMapper.updatePassword(vo);
	}
	
	@Override
	public void updateLastLogOn(UserVO vo) {
		loginMapper.updateLastLogOn(vo);
	}
	
	@Transactional
	@Override
	public void requestCompanyRegistration(CompanyVO company, UserManageVO user) {
	    
		// DB에 이미 해당 기업이 존재하는지 체크
	    String existingStatus = loginMapper.selectCompanyStatus(company.getBizNo());
	    if (existingStatus != null) {
	        throw new IllegalArgumentException("이미 등록된 사업자번호입니다.");
	    }
	    
	    // 기업 전화번호 하이픈(-) 자동 포맷팅
	    String formattedCompanyTel = formatPhoneNumber(company.getTel());
	    company.setTel(formattedCompanyTel);
	    
	    // 기업 상태: '03ACTIVE' (승인 요청/대기) 강제 세팅
	    company.setIsActiveCd("03ACTIVE");
	    companyMapper.insert(company);
	    
	    // 기업 관리자 계정 초기값 세팅
	    user.setBizNo(company.getBizNo());
	    user.setPassword(passwordEncoder.encode(user.getLogin())); 
	    user.setAdminCd("02ROLE"); // 기업관리자 권한
	    
	    // 계정 상태: '02ACTIVE' (비활성) 강제 세팅
	    user.setStatusCd("02ACTIVE"); 
	    
	    user.setPrjManagerCd("01ACTIVE"); 
	    user.setMcpCd("01ACTIVE"); // 나중에 활성화 시 비밀번호 재설정 필요하도록 세팅
	    
	    if (user.getName() == null || user.getName().trim().isEmpty()) {
	        user.setName(company.getCompanyName() + " 관리자"); 
	    }
	    
	    // 관리자 전화번호 셋팅 및 포맷팅
	    if (user.getTel() == null || user.getTel().trim().isEmpty()) {
	        user.setTel(formattedCompanyTel); // 기업번호를 그대로 쓸 경우 이미 포맷팅된 번호 사용
	    } else {
	        user.setTel(formatPhoneNumber(user.getTel())); // 유저가 따로 입력한 번호도 포맷팅
	    }

	    // 비활성 상태로 유저 INSERT
	    userManageMapper.insertUser(user);
	}


	private String formatPhoneNumber(String tel) {
	    if (tel == null || tel.trim().isEmpty()) return "";
	    
	    // 숫자 이외의 모든 문자(하이픈, 공백 등)를 제거
	    String digits = tel.replaceAll("[^0-9]", "");
	    String formatted = digits;
	    
	    // 길이에 따른 정규식 포맷팅
	    if (digits.length() == 8) { 
	        // 1588-1588 (9자리)
	        formatted = digits.replaceFirst("^(\\d{4})(\\d{4})$", "$1-$2");
	    } else if (digits.startsWith("02")) { 
	        // 서울 (02)
	        if (digits.length() == 9) { // 02-123-4567 (11자리)
	            formatted = digits.replaceFirst("^(\\d{2})(\\d{3})(\\d{4})$", "$1-$2-$3");
	        } else if (digits.length() == 10) { // 02-1234-5678 (12자리)
	            formatted = digits.replaceFirst("^(\\d{2})(\\d{4})(\\d{4})$", "$1-$2-$3");
	        }
	    } else if (digits.length() == 10) { 
	        // 그 외 지역번호 3자리 (031-123-4567) (12자리)
	        formatted = digits.replaceFirst("^(\\d{3})(\\d{3})(\\d{4})$", "$1-$2-$3");
	    } else if (digits.length() == 11) { 
	        // 휴대전화 (010-1234-5678) (13자리 - 최대치!)
	        formatted = digits.replaceFirst("^(\\d{3})(\\d{4})(\\d{4})$", "$1-$2-$3");
	    } 
	    
	    // DB 제약조건(VARCHAR2(13))
	    if (formatted.length() > 13) {
	        // 무조건 13자리가 넘으면 하이픈을 빼버리거나 잘라내서 DB 에러를 막습니다.
	        return digits.length() > 13 ? digits.substring(0, 13) : digits;
	    }
	    
	    return formatted;
	}
}
