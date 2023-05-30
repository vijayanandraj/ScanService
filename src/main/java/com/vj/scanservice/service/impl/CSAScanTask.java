package com.vj.scanservice.service.impl;

import com.vj.scanservice.dto.Result;
import com.vj.scanservice.dto.ScanRequest;
import com.vj.scanservice.service.DotnetTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

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
        log.info("Previous Task Result ==> {}", previousResult.getData());
        List<String> data = List.of("Dummy data from CSAScanTask");
        log.info("CSAScanTask executed with result: {}", data);
        return CompletableFuture.completedFuture(new Result(scanRequest.getSpk(), "Completed CSAScanTask", data));
    }
}
