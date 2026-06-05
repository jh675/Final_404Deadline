package com.example.demo.util.attach.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface AttachService {
    int insertAttach(List<AttachVO> attachVO);

    List<AttachVO> selectAttachList(String tableName, Long containerId);
    
    List<AttachVO> selectAttachListByContainer(String containerType, Long containerId);

    int deleteAttach(Long id);

	public List<AttachVO> saveAttach(MultipartFile[] attachments, String containerType, Long containerId,String tableName);

    void saveAndInsertAttachments(Long containerId, MultipartFile[] attachments, String tableName,
            String containerType);
    
    AttachVO selectAttach(Long id);
    
    void removeAttach(AttachVO attachVO) throws IOException;
    
    public boolean hasAttachmentFiles(MultipartFile[] attachments);
}
