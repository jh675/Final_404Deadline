package com.example.demo.project.option.service;

import lombok.Getter;
import lombok.Setter;

/** {@code PROC_ROLE_CREATE} — 권한 등록 */
@Getter
@Setter
public class RoleCreateProcParam {

    private Long prjId;
    private String roleName;
    /** 콤마(,)로 구분된 {@code MENU.ROLE_ID} 목록 */
    private String roleDetail;
    /** 콤마(,)로 구분된 {@code GRP.ID} 목록 — {@code NULL}이면 그룹 권한 부여 생략 */
    private String grpIds;
    private String resultStatus;
    private String resultMsg;
}
