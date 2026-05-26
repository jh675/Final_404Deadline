package com.example.demo.project.option.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.example.demo.project.option.mapper.RoleMapper;
import com.example.demo.project.option.service.*;
import com.example.demo.project.option.service.RoleMenuSectionVO.CrudSlot;
import com.example.demo.project.option.service.RoleMenuSectionVO.LabelSlot;

import lombok.RequiredArgsConstructor;

/** 권한 조회·삭제 — 목록 제거는 PROC_ROLE_DELETE, 그룹 회수는 PROC_GRP_ROLE_DELETE */
@Service
@Primary
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleMapper roleMapper;

    @Override
    public List<RoleVO> selectRoleList(RoleVO roleVO) {
        return roleMapper.selectRoleList(roleVO);
    }

    @Override
    public RoleVO selectRoleByPrjAndCd(Long prjId, Long roleCd) {
        if (prjId == null || roleCd == null) {
            return null;
        }
        return roleMapper.selectRoleByPrjAndCd(prjId, roleCd);
    }

    @Override
    public List<RoleVO> selectAllMenus() {
        return roleMapper.selectAllMenus();
    }

    @Override
    public List<String> selectMenuRoleIdsByRoleCd(Long roleCd) {
        if (roleCd == null) {
            return List.of();
        }
        return roleMapper.selectMenuRoleIdsByRoleCd(roleCd);
    }

    @Override
    public List<String> selectMenuNamesByRoleCd(Long prjId, Long roleCd) {
        if (prjId == null || roleCd == null) {
            return List.of();
        }
        if (roleMapper.selectRoleByPrjAndCd(prjId, roleCd) == null) {
            return List.of();
        }
        List<String> names = roleMapper.selectMenuNamesByRoleCd(roleCd);
        return names == null ? List.of() : names;
    }

    @Override
    public List<RoleMenuSectionVO> selectMenuSectionsByRoleCd(Long prjId, Long roleCd) {
        if (prjId == null || roleCd == null) {
            return List.of();
        }
        if (roleMapper.selectRoleByPrjAndCd(prjId, roleCd) == null) {
            return List.of();
        }
        List<RoleVO> allMenus = roleMapper.selectAllMenus();
        Set<String> linked = new HashSet<>(roleMapper.selectMenuRoleIdsByRoleCd(roleCd));
        return buildMenuSections(allMenus, linked);
    }

    @Override
    public List<RoleMenuSectionVO> buildMenuSections(List<RoleVO> allMenus, Set<String> linked) {
        List<RoleVO> safeMenus = allMenus == null ? List.of() : allMenus;
        Set<String> safeLinked = linked == null ? Set.of() : linked;

        Map<String, List<RoleVO>> byTp = safeMenus.stream()
                .collect(Collectors.groupingBy(
                        m -> (m.getRoleTp() != null && !m.getRoleTp().isBlank())
                                ? m.getRoleTp()
                                : "기타",
                        LinkedHashMap::new,
                        Collectors.toList()));

        List<RoleMenuSectionVO> sections = new ArrayList<>();
        for (Map.Entry<String, List<RoleVO>> e : byTp.entrySet()) {
            List<RoleVO> items = e.getValue();
            List<RoleVO> fullRows = new ArrayList<>();
            List<RoleVO> nonFullRows = new ArrayList<>();
            for (RoleVO m : items) {
                if (isAllMth(m.getRoleMth())) {
                    fullRows.add(m);
                } else {
                    nonFullRows.add(m);
                }
            }

            boolean anyFullLinked =
                    fullRows.stream().anyMatch(m -> safeLinked.contains(m.getRoleId()));

            boolean viewOnlySection = isViewOnlySection(e.getKey());

            String[] mths = {"GET", "POST", "PUT", "DELETE"};
            String[] labels = {"조회", "등록", "수정", "삭제"};
            List<CrudSlot> crud = new ArrayList<>(4);
            for (int i = 0; i < mths.length; i++) {
                if (viewOnlySection && !"GET".equals(mths[i])) {
                    continue;
                }
                RoleVO row = findFirstByMth(nonFullRows, mths[i]);
                boolean checked = anyFullLinked
                        || (row != null && safeLinked.contains(row.getRoleId()));
                String menuRoleId = row != null ? row.getRoleId() : null;
                crud.add(new CrudSlot(labels[i], checked, menuRoleId));
            }

            boolean allCrudChecked =
                    crud.size() == mths.length
                            && crud.stream().allMatch(CrudSlot::isChecked);

            List<LabelSlot> fullSlots;
            if (viewOnlySection) {
                fullSlots = List.of();
            } else if (fullRows.isEmpty()) {
                fullSlots =
                        crud.isEmpty()
                                ? List.of()
                                : List.of(new LabelSlot("전체 관리", allCrudChecked, null));
            } else {
                fullSlots = fullRows.stream()
                        .map(m -> new LabelSlot(
                                m.getRoleName() != null ? m.getRoleName() : "전체 관리",
                                safeLinked.contains(m.getRoleId()) || allCrudChecked,
                                m.getRoleId()))
                        .collect(Collectors.toList());
            }

            List<LabelSlot> extras =
                    viewOnlySection
                            ? List.of()
                            : nonFullRows.stream()
                                    .filter(m -> !isCrudMth(m.getRoleMth()))
                                    .map(m -> new LabelSlot(
                                            m.getRoleName() != null
                                                    ? m.getRoleName()
                                                    : m.getRoleId(),
                                            safeLinked.contains(m.getRoleId()),
                                            m.getRoleId()))
                                    .collect(Collectors.toList());

            sections.add(new RoleMenuSectionVO(e.getKey(), fullSlots, crud, extras));
        }
        return sections;
    }

    /** HISTORY·GANTT(간트차트) 구역은 조회(GET) 체크박스만 노출 */
    private static boolean isViewOnlySection(String sectionTp) {
        if (sectionTp == null) {
            return false;
        }
        String trimmed = sectionTp.trim();
        if (trimmed.isEmpty()) {
            return false;
        }
        String upper = trimmed.toUpperCase(Locale.ROOT);
        return "HISTORY".equals(upper)
                || "GANTT".equals(upper)
                || "간트차트".equals(trimmed);
    }

    private static boolean isAllMth(String roleMth) {
        if (roleMth == null || roleMth.isBlank()) {
            return false;
        }
        String u = roleMth.trim().toUpperCase(Locale.ROOT);
        return "ALL".equals(u) || "전체".equals(roleMth.trim());
    }

    private static boolean isCrudMth(String roleMth) {
        if (roleMth == null || roleMth.isBlank()) {
            return false;
        }
        String u = roleMth.trim().toUpperCase(Locale.ROOT);
        return "GET".equals(u)
                || "POST".equals(u)
                || "PUT".equals(u)
                || "DELETE".equals(u)
                || "조회".equals(roleMth.trim())
                || "등록".equals(roleMth.trim())
                || "수정".equals(roleMth.trim())
                || "삭제".equals(roleMth.trim());
    }

    private static RoleVO findFirstByMth(List<RoleVO> rows, String code) {
        for (RoleVO m : rows) {
            if (mthMatches(m.getRoleMth(), code)) {
                return m;
            }
        }
        return null;
    }

    private static boolean mthMatches(String raw, String code) {
        if (raw == null || raw.isBlank()) {
            return false;
        }
        String t = raw.trim();
        String u = t.toUpperCase(Locale.ROOT);
        if (u.equals(code)) {
            return true;
        }
        return switch (code) {
            case "GET" -> "조회".equals(t);
            case "POST" -> "등록".equals(t);
            case "PUT" -> "수정".equals(t);
            case "DELETE" -> "삭제".equals(t);
            default -> false;
        };
    }

    @Override
    public List<RoleGroupRowVO> selectRoleGroupsList(Long prjId, Long roleCd) {
        if (prjId == null || roleCd == null) {
            return List.of();
        }
        return roleMapper.selectRoleGroupsList(prjId, roleCd);
    }

    /** 프로젝트 소속 확인 후 역할마다 PROC_ROLE_DELETE 호출 */
    @Override
    public void deleteRolesForProject(Long prjId, List<Long> roleCds) {
        if (prjId == null || roleCds == null || roleCds.isEmpty()) {
            throw new IllegalArgumentException("프로젝트 ID와 삭제할 역할이 필요합니다.");
        }
        List<Long> distinct =
                roleCds.stream().filter(Objects::nonNull).distinct().toList();
        if (distinct.isEmpty()) {
            throw new IllegalArgumentException("유효한 역할 코드가 없습니다.");
        }
        for (Long roleCd : distinct) {
            if (roleMapper.selectRoleByPrjAndCd(prjId, roleCd) == null) {
                throw new IllegalArgumentException("프로젝트에 존재하지 않는 역할입니다: " + roleCd);
            }
            roleMapper.callProcRoleDelete(roleCd, prjId);
        }
    }

    @Override
    public int countGroupsWithRole(Long roleCd) {
        if (roleCd == null) {
            return 0;
        }
        return roleMapper.countGrpByRoleCd(roleCd);
    }

    @Override
    public RoleRevokeResultVO createRole(
            Long prjId, String roleName, List<String> menuRoleIds, List<Long> grpIds) {
        if (prjId == null) {
            throw new IllegalArgumentException("프로젝트 ID가 필요합니다.");
        }
        if (roleName == null || roleName.isBlank()) {
            throw new IllegalArgumentException("역할명이 필요합니다.");
        }

        String roleDetail = null;
        if (menuRoleIds != null && !menuRoleIds.isEmpty()) {
            List<String> tokens =
                    menuRoleIds.stream()
                            .filter(Objects::nonNull)
                            .map(String::trim)
                            .filter(s -> !s.isEmpty())
                            .distinct()
                            .toList();
            if (!tokens.isEmpty()) {
                roleDetail = String.join(",", tokens);
            }
        }

        String grpIdsStr = joinGrpIds(grpIds);

        RoleCreateProcParam param = new RoleCreateProcParam();
        param.setPrjId(prjId);
        param.setRoleName(roleName.trim());
        param.setRoleDetail(roleDetail);
        param.setGrpIds(grpIdsStr);
        roleMapper.callProcRoleCreate(param);

        String status = param.getResultStatus() == null ? "" : param.getResultStatus().trim();
        String msg = param.getResultMsg() == null ? "" : param.getResultMsg().trim();
        return new RoleRevokeResultVO(status, msg);
    }

    private static String joinGrpIds(List<Long> grpIds) {
        if (grpIds == null || grpIds.isEmpty()) {
            return null;
        }
        String joined =
                grpIds.stream()
                        .filter(Objects::nonNull)
                        .distinct()
                        .map(String::valueOf)
                        .collect(java.util.stream.Collectors.joining(","));
        return joined.isEmpty() ? null : joined;
    }

    @Override
    public RoleRevokeResultVO updateRole(
            Long prjId, Long roleCd, List<String> menuRoleIds, List<Long> grpIds) {
        if (prjId == null) {
            throw new IllegalArgumentException("프로젝트 ID가 필요합니다.");
        }
        if (roleCd == null) {
            throw new IllegalArgumentException("역할 코드가 필요합니다.");
        }
        if (roleMapper.selectRoleByPrjAndCd(prjId, roleCd) == null) {
            throw new IllegalArgumentException("프로젝트에 존재하지 않는 역할입니다.");
        }

        String roleDetail = null;
        if (menuRoleIds != null && !menuRoleIds.isEmpty()) {
            List<String> tokens =
                    menuRoleIds.stream()
                            .filter(Objects::nonNull)
                            .map(String::trim)
                            .filter(s -> !s.isEmpty())
                            .distinct()
                            .toList();
            if (!tokens.isEmpty()) {
                roleDetail = String.join(",", tokens);
            }
        }

        String grpIdsStr = joinGrpIds(grpIds);

        RoleUpdateProcParam param = new RoleUpdateProcParam();
        param.setRoleCd(roleCd);
        param.setRoleDetail(roleDetail);
        param.setGrpIds(grpIdsStr);
        roleMapper.callProcRoleUpdate(param);

        String status = param.getResultStatus() == null ? "" : param.getResultStatus().trim();
        String msg = param.getResultMsg() == null ? "" : param.getResultMsg().trim();
        return new RoleRevokeResultVO(status, msg);
    }

    @Override
    public RoleRevokeResultVO revokeRoleFromGroup(
            Long roleCd, Long grpId, boolean deleteRoleIfUnused) {
        if (roleCd == null) {
            throw new IllegalArgumentException("역할 코드가 필요합니다.");
        }
        if (grpId == null) {
            throw new IllegalArgumentException("그룹 ID가 필요합니다.");
        }

        RoleRevokeProcParam param = new RoleRevokeProcParam();
        param.setRoleCd(roleCd);
        param.setGrpId(grpId);
        param.setIsDelete(deleteRoleIfUnused ? "1" : "0");
        roleMapper.callProcRoleRevokeFromGroup(param);

        String status = param.getResultStatus() == null ? "" : param.getResultStatus().trim();
        String msg = param.getResultMsg() == null ? "" : param.getResultMsg().trim();
        return new RoleRevokeResultVO(status, msg);
    }
}
