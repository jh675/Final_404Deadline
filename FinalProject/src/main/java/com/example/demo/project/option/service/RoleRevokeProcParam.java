package com.example.demo.project.option.service;

import lombok.Getter;
import lombok.Setter;

/** {@code PROC_GRP_ROLE_DELETE} — 그룹별 권한 회수 */
@Getter
@Setter
public class RoleRevokeProcParam {

    private Long roleCd;
    private Long grpId;
    /** {@code 1} = 권한 자체 삭제, 그 외 = GRP_ROLE만 회수 */
    private String isDelete;
    private String resultStatus;
    private String resultMsg;
}
