package com.example.demo.project.option.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.example.demo.project.option.service.RoleGroupRowVO;
import com.example.demo.project.option.service.RoleVO;

/** 권한 MyBatis — ROLE·MENU·GRP_ROLE */
@Mapper
public interface RoleMapper {

    /** 프로젝트 ROLE 목록 (보유 그룹 수 포함) */
    List<RoleVO> selectRoleList(RoleVO roleVO);

    /** prjId + roleCd로 ROLE 1건 */
    RoleVO selectRoleByPrjAndCd(@Param("prjId") Long prjId, @Param("roleCd") Long roleCd);

    /** MENU 마스터 전체 */
    List<RoleVO> selectAllMenus();

    /** ROLE_MENU에 연결된 MENU.ROLE_ID */
    List<String> selectMenuRoleIdsByRoleCd(@Param("roleCd") Long roleCd);

    /** GRP_ROLE로 이 ROLE을 가진 그룹 목록 */
    List<RoleGroupRowVO> selectRoleGroupsList(
            @Param("prjId") Long prjId,
            @Param("roleCd") Long roleCd);

    /** DB {@code PROC_ROLE_DELETE} — GRP_ROLE, ROLE_MENU, ROLE 정리 후 COMMIT */
    void callProcRoleDelete(@Param("roleCd") Long roleCd, @Param("prjId") Long prjId);
}
