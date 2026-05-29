package com.example.demo.project.member.service.impl;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.project.member.mapper.MemberMapper;
import com.example.demo.project.member.service.*;
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
    public List<CompanyMemberRowVO> selectCompanyMembersByPrjId(
            Long prjId, boolean excludeRegistered) {
        if (prjId == null) {
            return List.of();
        }
        List<CompanyMemberRowVO> rows =
                memberMapper.selectCompanyMembersByPrjId(prjId, excludeRegistered);
        return rows != null ? rows : List.of();
    }

    @Override
    public List<MemberGroupPickRowVO> selectProjectGroupsByPrjId(Long prjId) {
        if (prjId == null) {
            return List.of();
        }
        List<MemberGroupPickRowVO> rows = memberMapper.selectProjectGroupsByPrjId(prjId);
        return rows != null ? rows : List.of();
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

    @Override
    @Transactional
    public Long registerMember(Long prjId, Long userId, Long grpId) {
        if (prjId == null) {
            throw new IllegalArgumentException("프로젝트 ID가 필요합니다.");
        }
        if (userId == null) {
            throw new IllegalArgumentException("직원을 선택하세요.");
        }
        if (grpId == null) {
            throw new IllegalArgumentException("소속 그룹을 선택하세요.");
        }

        if (memberMapper.countGrpInProject(prjId, grpId) < 1) {
            throw new IllegalArgumentException("선택한 그룹이 이 프로젝트에 존재하지 않습니다.");
        }
        if (memberMapper.countActiveMember(userId, grpId) > 0) {
            throw new IllegalArgumentException("이미 해당 그룹에 등록된 구성원입니다.");
        }

        MemberRegisterParam param = new MemberRegisterParam();
        param.setUserId(userId);
        param.setGrpId(grpId);

        int inserted = memberMapper.insertMember(param);
        if (inserted < 1 || param.getMemberId() == null) {
            throw new IllegalStateException("구성원 등록에 실패했습니다.");
        }
        return param.getMemberId();
    }

    @Override
    @Transactional
    public int registerMembers(Long prjId, List<Long> userIds, Long grpId) {
        if (prjId == null) {
            throw new IllegalArgumentException("프로젝트 ID가 필요합니다.");
        }
        if (grpId == null) {
            throw new IllegalArgumentException("소속 그룹을 선택하세요.");
        }
        if (userIds == null || userIds.isEmpty()) {
            throw new IllegalArgumentException("등록할 직원을 선택하세요.");
        }
        List<Long> uniqueIds = new ArrayList<>(new LinkedHashSet<>(
                userIds.stream().filter(java.util.Objects::nonNull).toList()));
        if (uniqueIds.isEmpty()) {
            throw new IllegalArgumentException("등록할 직원을 선택하세요.");
        }

        if (memberMapper.countGrpInProject(prjId, grpId) < 1) {
            throw new IllegalArgumentException("선택한 그룹이 이 프로젝트에 존재하지 않습니다.");
        }

        for (Long uid : uniqueIds) {
            if (memberMapper.countActiveMember(uid, grpId) > 0) {
                throw new IllegalArgumentException(
                        "이미 해당 그룹에 등록된 구성원이 포함되어 있습니다. (userId=" + uid + ")");
            }
        }

        memberMapper.insertMembers(uniqueIds, grpId);
        return uniqueIds.size();
    }

    @Override
    @Transactional
    public void updateMember(Long prjId, Long userId, Long oldGrpId, Long grpId) {
        if (prjId == null) {
            throw new IllegalArgumentException("프로젝트 ID가 필요합니다.");
        }
        if (userId == null) {
            throw new IllegalArgumentException("구성원 정보가 올바르지 않습니다.");
        }
        if (oldGrpId == null) {
            throw new IllegalArgumentException("기존 그룹 정보가 올바르지 않습니다.");
        }
        if (grpId == null) {
            throw new IllegalArgumentException("소속 그룹을 선택하세요.");
        }

        MemberDetailVO detail = memberMapper.selectMemberDetail(prjId, userId, oldGrpId);
        if (detail == null) {
            throw new IllegalArgumentException("수정할 구성원을 찾을 수 없습니다.");
        }

        if (memberMapper.countGrpInProject(prjId, grpId) < 1) {
            throw new IllegalArgumentException("선택한 그룹이 이 프로젝트에 존재하지 않습니다.");
        }
        if (!oldGrpId.equals(grpId) && memberMapper.countActiveMember(userId, grpId) > 0) {
            throw new IllegalArgumentException("이미 해당 그룹에 등록된 구성원입니다.");
        }

        MemberUpdateParam param = new MemberUpdateParam();
        param.setPrjId(prjId);
        param.setUserId(userId);
        param.setOldGrpId(oldGrpId);
        param.setGrpId(grpId);

        int updated = memberMapper.updateMember(param);
        if (updated < 1) {
            throw new IllegalStateException("구성원 수정에 실패했습니다.");
        }
    }
}
