package com.example.demo.project.option.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import com.example.demo.project.option.service.RoleVO;

@Mapper
public interface RoleMapper {

    long selectRoleListCount(RoleVO roleVO);

    List<RoleVO> selectRoleList(RoleVO roleVO);
}
