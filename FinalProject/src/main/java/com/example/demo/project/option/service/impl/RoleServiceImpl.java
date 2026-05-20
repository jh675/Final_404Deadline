package com.example.demo.project.option.service.impl;

import java.util.List;
import java.util.Objects;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.example.demo.project.option.mapper.RoleMapper;
import com.example.demo.project.option.service.*;

import lombok.RequiredArgsConstructor;

/** 권한 조회·삭제 — 목록 제거는 PROC_ROLE_DELETE, 그룹 회수는 PROC_GRP_ROLE_DELETE */
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
    public List<String> selectMenuNamesByRoleCd(Long prjId, Long roleCd) {
        if (prjId == null || roleCd == null) {
            return List.of();
        }
        if (roleMapper.selectRoleByPrjAndCd(prjId, roleCd) == null) {
            return List.of();
        }
        List<String> names = roleMapper.selectMenuNamesByRoleCd(roleCd);
        return names == null ? List.of() : names;
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

    @Override
    public int countGroupsWithRole(Long roleCd) {
        if (roleCd == null) {
            return 0;
        }
        return roleMapper.countGrpByRoleCd(roleCd);
    }

    @Override
    public RoleRevokeResultVO createRole(
            Long prjId, String roleName, List<String> menuRoleIds, List<Long> grpIds) {
        if (prjId == null) {
            throw new IllegalArgumentException("프로젝트 ID가 필요합니다.");
        }
        if (roleName == null || roleName.isBlank()) {
            throw new IllegalArgumentException("역할명이 필요합니다.");
        }

        String roleDetail = null;
        if (menuRoleIds != null && !menuRoleIds.isEmpty()) {
            List<String> tokens =
                    menuRoleIds.stream()
                            .filter(Objects::nonNull)
                            .map(String::trim)
                            .filter(s -> !s.isEmpty())
                            .distinct()
                            .toList();
            if (!tokens.isEmpty()) {
                roleDetail = String.join(",", tokens);
            }
        }

        String grpIdsStr = joinGrpIds(grpIds);

        RoleCreateProcParam param = new RoleCreateProcParam();
        param.setPrjId(prjId);
        param.setRoleName(roleName.trim());
        param.setRoleDetail(roleDetail);
        param.setGrpIds(grpIdsStr);
        roleMapper.callProcRoleCreate(param);

        String status = param.getResultStatus() == null ? "" : param.getResultStatus().trim();
        String msg = param.getResultMsg() == null ? "" : param.getResultMsg().trim();
        return new RoleRevokeResultVO(status, msg);
    }

    private static String joinGrpIds(List<Long> grpIds) {
        if (grpIds == null || grpIds.isEmpty()) {
            return null;
        }
        String joined =
                grpIds.stream()
                        .filter(Objects::nonNull)
                        .distinct()
                        .map(String::valueOf)
                        .collect(java.util.stream.Collectors.joining(","));
        return joined.isEmpty() ? null : joined;
    }

    @Override
    public RoleRevokeResultVO updateRole(
            Long prjId, Long roleCd, List<String> menuRoleIds, List<Long> grpIds) {
        if (prjId == null) {
            throw new IllegalArgumentException("프로젝트 ID가 필요합니다.");
        }
        if (roleCd == null) {
            throw new IllegalArgumentException("역할 코드가 필요합니다.");
        }
        if (roleMapper.selectRoleByPrjAndCd(prjId, roleCd) == null) {
            throw new IllegalArgumentException("프로젝트에 존재하지 않는 역할입니다.");
        }

        String roleDetail = null;
        if (menuRoleIds != null && !menuRoleIds.isEmpty()) {
            List<String> tokens =
                    menuRoleIds.stream()
                            .filter(Objects::nonNull)
                            .map(String::trim)
                            .filter(s -> !s.isEmpty())
                            .distinct()
                            .toList();
            if (!tokens.isEmpty()) {
                roleDetail = String.join(",", tokens);
            }
        }

        String grpIdsStr = joinGrpIds(grpIds);

        RoleUpdateProcParam param = new RoleUpdateProcParam();
        param.setRoleCd(roleCd);
        param.setRoleDetail(roleDetail);
        param.setGrpIds(grpIdsStr);
        roleMapper.callProcRoleUpdate(param);

        String status = param.getResultStatus() == null ? "" : param.getResultStatus().trim();
        String msg = param.getResultMsg() == null ? "" : param.getResultMsg().trim();
        return new RoleRevokeResultVO(status, msg);
    }

    @Override
    public RoleRevokeResultVO revokeRoleFromGroup(
            Long roleCd, Long grpId, boolean deleteRoleIfUnused) {
        if (roleCd == null) {
            throw new IllegalArgumentException("역할 코드가 필요합니다.");
        }
        if (grpId == null) {
            throw new IllegalArgumentException("그룹 ID가 필요합니다.");
        }

        RoleRevokeProcParam param = new RoleRevokeProcParam();
        param.setRoleCd(roleCd);
        param.setGrpId(grpId);
        param.setIsDelete(deleteRoleIfUnused ? "1" : "0");
        roleMapper.callProcRoleRevokeFromGroup(param);

        String status = param.getResultStatus() == null ? "" : param.getResultStatus().trim();
        String msg = param.getResultMsg() == null ? "" : param.getResultMsg().trim();
        return new RoleRevokeResultVO(status, msg);
    }
}
