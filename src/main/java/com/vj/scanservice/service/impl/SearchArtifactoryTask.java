package com.vj.scanservice.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.vj.scanservice.dto.*;
import com.vj.scanservice.service.JavaTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class SearchArtifactoryTask implements JavaTask {


    @Value("${artifact.json.directory}")
    private String outputDirectory;

    Pattern pattern = Pattern.compile("(.+?)(-\\d+.*|_\\d+.*|\\.\\d+.*)\\.(ear|war|jar|zip)$");

    @Override
    public CompletableFuture<Result> execute(UUID requestId, ScanRequest scanRequest, Result previousResult) {

        String spk = scanRequest.getSpk();
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Construct the command
                ProcessBuilder processBuilder = new ProcessBuilder("jfrog", "rt", "s", "--recursive", "--sort-by-created", "sort-order", "desc", "libs-release-*/com/baml/" + scanRequest.getSpk());
                Process process = processBuilder.start();

                // Capture the output
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                StringBuilder output = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line);
                }

                // Wait for the process to finish
                process.waitFor();

                // Parse JSON
                ObjectMapper objectMapper = new ObjectMapper();
                List<Artifact> artifacts = objectMapper.readValue(output.toString(), new TypeReference<>() {});
                Map<String, Artifact> uniqueArtifacts = findUniqueArtifacts(artifacts);

                // Prepare output list
                List<ArtifactOutput> artifactOutputs = new ArrayList<>();
                List<TaskResult> searchResult = new ArrayList<>();
                for (Map.Entry<String, Artifact> entry : uniqueArtifacts.entrySet()){

                    String fileKey = entry.getKey();
                    Artifact artifact = entry.getValue();
                    ArtifactOutput artifactOutput = new ArtifactOutput();
                    artifactOutput.setAit(artifact.getProps().getAitNumber().get(0));
                    artifactOutput.setBuildCreated(artifact.getCreated());
                    artifactOutput.setDownloadPath(artifact.getPath());
                    TaskResult taskResult = new TaskResult(fileKey, artifact.getPath());
                    searchResult.add(taskResult);
                    artifactOutput.setRepoUrl(artifact.getProps().getScmLocation().get(0));
                    artifactOutput.setSpk(artifact.getProps().getSpk().get(0));
                    artifactOutputs.add(artifactOutput);
                }

                // Convert `artifactOutputs` to JSON string and write to file
                String outputJson = objectMapper.writeValueAsString(artifactOutputs);
                Path outputPath = Path.of(outputDirectory, scanRequest.getSpk() + ".json");
                Files.writeString(outputPath, outputJson);
                log.info("Search Result ==> {}", searchResult);
                return new Result(spk, "Completed SearchArtifactoryTask", searchResult);


            } catch (Exception e) {
                throw new RuntimeException("Failed SearchArtifactoryTask", e);

            }
        });
    }

    private Map<String, Artifact> findUniqueArtifacts(List<Artifact> artifacts) {
        Map<String, Artifact> uniqueArtifacts = new HashMap<>();

        for (Artifact artifact : artifacts) {
            String tileKey = getFileKeyFromPath(artifact.getPath());
            Artifact existingArtifact = uniqueArtifacts.get(tileKey);
            if (existingArtifact == null || artifact.getCreated().compareTo(existingArtifact.getCreated()) > 0) {
                uniqueArtifacts.put(tileKey, artifact);
            }
        }

        return uniqueArtifacts;
    }

    private String getFileKeyFromPath(String path) {
        Matcher matcher = pattern.matcher(path);
        if (matcher.find()) {
            return matcher.group(1) + "." + matcher.group(3);
        }
        return null;
    }

}
