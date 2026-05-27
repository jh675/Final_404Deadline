package com.example.demo.management.userManage.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.management.userManage.mapper.CAUserManageMapper;
import com.example.demo.management.userManage.service.CAUserManageService;
import com.example.demo.management.userManage.service.UserManageVO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CAUserManageServiceImpl implements CAUserManageService { 
	
	private final CAUserManageMapper caUserManageMapper;
	private final PasswordEncoder passwordEncoder;
	
	@Override
	public List<UserManageVO> selectAll(UserManageVO userManage) {
		return caUserManageMapper.selectAll(userManage);
	}
	
	@Override
	public int insertUser(UserManageVO vo) {
        String rawPassword = vo.getLogin();
        String encodedPassword = passwordEncoder.encode(rawPassword);
        vo.setPassword(encodedPassword);

        return caUserManageMapper.insertUser(vo);
	}

	@Override
	public int updateUser(UserManageVO vo) {
		
		// 비활성화 된 계정이라면 비밀번호변경 필요 활성화 강제로 넣어두기
		if ("02ACTIVE".equals(vo.getStatusCd())) {
	        vo.setMcpCd("01ACTIVE");
	    }
		
		// 수정하려는 아이디가 동일 기업 내의 다른 사람과 중복되는지 체크
	    if (caUserManageMapper.checkUpdateIdDuplicate(vo) > 0) {
	        vo.setResult("DUPLICATE_LOGIN"); 
	        return 0; 
	    }
	    return caUserManageMapper.updateUser(vo);
	}

	@Override
	public UserManageVO selectOne(Long id) {
	    return caUserManageMapper.selectOne(id);
	}
	
	@Override
	public int bulkUpdateUsers(Map<String, Object> payload) {
	    return caUserManageMapper.bulkUpdateUsers(payload);
	}
}