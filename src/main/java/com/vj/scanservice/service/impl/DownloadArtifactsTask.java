package com.vj.scanservice.service.impl;

import com.vj.scanservice.dto.Result;
import com.vj.scanservice.dto.ScanRequest;
import com.vj.scanservice.service.JavaTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.nio.file.Paths;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;

@Component
@Slf4j
public class DownloadArtifactsTask implements JavaTask {

    @Value("${artifact.download.directory}")
    private String downloadDirectory;

    @Autowired
    private ScanStatusService scanStatusService;

    @Override
    public CompletableFuture<Result> execute(UUID requestId, ScanRequest scanRequest, Result previousResult) {
        try {
            // Set status to STARTED
            scanStatusService.updateScanStatus(requestId, "STARTED", "DownloadArtifactsTask has started", null, scanRequest.getSpk());

            List<String> artifacts = previousResult.getData();
            List<CompletableFuture<String>> futures = new ArrayList<>();

            for (String artifact : artifacts) {
                futures.add(downloadArtifact(artifact, scanRequest));
            }

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]))
                    .exceptionally(ex -> {
                        // Set status to ERROR
                        scanStatusService.updateScanStatus(requestId, "ERROR", "Error in DownloadArtifactsTask: " + ex.getMessage(), null, scanRequest.getSpk());
                        throw new RuntimeException("Failed DownloadArtifactsTask", ex);
                    });

            List<String> downloadedFiles = futures.stream()
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

    @Async("taskExecutor")
    public CompletableFuture<String> downloadArtifact(String artifact, ScanRequest scanRequest) {
        try {
            String downloadPath = artifact;
            ProcessBuilder processBuilder = new ProcessBuilder("jfrog", "rt", "dl", downloadPath, downloadDirectory);
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);  // Log the output
            }
            process.waitFor();

            return CompletableFuture.completedFuture(Paths.get(downloadDirectory, Paths.get(downloadPath).getFileName().toString()).toString());

        } catch (Exception e) {
            throw new RuntimeException("Failed to download artifact: " + artifact, e);
        }
    }
}
