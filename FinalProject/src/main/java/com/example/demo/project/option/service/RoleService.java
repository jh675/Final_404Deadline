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

    /** 상세 화면 — 이 ROLE을 보유한 GRP 목록 */
    List<RoleGroupRowVO> selectRoleGroupsList(Long prjId, Long roleCd);

    /**
     * 역할 제거 — Oracle {@code PROC_ROLE_DELETE}를 역할 코드마다 호출합니다.
     *
     * @throws IllegalArgumentException 요청 값이 비었거나, 해당 프로젝트에 없는 역할이 포함된 경우
     */
    void deleteRolesForProject(Long prjId, List<Long> roleCds);
}
