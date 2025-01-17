package org.cancer_models.entity2ontology.map.service;

import org.cancer_models.entity2ontology.common.model.TargetEntityDataFields;
import org.cancer_models.entity2ontology.exceptions.MappingException;
import org.cancer_models.entity2ontology.map.model.MappingConfiguration;
import org.cancer_models.entity2ontology.map.model.SourceEntity;
import org.cancer_models.entity2ontology.map.model.Suggestion;
import org.cancer_models.entity2ontology.common.model.TargetEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultSuggestionsFinderTest {

    @Mock
    private RulesSearcher rulesSearcher;

    @Mock
    private OntologiesSearcher ontologiesSearcher;

    private DefaultSuggestionsFinder instance;

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
        instance = new DefaultSuggestionsFinder(rulesSearcher, ontologiesSearcher);
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

        TargetEntity targetEntity1 = new TargetEntity(
            "key_1", "diagnosis", "rule", new TargetEntityDataFields(), null, null);
        suggestionExactRule = new Suggestion(targetEntity1);
        suggestionExactRule.setScore(100.0);

        TargetEntity targetEntity2 = new TargetEntity(
            "key_2", "diagnosis", "rule", new TargetEntityDataFields(), null, null);
        suggestionSimilarRule = new Suggestion(targetEntity2);
        suggestionSimilarRule.setScore(90.0);

        TargetEntity targetEntity3 = new TargetEntity(
            "key_3", "diagnosis", "ontology", new TargetEntityDataFields(), null, null);
        suggestionExactOntology = new Suggestion(targetEntity3);
        suggestionExactOntology.setScore(100.0);

        TargetEntity targetEntity4 = new TargetEntity(
            "key_4", "diagnosis", "ontology", new TargetEntityDataFields(), null, null);
        suggestionSimilarOntology = new Suggestion(targetEntity4);
        suggestionSimilarOntology.setScore(80.0);
    }

    @Test
    void testFindSuggestions_foundOneExactRuleMatch() throws MappingException {
        when(rulesSearcher.findExactMatchingRules(sourceEntity, INDEX_PATH, conf))
            .thenReturn(List.of(suggestionExactRule));

        List<Suggestion> suggestions = instance.findSuggestions(sourceEntity, INDEX_PATH, 10, conf);

        assertEquals(1, suggestions.size(), "We expect 1 suggestion");
        Suggestion suggestion = suggestions.getFirst();
        assertEquals("key_1", suggestion.getTargetEntity().id(), "Unexpected suggestion");
    }

    @Test
    void testFindSuggestions_foundOneSimilarRuleMatch() throws MappingException {
        when(rulesSearcher.findExactMatchingRules(sourceEntity, INDEX_PATH, conf))
            .thenReturn(List.of(suggestionSimilarRule));

        List<Suggestion> suggestions = instance.findSuggestions(sourceEntity, INDEX_PATH, 10, conf);

        assertEquals(1, suggestions.size(), "We expect 1 suggestion");
        Suggestion suggestion = suggestions.getFirst();
        assertEquals("key_2", suggestion.getTargetEntity().id(), "Unexpected suggestion");
    }

    @Test
    void testFindSuggestions_foundOneExactOntologyMatch() throws MappingException {
        when(rulesSearcher.findExactMatchingRules(sourceEntity, INDEX_PATH, conf))
            .thenReturn(List.of(suggestionExactOntology));

        List<Suggestion> suggestions = instance.findSuggestions(sourceEntity, INDEX_PATH, 10, conf);

        assertEquals(1, suggestions.size(), "We expect 1 suggestion");
        Suggestion suggestion = suggestions.getFirst();
        assertEquals("key_3", suggestion.getTargetEntity().id(), "Unexpected suggestion");
    }

    @Test
    void testFindSuggestions_foundOneSimilarOntologyMatch() throws MappingException {
        when(rulesSearcher.findExactMatchingRules(sourceEntity, INDEX_PATH, conf))
            .thenReturn(List.of(suggestionSimilarOntology));

        List<Suggestion> suggestions = instance.findSuggestions(sourceEntity, INDEX_PATH, 10, conf);

        assertEquals(1, suggestions.size(), "We expect 1 suggestion");
        Suggestion suggestion = suggestions.getFirst();
        assertEquals("key_4", suggestion.getTargetEntity().id(), "Unexpected suggestion");
    }

    @Test
    void testFindSuggestions_foundOneSuggestionEachStep() throws MappingException {
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