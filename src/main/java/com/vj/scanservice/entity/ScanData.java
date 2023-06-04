package com.vj.scanservice.entity;

import com.vj.scanservice.dto.ScanDataCsv;
import com.vj.scanservice.dto.ScanRequest;
import com.vj.scanservice.dto.TaskResult;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "scan_data")
public class ScanData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ait_id")
    private Integer aitId;

    @Column(name = "spk")
    private String spk;

    @Column(name = "file_key")
    private String fileKey;

    @Column(name = "rule_id")
    private String ruleId;

    @Column(name = "issue_category")
    private String issueCategory;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    @Lob
    private String description;

    @Column(name = "links")
    @Lob
    private String links;

    @Column(name = "application")
    private String application;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "line")
    private String line;

    @Column(name = "story_points")
    private String storyPoints;

    @Column(name = "parent_application")
    private String parentApplication;

    @Column(name = "created_dt")
    private LocalDateTime createdDt;

    @Column(name = "modified_dt")
    private LocalDateTime modifiedDt;

    public static ScanData fromCsv(ScanDataCsv csv, ScanRequest scanRequest, TaskResult taskResult) {
        ScanData scanData = new ScanData();

        scanData.setAitId(Integer.getInteger(scanRequest.getAitId()));
        scanData.setSpk(scanRequest.getSpk());
        scanData.setFileKey(taskResult.getFileKey());
        scanData.setRuleId(csv.getRuleId());
        scanData.setIssueCategory(csv.getIssueCategory());
        scanData.setTitle(csv.getTitle());
        scanData.setDescription(csv.getDescription());
        scanData.setLinks(csv.getLinks());
        scanData.setApplication(csv.getApplication());
        scanData.setFileName(csv.getFileName());
        scanData.setFilePath(csv.getFilePath());
        scanData.setLine(csv.getLine());
        scanData.setStoryPoints(csv.getStoryPoints());
        scanData.setParentApplication(taskResult.getParentFile());

        return scanData;
    }

    // getters and setters
}
