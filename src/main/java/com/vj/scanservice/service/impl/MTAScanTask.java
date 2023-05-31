package com.vj.scanservice.service.impl;

import com.vj.scanservice.dto.Result;
import com.vj.scanservice.dto.ScanRequest;
import com.vj.scanservice.service.JavaTask;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;


public class MTAScanTask implements JavaTask {

    private final ScanStatusService scanStatusService;

    public ScanCodeTask(ScanStatusService scanStatusService) {
        this.scanStatusService = scanStatusService;
    }

    @Override
    public CompletableFuture<Result> execute(UUID requestId, ScanRequest scanRequest, Result previousResult) {
        // Assuming result.getResult() for TaskB is List<String> (List of file paths)
        List<String> filePaths = (List<String>) result.getResult();

        ExecutorService executorService = Executors.newFixedThreadPool(5); // Adjust number as needed

        List<CompletableFuture<Void>> futures = filePaths.stream()
                .map(filePath -> CompletableFuture.runAsync(() -> {
                    // Update scan status to "started"
                    scanStatusService.updateScanStatus(requestId, "Started", "", aitId, spk);

                    try {
                        List<String> command = Arrays.asList(
                                "windup-cli",
                                "--batchMode",
                                "--input",
                                filePath,
                                "--output",
                                "C:\\input\\Temp\\gpbs\\reports",
                                "--target",
                                technology,  // use technology from ScanRequest
                                "--target",
                                "cloud-readiness",
                                "--exportCSV",
                                "--packages",
                                "com.boa",
                                "com.bofa",
                                "com.baml",
                                "com.bankofamerica");

                        ProcessBuilder processBuilder = new ProcessBuilder(command);
                        Process process = processBuilder.start();

                        // Wait for the process to finish
                        process.waitFor();

                    } catch (IOException | InterruptedException e) {
                        // Update scan status to "Error" and set notes to the exception message
                        scanStatusService.updateScanStatus(requestId, "Error", e.getMessage(), aitId, spk);
                    }

                    // Update scan status to "completed"
                    scanStatusService.updateScanStatus(requestId, "Completed", "", aitId, spk);

                }, executorService))
                .collect(Collectors.toList());

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }
}



}
