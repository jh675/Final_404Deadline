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
    public long countRoleList(RoleVO roleVO) {
        applyPaging(roleVO);
        return roleMapper.selectRoleListCount(roleVO);
    }

    @Override
    public List<RoleVO> selectRoleList(RoleVO roleVO) {
        applyPaging(roleVO);
        return roleMapper.selectRoleList(roleVO);
    }

    private static void applyPaging(RoleVO roleVO) {
        int page = Math.max(1, roleVO.getPage());
        int pageSize = Math.max(1, Math.min(100, roleVO.getPageSize()));
        roleVO.setPage(page);
        roleVO.setPageSize(pageSize);
        roleVO.setOffset((page - 1) * pageSize);
        roleVO.setLimit(pageSize);
    }
}
