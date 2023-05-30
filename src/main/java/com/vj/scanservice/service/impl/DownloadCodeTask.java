package com.vj.scanservice.service.impl;

import com.vj.scanservice.dto.Result;
import com.vj.scanservice.dto.ScanRequest;
import com.vj.scanservice.service.DotnetTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@Order(1)
public class DownloadCodeTask implements DotnetTask {

    @Override
    public CompletableFuture<Result> execute(UUID requestId, ScanRequest scanRequest, Result previousResult) {
        List<String> data = List.of("Dummy data from DownloadCodeTask");
        log.info("DownloadCodeTask executed with result: {}", data);
        return CompletableFuture.completedFuture(new Result(scanRequest.getSpk(), "Completed DownloadCodeTask", data));
    }
}
