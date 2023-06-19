package com.vj.scanservice.service.impl;

import com.vj.scanservice.dto.Result;
import com.vj.scanservice.dto.ScanRequest;
import com.vj.scanservice.dto.ScanResponse;
import com.vj.scanservice.entity.ScanStatus;
import com.vj.scanservice.repository.ScanStatusRepository;
import com.vj.scanservice.service.ScanService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class ScanServiceImpl implements ScanService {

    @Autowired
    private ScanPipelineService scanPipelineService;

    @Autowired
    private ScanStatusService scanStatusService;

//    public ScanService(PipelineService pipelineService, ScanStatusRepository scanStatusRepository) {
//        this.pipelineService = pipelineService;
//        this.scanStatusRepository = scanStatusRepository;
//    }

    public ScanResponse startScan(ScanRequest request) {
        UUID requestId = UUID.randomUUID();
        MDC.put("requestId", requestId.toString());
        scanStatusService.updateScanStatus(requestId, "Initiated", "", request.getAitId(), request.getSpk());
        log.info("Service started from Scan Class");

        // Start the pipeline in the background
        try {
            scanPipelineService.startScanPipeline(requestId, request).whenComplete((result, exception) -> {
                if (exception != null) {
                    // If any task in the pipeline throws an exception, update the status to "Error"
                    scanStatusService.updateScanStatus(requestId, "Error", exception.getMessage(), request.getAitId(), request.getSpk());
                } else {
                    // If all tasks in the pipeline complete successfully, update the status to "Completed"
                    scanStatusService.updateScanStatus(requestId, "Completed", "", request.getAitId(), request.getSpk());
                }
            });
        }finally {
            MDC.clear();
        }
        return new ScanResponse(requestId, "Initiated", request.getAitId(), request.getSpk());
    }


}