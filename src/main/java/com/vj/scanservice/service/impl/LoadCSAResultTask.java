package com.vj.scanservice.service.impl;

import com.vj.scanservice.dto.Result;
import com.vj.scanservice.dto.ScanRequest;
import com.vj.scanservice.dto.TaskResult;
import com.vj.scanservice.service.DotnetTask;
import com.vj.scanservice.util.aop.HowManySeconds;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@Order(3)
public class LoadCSAResultTask implements DotnetTask {

    @Override
    @HowManySeconds
    public CompletableFuture<Result> execute(UUID requestId, ScanRequest scanRequest, Result previousResult) {
        log.info("Previous Task Result ==> {}", previousResult.getData());
        //List<String> data = List.of("Dummy data from LoadCSAResultTask");
        List<TaskResult> data = new ArrayList<>();
        TaskResult csaScanTask = new TaskResult("Load CSA Result Task - Dummy File Key", "Load CSA Result  Task - Path", "Load CSA Result  Task - Parent File");
        data.add(csaScanTask);
        log.info("LoadCSAResultTask executed with result: {}", data);
        return CompletableFuture.completedFuture(new Result(scanRequest.getSpk(), "Completed LoadCSAResultTask", data));
    }
}
