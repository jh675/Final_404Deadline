package com.example.demo.project.option.service.impl;

import java.util.List;
import java.util.Objects;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.example.demo.project.option.mapper.RoleMapper;
import com.example.demo.project.option.service.RoleGroupRowVO;
import com.example.demo.project.option.service.RoleService;
import com.example.demo.project.option.service.RoleVO;

import lombok.RequiredArgsConstructor;

/** 권한 조회·삭제 — 삭제 시 DB PROC_ROLE_DELETE 호출 */
@Service
@Primary
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

    /** 프로젝트 소속 확인 후 역할마다 PROC_ROLE_DELETE 호출 */
    @Override
    public void deleteRolesForProject(Long prjId, List<Long> roleCds) {
        if (prjId == null || roleCds == null || roleCds.isEmpty()) {
            throw new IllegalArgumentException("프로젝트 ID와 삭제할 역할이 필요합니다.");
        }
        List<Long> distinct =
                roleCds.stream().filter(Objects::nonNull).distinct().toList();
        if (distinct.isEmpty()) {
            throw new IllegalArgumentException("유효한 역할 코드가 없습니다.");
        }
        for (Long roleCd : distinct) {
            if (roleMapper.selectRoleByPrjAndCd(prjId, roleCd) == null) {
                throw new IllegalArgumentException("프로젝트에 존재하지 않는 역할입니다: " + roleCd);
            }
            roleMapper.callProcRoleDelete(roleCd, prjId);
        }
    }
}
