package com.example.demo.util.attach.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface AttachService {
    int insertAttach(List<AttachVO> attachVO);

    List<AttachVO> selectAttachList(String tableName, Long containerId);

    int deleteAttach(Long id);

    List<AttachVO> saveAttach(MultipartFile[] attachments,String tableName);

    void saveAndInsertAttachments(Long containerId, MultipartFile[] attachments, String tableName,
            String containerType);
    
    AttachVO selectAttach(Long id);
    
}
