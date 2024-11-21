package org.cancer_models.entity2ontology.common.utils;

import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class FileUtils {

    public static boolean isFileEmpty(File file) {
        if (!file.exists()) {
            throw new IllegalArgumentException(
                file.getPath() + " (No such file or directory)");
        }
        return file.length() == 0;
    }

    public static File getNonEmptyFileFromPath(String filePath) throws IOException {
        File file = new File(filePath);
        if (isFileEmpty(file)) {
            throw new IllegalArgumentException("File is empty: " + file.getPath());
        }
        return file;
    }

    public static String getStringFromUrl(String url) throws IOException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .build();

        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return response.body();
    }

    public static void deleteRecursively(File dir) {
        FileSystemUtils.deleteRecursively(dir);
    }
}

