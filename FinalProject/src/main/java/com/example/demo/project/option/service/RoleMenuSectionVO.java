package com.example.demo.project.option.service;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 역할 상세 화면에서 {@code MENU} 권한을 구분({@code ROLE_TP})별로 묶어 표시하기 위한 DTO.
 */
@Getter
@AllArgsConstructor
public class RoleMenuSectionVO {

    /** {@code MENU.ROLE_TP} (없으면 "기타") */
    private final String sectionTitle;

    /** {@code ROLE_MTH} 가 전체(ALL 등)인 메뉴 — 별도 "전체 관리" 블록 */
    private final List<LabelSlot> fullControlSlots;

    /** 조회·등록·수정·삭제 — 전체 관리가 연결되면 모두 체크 표시 */
    private final List<CrudSlot> crudSlots;

    /** ALL·CRUD 이외의 {@code ROLE_MTH} */
    private final List<LabelSlot> extraSlots;

    @Getter
    @AllArgsConstructor
    public static class LabelSlot {
        private final String label;
        private final boolean checked;
    }

    @Getter
    @AllArgsConstructor
    public static class CrudSlot {
        private final String label;
        private final boolean checked;
    }
}
