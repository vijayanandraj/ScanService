package com.vj.scanservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class Artifact {
    @JsonProperty("path")
    private String path;

    @JsonProperty("created")
    private String created;

    @JsonProperty("props")
    private Properties props;

    @Data
    public static class Properties {
        @JsonProperty("ait.number")
        private List<String> aitNumber;

        @JsonProperty("scm:location")
        private List<String> scmLocation;

        @JsonProperty("spk")
        private List<String> spk;
    }
}
