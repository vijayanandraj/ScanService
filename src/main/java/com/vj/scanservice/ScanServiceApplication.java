package com.vj.scanservice;

import com.vj.scanservice.config.PasswordInitializer;
import com.vj.scanservice.dto.Result;
import com.vj.scanservice.dto.ScanRequest;
import com.vj.scanservice.service.ScanService;
import com.vj.scanservice.service.impl.ScanPipelineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.UUID;

@SpringBootApplication
public class ScanServiceApplication {

    @Autowired
    private ScanService scanPipelineService;

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(ScanServiceApplication.class);
        application.addInitializers(new PasswordInitializer());
        application.run(args);
    }


//    @Bean
//    ApplicationRunner setup(ConfigurableEnvironment environment) {
//        return args -> {
//            System.out.println("ApplicationRunner Setup...");
//            System.setProperty("spring.datasource.password", "Welcome123$");
//        };
//    }

//    @Override
//    public void run(String... args) throws Exception {
//
//        ScanRequest dotnetScanRequest = new ScanRequest();
//        dotnetScanRequest.setSpk("spkDotnet");
//        dotnetScanRequest.setTechnology(String.valueOf(ScanPipelineService.TechnologyType.DOTNET));
//
//        // Execute the pipeline and print the results
//        scanPipelineService.startScan(dotnetScanRequest);
//
//    }
}
