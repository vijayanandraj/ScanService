package com.vj.scanservice.service;

import com.vj.scanservice.dto.ArtifactOutput;
import com.vj.scanservice.dto.Result;
import com.vj.scanservice.dto.ScanRequest;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface Task {
    CompletableFuture<Result> execute(UUID requestId, ScanRequest scanRequest, Result previousResult);
}

