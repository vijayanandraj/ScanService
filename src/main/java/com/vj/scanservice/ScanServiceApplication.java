package com.vj.scanservice;

import com.vj.scanservice.dto.Result;
import com.vj.scanservice.dto.ScanRequest;
import com.vj.scanservice.service.impl.ScanPipelineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.UUID;

@SpringBootApplication
public class ScanServiceApplication implements CommandLineRunner {

    @Autowired
    private ScanPipelineService scanPipelineService;

    public static void main(String[] args) {
        SpringApplication.run(ScanServiceApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        ScanRequest dotnetScanRequest = new ScanRequest();
        dotnetScanRequest.setSpk("spkDotnet");
        dotnetScanRequest.setTechnology(String.valueOf(ScanPipelineService.TechnologyType.DOTNET));

        // Execute the pipeline and print the results
        Result dotnetResult = scanPipelineService.startScanPipeline(UUID.randomUUID(), dotnetScanRequest).join();
        System.out.println("DotNet pipeline result: " + dotnetResult);

    }
}
