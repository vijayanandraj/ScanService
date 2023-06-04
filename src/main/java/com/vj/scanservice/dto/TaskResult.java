package com.vj.scanservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskResult {

    private String fileKey;
    private String path;
    private String parentFile;
    private String status;

    public TaskResult(String fileKey, String path) {
        this.fileKey = fileKey;
        this.path = path;
    }

    public TaskResult(String fileKey, String path, String parentFile) {
        this.fileKey = fileKey;
        this.path = path;
        this.parentFile = parentFile;
    }


}
