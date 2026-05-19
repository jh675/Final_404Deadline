package com.example.demo.project.ganttchart;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
public class GanttchartVO {
  private Integer id; 
  private Integer prj_Id; // 프로젝트코드
  private String subject; // 이슈명
  private String statusCd; // 이슈상태 
  private String priorityCd; // 우선순위 
  private String memId; // 담당자 
  
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private String createdCd; // 시작예정일  
  private String startDate; // 시작일  
  private String dueDate; // 종료일
  
  private double doneRatio; // 진척도 
}
