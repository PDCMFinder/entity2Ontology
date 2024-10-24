package org.cancerModels.entity2ontology.map.service;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.cancerModels.entity2ontology.index.service.AnalyzerProvider;
import org.cancerModels.entity2ontology.map.model.MappingConfiguration;
import org.cancerModels.entity2ontology.map.model.SourceEntity;
import org.cancerModels.entity2ontology.map.model.Suggestion;
import org.cancerModels.entity2ontology.map.model.TargetEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OntologiesSearcherTest {

    private static final String CONFIGURATION_FILE =
        "src/test/resources/mappingConfigurations/pdcmMappingConfiguration.json";

    private final QueryBuilder queryBuilder = new QueryBuilder();



//    private final QueryResultProcessor queryResultProcessor = new QueryResultProcessor();
    private final TemplateQueryProcessor templateQueryProcessor = new TemplateQueryProcessor();
    private final ScoreCalculator scoreCalculator = new ScoreCalculator();

    @Mock
    private Searcher searcherMock;

    @Mock
    private IndexSearcher indexSearcherMock;

    @Mock
    private QueryResultProcessor queryResultProcessorMock;


    @Mock
    private TopDocs topDocsMock;

    private OntologiesSearcher instance;

    @BeforeEach
    public void setup()
    {
        instance = new OntologiesSearcher(queryBuilder, templateQueryProcessor, searcherMock, queryResultProcessorMock, scoreCalculator);
    }

    @Test
    public void testFindExactMatchingOntologies_validData() throws IOException {

        SourceEntity sourceEntity = new SourceEntity();
        sourceEntity.setId("key_1");
        sourceEntity.setType("diagnosis");
        Map<String, String> data = new HashMap<>();
        data.put("SampleDiagnosis", "Lung Carcinoma");
        data.put("OriginTissue", "lung");
        data.put("TumorType", "Recurrent ");
        sourceEntity.setData(data);

        MappingConfiguration mappingConfiguration = MappingIO.readMappingConfiguration(CONFIGURATION_FILE);

        String indexPath = "indexPath";

        List<Suggestion> suggestionList = new ArrayList<>();

        TargetEntity targetEntity = new TargetEntity();
        targetEntity.setId("id_1");
        targetEntity.setEntityType("diagnosis");
        targetEntity.setTargetType("ontology");
        targetEntity.setLabel("Recurrent Lung Carcinoma");
        Map<String, Object> targetEntityEntityData = new HashMap<>();
        targetEntityEntityData.put("label", "Recurrent Lung Carcinoma");
        targetEntityEntityData.put(
            "synonyms",
            Arrays.asList(
                "Recurrent Lung Cancer",
                "Recurrent Unspecified Carcinoma of Lung",
                "Recurrent Unspecified Carcinoma of the Lung",
                "Recurrent Unspecified Lung Carcinoma"));
        targetEntity.setData(targetEntityEntityData);
        Suggestion suggestion = new Suggestion(targetEntity);

        suggestionList.add(suggestion);

        when(searcherMock.getIndexSearcher(indexPath)).thenReturn(indexSearcherMock);
        when(searcherMock.search(any(), eq("indexPath"))).thenReturn(topDocsMock);
        when(queryResultProcessorMock.processQueryResponse(topDocsMock, indexSearcherMock)).thenReturn(suggestionList);

        List<Suggestion> suggestions = instance.findExactMatchingOntologies(
            sourceEntity, "indexPath", mappingConfiguration);

        assertEquals(1, suggestions.size());
        assertEquals(100.0, suggestions.get(0).getScore());
    }

    }