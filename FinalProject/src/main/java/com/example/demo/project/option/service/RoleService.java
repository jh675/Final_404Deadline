package com.example.demo.project.option.service;

import java.util.List;

public interface RoleService {

    long countRoleList(RoleVO roleVO);

    List<RoleVO> selectRoleList(RoleVO roleVO);
}
