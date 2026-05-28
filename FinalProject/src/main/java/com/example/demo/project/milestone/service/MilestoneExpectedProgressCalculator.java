package com.example.demo.project.milestone.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.example.demo.project.calender.service.HolidayVO;

/**
 * 예상진척도 = 휴일을 제외한 진행기간 / (전체기간 - 휴일기간)
 */
public final class MilestoneExpectedProgressCalculator {

	private MilestoneExpectedProgressCalculator() {
	}

	public static Long calculate(Date startDate, Date endDate, List<HolidayVO> holidays) {
		return calculate(startDate, endDate, holidays, LocalDate.now());
	}

	public static Long calculate(Date startDate, Date endDate, List<HolidayVO> holidays, LocalDate asOf) {
		LocalDate start = toLocalDate(startDate);
		LocalDate end = toLocalDate(endDate);
		if (start == null || end == null || end.isBefore(start) || asOf == null) {
			return null;
		}

		Set<LocalDate> holidaySet = toHolidayDateSet(holidays);

		int totalDays = 0;
		int holidaysInPeriod = 0;
		for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
			totalDays++;
			if (holidaySet.contains(d)) {
				holidaysInPeriod++;
			}
		}

		int totalWorkingDays = totalDays - holidaysInPeriod;
		if (totalWorkingDays <= 0) {
			return 0L;
		}

		if (asOf.isBefore(start)) {
			return 0L;
		}

		LocalDate progressEnd = asOf.isAfter(end) ? end : asOf;

		int elapsedDays = 0;
		int holidaysInElapsed = 0;
		for (LocalDate d = start; !d.isAfter(progressEnd); d = d.plusDays(1)) {
			elapsedDays++;
			if (holidaySet.contains(d)) {
				holidaysInElapsed++;
			}
		}

		int elapsedWorkingDays = elapsedDays - holidaysInElapsed;
		long percent = Math.round((elapsedWorkingDays * 100.0) / totalWorkingDays);
		return Math.max(0L, Math.min(100L, percent));
	}

	private static Set<LocalDate> toHolidayDateSet(List<HolidayVO> holidays) {
		Set<LocalDate> set = new HashSet<>();
		if (holidays == null) {
			return set;
		}
		for (HolidayVO h : holidays) {
			if (h == null) {
				continue;
			}
			try {
				set.add(LocalDate.of(h.getYear(), h.getMonth(), h.getDay()));
			} catch (RuntimeException ignored) {
				// 잘못된 날짜는 제외
			}
		}
		return set;
	}

	private static LocalDate toLocalDate(Date date) {
		if (date == null) {
			return null;
		}
		return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}
}
