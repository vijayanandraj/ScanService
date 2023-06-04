package com.vj.scanservice.service.impl;

import com.vj.scanservice.dto.Result;
import com.vj.scanservice.dto.ScanRequest;
import com.vj.scanservice.dto.TaskResult;
import com.vj.scanservice.service.JavaTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Async;

@Component
@Slf4j
public class MTAScanTask implements JavaTask {

    @Value("${scan.output.directory}")
    private String scanOutputDirectory;

    @Autowired
    private ScanStatusService scanStatusService;

    @Qualifier("outputReadingExecutor")
    private TaskExecutor outputReadingExecutor;

    @Override
    public CompletableFuture<Result> execute(UUID requestId, ScanRequest scanRequest, Result previousResult) {
        try {
            // Set status to STARTED
            scanStatusService.updateScanStatus(requestId, "MTA Scan STARTED", "MTA Scan has started", scanRequest.getAitId(), scanRequest.getSpk());

            List<TaskResult> artifacts = previousResult.getData();
            List<CompletableFuture<TaskResult>> futures = new ArrayList<>();

            for (TaskResult artifact : artifacts) {
                futures.add(scanArtifact(artifact, scanRequest));
            }

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]))
                    .exceptionally(ex -> {
                        // Set status to ERROR
                        scanStatusService.updateScanStatus(requestId, "ERROR", "Error in MTAScanTask: " + ex.getMessage(), null, scanRequest.getSpk());
                        throw new RuntimeException("Failed MTAScanTask", ex);
                    });

            List<TaskResult> downloadedFiles = futures.stream()
                    .map(CompletableFuture::join)
                    .collect(Collectors.toList());

            // Set status to COMPLETED
            scanStatusService.updateScanStatus(requestId, "MTA Scan COMPLETED", "MTAScanTask has completed", null, scanRequest.getSpk());

            return CompletableFuture.completedFuture(new Result(scanRequest.getSpk(), "Completed MTAScanTask", downloadedFiles));

        } catch (Exception e) {
            // Set status to ERROR if anything goes wrong during setup
            scanStatusService.updateScanStatus(requestId, "ERROR", "Error in MTAScanTask: " + e.getMessage(), null, scanRequest.getSpk());
            throw new RuntimeException("Failed MTAScanTask", e);
        }
    }

    @Async("taskExecutor")
    public CompletableFuture<TaskResult> scanArtifact(TaskResult taskResult, ScanRequest scanRequest) {
        try {
            //Create a reports folder with SPK name, under base scan output directory.
            String spk = scanRequest.getSpk();

            Path spkFolder = Paths.get(scanOutputDirectory, spk);
            if (!Files.exists(spkFolder)) {
                Files.createDirectories(spkFolder);
            }
            //Target path to output reports.
            String artifactPath = taskResult.getPath();
            String artifactName = Paths.get(artifactPath).getFileName().toString();
            Path targetPath = Paths.get(spkFolder.toString(), artifactName);
            if(!Files.exists(targetPath)){
                Files.createDirectories(targetPath);
            }
            log.info("MTA Scan Target Report Path ==> {}", targetPath.toString());
            String tech = scanRequest.getTechnology();
            String scanTarget = "";
            if(tech.equals("WebSphere")){
                scanTarget = "openliberty";
            }else{
                scanTarget = "eap:7";
            }
            //String scanOutputDirectory = "";
            List<String> command = Arrays.asList(
                    "windup-cli",
                    "--batchMode",
                    "--input",
                    artifactPath,
                    "--output",
                    targetPath.toString(),
                    "--target",
                    scanTarget,
                    "--target",
                    "cloud-readiness",
                    "--exportCSV",
                    "--packages",
                    "com.boa",
                    "com.bofa",
                    "com.baml",
                    "com.bankofamerica");
            log.info("MTA Scan Command ==> {}", command);
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            Process process = processBuilder.start();
            //Create a Scan log file for each artifact.
            String filenameWithoutExtension = Paths.get(artifactPath).getFileName().toString().replaceAll("\\..+$", "");
            String logFilename = filenameWithoutExtension + "_scan.log";
            outputReadingExecutor.execute(() -> {
                try(BufferedWriter writer = new BufferedWriter(new FileWriter(logFilename))) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        writer.write(line);
                        writer.newLine();
                        //System.out.println(line);  // Log the output
                    }
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
         process.waitFor();

            // Return the CSV Path...
            String csvPath = Paths.get(targetPath.toString(), "AllIssues.csv").toString();
            log.info("CSV Ouput Path ==> {}", csvPath);
            TaskResult result = new TaskResult(taskResult.getFileKey(), csvPath, taskResult.getParentFile());
            return CompletableFuture.completedFuture(result);

        } catch (Exception e) {
            throw new RuntimeException("Failed to Scan artifact: " + taskResult.getPath(), e);
        }
    }
}
