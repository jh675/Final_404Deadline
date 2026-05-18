package com.example.demo.util.attach.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.demo.util.attach.service.AttachVO;


@Mapper
public interface AttachMapper {
    
    int insertAttach(AttachVO attachVO);

    List<AttachVO> selectAttachList(@Param("tableName") String tableName, @Param("containerId") Long containerId);

    int deleteAttach(@Param("id") Long id);
    
    AttachVO selectAttach(Long id);



}
