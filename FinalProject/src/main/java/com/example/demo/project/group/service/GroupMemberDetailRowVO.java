package com.example.demo.project.group.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** 그룹 상세 — 구성원 정보 행 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupMemberDetailRowVO {

    private Long userId;
    private String userName;
    private String tel;
    private String email;
    private String grpName;
    private String prjStartDate;
}
