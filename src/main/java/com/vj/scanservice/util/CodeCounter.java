package com.vj.scanservice.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class CodeCounter {

    private static final Map<String, String> FILE_EXTENSIONS = new HashMap<>();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        FILE_EXTENSIONS.put("java", "Java");
        FILE_EXTENSIONS.put("js", "JavaScript");
        FILE_EXTENSIONS.put("cs", "C#");
        FILE_EXTENSIONS.put("py", "Python");
        FILE_EXTENSIONS.put("scala", "Scala");
        FILE_EXTENSIONS.put("css", "CSS");
        FILE_EXTENSIONS.put("html", "HTML");
        FILE_EXTENSIONS.put("htm", "HTML");
        FILE_EXTENSIONS.put("xml", "XML");
        FILE_EXTENSIONS.put("ts", "TypeScript");
        FILE_EXTENSIONS.put("jsx", "React (JSX)");
        FILE_EXTENSIONS.put("tsx", "TypeScript (TSX)");
        FILE_EXTENSIONS.put("md", "MD");
        FILE_EXTENSIONS.put("txt", "TXT");
        // Add more extensions if needed
    }

//    public static void main(String[] args) throws Exception {
////        if (args.length < 1) {
////            System.out.println("Please provide the directory path");
////            return;
////        }
//
//        //String dirPath = args[0];
//        String dirPath = "D:\\Vijay\\Working\\Projects\\IdeaProjects\\ScanService";
//        String jsonResponse = computeCodeStatistics(dirPath);
//        System.out.println(jsonResponse);
//    }

    public static void main(String[] args) throws Exception {
//        if (args.length < 1) {
//            System.out.println("Please provide the directory path");
//            return;
//        }

        //String dirPath = args[0];
        String dirPath = "D:\\Vijay\\Working\\Projects\\IdeaProjects\\ScanService\\src";
        List<String> excludedFolders = List.of(".mvn", ".idea", "target");  // Add folders you want to exclude here.
        String jsonResponse = computeCodeStatistics(dirPath, excludedFolders);
        System.out.println(jsonResponse);
    }

    public static String computeCodeStatistics(String dirPath, List<String> excludeFolders) throws Exception {
        Map<String, Integer> codeLinesCount = new ConcurrentHashMap<>();

        Files.walk(Paths.get(dirPath))
                .parallel()  // Enable parallel processing
                .filter(path -> !excludeFolders.contains(path.getFileName().toString()) && !path.getFileName().toString().startsWith("."))
                .filter(Files::isRegularFile)
                .collect(Collectors.toList())  // Collect into a list to avoid interference with parallel processing
                .forEach(file -> {
                    String fileName = file.getFileName().toString();
                    String fileExtension = getFileExtension(fileName);

                    if (FILE_EXTENSIONS.containsKey(fileExtension)) {
                        String language = FILE_EXTENSIONS.get(fileExtension);
                        int count = countLines(file);
                        codeLinesCount.merge(language, count, Integer::sum);  // Atomic update
                    }
                });

        int totalLines = codeLinesCount.values().stream().mapToInt(Integer::intValue).sum();

        Map<String, Object> response = new HashMap<>();
        Map<String, Double> percentages = new HashMap<>();
        for (Map.Entry<String, Integer> entry : codeLinesCount.entrySet()) {
            double percentage = Math.round(100.0 * entry.getValue() / totalLines * 100.0) / 100.0;
            percentages.put(entry.getKey(), percentage);
        }
        response.put("totalloc", totalLines);
        response.put("loc", codeLinesCount);
        response.put("percentage", percentages);

        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(response);
    }

    private static String getFileExtension(String fileName) {
        int lastIndex = fileName.lastIndexOf(".");
        if (lastIndex == -1) {
            return ""; // no extension
        }
        return fileName.substring(lastIndex + 1);
    }

    private static int countLines(java.nio.file.Path file) {
        try {
            return (int) Files.lines(file).count();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

}
