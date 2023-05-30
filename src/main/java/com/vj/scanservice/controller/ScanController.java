package com.vj.scanservice.controller;

import com.vj.scanservice.dto.ScanRequest;
import com.vj.scanservice.dto.ScanResponse;
import com.vj.scanservice.service.ScanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;

@RestController
public class ScanController {

    @Autowired
    private ScanService scanService;

    @PostMapping("/startScan")
    @Operation(summary = "Starts a scan")
    public ScanResponse startScan(@RequestBody ScanRequest request) {
        return scanService.startScan(request);
    }

    //Get API to Get SPKS for an AIT

    //Get API to Get Historical Scan Status for an AIT for the SPKs selected

    //Get Scan Status of Specific Scan (Auto Referesh in Angular).

    //Get Detailed Status

}