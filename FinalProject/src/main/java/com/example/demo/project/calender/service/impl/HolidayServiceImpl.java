package com.example.demo.project.calender.service.impl;

import java.net.URI; // 임포트 추가
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value; // 롬복 대신 스프링 Value로 변경 완료!
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate; // 임포트 추가

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
  
  // 정상적으로 application.properties에서 키를 안전하게 주입받습니다.
  @Value("${openapi.holiday.service-key}")
  private String serviceKey;
  
  @Override
  public List<HolidayVO> getHolidays(int year) {
	  	List<HolidayVO> list = holidayMapper.getHolidays(year);
	  	if(list == null || list.isEmpty()) {
	  		fetchAndSave(year);
	  		list = holidayMapper.getHolidays(year);
	  }
	  return list;
  }
  
  @Override
  @Transactional
  public void fetchAndSave(int year) {
      log.info("공공 API로부터 {}년도 공휴일 데이터 수집 및 DB 저장 시작", year);
      
      // 1. 공공 API URL 설정 (JSON 포맷 강제 및 넉넉하게 100줄 요청)
      String url = "http://apis.data.go.kr/B090041/openapi/service/SpcdeInfoService/getRestDeInfo"
              + "?serviceKey=" + serviceKey
              + "&solYear=" + year
              + "&_type=json&numOfRows=100";
      
      try {
          RestTemplate restTemplate = new RestTemplate();
          // URI 객체로 감싸서 요청해야 인증키 특수문자 깨짐 현상이 안 일어납니다.
          Map<String, Object> apiResult = restTemplate.getForObject(new URI(url), Map.class);
          
          // 2. 공공 API의 복잡한 response -> body -> items -> item 트리 구조 분해
          Map<String, Object> response = (Map<String, Object>) apiResult.get("response");
          Map<String, Object> body = (Map<String, Object>) response.get("body");
          Map<String, Object> items = (Map<String, Object>) body.get("items");
          
          if (items != null && items.get("item") != null) {
              Object itemObj = items.get("item");
              List<Map<String, Object>> apiHolidays = new ArrayList<>();
              
              // 휴일이 1개일 땐 Map, 여러 개일 땐 List로 들어오는 공공 API 특성 예외 처리
              if (itemObj instanceof List) {
                  apiHolidays = (List<Map<String, Object>>) itemObj;
              } else if (itemObj instanceof Map) {
                  apiHolidays.add((Map<String, Object>) itemObj);
              }
              
              holidayMapper.deleteByYear(year);
              
              // 3. 파싱한 공휴일 정보를 내 HolidayVO에 바인딩하여 오라클 DB에 차곡차곡 insert
              for (Map<String, Object> item : apiHolidays) {
                  String locdate = String.valueOf(item.get("locdate")); // 예: "20260505"
                  
                  HolidayVO holiday = new HolidayVO();
                  holiday.setYear(Integer.parseInt(locdate.substring(0, 4)));
                  holiday.setMonth(Integer.parseInt(locdate.substring(4, 6)));
                  holiday.setDay(Integer.parseInt(locdate.substring(6, 8)));
                  holiday.setHolidayName(String.valueOf(item.get("dateName")));
                  
                  // 미리 정의해 둔 Mapper의 insert 문을 실행합니다.
                  holidayMapper.insertHoliday(holiday); 
              }
              log.info("Successfully saved {} holidays for year {}", apiHolidays.size(), year);
          } else {
              log.warn("{}년도에 해당하는 공휴일 데이터가 API 서버에 존재하지 않습니다.", year);
          }
          
      } catch (Exception e) {
          log.error("{}년도 공휴일 수집 중 에러 발생", year, e);
      }
  }
  
  @Override
  public void fetchAndRange(int startYear, int endYear) {
      log.info("{}년부터 {}년까지 공휴일 일괄 수집 진행", startYear, endYear);
      for (int year = startYear; year <= endYear; year++) {
          fetchAndSave(year); // 위에 만들어둔 단일 연도 저장 로직을 루프 돌리며 반복 실행
      }
  }
}