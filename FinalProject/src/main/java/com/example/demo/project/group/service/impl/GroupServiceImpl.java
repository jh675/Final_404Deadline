package com.example.demo.project.group.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.example.demo.alarm.event.NotificationEvent;
import com.example.demo.project.group.mapper.GroupMapper;
import com.example.demo.project.group.service.*;
import com.example.demo.project.option.service.*;

import lombok.RequiredArgsConstructor;

/** 그룹 조회·등록·삭제 — DB 프로시저 호출 */
@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    private final GroupMapper groupMapper;
    private final RoleService roleService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public List<ProjectGroupRowVO> selectProjectGroupList(GroupListCriteria criteria) {
        if (criteria == null || criteria.getPrjId() == null) {
            return List.of();
        }
        return groupMapper.selectProjectGroupList(criteria);
    }

    @Override
    public GroupDetailVO selectGroupDetail(Long prjId, Long grpId) {
        if (prjId == null || grpId == null) {
            return null;
        }
        return groupMapper.selectGroupDetail(prjId, grpId);
    }

    @Override
    public String selectProjectName(Long prjId) {
        if (prjId == null) {
            return "";
        }
        String name = groupMapper.selectProjectNameByPrjId(prjId);
        return name == null ? "" : name;
    }

    @Override
    public boolean existsGroupName(Long prjId, String grpName) {
        if (prjId == null || grpName == null || grpName.isBlank()) {
            return false;
        }
        return groupMapper.countGrpByPrjIdAndName(prjId, grpName.trim()) > 0;
    }

    @Override
    public List<GroupMemberDetailRowVO> selectGroupMembers(Long prjId, Long grpId) {
        if (prjId == null || grpId == null) {
            return List.of();
        }
        return groupMapper.selectGroupMembers(prjId, grpId);
    }

    @Override
    public List<GroupMemberPickRowVO> selectGroupMemberPickList(Long prjId) {
        if (prjId == null) {
            return List.of();
        }
        return groupMapper.selectGroupMemberPickList(prjId);
    }

    @Override
    public List<GroupRoleDetailRowVO> selectGroupRoles(Long prjId, Long grpId) {
        if (prjId == null || grpId == null) {
            return List.of();
        }
        return groupMapper.selectGroupRoles(prjId, grpId);
    }

    @Override
    public boolean isRoleAssignedToGroup(Long prjId, Long grpId, Long roleCd) {
        if (prjId == null || grpId == null || roleCd == null) {
            return false;
        }
        return groupMapper.countGrpRoleAssignment(prjId, grpId, roleCd) > 0;
    }

    @Override
    public void insertGroup(Long prjId, String grpName, List<Long> userIds, List<Long> roleCds) {
        if (prjId == null) {
            throw new IllegalArgumentException("프로젝트 ID가 필요합니다.");
        }
        String name = grpName == null ? "" : grpName.trim();
        if (name.isEmpty()) {
            throw new IllegalArgumentException("그룹명을 입력하세요.");
        }
        if (existsGroupName(prjId, name)) {
            throw new IllegalArgumentException("동일한 그룹이 존재합니다");
        }

        GroupInsertProcParam param = new GroupInsertProcParam();
        param.setPrjId(prjId);
        param.setGrpName(name);
        param.setMemIds(joinUserIds(userIds));
        groupMapper.callProcGrpInsert(param);

        String msg = param.getResultMsg();
        if (msg == null) {
            throw new IllegalStateException("그룹 등록 결과를 받지 못했습니다.");
        }
        if (!"OK".equalsIgnoreCase(msg.trim())) {
            throw new IllegalArgumentException(msg);
        }

        if (roleCds != null && !roleCds.isEmpty()) {
            Long grpId = groupMapper.selectGrpIdByPrjIdAndName(prjId, name);
            if (grpId == null) {
                throw new IllegalStateException("등록된 그룹을 찾을 수 없습니다.");
            }
            updateGroupRoles(prjId, grpId, roleCds);
        }
    }

    @Override
    public void updateGroup(Long prjId, Long grpId, List<Long> userIds) {
        if (prjId == null) {
            throw new IllegalArgumentException("프로젝트 ID가 필요합니다.");
        }
        if (grpId == null) {
            throw new IllegalArgumentException("그룹 ID가 필요합니다.");
        }
        if (groupMapper.selectGroupDetail(prjId, grpId) == null) {
            throw new IllegalArgumentException("프로젝트에 존재하지 않는 그룹입니다.");
        }
        List<GroupMemberDetailRowVO> beforeMembers = groupMapper.selectGroupMembers(prjId, grpId);
        Set<Long> beforeUserIds = beforeMembers.stream()
            .map(GroupMemberDetailRowVO::getUserId)
            .collect(Collectors.toSet());

        GroupUpdateProcParam param = new GroupUpdateProcParam();
        param.setPrjId(prjId);
        param.setGrpId(grpId);
        param.setMemIds(joinUserIdsForUpdate(userIds));
        groupMapper.callProcGrpUpdate(param);

        String msg = param.getResultMsg();
        if (msg == null) {
            throw new IllegalStateException("그룹 수정 결과를 받지 못했습니다.");
        }
        if (!"OK".equalsIgnoreCase(msg.trim())) {
            throw new IllegalArgumentException(msg);
        }
        
     // ✅ 새로 추가된 유저만 골라내기
        String prjName = groupMapper.selectProjectNameByPrjId(prjId);
        GroupDetailVO groupDetail = groupMapper.selectGroupDetail(prjId, grpId);
        String grpName = groupDetail != null ? groupDetail.getGrpName() : "알 수 없음";

        if (userIds != null) {
            userIds.stream()
                .filter(Objects::nonNull)
                .filter(userId -> !beforeUserIds.contains(userId)) // 기존에 없던 유저만
                .forEach(userId -> {
                    eventPublisher.publishEvent(new NotificationEvent(
                        this,
                        "프로젝트 그룹에 참여되었습니다: [" + prjName + "] " + grpName,
                        String.valueOf(userId) // ← 특정 유저에게만 전송
                    ));
                });
        }
    }

    @Override
    public void updateGroupRoles(Long prjId, Long grpId, List<Long> roleCds) {
        if (prjId == null) {
            throw new IllegalArgumentException("프로젝트 ID가 필요합니다.");
        }
        if (grpId == null) {
            throw new IllegalArgumentException("그룹 ID가 필요합니다.");
        }
        if (groupMapper.selectGroupDetail(prjId, grpId) == null) {
            throw new IllegalArgumentException("프로젝트에 존재하지 않는 그룹입니다.");
        }

        Set<Long> desired = new HashSet<>();
        if (roleCds != null) {
            for (Long roleCd : roleCds) {
                if (roleCd != null) {
                    desired.add(roleCd);
                }
            }
        }

        Set<Long> current = new HashSet<>();
        for (GroupRoleDetailRowVO row : selectGroupRoles(prjId, grpId)) {
            if (row != null && row.getRoleCd() != null) {
                current.add(row.getRoleCd());
            }
        }

        for (Long roleCd : current) {
            if (!desired.contains(roleCd)) {
                RoleRevokeResultVO result =
                        roleService.revokeRoleFromGroup(roleCd, grpId, false);
                if (!result.isOk()) {
                    throw new IllegalArgumentException(result.getResultMsg());
                }
            }
        }

        for (Long roleCd : desired) {
            if (current.contains(roleCd)) {
                continue;
            }
            if (roleService.selectRoleByPrjAndCd(prjId, roleCd) == null) {
                throw new IllegalArgumentException("프로젝트에 존재하지 않는 역할입니다: " + roleCd);
            }
            List<Long> grpIds = new ArrayList<>();
            for (RoleGroupRowVO g : roleService.selectRoleGroupsList(prjId, roleCd)) {
                if (g != null && g.getId() != null) {
                    grpIds.add(g.getId());
                }
            }
            if (!grpIds.contains(grpId)) {
                grpIds.add(grpId);
            }
            List<String> menuIds = roleService.selectMenuRoleIdsByRoleCd(roleCd);
            RoleRevokeResultVO result =
                    roleService.updateRole(prjId, roleCd, menuIds, grpIds);
            if (!result.isOk()) {
                throw new IllegalArgumentException(result.getResultMsg());
            }
        }
    }

    /** 등록·INSERT — 빈 목록이면 memIds 미전달(null) */
    private static String joinUserIds(List<Long> userIds) {
        return joinUserIdsCsv(userIds, true);
    }

    /** 수정·UPDATE — 빈 목록이면 ''(그룹 구성원 전원 제거) */
    private static String joinUserIdsForUpdate(List<Long> userIds) {
        if (userIds == null) {
            return null;
        }
        return joinUserIdsCsv(userIds, false);
    }

    private static String joinUserIdsCsv(List<Long> userIds, boolean nullWhenEmpty) {
        if (userIds == null) {
            return null;
        }
        if (userIds.isEmpty()) {
            return nullWhenEmpty ? null : "";
        }
        String joined = userIds.stream()
                .filter(Objects::nonNull)
                .distinct()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        return joined.isEmpty() ? (nullWhenEmpty ? null : "") : joined;
    }

    /** 프로젝트 소속 확인 후 그룹마다 PROC_GRP_DELETE 호출 */
    @Override
    public void deleteGroups(Long prjId, List<Long> grpIds) {
        if (prjId == null) {
            throw new IllegalArgumentException("프로젝트 ID가 필요합니다.");
        }
        if (grpIds == null || grpIds.isEmpty()) {
            throw new IllegalArgumentException("삭제할 그룹을 선택하세요.");
        }
        List<Long> distinct =
                grpIds.stream().filter(Objects::nonNull).distinct().toList();
        if (distinct.isEmpty()) {
            throw new IllegalArgumentException("유효한 그룹 ID가 없습니다.");
        }
        for (Long grpId : distinct) {
            if (groupMapper.selectGroupDetail(prjId, grpId) == null) {
                throw new IllegalArgumentException("프로젝트에 존재하지 않는 그룹입니다: " + grpId);
            }
            groupMapper.callProcGrpDelete(grpId);
        }
    }
}
