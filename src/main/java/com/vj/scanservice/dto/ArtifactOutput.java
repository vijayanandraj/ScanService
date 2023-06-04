package com.vj.scanservice.dto;

import lombok.Data;

@Data
public class ArtifactOutput {
    private String ait;
    private String buildCreated;
    private String downloadPath;
    private String repoUrl;
    private String spk;
    private String fileKey;
}
