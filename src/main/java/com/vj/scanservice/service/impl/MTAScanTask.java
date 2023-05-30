package com.vj.scanservice.service.impl;

import com.vj.scanservice.dto.Result;
import com.vj.scanservice.dto.ScanRequest;
import com.vj.scanservice.service.JavaTask;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MTAScanTask implements JavaTask {
    @Override
    public CompletableFuture<Result> execute(UUID requestId, ScanRequest scanRequest, Result previousResult) {
        return null;
    }
}
