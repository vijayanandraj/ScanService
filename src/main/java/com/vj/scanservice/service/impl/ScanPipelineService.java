package com.vj.scanservice.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.vj.scanservice.dto.Result;
import com.vj.scanservice.dto.ScanRequest;
import com.vj.scanservice.service.DotnetTask;
import com.vj.scanservice.service.JavaTask;
import com.vj.scanservice.service.Task;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class ScanPipelineService {

    public enum TechnologyType {
        JAVA,
        DOTNET;
    }

    private final Map<TechnologyType, List<? extends Task>> tasks;


    @Autowired
    public ScanPipelineService(List<JavaTask> javaTasks, List<DotnetTask> dotnetTasks) {
        tasks = new HashMap<>();
        tasks.put(TechnologyType.JAVA, javaTasks);
        tasks.put(TechnologyType.DOTNET, dotnetTasks);
    }

    public CompletableFuture<Result> startScanPipeline(UUID requestId, ScanRequest scanRequest) {
        TechnologyType techType = TechnologyType.valueOf(scanRequest.getTechnology());
        if (!tasks.containsKey(techType)) {
            throw new IllegalArgumentException("Unsupported technology: " + techType);
        }

        CompletableFuture<Result> future = CompletableFuture.completedFuture(new Result());
        for (Task task : tasks.get(techType)) {
            future = future.thenCompose(result -> task.execute(requestId, scanRequest, result));
        }
        return future;
    }

}

