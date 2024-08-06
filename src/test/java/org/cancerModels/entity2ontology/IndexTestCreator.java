package org.cancerModels.entity2ontology;

import org.cancerModels.entity2ontology.common.utils.FileUtils;
import org.cancerModels.entity2ontology.common.utils.JsonConverter;
import org.cancerModels.entity2ontology.index.service.Indexer;

import java.io.File;
import java.io.IOException;

/**
 * Utility class to create an index (for testing purposes) from a JSON file with target entities
 */
public class IndexTestCreator {
    private static final String INPUT_DATA_DIR = "src/test/resources/manualIndexCreation/";
        private static final String OUTPUT_DATA_DIR = "src/test/output/";
    private static final Indexer indexer = new Indexer();

    public static String createIndex(String inputFile) throws IOException {
        String jsonFilePath = INPUT_DATA_DIR + inputFile;
        File jsonFile = FileUtils.getNonEmptyFileFromPath(jsonFilePath);
        IndexInputDataStructure input = JsonConverter.fromJsonFile(jsonFile, IndexInputDataStructure.class);
        System.out.println(input.name);
        System.out.println(input.targetEntities);
        String indexLocation = OUTPUT_DATA_DIR + input.name;
        indexer.indexEntities(input.targetEntities, indexLocation);
        return indexLocation;
    }
}
