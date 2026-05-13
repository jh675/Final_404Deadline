package com.example.demo.project.option.service;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 권한 관리 화면용 VO.
 * DB: {@code menu}(권한 마스터), {@code role_menu}·{@code role}·{@code grp_role}·{@code grp}(프로젝트별 그룹 수 집계).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleVO {

    /** project.prj_id — 목록 집계 시 해당 프로젝트만 */
    private Long projectId;

    /** 검색: menu.role_id 부분 일치 */
    private String permissionKey;

    /** 검색: menu.role_name 부분 일치 */
    private String permissionName;

    /** 검색: ROLE.created_on 구간 시작(yyyy-MM-dd) */
    private String createdFrom;

    /** 검색: ROLE.created_on 구간 끝(yyyy-MM-dd) */
    private String createdTo;

    /** 페이징: 1부터 */
    @Builder.Default
    private int page = 1;

    @Builder.Default
    private int pageSize = 10;

    /** MyBatis 페이징용 */
    private int offset;
    private int limit;

    /** menu.role_id (PK, 권한 코드) */
    private String roleId;

    /** menu.role_name */
    private String roleName;

    /** menu.role_url, role_tp, role_mth — 상세·등록·수정 시 사용 */
    private String roleUrl;
    private String roleTp;
    private String roleMth;

    /** 목록·상세: ROLE.created_on (메뉴에 매핑된 프로젝트 역할들 중 집계·MAX). 연결 없으면 null */
    private LocalDate createdOn;

    /** 프로젝트 내 해당 권한을 갖는 역할을 보유한 grp 수 */
    private int grpCnt;
}
