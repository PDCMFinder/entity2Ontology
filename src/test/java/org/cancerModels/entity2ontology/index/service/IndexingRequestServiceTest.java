package org.cancerModels.entity2ontology.index.service;

import org.cancerModels.entity2ontology.common.utils.JsonConverter;
import org.cancerModels.entity2ontology.index.model.IndexingResponse;
import org.cancerModels.entity2ontology.map.model.TargetEntity;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

class IndexingRequestServiceTest {

    private static final String DATA_DIR = "src/test/resources/indexingRequest/";

    private final IndexingRequestService instance = new IndexingRequestService();

    @Test
    public void shouldProcessIndexingRequestByFile() throws IOException {
        // Given a file that has right data
        String fileToRead = DATA_DIR + "indexingRequestTreatment.json";

        // When we process an indexing request by passing a file
        IndexingResponse response = instance.processRequest(fileToRead);
        System.out.println(response);

        // Then we get the expected data

    }

    @Test
    public void dummy() throws IOException {
        TargetEntity targetEntity1 = new TargetEntity();
        targetEntity1.setId("id1");
        targetEntity1.setTargetType("Rule");
        Map<String, Object> data1 = new HashMap<>();
        data1.put("treatmentName", "t1");
        targetEntity1.setData(data1);
        System.out.println(targetEntity1);

        TargetEntity targetEntity2 = new TargetEntity();
        targetEntity2.setId("id1");
        targetEntity2.setTargetType("Ontology");
        Map<String, Object> data2 = new HashMap<>();
        data2.put("synonyms", Arrays.asList("s1", "s2"));
        targetEntity2.setData(data2);
        System.out.println(targetEntity2);
        JsonConverter.toJsonFile(targetEntity1, "json1.json");
        JsonConverter.toJsonFile(targetEntity2, "json2.json");
        var o1 = JsonConverter.fromJsonFile(new File("json1.json"), TargetEntity.class);
        var o2 = JsonConverter.fromJsonFile(new File("json2.json"), TargetEntity.class);
        System.out.println("again");
        System.out.println(o1);
        System.out.println(o2);
    }
}
