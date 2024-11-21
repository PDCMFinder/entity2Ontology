package org.cancer_models.entity2ontology.map.service;

import org.cancer_models.entity2ontology.map.model.MappingConfiguration;
import org.cancer_models.entity2ontology.map.model.MappingRequest;
import org.cancer_models.entity2ontology.common.utils.FileUtils;
import org.cancer_models.entity2ontology.common.utils.JsonConverter;
import org.cancer_models.entity2ontology.map.model.MappingResponse;

import java.io.File;
import java.io.IOException;

/**
 * Utility class for reading and writing mapping related objects to and from JSON files.
 */
public class MappingIO {

    // Suppress default constructor for non-instantiability
    private MappingIO() {
        throw new AssertionError();
    }

    /**
     * Reads a {@link MappingRequest} from a JSON file.
     *
     * @param jsonFilePath the JSON file path
     * @return the {@link MappingRequest}
     * @throws IOException if an error occurs while reading the file
     */
    public static MappingRequest readMappingRequest(String jsonFilePath) throws IOException {
        File jsonFile = FileUtils.getNonEmptyFileFromPath(jsonFilePath);
        return JsonConverter.fromJsonFile(jsonFile, MappingRequest.class);
    }

    /**
     * Writes a {@link MappingResponse} to a JSON file.
     *
     * @param response the {@link MappingResponse} to write
     * @param jsonFilePath the path of the file to write the JSON data to
     * @throws IOException if an error occurs while writing the file
     */
    public static void writeMappingResponse(MappingResponse response, String jsonFilePath) throws IOException {
        JsonConverter.toJsonFile(response, jsonFilePath);
    }

    /**
     * Reads a {@link MappingConfiguration} from a JSON file.
     *
     * @param jsonFilePath the JSON file path
     * @return the {@link MappingConfiguration}
     * @throws IOException if an error occurs while reading the file
     */
    public static MappingConfiguration readMappingConfiguration(String jsonFilePath) throws IOException {
        File jsonFile = FileUtils.getNonEmptyFileFromPath(jsonFilePath);
        return JsonConverter.fromJsonFile(jsonFile, MappingConfiguration.class);
    }

}
