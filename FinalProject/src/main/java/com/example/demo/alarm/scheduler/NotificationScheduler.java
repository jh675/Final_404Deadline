package com.example.demo.alarm.scheduler;

import java.util.Date;
import java.util.List;

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

    // 일정 1시간 전 알림 (1분마다)
    @Scheduled(fixedRate = 60000)
    public void checkScheduleReminder() {
        Date oneHourLater    = new Date(System.currentTimeMillis() + 60 * 60 * 1000);
        Date oneHourLaterEnd = new Date(System.currentTimeMillis() + 61 * 60 * 1000);

        List<CalenderVO> upcoming = calenderMapper
            .findByCalStartBetween(oneHourLater, oneHourLaterEnd);

        upcoming.forEach(cal ->
            alarmService.sendToAll(
                "1시간 후 일정이 시작됩니다: " + cal.getCalText()
            )
        );
    }
}