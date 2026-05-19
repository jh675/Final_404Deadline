package com.example.demo.project.member.service.impl;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.project.member.mapper.MemberMapper;
import com.example.demo.project.member.service.MemberDetailVO;
import com.example.demo.project.member.service.MemberIssueRowVO;
import com.example.demo.project.member.service.MemberListCriteria;
import com.example.demo.project.member.service.MemberService;
import com.example.demo.project.member.service.ProjectMemberRowVO;
import lombok.RequiredArgsConstructor;

/** 구성원 조회·제거 — 검증 후 Mapper 호출 */
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberMapper memberMapper;

    @Override
    public List<ProjectMemberRowVO> selectProjectMemberList(MemberListCriteria criteria) {
        if (criteria == null || criteria.getPrjId() == null) {
            return List.of();
        }
        return memberMapper.selectProjectMemberList(criteria);
    }

    @Override
    public String selectProjectName(Long prjId) {
        if (prjId == null) {
            return "";
        }
        String name = memberMapper.selectProjectNameByPrjId(prjId);
        return name == null ? "" : name;
    }

    @Override
    public MemberDetailVO selectMemberDetail(Long prjId, Long userId, Long grpId) {
        if (prjId == null || userId == null || grpId == null) {
            return null;
        }
        return memberMapper.selectMemberDetail(prjId, userId, grpId);
    }

    @Override
    public List<MemberIssueRowVO> selectMemberIssues(Long prjId, Long userId, Long grpId) {
        if (prjId == null || userId == null || grpId == null) {
            return List.of();
        }
        return memberMapper.selectMemberIssues(prjId, userId, grpId);
    }

    /** 검증 후 MEMBER 삭제 — 실패 시 롤백 */
    @Override
    @Transactional
    public void deleteMembers(Long prjId, List<ProjectMemberRowVO> rows) {
        if (prjId == null) {
            throw new IllegalArgumentException("프로젝트 ID가 필요합니다.");
        }
        if (rows == null || rows.isEmpty()) {
            throw new IllegalArgumentException("삭제할 멤버를 선택하세요.");
        }
        for (ProjectMemberRowVO r : rows) {
            if (r.getUserId() == null || r.getGrpId() == null) {
                throw new IllegalArgumentException("사용자·그룹 정보가 올바르지 않습니다.");
            }
        }
        memberMapper.deleteMemberRows(prjId, rows);
    }
}
