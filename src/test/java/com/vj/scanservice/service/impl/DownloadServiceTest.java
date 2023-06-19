package com.vj.scanservice.service.impl;

import com.vj.scanservice.dto.ScanRequest;
import com.vj.scanservice.dto.TaskResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class DownloadServiceTest {
    @Mock
    private Environment env;
    @InjectMocks
    private DownloadService downloadService;
    private ScanRequest scanRequest;
    private TaskResult taskResult;

    @BeforeEach
    void setUp() {
        scanRequest = new ScanRequest();
        scanRequest.setSpk("spk");
        taskResult = new TaskResult();
        taskResult.setFileKey("fileKey");
        taskResult.setPath("path");
    }

    @Test
    void testDownloadArtifact() throws ExecutionException, InterruptedException {
        TaskResult result = downloadService.downloadArtifact(taskResult, scanRequest).get();
        assertEquals(taskResult.getFileKey(), result.getFileKey());
        assertEquals(taskResult.getParentFile(), result.getParentFile());
        assertEquals(taskResult.getPath(), result.getPath());
    }
}
