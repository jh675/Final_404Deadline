package com.example.demo.project.group.service;

import lombok.Getter;
import lombok.Setter;

/** {@code PROC_GRP_UPDATE} 호출 파라미터 (memIds null = 구성원 변경 없음) */
@Getter
@Setter
public class GroupUpdateProcParam {

    private Long prjId;
    private Long grpId;
    /** 콤마 구분 user_id 목록 — 없으면 null */
    private String memIds;
    private String resultMsg;
}
