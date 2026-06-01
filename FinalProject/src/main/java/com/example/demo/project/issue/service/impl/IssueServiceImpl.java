package com.example.demo.project.issue.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.alarm.event.NotificationEvent;
import com.example.demo.management.mapper.ProjectMapper;
import com.example.demo.management.service.ProjectVO;
import com.example.demo.project.history.service.HistoryVO;
import com.example.demo.project.issue.mapper.IssueMapper;
import com.example.demo.project.issue.service.CommentInputVO;
import com.example.demo.project.issue.service.CommentOutputVO;
import com.example.demo.project.issue.service.IssueInputVO;
import com.example.demo.project.issue.service.IssueOutputVO;
import com.example.demo.project.issue.service.IssueService;
import com.example.demo.project.issue.service.IssueSummaryVO;
import com.example.demo.project.issue.service.IssueVulkVO;
import com.example.demo.project.milestone.mapper.MilestoneMapper;
import com.example.demo.project.milestone.service.MilestoneIssueVO;
import com.example.demo.project.milestone.service.MilestoneSyncException;
import com.example.demo.util.subCode.mapper.SubcodeMapper;

@Service
public class IssueServiceImpl implements IssueService {

	private static final List<String> STATUS_COLS = List.of("신규", "진행중", "검토", "완료");
	private static final List<String> PRIORITY_COLS = List.of("최상", "상", "중", "하");
	private static final List<String> CATEGORY_COLS = List.of("버그", "기능", "작업", "개선");
	private static final String MILESTONE_UNLINK_BLOCKED_MSG =
			"타임라인이 등록된 이슈는 마일스톤에서 해제할 수 없습니다. "
			+ "마일스톤 화면에서 타임라인을 삭제하거나 다른 마일스톤으로 이동해 주세요.";

	@Autowired
	IssueMapper mapper;
	@Autowired
	ProjectMapper projectMapper;
	@Autowired
	SubcodeMapper subcodeMapper;
	@Autowired
	MilestoneMapper milestoneMapper;
	
	@Autowired private ApplicationEventPublisher eventPublisher;
	
	@Override
	public List<IssueOutputVO> selectIssueList(IssueInputVO issueVO) {

		return mapper.selectIssueList(issueVO);
	}

	@Override
	public IssueOutputVO selectIssue(Long id) {
		return mapper.selectIssue(id);
	}

	@Override
	public IssueInputVO selectIssueForForm(Long id) {
		if (id == null) {
			return IssueInputVO.builder().build();
		}
		IssueInputVO vo = mapper.selectIssueForForm(id);
		return vo != null ? vo : IssueInputVO.builder().build();
	}

	@Override
	@Transactional
	public Long insertIssue(IssueInputVO issueVO) {
		mapper.insertIssue(issueVO);
		syncMilestoneIssue(issueVO.getId(), issueVO.getMilestoneId());
		ProjectVO project = projectMapper.getprojectid(issueVO.getPrjId());
		String prjName = project != null ? project.getPrjName() : "알 수 없음";
		eventPublisher.publishEvent(new NotificationEvent(
				this,
				"이슈가 등록되었습니다: [" + prjName + "] " + issueVO.getSubject()));
		return issueVO.getId();
	}

	@Override
	@Transactional
	public Long updateIssue(IssueInputVO issueVO) {
		syncMilestoneIssue(issueVO.getId(), issueVO.getMilestoneId());

		if (issueVO.getStatusCd() != null) {
			ProjectVO project = projectMapper.getprojectid(issueVO.getPrjId());
			String prjName = project != null ? project.getPrjName() : "알 수 없음";
			eventPublisher.publishEvent(new NotificationEvent(
					this,
					"이슈 상태가 변경되었습니다: [" + prjName + "] "
							+ issueVO.getSubject() + " → "
							+ subcodeMapper.selectScodeNm(issueVO.getStatusCd())));
		}

		return mapper.updateIssue(issueVO);
	}

	@Override
	public boolean isMilestoneUnlinkBlocked(Long issueId) {
		if (issueId == null) {
			return false;
		}
		MilestoneIssueVO existing = milestoneMapper.selectMilestoneIssueByIssueId(issueId);
		if (existing == null || existing.getId() == null) {
			return false;
		}
		return hasTimelineEvents(existing.getId());
	}

	/**
	 * milestone_issue 와 요청 milestoneId 를 동기화합니다.
	 * 없음 → INSERT, 변경 → UPDATE(moveIssue), 해제 → DELETE(타임라인 있으면 거부).
	 */
	private void syncMilestoneIssue(Long issueId, Long newMilestoneId) {
		if (issueId == null) {
			return;
		}

		MilestoneIssueVO existing = milestoneMapper.selectMilestoneIssueByIssueId(issueId);

		if (newMilestoneId == null) {
			if (existing == null) {
				return;
			}
			if (hasTimelineEvents(existing.getId())) {
				throw new MilestoneSyncException(MILESTONE_UNLINK_BLOCKED_MSG);
			}
			milestoneMapper.deleteMilestoneIssue(existing.getId());
			return;
		}

		if (existing == null) {
			MilestoneIssueVO created = new MilestoneIssueVO();
			created.setIssueId(issueId);
			created.setMilestoneId(newMilestoneId);
			milestoneMapper.insertMilestoneIssue(created);
			return;
		}

		if (newMilestoneId.equals(existing.getMilestoneId())) {
			return;
		}

		MilestoneIssueVO move = new MilestoneIssueVO();
		move.setId(existing.getId());
		move.setMilestoneId(newMilestoneId);
		milestoneMapper.moveIssue(move);
	}

	private boolean hasTimelineEvents(Long milestoneIssueId) {
		if (milestoneIssueId == null) {
			return false;
		}
		Long count = milestoneMapper.countTimelineByMilestoneIssueId(milestoneIssueId);
		return count != null && count > 0;
	}


	@Override
	public List<IssueOutputVO> selectChildIssueList(Long id) {
		return mapper.selectChildIssueList(id);
	}

	@Override
	public long countChildIssues(Long id){
		if (id == null) {
			return 0L;
		}
		return mapper.countChildIssues(id);
	}

	@Override
	public IssueSummaryVO getParentIssue(Long id) {
		if (id == null) {
			return null;
		}
		return mapper.getParentIssue(id);
	}
	
	@Override
	public List<CommentOutputVO> getComment(Long id) {
		if (id == null) {
			return Collections.emptyList();
		}
		List<CommentOutputVO> list = mapper.getComment(id);
		return list != null ? list : Collections.emptyList();
	}

	@Override
	public Long insertComment(CommentInputVO vo) {
		return mapper.insertComment(vo);
	}

	@Override
	public List<IssueSummaryVO> getIssueIds(Long prjId, Long issueId) {
		if (prjId == null) {
			return Collections.emptyList();
		}
		List<IssueSummaryVO> list = mapper.getIssueIds(prjId, issueId);
		return list != null ? list : Collections.emptyList();
	}

	@Override
	public List<IssueOutputVO> getRelationIssue(Long id) {
		if (id == null) {
			return Collections.emptyList();
		}
		List<IssueOutputVO> list = mapper.getRelationIssue(id);
		return list != null ? list : Collections.emptyList();
	}

	@Override
	public List<HistoryVO> getHistory(Long id) {
		if (id == null) {
			return Collections.emptyList();
		}
		List<HistoryVO> list = mapper.getHistory(id);
		return list != null ? list : Collections.emptyList();
	}

	@Override
	public List<Map<String, Object>> getPivotStatus(Long prjId) {
		if (prjId == null) {
			return Collections.emptyList();
		}
		return normalizePivotRows(mapper.getPivotStatus(prjId), STATUS_COLS);
	}

	@Override
	public List<Map<String, Object>> getPivotPriority(Long prjId) {
		if (prjId == null) {
			return Collections.emptyList();
		}
		return normalizePivotRows(mapper.getPivotPriority(prjId), PRIORITY_COLS);
	}

	@Override
	public List<Map<String, Object>> getPivotCategory(Long prjId) {
		if (prjId == null) {
			return Collections.emptyList();
		}
		return normalizePivotRows(mapper.getPivotCategory(prjId), CATEGORY_COLS);
	}

	/**
	 * MyBatis Map 키(대소문자·Oracle PIVOT 열명)를 화면용으로 통일합니다.
	 * label + valueColumns 키만 남깁니다.
	 */
	private List<Map<String, Object>> normalizePivotRows(List<Map<String, Object>> raw, List<String> valueColumns) {
		if (raw == null || raw.isEmpty()) {
			return Collections.emptyList();
		}
		List<Map<String, Object>> result = new ArrayList<>(raw.size());
		for (Map<String, Object> row : raw) {
			if (row == null) {
				continue;
			}
			Map<String, Object> normalized = new LinkedHashMap<>();
			String label = asString(findMapValue(row, "name", "NAME", "label", "LABEL"));
			if (label == null || label.isBlank()) {
				label = "미배정";
			}
			normalized.put("label", label);
			for (String col : valueColumns) {
				normalized.put(col, toCount(findMapValue(row, col)));
			}
			result.add(normalized);
		}
		return result;
	}

	private Object findMapValue(Map<String, Object> row, String... keys) {
		for (String key : keys) {
			if (row.containsKey(key)) {
				return row.get(key);
			}
		}
		for (Map.Entry<String, Object> entry : row.entrySet()) {
			String mapKey = entry.getKey();
			if (isMetaColumn(mapKey)) {
				continue;
			}
			for (String wanted : keys) {
				if (mapKey.equals(wanted)
						|| mapKey.trim().equals(wanted)
						|| mapKey.equalsIgnoreCase(wanted)) {
					return entry.getValue();
				}
			}
		}
		return null;
	}

	private boolean isMetaColumn(String key) {
		return "MEM_ID".equalsIgnoreCase(key) || "mem_id".equalsIgnoreCase(key) || "memId".equalsIgnoreCase(key);
	}

	private String asString(Object value) {
		return value == null ? null : value.toString();
	}

	private int toCount(Object value) {
		if (value == null) {
			return 0;
		}
		if (value instanceof Number number) {
			return number.intValue();
		}
		try {
			return Integer.parseInt(value.toString());
		} catch (NumberFormatException ex) {
			return 0;
		}
	}

	@Override
	public Long registerStartDate(IssueVulkVO vulkVO) {
		return mapper.registerStartDate(vulkVO);
	}

	@Override
	public Long registerClosedDate(IssueVulkVO vulkVO) {
		return mapper.registerClosedDate(vulkVO);
	}

	@Override
	public Long updateVulk(IssueVulkVO vulkVO) {
		return mapper.updateVulk(vulkVO);
	}

	@Override
	public List<IssueOutputVO> searchIssuesForLink(Long prjId, String q) {
		if (prjId == null) {
			return Collections.emptyList();
		}
		String term = q != null ? q.trim() : "";
		List<IssueOutputVO> issues = mapper.searchIssuesForLink(prjId, term.isEmpty() ? null : term);
		return issues != null ? issues : Collections.emptyList();
	}
}
