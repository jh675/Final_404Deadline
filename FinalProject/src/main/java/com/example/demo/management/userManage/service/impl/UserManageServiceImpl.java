package com.example.demo.management.userManage.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.company.service.CompanyVO;
import com.example.demo.management.userManage.mapper.UserManageMapper;
import com.example.demo.management.userManage.service.UserManageService;
import com.example.demo.management.userManage.service.UserManageVO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserManageServiceImpl implements UserManageService {

	private final UserManageMapper userManageMapper;
	private final PasswordEncoder passwordEncoder;

	@Override
	public List<UserManageVO> selectAll(UserManageVO userManage) {
		return userManageMapper.selectAll(userManage);
	}

	@Override
	public List<CompanyVO> selectCompanyList() {
		return userManageMapper.selectCompanyList();
	}

	@Override
	public int insertUser(UserManageVO vo) {
		// 권한별 기업번호 보안 검증
		validateBizNoWithAdminCd(vo);

		// 실제 존재하는 기업인지 검증
		if (userManageMapper.checkCompanyExists(vo.getBizNo()) == 0) {
			vo.setResult("INVALID_BIZNO"); // 결과 코드
			return 0;
		}
		
		// 전화번호 포맷팅
		vo.setTel(formatPhoneNumber(vo.getTel()));
		
		// 초기 비밀번호 = 아이디
		String rawPassword = vo.getLogin();

		// 암호화
		String encodedPassword = passwordEncoder.encode(rawPassword);

		// VO 세팅
		vo.setPassword(encodedPassword);

		return userManageMapper.insertUser(vo);
	}

	@Override
	public int updateUser(UserManageVO vo) {
		validateBizNoWithAdminCd(vo);

		// 단일 수정 시, 상태가 '비활성(02ACTIVE)'으로 들어오면
		// 화면에서 어떤 값을 넘겼든 무시하고 비밀번호 초기화를 '필요(01ACTIVE)'로 강제 세팅합니다.
		if ("02ACTIVE".equals(vo.getStatusCd())) {
			vo.setMcpCd("01ACTIVE");
		}

		// 실제 존재하는 기업인지 검증
		if (userManageMapper.checkCompanyExists(vo.getBizNo()) == 0) {
			vo.setResult("INVALID_BIZNO"); // 결과 코드
			return 0;
		}

		// 수정하려는 아이디가 동일 기업 내의 다른 사람과 중복되는지 체크
		if (userManageMapper.checkUpdateIdDuplicate(vo) > 0) {
			vo.setResult("DUPLICATE_LOGIN");
			return 0;
		}
		
		// 전화번호 포맷팅
		vo.setTel(formatPhoneNumber(vo.getTel()));

		return userManageMapper.updateUser(vo);
	}

	// 소속기업 번호 확인 로직
	private void validateBizNoWithAdminCd(UserManageVO vo) {
		String hqBizNo = "124-87-03358"; // 본사 사업자번호

		if ("01ROLE".equals(vo.getAdminCd())) {
			// 1. 시스템관리자라면: 프론트에서 어떤 조작된 번호를 보냈더라도 강제로 본사 번호로 덮어씌움
			vo.setBizNo(hqBizNo);
		} else {
			// 2. 그 외 권한인데: 본사 번호를 달고 왔다면 (악의적 DOM 조작) 차단!
			if (hqBizNo.equals(vo.getBizNo())) {
				throw new IllegalArgumentException("보안 경고: 일반/기업 계정은 본사 소속으로 등록할 수 없습니다.");
			}
		}
	}

	@Override
	public UserManageVO selectOne(Long id) {
		return userManageMapper.selectOne(id);
	}

	@Override
	public int bulkUpdateUsers(Map<String, Object> payload) {
		return userManageMapper.bulkUpdateUsers(payload);
	}

	@Override
	public String updateMyInfo(UserManageVO vo) {
		// 아이디 중복 검사
		if (userManageMapper.checkUpdateIdDuplicate(vo) > 0) {
			return "DUPLICATE_LOGIN";
		}
		
		// 정보 업데이트 (비밀번호 변경 포함)
		int result = userManageMapper.updateMyInfo(vo);
		
		return result > 0 ? "SUCCESS" : "FAIL";
	}
	// 전화번호 formatting
	private String formatPhoneNumber(String tel) {
	    if (tel == null || tel.trim().isEmpty()) return "";
	    
	    String digits = tel.replaceAll("[^0-9]", "");
	    String formatted = digits;
	    
	    if (digits.startsWith("02")) { 
	        // 서울 유선전화
	        if (digits.length() == 9) {
	            formatted = digits.replaceFirst("^(\\d{2})(\\d{3})(\\d{4})$", "$1-$2-$3");
	        } else if (digits.length() == 10) {
	            formatted = digits.replaceFirst("^(\\d{2})(\\d{4})(\\d{4})$", "$1-$2-$3");
	        }
	    } else if (digits.length() == 10) { 
	        // 그 외 지역 유선전화 (예: 031)
	        formatted = digits.replaceFirst("^(\\d{3})(\\d{3})(\\d{4})$", "$1-$2-$3");
	    } else if (digits.length() == 11) { 
	        // 휴대전화 (010)
	        formatted = digits.replaceFirst("^(\\d{3})(\\d{4})(\\d{4})$", "$1-$2-$3");
	    } 
	    
	    // DB 제약조건(13 Byte) 방어선
	    if (formatted.length() > 13) {
	        return digits.length() > 13 ? digits.substring(0, 13) : digits;
	    }
	    return formatted;
	}
}
