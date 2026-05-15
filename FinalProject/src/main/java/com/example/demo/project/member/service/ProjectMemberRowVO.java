package com.example.demo.project.member.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** 프로젝트 멤버 목록 한 행 (삭제 시 {@link #grpId} 필요) */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectMemberRowVO {

    private Long userId;
    private Long grpId;
    private String userName;
    private String tel;
    private String email;
    private String grpName;
    /** {@code yyyy-MM-dd} (조회 전용 문자열) */
    private String prjStartDate;
}
