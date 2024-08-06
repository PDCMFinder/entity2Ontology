package org.cancerModels.entity2ontology.map.service;

import org.cancerModels.entity2ontology.IndexTestCreator;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class GeneralTest {
    @Test
    public void test() throws IOException {
        String indexLocation = IndexTestCreator.createIndex("input_data_small_diagnosis_index/data.json");
        System.out.println("Created index: " + indexLocation);
    }
}
