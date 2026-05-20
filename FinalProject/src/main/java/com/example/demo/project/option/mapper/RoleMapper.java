package com.example.demo.project.option.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.example.demo.project.option.service.*;

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

    /** ROLE_MENU 기준 메뉴 표시명 목록(정렬: ROLE_TP, ROLE_ID) */
    List<String> selectMenuNamesByRoleCd(@Param("roleCd") Long roleCd);

    /** GRP_ROLE로 이 ROLE을 가진 그룹 목록 */
    List<RoleGroupRowVO> selectRoleGroupsList(
            @Param("prjId") Long prjId,
            @Param("roleCd") Long roleCd);

    /** DB {@code PROC_ROLE_DELETE}(p_role_cd, p_prj_id) — 목록 화면 역할 일괄 제거 */
    void callProcRoleDelete(@Param("roleCd") Long roleCd, @Param("prjId") Long prjId);

    /** DB {@code PROC_GRP_ROLE_DELETE} — 그룹에서 권한 회수 */
    void callProcRoleRevokeFromGroup(RoleRevokeProcParam param);

    /** DB {@code PROC_ROLE_CREATE} — 권한 등록 */
    void callProcRoleCreate(RoleCreateProcParam param);

    /** DB {@code PROC_ROLE_UPDATE} — 권한(메뉴) 수정 */
    void callProcRoleUpdate(RoleUpdateProcParam param);

    /** {@code GRP_ROLE}에 역할을 부여한 그룹 수 */
    int countGrpByRoleCd(@Param("roleCd") Long roleCd);
}
