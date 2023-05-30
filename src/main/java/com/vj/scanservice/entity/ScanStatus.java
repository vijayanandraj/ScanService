package com.vj.scanservice.entity;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "scan_status")
public class ScanStatus {
    @Id
    private UUID requestId;
    private String status;
    private String aitId;
    private String spk;
    private String notes;

    @OneToMany(mappedBy = "scanStatus", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CodeScanStatus> codeScanStatuses;

}