package org.cancer_models.entity2ontology.index.service;

import org.cancer_models.entity2ontology.common.utils.FileUtils;
import org.cancer_models.entity2ontology.common.utils.JsonConverter;
import org.cancer_models.entity2ontology.index.model.IndexingRequest;

import java.io.File;
import java.io.IOException;

/**
 * Utility class for reading  {@link IndexingRequest} from JSON files.
 */
public class IndexingRequestReader {

    /**
     * Reads a {@link IndexingRequest} from a JSON file.
     *
     * @param jsonFilePath the JSON file path
     * @return the {@link IndexingRequest}
     * @throws IOException if an error occurs while reading the file
     */
    public static IndexingRequest readIndexingRequest(String jsonFilePath) throws IOException {
        File jsonFile = FileUtils.getNonEmptyFileFromPath(jsonFilePath);
        return JsonConverter.fromJsonFile(jsonFile, IndexingRequest.class);
    }

}
