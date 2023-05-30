package com.vj.scanservice.service;

import com.vj.scanservice.dto.ScanRequest;
import com.vj.scanservice.dto.ScanResponse;

public interface ScanService {

    ScanResponse startScan(ScanRequest request);
}
