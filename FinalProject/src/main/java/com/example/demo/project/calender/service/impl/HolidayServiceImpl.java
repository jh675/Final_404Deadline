package com.example.demo.project.calender.service.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.demo.project.calender.mapper.HolidayMapper;
import com.example.demo.project.calender.service.HolidayService;
import com.example.demo.project.calender.service.HolidayVO;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class HolidayServiceImpl implements HolidayService {

    private final HolidayMapper holidayMapper;
    private final RestTemplate restTemplate = createRestTemplate();

    // 외부 공휴일 API가 응답하지 않을 때 요청 스레드가 무한 대기하지 않도록 타임아웃 설정
    private static RestTemplate createRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3000);
        factory.setReadTimeout(5000);
        return new RestTemplate(factory);
    }

    @Value("${openapi.holiday.service-key}")
    private String serviceKey;

    @Override
    public List<HolidayVO> getHolidays(int year) {
        List<HolidayVO> list = holidayMapper.getHolidays(year);
        if (list == null || list.isEmpty()) {
            fetchAndSave(year);
            list = holidayMapper.getHolidays(year);
        }
        return list;
    }

    @Override
    @Transactional
    public void fetchAndSave(int year) {
        log.info("공공 API로부터 {}년도 공휴일 데이터 수집 및 DB 저장 시작", year);

        String url = "http://apis.data.go.kr/B090041/openapi/service/SpcdeInfoService/getRestDeInfo"
                + "?serviceKey=" + serviceKey
                + "&solYear=" + year
                + "&_type=json&numOfRows=100";

        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    new URI(url),
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Map<String, Object>>() {});

            Map<String, Object> apiResult = response.getBody();
            if (apiResult == null) {
                log.warn("{}년도 공휴일 API 응답 본문이 비어 있습니다.", year);
                return;
            }

            List<Map<String, Object>> apiHolidays = parseHolidayItems(apiResult);
            if (apiHolidays.isEmpty()) {
                log.warn("{}년도에 해당하는 공휴일 데이터가 API 서버에 존재하지 않습니다.", year);
                return;
            }

            holidayMapper.deleteByYear(year);

            for (Map<String, Object> item : apiHolidays) {
                String locdate = String.valueOf(item.get("locdate"));

                HolidayVO holiday = new HolidayVO();
                holiday.setYear(Integer.parseInt(locdate.substring(0, 4)));
                holiday.setMonth(Integer.parseInt(locdate.substring(4, 6)));
                holiday.setDay(Integer.parseInt(locdate.substring(6, 8)));
                holiday.setHolidayName(String.valueOf(item.get("dateName")));

                holidayMapper.insertHoliday(holiday);
            }
            log.info("Successfully saved {} holidays for year {}", apiHolidays.size(), year);
        } catch (Exception e) {
            log.error("{}년도 공휴일 수집 중 에러 발생", year, e);
        }
    }

    @Override
    public void fetchAndRange(int startYear, int endYear) {
        log.info("{}년부터 {}년까지 공휴일 일괄 수집 진행", startYear, endYear);
        for (int year = startYear; year <= endYear; year++) {
            fetchAndSave(year);
        }
    }

    @SuppressWarnings("unchecked")
    private static List<Map<String, Object>> parseHolidayItems(Map<String, Object> apiResult) {
        List<Map<String, Object>> empty = List.of();

        Object responseObj = apiResult.get("response");
        if (!(responseObj instanceof Map<?, ?>)) {
            return empty;
        }
        Map<String, Object> response = (Map<String, Object>) responseObj;

        Object bodyObj = response.get("body");
        if (!(bodyObj instanceof Map<?, ?>)) {
            return empty;
        }
        Map<String, Object> body = (Map<String, Object>) bodyObj;

        Object itemsObj = body.get("items");
        if (!(itemsObj instanceof Map<?, ?>)) {
            return empty;
        }
        Map<String, Object> items = (Map<String, Object>) itemsObj;

        Object itemObj = items.get("item");
        if (itemObj == null) {
            return empty;
        }

        List<Map<String, Object>> apiHolidays = new ArrayList<>();
        if (itemObj instanceof List<?> itemList) {
            for (Object entry : itemList) {
                if (entry instanceof Map<?, ?>) {
                    apiHolidays.add((Map<String, Object>) entry);
                }
            }
        } else if (itemObj instanceof Map<?, ?>) {
            apiHolidays.add((Map<String, Object>) itemObj);
        }
        return apiHolidays;
    }
}
