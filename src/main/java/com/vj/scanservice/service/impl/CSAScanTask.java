package com.vj.scanservice.service.impl;

import com.vj.scanservice.dto.Result;
import com.vj.scanservice.dto.ScanRequest;
import com.vj.scanservice.dto.TaskResult;
import com.vj.scanservice.service.DotnetTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@Order(2)
public class CSAScanTask implements DotnetTask {

    @Async("customTaskExecutor")
    @Override
    public CompletableFuture<Result> execute(UUID requestId, ScanRequest scanRequest, Result previousResult) {
        log.info("Executing CSAScanTask in thread: {}", Thread.currentThread().getName());
        log.info("Previous Task Result ==> {}", previousResult.getData());
        //List<String> data = List.of("Dummy data from CSAScanTask");
        List<TaskResult> data = new ArrayList<>();
        TaskResult csaScanTask = new TaskResult("CSA Scan Task - Dummy File Key", "CSA Scan Task - Path", "CSA Scan Task - Parent File");
        data.add(csaScanTask);
        log.info("CSAScanTask executed with result: {}", data);
        return CompletableFuture.completedFuture(new Result(scanRequest.getSpk(), "Completed CSAScanTask", data));
    }
}
