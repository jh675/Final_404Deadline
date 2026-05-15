package com.example.demo.project.option.service;

import java.util.List;

public interface RoleService {

    List<RoleVO> selectRoleList(RoleVO roleVO);

    RoleVO selectRoleByPrjAndCd(Long prjId, Long roleCd);

    List<RoleVO> selectAllMenus();

    List<String> selectMenuRoleIdsByRoleCd(Long roleCd);

    List<RoleGroupRowVO> selectRoleGroupsList(Long prjId, Long roleCd);

    /**
     * 역할 제거 — Oracle {@code PROC_ROLE_DELETE}를 역할 코드마다 호출합니다.
     *
     * @throws IllegalArgumentException 요청 값이 비었거나, 해당 프로젝트에 없는 역할이 포함된 경우
     */
    void deleteRolesForProject(Long prjId, List<Long> roleCds);
}
