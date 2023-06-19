package com.vj.scanservice.service.impl;

import com.vj.scanservice.dto.ScanRequest;
import com.vj.scanservice.dto.TaskResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class DownloadService {

    @Value("${artifact.download.directory}")
    private String downloadDirectory;


    @Async("customTaskExecutor")
    public CompletableFuture<TaskResult> downloadArtifact(TaskResult artifact, ScanRequest scanRequest) {
        try {
            String spk = scanRequest.getSpk();
            Path path = Paths.get(downloadDirectory, spk);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
            String downloadPath = artifact.getPath();
            String targetPath = Paths.get(downloadDirectory, spk, Paths.get(downloadPath).getFileName().toString()).toString();
            List<String> command = Arrays.asList("jfrog", "rt", "dl", downloadPath, targetPath, "--flat=true");
            log.info("Download Artifact Jfrog Command ==> {}", command);
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);  // Log the output
            }
            process.waitFor();
            String parent_file = Paths.get(downloadPath).getFileName().toString();
            TaskResult taskResult = new TaskResult(artifact.getFileKey(), targetPath, parent_file);
            return CompletableFuture.completedFuture(taskResult);
        } catch (Exception e) {
            throw new RuntimeException("Failed to download artifact: " + artifact, e);
        }
    }

}
