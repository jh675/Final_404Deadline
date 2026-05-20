package com.example.demo.project.option.service;

import lombok.Getter;
import lombok.Setter;

/** {@code PROC_ROLE_UPDATE} — 권한(메뉴) 수정 */
@Getter
@Setter
public class RoleUpdateProcParam {

    private Long roleCd;
    /** 콤마(,)로 구분된 {@code MENU.ROLE_ID} 목록 */
    private String roleDetail;
    /** 콤마(,)로 구분된 {@code GRP.ID} 목록 — {@code NULL}이면 DB에서 관리 권한 시 관리 그룹 자동 부여 등 분기 */
    private String grpIds;
    private String resultStatus;
    private String resultMsg;
}
