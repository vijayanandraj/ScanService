package com.vj.scanservice.repository;

import com.vj.scanservice.entity.CodeScanStatus;
import com.vj.scanservice.entity.ScanStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CodeScanStatusRepository extends JpaRepository<CodeScanStatus, UUID> {

    List<CodeScanStatus> findByScanStatus(ScanStatus scanStatus);
}
