package com.vj.scanservice.service.impl;

import com.vj.scanservice.dto.Result;
import com.vj.scanservice.dto.ScanDataCsv;
import com.vj.scanservice.dto.ScanRequest;
import com.vj.scanservice.dto.TaskResult;
import com.vj.scanservice.entity.ScanData;
import com.vj.scanservice.repository.ScanDataRepository;
import com.vj.scanservice.service.JavaTask;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class LoadMTAResultTask implements JavaTask {

    @Value("${mta.resultload.batchsize}")
    private Integer batchSize;

    @Autowired
    private ScanStatusService scanStatusService;

    @Autowired
    private ScanDataRepository scanDataRepository;

    @Override
    public CompletableFuture<Result> execute(UUID requestId, ScanRequest scanRequest, Result previousResult) {
        try {
            scanStatusService.updateScanStatus(requestId, "STARTED", "LoadMTAResultTask has started", scanRequest.getAitId(), scanRequest.getSpk());

            List<TaskResult> taskResults = previousResult.getData();
            List<ScanData> scanDataList = new ArrayList<>();

            for (TaskResult taskResult : taskResults) {
                File csvFile = new File(taskResult.getPath());
                CsvMapper mapper = new CsvMapper();
                CsvSchema schema = CsvSchema.emptySchema().withHeader();
                MappingIterator<ScanDataCsv> it = mapper.readerFor(ScanDataCsv.class).with(schema).readValues(csvFile);

                while (it.hasNext()) {
                    ScanDataCsv scanDataCsv = it.next();
                    ScanData scanData = ScanData.fromCsv(scanDataCsv, scanRequest, taskResult);
                    scanDataList.add(scanData);
                }
            }
            saveScanData(scanDataList);
            scanStatusService.updateScanStatus(requestId, "COMPLETED", "LoadMTAResultTask has completed", scanRequest.getAitId(), scanRequest.getSpk());
            TaskResult taskResult = new TaskResult();
            taskResult.setStatus(String.format("Scan Completed for AIT ==> %s  and SPK ==> %s",scanRequest.getAitId(), scanRequest.getSpk()));
            return CompletableFuture.completedFuture(new Result(scanRequest.getSpk(), "Completed LoadMTAResultTask", List.of(taskResult)));

        } catch (Exception e) {
            scanStatusService.updateScanStatus(requestId, "ERROR", "Error in LoadMTAResultTask: " + e.getMessage(), null, scanRequest.getSpk());
            throw new RuntimeException("Failed LoadMTAResultTask", e);
        }
    }

    @Transactional
    public void saveScanData(List<ScanData> scanDataList) {
        //Batch it for Avoid Out of Memory and Load DB
        List<ScanData> batch = new ArrayList<>(batchSize);
        for (ScanData scanData : scanDataList) {
            batch.add(scanData);
            if (batch.size() == batchSize) {
                scanDataRepository.saveAll(batch);
                batch.clear();
            }
        }
        if (!batch.isEmpty()) {
            scanDataRepository.saveAll(batch);
        }
    }
}
