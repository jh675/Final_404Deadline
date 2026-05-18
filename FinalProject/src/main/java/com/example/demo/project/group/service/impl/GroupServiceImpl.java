package com.example.demo.project.group.service.impl;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.project.group.mapper.GroupMapper;
import com.example.demo.project.group.service.GroupListCriteria;
import com.example.demo.project.group.service.GroupService;
import com.example.demo.project.group.service.ProjectGroupRowVO;
import lombok.RequiredArgsConstructor;

/** 그룹 조회·연쇄 삭제 — @Transactional로 한 번에 커밋 */
@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    private final GroupMapper groupMapper;

    @Override
    public List<ProjectGroupRowVO> selectProjectGroupList(GroupListCriteria criteria) {
        if (criteria == null || criteria.getPrjId() == null) {
            return List.of();
        }
        return groupMapper.selectProjectGroupList(criteria);
    }

    /** MEMBER → GRP_ROLE → GRP 순 삭제 — 중간 실패 시 전부 롤백 */
    @Override
    @Transactional
    public void deleteGroups(Long prjId, List<Long> grpIds) {
        if (prjId == null) {
            throw new IllegalArgumentException("프로젝트 ID가 필요합니다.");
        }
        if (grpIds == null || grpIds.isEmpty()) {
            throw new IllegalArgumentException("삭제할 그룹을 선택하세요.");
        }
        groupMapper.deleteGroupMembers(prjId, grpIds);
        groupMapper.deleteGroupRoles(prjId, grpIds);
        groupMapper.deleteGroupRows(prjId, grpIds);
    }
}
