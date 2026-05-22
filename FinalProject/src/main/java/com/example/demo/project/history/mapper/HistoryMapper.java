package com.example.demo.project.history.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.example.demo.project.history.service.HistoryDetailVO;
import com.example.demo.project.history.service.HistoryListCriteria;
import com.example.demo.project.history.service.HistoryVO;

@Mapper
public interface HistoryMapper {

    List<HistoryVO> selectHistoryList(HistoryListCriteria criteria);

    List<HistoryDetailVO> selectHistoryDetailList(@Param("historyId") Long historyId);
}
