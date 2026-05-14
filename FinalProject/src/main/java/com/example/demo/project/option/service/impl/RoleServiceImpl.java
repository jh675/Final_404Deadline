package com.example.demo.project.option.service.impl;

import java.util.List;
import org.springframework.stereotype.Service;
import com.example.demo.project.option.mapper.RoleMapper;
import com.example.demo.project.option.service.RoleGroupRowVO;
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

    @Override
    public RoleVO selectRoleByPrjAndCd(Long prjId, Long roleCd) {
        if (prjId == null || roleCd == null) {
            return null;
        }
        return roleMapper.selectRoleByPrjAndCd(prjId, roleCd);
    }

    @Override
    public List<RoleVO> selectAllMenus() {
        return roleMapper.selectAllMenus();
    }

    @Override
    public List<String> selectMenuRoleIdsByRoleCd(Long roleCd) {
        if (roleCd == null) {
            return List.of();
        }
        return roleMapper.selectMenuRoleIdsByRoleCd(roleCd);
    }

    @Override
    public List<RoleGroupRowVO> selectRoleGroupsList(Long prjId, Long roleCd) {
        if (prjId == null || roleCd == null) {
            return List.of();
        }
        return roleMapper.selectRoleGroupsList(prjId, roleCd);
    }
}
