package com.example.demo.project.option.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** {@code PROC_GRP_ROLE_DELETE} 실행 결과 */
@Getter
@AllArgsConstructor
public class RoleRevokeResultVO {

    private final String resultStatus;
    private final String resultMsg;

    public boolean isOk() {
        return resultStatus != null && "OK".equalsIgnoreCase(resultStatus.trim());
    }
}
