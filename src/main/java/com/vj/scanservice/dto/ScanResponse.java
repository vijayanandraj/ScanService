package com.vj.scanservice.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ScanResponse {
    private UUID requestId;
    private String status;
    private String aitId;
    private String spk;
}
