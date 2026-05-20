package com.example.demo.project.option.service;

import java.util.List;

/**
 * 역할 상세 화면에서 {@code MENU} 권한을 구분({@code ROLE_TP})별로 묶어 표시하기 위한 DTO.
 */
public class RoleMenuSectionVO {

    private final String sectionTitle;
    private final List<LabelSlot> fullControlSlots;
    private final List<CrudSlot> crudSlots;
    private final List<LabelSlot> extraSlots;

    public RoleMenuSectionVO(
            String sectionTitle,
            List<LabelSlot> fullControlSlots,
            List<CrudSlot> crudSlots,
            List<LabelSlot> extraSlots) {
        this.sectionTitle = sectionTitle;
        this.fullControlSlots = fullControlSlots;
        this.crudSlots = crudSlots;
        this.extraSlots = extraSlots;
    }

    public String getSectionTitle() {
        return sectionTitle;
    }

    public List<LabelSlot> getFullControlSlots() {
        return fullControlSlots;
    }

    public List<CrudSlot> getCrudSlots() {
        return crudSlots;
    }

    public List<LabelSlot> getExtraSlots() {
        return extraSlots;
    }

    /** 메뉴 권한 슬롯 (전체 관리·기타) */
    public static final class LabelSlot {
        private final String label;
        private final boolean checked;
        private final String roleId;

        public LabelSlot(String label, boolean checked, String roleId) {
            this.label = label;
            this.checked = checked;
            this.roleId = roleId;
        }

        public String getLabel() {
            return label;
        }

        public boolean isChecked() {
            return checked;
        }

        public String getRoleId() {
            return roleId;
        }
    }

    /** 조회·등록·수정·삭제 체크박스 */
    public static final class CrudSlot {
        private final String label;
        private final boolean checked;
        private final String roleId;

        public CrudSlot(String label, boolean checked, String roleId) {
            this.label = label;
            this.checked = checked;
            this.roleId = roleId;
        }

        public String getLabel() {
            return label;
        }

        public boolean isChecked() {
            return checked;
        }

        public String getRoleId() {
            return roleId;
        }
    }
}
