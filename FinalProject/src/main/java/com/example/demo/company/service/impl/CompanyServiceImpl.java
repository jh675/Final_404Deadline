package com.example.demo.company.service.impl;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.company.mapper.CompanyMapper;
import com.example.demo.company.service.CompanyService;
import com.example.demo.company.service.CompanyVO;
import com.example.demo.management.userManage.mapper.UserManageMapper;
import com.example.demo.management.userManage.service.UserManageVO;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CompanyServiceImpl implements CompanyService {

	private final CompanyMapper companyMapper;
	private final UserManageMapper userManageMapper;
	private final PasswordEncoder passwordEncoder;

	@Override
	public List<CompanyVO> selectAll(CompanyVO company) {

		return companyMapper.selectAll(company);
	}

	@Override
	public CompanyVO selectOne(String bizNo) {

		return companyMapper.selectOne(bizNo);
	}

	@Override
	public int delete(String bizNo) {

		return companyMapper.delete(bizNo);
	}

	@Override
	public int update(CompanyVO company) {

		return companyMapper.update(company);
	}

	@Override
	public int insert(CompanyVO company) {

		return companyMapper.insert(company);
	}

	// 기업 등록 + 관리자 계정 동시 생성 로직
	@Transactional
	@Override
	public void insertCompanyWithAdmin(CompanyVO company, UserManageVO user) {

		companyMapper.insert(company);

		// 기업 관리자(UserVO) 기본값 강제 세팅
		user.setBizNo(company.getBizNo());
		user.setPassword(passwordEncoder.encode(user.getLogin())); // 초기 비밀번호는 아이디와 동일
		user.setAdminCd("02ROLE"); // 기업관리자 권한
		user.setStatusCd("01ACTIVE"); // 계정 활성 상태
		user.setPrjManagerCd("01ACTIVE"); // 프로젝트 매니저 여부
		user.setMcpCd("01ACTIVE"); // 비밀번호 재설정 필요

		// HTML 폼에 '이름(name)' 칸이 없으므로, 프로시저 오류 방지를 위해 기본값 부여
		if (user.getName() == null || user.getName().trim().isEmpty()) {
			user.setName(company.getCompanyName() + " 관리자");
		}
		// 전화번호도 company의 것을 그대로 사용 (HTML에 userTel이 따로 없으므로)
		if (user.getTel() == null || user.getTel().trim().isEmpty()) {
			user.setTel(company.getTel());
		}

		// 관리자 계정 INSERT (PROC_USER_INSERT 프로시저 실행)
		userManageMapper.insertUser(user);
	}

	// 기업 수정 + 관리자 이메일 동시 수정 로직
	@Transactional
	@Override
	public void updateCompanyWithAdmin(CompanyVO company, UserManageVO user) {

		// 기업 정보 UPDATE
		companyMapper.update(company);

		// bizNo와 login을 이용해 기존 유저(관리자) 정보 조회
		UserManageVO searchParam = new UserManageVO();
		searchParam.setBizNo(company.getBizNo());
		searchParam.setLogin(user.getLogin());

		UserManageVO existingUser = userManageMapper.selectByLoginAndBizNo(searchParam);

		// 해당하는 관리자 유저가 존재할 경우에만 업데이트 진행
		if (existingUser != null) {

			// 화면에서 변경된 관리자 정보(이메일, 전화번호)를 덮어씌웁니다.
			existingUser.setEmail(user.getEmail());
			existingUser.setTel(user.getTel()); // 기업 전화번호와 연동되거나 혹은 관리자 번호

	        // 기업 상태와 관리자 계정 상태 연동 로직
	        if ("01ACTIVE".equals(company.getIsActiveCd())) {
	            // 기업을 활성해주면 관리자 계정도 활성
	            existingUser.setStatusCd("01ACTIVE"); 
	        } else {
	            // 기업을 비활성 처리하면 관리자 계정도 비활성!
	            existingUser.setStatusCd("02ACTIVE");
	            existingUser.setMcpCd("01ACTIVE"); // 계정이 비활성화 되면 비밀번호 변경 필요 상태로 전환
	        }
	        
			userManageMapper.updateUser(existingUser);
		}
	}
}
