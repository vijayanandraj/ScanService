package com.vj.scanservice.entity;
import javax.persistence.*;
import java.util.UUID;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "code_scan_status")
public class CodeScanStatus {

    @Id
    private UUID id;
    private String artifact;
    private String status;


    @ManyToOne
    @JoinColumn(name = "scan_status_id", nullable = false)
    private ScanStatus scanStatus;
}
