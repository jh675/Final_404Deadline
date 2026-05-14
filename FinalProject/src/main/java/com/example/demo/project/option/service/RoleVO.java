package com.example.demo.project.option.service;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 권한 관리 화면({@code roleManagement.html}) 그리드 행 VO.
 * 컬럼: {@code id}, {@code roleId}, {@code roleName}, {@code grpCnt}, {@code createdOn}
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleVO {

    /** 프로젝트 ID ({@code PRJ_ID}) — 목록 집계·검색 조건 */
    private Long prjId;

    /** 검색: {@code MENU.ROLE_ID} 부분 일치 */
    private String permissionKey;

    /** 검색: {@code MENU.ROLE_NAME} 부분 일치 */
    private String permissionName;

    /** 검색: {@code ROLE.CREATED_ON} 구간 시작 (yyyy-MM-dd) */
    private String createdFrom;

    /** 검색: {@code ROLE.CREATED_ON} 구간 끝 (yyyy-MM-dd) */
    private String createdTo;

    /**
     * 그리드 권한명 링크용 식별자 — {@code roleManagement.html} 에서 {@code row.id} 로 사용.
     * 메뉴 권한 코드와 동일하게 {@link #roleId} 와 매핑.
     */
    private String id;

    /** {@code MENU.ROLE_ID} (권한 코드) */
    private String roleId;

    /** {@code MENU.ROLE_NAME} */
    private String roleName;

    /** {@code MENU} 기타 — 상세 화면 등에서 사용 */
    private String roleUrl;
    private String roleTp;
    private String roleMth;

    /** 해당 프로젝트에서 메뉴와 연결된 {@code ROLE} 들 중 최신 {@code CREATED_ON} */
    private LocalDateTime createdOn;

    /** 해당 권한을 갖는 역할을 보유한 {@code GRP} 수 */
    private int grpCnt;
}
