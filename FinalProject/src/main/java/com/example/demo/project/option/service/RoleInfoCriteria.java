package com.example.demo.project.option.service;

import lombok.Getter;
import lombok.Setter;

/** 권한 상세·등록 화면 요청 */
@Getter
@Setter
public class RoleInfoCriteria {

    private Long prjId;
    private Long roleCd;
}
