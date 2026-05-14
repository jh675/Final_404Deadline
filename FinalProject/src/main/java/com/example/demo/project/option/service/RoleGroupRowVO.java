package com.example.demo.project.option.service;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * 권한 상세 화면 — 해당 메뉴 권한({@code MENU.ROLE_ID})을 보유한 프로젝트 그룹 행.
 */
@Getter
@Setter
public class RoleGroupRowVO {

    /** {@code GRP.ID} */
    private Long id;

    /** {@code GRP.NAME} */
    private String grpName;

    /** 그룹 소속 서로 다른 사용자 수 {@code COUNT(DISTINCT MEMBER.USER_ID)} */
    private Integer grpMemberCnt;

    /** {@code GRP.CREATED_ON} */
    private LocalDateTime createdOn;
}
