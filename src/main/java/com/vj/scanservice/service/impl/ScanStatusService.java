package com.vj.scanservice.service.impl;

import com.vj.scanservice.entity.ScanStatus;
import com.vj.scanservice.repository.ScanStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ScanStatusService {

    @Autowired
    private ScanStatusRepository scanStatusRepository;

    public void updateScanStatus(UUID requestId, String status, String notes, String aitId, String spk) {
        Optional<ScanStatus> optionalScanStatus = scanStatusRepository.findByRequestId(requestId);
        ScanStatus scanStatus;
        if(optionalScanStatus.isPresent()){
            scanStatus = optionalScanStatus.get();
        } else {
            scanStatus = new ScanStatus();
            scanStatus.setRequestId(requestId);
            scanStatus.setAitId(aitId);
            scanStatus.setSpk(spk);
        }

        scanStatus.setStatus(status);
        scanStatus.setNotes(notes);
        scanStatusRepository.save(scanStatus);
    }




}
