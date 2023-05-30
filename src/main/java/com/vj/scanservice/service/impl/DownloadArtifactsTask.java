package com.vj.scanservice.service.impl;


import com.vj.scanservice.dto.ArtifactOutput;
import com.vj.scanservice.dto.Result;
import com.vj.scanservice.dto.ScanRequest;
import com.vj.scanservice.service.Task;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.nio.file.Paths;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class DownloadArtifactsTask implements Task {

    @Value("${artifact.download.directory}")
    private String downloadDirectory;

    @Override
    public CompletableFuture<Result> execute(UUID requestId, ScanRequest scanRequest, Result previousResult) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<String> downloadedFiles = new ArrayList<>();
                List<String> artifacts = previousResult.getData();
                for (String artifact : artifacts) {
                    String downloadPath = artifact;
                    // Execute the jfrog command to download the artifact
                    ProcessBuilder processBuilder = new ProcessBuilder("jfrog", "rt", "dl", downloadPath, downloadDirectory);
                    Process process = processBuilder.start();

                    // Capture the output
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);  // Log the output
                    }

                    // Wait for the process to finish
                    process.waitFor();

                    String downloadedFile = Paths.get(downloadDirectory, Paths.get(downloadPath).getFileName().toString()).toString();
                    downloadedFiles.add(downloadedFile);
                }

                return new Result(scanRequest.getSpk(), "Completed DownloadArtifactsTask", downloadedFiles);
            } catch (Exception e) {
                throw new RuntimeException("Failed DownloadArtifactsTask", e);
            }
        });
    }
}
