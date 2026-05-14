package com.example.demo.project.option.service;

import java.util.List;

public interface RoleService {

    List<RoleVO> selectRoleList(RoleVO roleVO);

    RoleVO selectRoleByPrjAndCd(Long prjId, Long roleCd);

    List<RoleVO> selectAllMenus();

    List<String> selectMenuRoleIdsByRoleCd(Long roleCd);

    List<RoleGroupRowVO> selectRoleGroupsList(Long prjId, Long roleCd);
}
