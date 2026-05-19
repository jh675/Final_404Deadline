package com.example.demo.management.userManage.service.impl;

import java.util.List;

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
	    return userManageMapper.updateUser(vo);
	}

	@Override
	public UserManageVO selectOne(Long id) {
	    return userManageMapper.selectOne(id);
	}
}
