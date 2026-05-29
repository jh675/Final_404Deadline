package com.example.demo.project.member.service;

import lombok.Getter;
import lombok.Setter;

/** MEMBER 테이블 등록 파라미터 */
@Getter
@Setter
public class MemberRegisterParam {

    private Long memberId;
    private Long userId;
    private Long grpId;
}
