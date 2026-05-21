package com.example.demo.project.history.service;

import java.util.List;

public interface HistoryService {
  List<HistoryVO> selectHistoryList(HistoryListCriteria criteria);
}
