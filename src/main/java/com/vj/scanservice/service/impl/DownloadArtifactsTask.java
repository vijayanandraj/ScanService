package com.vj.scanservice.service.impl;

import com.vj.scanservice.dto.Result;
import com.vj.scanservice.dto.ScanRequest;
import com.vj.scanservice.dto.TaskResult;
import com.vj.scanservice.service.JavaTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;

@Component
@Slf4j
public class DownloadArtifactsTask implements JavaTask {

    @Autowired
    private DownloadService downloadService;

    @Autowired
    private ScanStatusService scanStatusService;

    @Override
    public CompletableFuture<Result> execute(UUID requestId, ScanRequest scanRequest, Result previousResult) {
        try {
            // Set status to STARTED
            scanStatusService.updateScanStatus(requestId, "DOWNLOAD ARTIFACT STARTED", "DownloadArtifactsTask has started", scanRequest.getAitId(), scanRequest.getSpk());

            List<TaskResult> artifacts = previousResult.getData();
            List<CompletableFuture<TaskResult>> futures = new ArrayList<>();

            for (TaskResult artifact : artifacts) {
                futures.add(downloadService.downloadArtifact(artifact, scanRequest));
            }

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]))
                    .exceptionally(ex -> {
                        // Set status to ERROR
                        scanStatusService.updateScanStatus(requestId, "ERROR", "Error in DownloadArtifactsTask: " + ex.getMessage(), null, scanRequest.getSpk());
                        throw new RuntimeException("Failed DownloadArtifactsTask", ex);
                    });

            List<TaskResult> downloadedFiles = futures.stream()
                    .map(CompletableFuture::join)
                    .collect(Collectors.toList());

            // Set status to COMPLETED
            scanStatusService.updateScanStatus(requestId, "COMPLETED", "DownloadArtifactsTask has completed", null, scanRequest.getSpk());

            return CompletableFuture.completedFuture(new Result(scanRequest.getSpk(), "Completed DownloadArtifactsTask", downloadedFiles));

        } catch (Exception e) {
            // Set status to ERROR if anything goes wrong during setup
            scanStatusService.updateScanStatus(requestId, "ERROR", "Error in DownloadArtifactsTask: " + e.getMessage(), null, scanRequest.getSpk());
            throw new RuntimeException("Failed DownloadArtifactsTask", e);
        }
    }

}
