package com.example.demo.project.option.service;

import java.util.List;

/** 프로젝트 권한(ROLE) 목록·상세·삭제 */
public interface RoleService {

    /** 프로젝트 ROLE 목록 (권한코드·이름·생성일 검색) */
    List<RoleVO> selectRoleList(RoleVO roleVO);

    /** 상세 화면 — 단일 ROLE 조회 */
    RoleVO selectRoleByPrjAndCd(Long prjId, Long roleCd);

    /** 상세 화면 — MENU 전체 목록 */
    List<RoleVO> selectAllMenus();

    /** 상세 화면 — 이 ROLE에 연결된 MENU.ROLE_ID 목록 */
    List<String> selectMenuRoleIdsByRoleCd(Long roleCd);

    /** 역할에 매핑된 메뉴 표시명 목록(프로젝트 소속 검증 후). */
    List<String> selectMenuNamesByRoleCd(Long prjId, Long roleCd);

    /**
     * 역할에 매핑된 MENU 권한을 구분(ROLE_TP)별 섹션 + 조회/등록/수정/삭제 매트릭스로 반환.
     * 그룹 상세 모달의 매트릭스 표 등 화면 공용으로 사용.
     */
    List<RoleMenuSectionVO> selectMenuSectionsByRoleCd(Long prjId, Long roleCd);

    /**
     * MENU 마스터 + 연결된 ROLE_ID 집합 → 화면 섹션 DTO로 변환.
     * 등록 모드 등 외부에서 직접 빌드해야 할 때 호출.
     */
    List<RoleMenuSectionVO> buildMenuSections(List<RoleVO> allMenus, java.util.Set<String> linkedRoleIds);

    /** 상세 화면 — 이 ROLE을 보유한 GRP 목록 */
    List<RoleGroupRowVO> selectRoleGroupsList(Long prjId, Long roleCd);

    /**
     * 역할 제거 — Oracle {@code PROC_ROLE_DELETE}를 역할 코드마다 호출합니다.
     *
     * @throws IllegalArgumentException 요청 값이 비었거나, 해당 프로젝트에 없는 역할이 포함된 경우
     */
    void deleteRolesForProject(Long prjId, List<Long> roleCds);

    /** 그룹에서 권한 회수 — {@code PROC_GRP_ROLE_DELETE} */
    RoleRevokeResultVO revokeRoleFromGroup(Long roleCd, Long grpId, boolean deleteRoleIfUnused);

    /** 권한 등록 — {@code PROC_ROLE_CREATE} */
    RoleRevokeResultVO createRole(
            Long prjId, String roleName, List<String> menuRoleIds, List<Long> grpIds);

    /** 권한(메뉴·그룹) 수정 — {@code PROC_ROLE_UPDATE} */
    RoleRevokeResultVO updateRole(
            Long prjId, Long roleCd, List<String> menuRoleIds, List<Long> grpIds);

    /** {@code GRP_ROLE}에 역할을 보유한 그룹 수 */
    int countGroupsWithRole(Long roleCd);
}
