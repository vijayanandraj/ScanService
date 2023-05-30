package com.vj.scanservice.repository;

import com.vj.scanservice.entity.ScanStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ScanStatusRepository extends JpaRepository<ScanStatus, UUID> {

    Optional<ScanStatus> findByRequestId(UUID requestId);

}
