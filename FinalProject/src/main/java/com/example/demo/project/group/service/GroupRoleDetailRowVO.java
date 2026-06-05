package com.example.demo.project.group.service;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** 그룹 상세 — 보유 권한 행(역할 단위, 메뉴는 모달에서 조회) */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupRoleDetailRowVO {

    private Long roleCd;
    private String roleName;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createdOn;
    private String remark;
}
