package com.vj.scanservice.service.impl;

import com.vj.scanservice.dto.Result;
import com.vj.scanservice.dto.ScanRequest;
import com.vj.scanservice.dto.TaskResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DownloadArtifactsTaskTest {
    @Mock
    private DownloadService downloadService;
    @Mock
    private ScanStatusService scanStatusService;
    @InjectMocks
    private DownloadArtifactsTask downloadArtifactsTask;
    private UUID requestId;
    private ScanRequest scanRequest;
    private Result previousResult;

    @BeforeEach
    void setUp() {
        requestId = UUID.randomUUID();
        scanRequest = new ScanRequest();
        scanRequest.setSpk("spk");
        previousResult = new Result();
        TaskResult taskResult = new TaskResult();
        taskResult.setFileKey("fileKey");
        taskResult.setPath("path");
        previousResult.setData(Arrays.asList(taskResult));
    }

    @Test
    void testExecute() throws ExecutionException, InterruptedException {
        TaskResult taskResult = new TaskResult();
        when(downloadService.downloadArtifact(any(), any())).thenReturn(CompletableFuture.completedFuture(taskResult));
        Result result = downloadArtifactsTask.execute(requestId, scanRequest, previousResult).get();

        verify(scanStatusService).updateScanStatus(eq(requestId), eq("DOWNLOAD ARTIFACT STARTED"), eq("DownloadArtifactsTask has started"), eq(scanRequest.getAitId()), eq(scanRequest.getSpk()));
        verify(scanStatusService).updateScanStatus(eq(requestId), eq("COMPLETED"), eq("DownloadArtifactsTask has completed"), eq(null), eq(scanRequest.getSpk()));

        assertEquals(Collections.singletonList(taskResult), result.getData());
    }

}
