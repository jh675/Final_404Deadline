package com.example.demo.project.history.service.impl;

import java.util.List;
import org.springframework.stereotype.Service;
import com.example.demo.project.history.mapper.HistoryMapper;
import com.example.demo.project.history.service.HistoryListCriteria;
import com.example.demo.project.history.service.HistoryService;
import com.example.demo.project.history.service.HistoryVO;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HistoryServiceImpl implements HistoryService {

    private final HistoryMapper historyMapper;

    @Override
    public List<HistoryVO> selectHistoryList(HistoryListCriteria criteria) {
        if (criteria == null || criteria.getPrjId() == null) {
            return List.of();
        }
        List<HistoryVO> list = historyMapper.selectHistoryList(criteria);
        return list != null ? list : List.of();
    }
}
