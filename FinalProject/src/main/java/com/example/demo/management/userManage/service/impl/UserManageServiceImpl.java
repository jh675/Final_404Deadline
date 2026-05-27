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
public class UserManageServiceImpl implements UserManageService{
	
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
	    // 단일 수정 시, 상태가 '비활성(02ACTIVE)'으로 들어오면
	    // 화면에서 어떤 값을 넘겼든 무시하고 비밀번호 초기화를 '필요(01ACTIVE)'로 강제 세팅합니다.
	    if ("02ACTIVE".equals(vo.getStatusCd())) {
	        vo.setMcpCd("01ACTIVE");
	    }
	    
	    return userManageMapper.updateUser(vo);
	}

	@Override
	public UserManageVO selectOne(Long id) {
	    return userManageMapper.selectOne(id);
	}
	
	@Override
	public int bulkUpdateUsers(Map<String, Object> payload) {
	    return userManageMapper.bulkUpdateUsers(payload);
	}
	
}
