package com.vj.scanservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ScanDataCsv {

    @JsonProperty("Rule Id")
    private String ruleId;

    @JsonProperty("Issue")
    private String issue;

    @JsonProperty("Category")
    private String issueCategory;

    @JsonProperty("Title")
    private String title;

    @JsonProperty("Description")
    private String description;

    @JsonProperty("Links")
    private String links;

    @JsonProperty("Application")
    private String application;

    @JsonProperty("File Name")
    private String fileName;

    @JsonProperty("File Path")
    private String filePath;

    @JsonProperty("Line")
    private String line;

    @JsonProperty("Story points")
    private String storyPoints;

    @JsonProperty("Parent Application")
    private String parentApplication;

}

