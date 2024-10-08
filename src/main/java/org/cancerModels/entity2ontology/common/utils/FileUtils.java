package org.cancerModels.entity2ontology.common.utils;

import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.stream.Stream;

public class FileUtils {

    public static String getStringFromFile(String path) throws IOException {

        StringBuilder sb = new StringBuilder();

        try (Stream<String> stream = Files.lines(Paths.get(path))){

            Iterator<String> itr = stream.iterator();
            while (itr.hasNext()) {
                sb.append(itr.next());
            }
        } catch (NoSuchFileException nfe) {
            throw new IOException("File not found: " + path);
        }
        catch (Exception e){
            System.out.println(e);
            throw new IOException("Error reading file: " + path);
        }
        return sb.toString();
    }

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

