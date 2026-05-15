package com.example.demo.project.option.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.example.demo.project.option.service.RoleGroupRowVO;
import com.example.demo.project.option.service.RoleVO;

@Mapper
public interface RoleMapper {

    List<RoleVO> selectRoleList(RoleVO roleVO);

    RoleVO selectRoleByPrjAndCd(@Param("prjId") Long prjId, @Param("roleCd") Long roleCd);

    List<RoleVO> selectAllMenus();

    List<String> selectMenuRoleIdsByRoleCd(@Param("roleCd") Long roleCd);

    List<RoleGroupRowVO> selectRoleGroupsList(
            @Param("prjId") Long prjId,
            @Param("roleCd") Long roleCd);

    /** DB {@code PROC_ROLE_DELETE} — GRP_ROLE, ROLE_MENU, ROLE 정리 후 COMMIT */
    void callProcRoleDelete(@Param("roleCd") Long roleCd, @Param("prjId") Long prjId);
}
