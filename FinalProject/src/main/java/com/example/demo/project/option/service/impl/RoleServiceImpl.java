package com.example.demo.project.option.service.impl;

import java.util.List;
import org.springframework.stereotype.Service;
import com.example.demo.project.option.mapper.RoleMapper;
import com.example.demo.project.option.service.RoleService;
import com.example.demo.project.option.service.RoleVO;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleMapper roleMapper;

    @Override
    public List<RoleVO> selectRoleList(RoleVO roleVO) {
        return roleMapper.selectRoleList(roleVO);
    }
}
