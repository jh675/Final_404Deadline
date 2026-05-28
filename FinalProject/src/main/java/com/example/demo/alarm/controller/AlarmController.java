package com.example.demo.alarm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.demo.alarm.service.AlarmService;

@Controller
@RequestMapping("/api/notifications")
public class AlarmController {

	@Autowired
	AlarmService alarmService;
	
	@GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	@ResponseBody
	public SseEmitter subscribe(@RequestParam("userId") String userId) {
	    return alarmService.subscribe(userId);
	}
}
