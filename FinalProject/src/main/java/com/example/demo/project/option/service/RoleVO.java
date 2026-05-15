package com.example.demo.project.option.service;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 권한 관리 VO.
 * <ul>
 *   <li>목록({@code selectRoleList}): 프로젝트 {@code ROLE} — {@link #roleCd}, {@link #roleName},
 *       {@link #grpCnt}, {@link #createdOn}</li>
 *   <li>상세 메뉴 체크: {@code MENU} — {@link #roleId}, {@link #roleName}, {@link #roleTp} 등</li>
 * </ul>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleVO {

    /** 프로젝트 ID ({@code PRJ_ID}) */
    private Long prjId;

    /** 검색: {@code ROLE.ROLE_CD} 문자열 부분 일치 (목록) */
    private String permissionKey;

    /** 검색: {@code ROLE.ROLE_NAME} 부분 일치 (목록) */
    private String permissionName;

    /** 검색: {@code ROLE.CREATED_ON} 구간 시작 (yyyy-MM-dd) */
    private String createdFrom;

    /** 검색: {@code ROLE.CREATED_ON} 구간 끝 (yyyy-MM-dd) */
    private String createdTo;

    /** 그리드·링크용 — 목록에서는 보통 {@code TO_CHAR(ROLE_CD)} */
    private String id;

    /** 목록: {@code ROLE_CD} 문자열 표기 또는 메뉴 행의 {@code MENU.ROLE_ID} */
    private String roleId;

    /** {@code ROLE.ROLE_NAME} 또는 {@code MENU.ROLE_NAME} */
    private String roleName;

    /** 프로젝트 역할 코드 ({@code ROLE.ROLE_CD}) — 목록 행 */
    private Long roleCd;

    /** {@code MENU} — 상세 메뉴 체크 목록 */
    private String roleUrl;
    private String roleTp;
    private String roleMth;

    /** 목록: {@code ROLE.CREATED_ON} */
    private LocalDateTime createdOn;

    /** 목록: 이 역할을 보유한 {@code GRP} 수 */
    private int grpCnt;
}
