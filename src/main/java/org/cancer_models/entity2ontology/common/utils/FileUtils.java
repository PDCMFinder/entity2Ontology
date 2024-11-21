package org.cancer_models.entity2ontology.common.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cancer_models.entity2ontology.index.service.Indexer;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

/**
 * Utility class to handle files.
 */
public class FileUtils {

    private static final Logger logger = LogManager.getLogger(FileUtils.class);

    // Suppress default constructor for non-instantiability
    private FileUtils() {
        throw new AssertionError();
    }

    public static boolean isFileEmpty(File file) {
        if (!file.exists()) {
            throw new IllegalArgumentException(
                file.getPath() + " (No such file or directory)");
        }
        return file.length() == 0;
    }

    public static File getNonEmptyFileFromPath(String filePath) {
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
            logger.error("Error while requesting: {}", url, e);
            Thread.currentThread().interrupt();
            throw new IOException("Request interrupted while fetching URL: " + url, e);
        }

        return response.body();
    }

    public static void deleteRecursively(File dir) {
        FileSystemUtils.deleteRecursively(dir);
    }
}

