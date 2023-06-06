package com.vj.scanservice;

import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ZipUtil {
    public static void main(String[] args) {
        Path path = Paths.get("path_to_your_zip_files");
        try {
            boolean found = Files.walk(path)
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".zip"))
                    .map(ZipUtil::processZipFile)
                    .reduce(Boolean::logicalOr)
                    .orElse(false);
            System.out.println("Java artifacts found: " + found);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean processZipFile(Path zipFilePath) {
        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(zipFilePath))) {
            return processZipInputStream(zis);
        } catch (IOException e) {
            System.out.println("Error processing zip file " + zipFilePath);
            e.printStackTrace();
        }
        return false;
    }

    private static boolean processZipInputStream(ZipInputStream zis) throws IOException {
        ZipEntry zipEntry;
        while ((zipEntry = zis.getNextEntry()) != null) {
            if (zipEntry.getName().endsWith(".jar") || zipEntry.getName().endsWith(".war") || zipEntry.getName().endsWith(".ear")) {
                System.out.println("Found Java artifact " + zipEntry.getName());
                return true;
            } else if (zipEntry.getName().endsWith(".zip")) {
                try (ZipInputStream nestedZis = new ZipInputStream(zis)) {
                    if (processZipInputStream(nestedZis)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
