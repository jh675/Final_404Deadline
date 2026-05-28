package com.example.demo.alarm.scheduler;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.demo.alarm.service.AlarmIssueVO;
import com.example.demo.alarm.service.AlarmService;
import com.example.demo.management.mapper.ProjectMapper;
import com.example.demo.management.service.ProjectVO;
import com.example.demo.project.calender.mapper.CalenderMapper;
import com.example.demo.project.calender.service.CalenderVO;
import com.example.demo.project.group.mapper.GroupMapper;
import com.example.demo.project.group.service.GroupDetailVO;
import com.example.demo.project.issue.mapper.IssueMapper;

@Component
public class NotificationScheduler {

    @Autowired private AlarmService alarmService;
    @Autowired private IssueMapper issueMapper;
    @Autowired private ProjectMapper projectMapper;
    @Autowired private GroupMapper groupMapper;
    @Autowired private CalenderMapper calenderMapper;

    // lastChecked 제거! DB 시간 기준으로만 처리

    // ✅ 이슈 등록 감지 (10초마다)
    @Scheduled(fixedRate = 10000)
    public void checkNewIssue() {
        List<AlarmIssueVO> newIssues = issueMapper.findByCreatedOnAfter(null);
        System.out.println("=== 새 이슈 개수: " + newIssues.size());

        newIssues.forEach(issue -> {
            ProjectVO project = projectMapper.getprojectid(issue.getPrjId());
            String prjName = project != null ? project.getPrjName() : "알 수 없음";
            alarmService.sendToAll(
                "이슈가 등록되었습니다: [" + prjName + "] " + issue.getSubject()
            );
        });
    }

    // 나머지 메서드들도 동일하게 null 전달
    @Scheduled(fixedRate = 10000)
    public void checkIssueStatusChanged() {
        List<AlarmIssueVO> changedIssues = issueMapper.findByUpdatedOnAfter(null);
        changedIssues.forEach(issue -> {
            ProjectVO project = projectMapper.getprojectid(issue.getPrjId());
            String prjName = project != null ? project.getPrjName() : "알 수 없음";
            alarmService.sendToAll(
                "이슈 상태가 변경되었습니다: [" + prjName + "] "
                + issue.getSubject()
                + " → " + issue.getStatusCd()
            );
        });
    }

    @Scheduled(fixedRate = 10000)
    public void checkGroupJoined() {
        List<GroupDetailVO> joined = groupMapper.findByCreatedOnAfter(null);
        joined.forEach(group ->
            alarmService.sendToAll(
                "프로젝트 그룹에 참여되었습니다: ["
                + group.getPrjName() + "] " + group.getGrpName()
            )
        );
    }

    @Scheduled(fixedRate = 10000)
    public void checkProjectStatusChanged() {
        List<ProjectVO> changed = projectMapper.findByUpdatedOnAfter(null);
        changed.forEach(project ->
            alarmService.sendToAll(
                "프로젝트 진행상태가 변경되었습니다: "
                + project.getPrjName()
                + " → " + project.getPrjStatusCd()
            )
        );
    }

    @Scheduled(fixedRate = 10000)
    public void checkNewSchedule() {
        List<CalenderVO> newSchedules = calenderMapper.findByCreatedOnAfter(null);
        newSchedules.forEach(cal ->
            alarmService.sendToAll(
                "회사 일정이 등록되었습니다: " + cal.getCalText()
            )
        );
    }

    @Scheduled(fixedRate = 60000)
    public void checkScheduleReminder() {
        Date oneHourLater    = new Date(System.currentTimeMillis() + 60 * 60 * 1000);
        Date oneHourLaterEnd = new Date(System.currentTimeMillis() + 61 * 60 * 1000);
        List<CalenderVO> upcoming = calenderMapper.findByCalStartBetween(oneHourLater, oneHourLaterEnd);
        upcoming.forEach(cal ->
            alarmService.sendToAll(
                "1시간 후 일정이 시작됩니다: " + cal.getCalText()
            )
        );
    }
}