package com.example.demo.project.group.service;

import lombok.Getter;
import lombok.Setter;

/** {@code PROC_GRP_INSERT} 호출 파라미터 (memIds null = 구성원 없음) */
@Getter
@Setter
public class GroupInsertProcParam {

    private Long prjId;
    private String grpName;
    /** 콤마 구분 user_id 목록 — 없으면 null */
    private String memIds;
    private String resultMsg;
}
