package com.example.demo.alarm.scheduler;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.demo.alarm.service.AlarmService;
import com.example.demo.project.calender.mapper.CalenderMapper;
import com.example.demo.project.calender.service.CalenderVO;

@Component
public class NotificationScheduler {

    @Autowired private AlarmService alarmService;
    @Autowired private CalenderMapper calenderMapper;

    // ✅ 알림 보낸 ID 메모리에 저장
    private final Set<Integer> sentReminderIds = new HashSet<>();

    @Scheduled(fixedRate = 60000)
    public void checkScheduleReminder() {
        System.out.println("=== 일정 체크 시작: " + new Date());

        List<CalenderVO> upcoming = calenderMapper.findByCalStartBetween(null, null);
        System.out.println("=== 해당 일정 개수: " + upcoming.size());

        upcoming.forEach(cal -> {
            // ✅ 이미 알림 보낸 일정은 스킵
            if (sentReminderIds.contains(cal.getId())) {
                System.out.println("=== 이미 알림 보낸 일정 스킵: " + cal.getCalText());
                return;
            }

            System.out.println("=== 일정 감지: " + cal.getCalText());
            alarmService.sendToAll(
                "1시간 후 일정이 시작됩니다: " + cal.getCalText()
            );

            // ✅ 알림 보낸 ID 저장
            sentReminderIds.add(cal.getId());
        });
    }
}