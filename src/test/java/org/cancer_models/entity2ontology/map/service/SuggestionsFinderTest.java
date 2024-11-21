package org.cancer_models.entity2ontology.map.service;

import org.cancer_models.entity2ontology.map.model.MappingConfiguration;
import org.cancer_models.entity2ontology.map.model.SourceEntity;
import org.cancer_models.entity2ontology.map.model.Suggestion;
import org.cancer_models.entity2ontology.map.model.TargetEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SuggestionsFinderTest {

    @Mock
    private RulesSearcher rulesSearcher;

    @Mock
    private OntologiesSearcher ontologiesSearcher;

    private SuggestionsFinder instance;

    private SourceEntity sourceEntity;

    // A suggestion (mocked) as the result of a perfect match with a rule
    private Suggestion suggestionExactRule;

    // A suggestion (mocked) as the result of a non-perfect match with a rule
    private Suggestion suggestionSimilarRule;

    // A suggestion (mocked) as the result of a perfect match with an ontology
    private Suggestion suggestionExactOntology;

    // A suggestion (mocked) as the result of a non-perfect match with an ontology
    private Suggestion suggestionSimilarOntology;

    private static final MappingConfiguration conf = new MappingConfiguration();

    private static final String INDEX_PATH = "dummyIndexPath";

    @BeforeEach
    void setup()
    {
        instance = new SuggestionsFinder(rulesSearcher, ontologiesSearcher);
        initSourceEntity();
        initSuggestions();
    }

    private void initSourceEntity() {
        sourceEntity = new SourceEntity();
        sourceEntity.setId("key_1");
        sourceEntity.setType("diagnosis");
        Map<String, String> data = new HashMap<>();
        data.put("SampleDiagnosis", "Fusion Negative Alveolar Rhabdomyosarcoma");
        data.put("OriginTissue", "orbit");
        data.put("TumorType", "primary");
        sourceEntity.setData(data);
    }

    private void initSuggestions() {
        TargetEntity targetEntity1 = new TargetEntity();
        targetEntity1.setId("key_1");
        targetEntity1.setTargetType("rule");
        targetEntity1.setData(new HashMap<>());
        suggestionExactRule = new Suggestion(targetEntity1);
        suggestionExactRule.setScore(100.0);

        TargetEntity targetEntity2 = new TargetEntity();
        targetEntity2.setId("key_2");
        targetEntity2.setTargetType("rule");
        targetEntity2.setData(new HashMap<>());
        suggestionSimilarRule = new Suggestion(targetEntity2);
        suggestionSimilarRule.setScore(90.0);

        TargetEntity targetEntity3 = new TargetEntity();
        targetEntity3.setId("key_3");
        targetEntity3.setTargetType("ontology");
        targetEntity3.setData(new HashMap<>());
        suggestionExactOntology = new Suggestion(targetEntity3);
        suggestionExactOntology.setScore(100.0);

        TargetEntity targetEntity4 = new TargetEntity();
        targetEntity4.setId("key_4");
        targetEntity4.setTargetType("ontology");
        targetEntity4.setData(new HashMap<>());
        suggestionSimilarOntology = new Suggestion(targetEntity4);
        suggestionSimilarOntology.setScore(80.0);
    }

    @Test
    void testFindSuggestions_foundOneExactRuleMatch() throws IOException {
        when(rulesSearcher.findExactMatchingRules(sourceEntity, INDEX_PATH, conf))
            .thenReturn(List.of(suggestionExactRule));

        List<Suggestion> suggestions = instance.findSuggestions(sourceEntity, INDEX_PATH, 10, conf);

        assertEquals(1, suggestions.size(), "We expect 1 suggestion");
        Suggestion suggestion = suggestions.getFirst();
        assertEquals("key_1", suggestion.getTargetEntity().getId(), "Unexpected suggestion");
    }

    @Test
    void testFindSuggestions_foundOneSimilarRuleMatch() throws IOException {
        when(rulesSearcher.findExactMatchingRules(sourceEntity, INDEX_PATH, conf))
            .thenReturn(List.of(suggestionSimilarRule));

        List<Suggestion> suggestions = instance.findSuggestions(sourceEntity, INDEX_PATH, 10, conf);

        assertEquals(1, suggestions.size(), "We expect 1 suggestion");
        Suggestion suggestion = suggestions.getFirst();
        assertEquals("key_2", suggestion.getTargetEntity().getId(), "Unexpected suggestion");
    }

    @Test
    void testFindSuggestions_foundOneExactOntologyMatch() throws IOException {
        when(rulesSearcher.findExactMatchingRules(sourceEntity, INDEX_PATH, conf))
            .thenReturn(List.of(suggestionExactOntology));

        List<Suggestion> suggestions = instance.findSuggestions(sourceEntity, INDEX_PATH, 10, conf);

        assertEquals(1, suggestions.size(), "We expect 1 suggestion");
        Suggestion suggestion = suggestions.getFirst();
        assertEquals("key_3", suggestion.getTargetEntity().getId(), "Unexpected suggestion");
    }

    @Test
    void testFindSuggestions_foundOneSimilarOntologyMatch() throws IOException {
        when(rulesSearcher.findExactMatchingRules(sourceEntity, INDEX_PATH, conf))
            .thenReturn(List.of(suggestionSimilarOntology));

        List<Suggestion> suggestions = instance.findSuggestions(sourceEntity, INDEX_PATH, 10, conf);

        assertEquals(1, suggestions.size(), "We expect 1 suggestion");
        Suggestion suggestion = suggestions.getFirst();
        assertEquals("key_4", suggestion.getTargetEntity().getId(), "Unexpected suggestion");
    }

    @Test
    void testFindSuggestions_foundOneSuggestionEachStep() throws IOException {
        when(rulesSearcher.findExactMatchingRules(sourceEntity, INDEX_PATH, conf))
            .thenReturn(List.of(
                suggestionExactRule, suggestionSimilarRule, suggestionExactOntology, suggestionSimilarOntology));

        List<Suggestion> suggestions = instance.findSuggestions(sourceEntity, INDEX_PATH, 10, conf);

        assertEquals(4, suggestions.size(), "We expect 4 suggestions");
        assertTrue(isSortedDescending(suggestions), "The suggestions should be sorted (desc) by `score`");
    }

    private boolean isSortedDescending(List<Suggestion> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            if (list.get(i).getScore() < list.get(i + 1).getScore()) {
                return false;
            }
        }
        return true;
    }
}