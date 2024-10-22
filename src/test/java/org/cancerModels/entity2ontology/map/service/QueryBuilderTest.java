package org.cancerModels.entity2ontology.map.service;

import org.apache.lucene.search.Query;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class QueryBuilderTest {

    private final QueryBuilder instance = new QueryBuilder();

//    @Test
//    void shouldFailWhenBuildExactMatchOntologiesQueryFromTemplateWithNullValues() {
//
//        // Given a null template
//        String template = null;
//
//        // When we try to build a query that matches exactly an ontology
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
//        {
//            instance.buildExactMatchOntologiesQueryFromTemplate(template, new HashMap<>(), new HashMap<>());
//        });
//        // Then we get an IllegalArgumentException
//        assertEquals("Template cannot be null", exception.getMessage());
//
//    }

//    @Test
//    void shouldFailWhenBuildExactMatchOntologiesQueryFromTemplateWithNotMatchingValuesAndWeights() {
//
//        // Given values map keys not matching weights map keys
//        String template = "";
//        Map<String, String> values = new HashMap<>();
//        values.put("key1", "value1");
//        Map<String, Double> weights = new HashMap<>();
//        weights.put("key2", 0.0);
//
//        // When we try to build a query that matches exactly an ontology
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
//        {
//            instance.buildExactMatchOntologiesQueryFromTemplate(template, values, weights);
//        });
//        // Then we get an IllegalArgumentException
//        assertEquals("Values and weights maps must have the same keys", exception.getMessage());
//
//    }

//    @Test
//    void shouldReturnExpectedQueryWhenBuildExactMatchOntologiesQueryFromTemplateWithValidData() {
//
//        // Given values map keys not matching weights map keys
//        List<String> keys = Arrays.asList("key1", "key2");
//        Map<String, String> values = new HashMap<>();
//        values.put("key1", "value1");
//        values.put("key2", "value2");
//        Map<String, Double> weights = new HashMap<>();
//        weights.put("key1", 1.0);
//        weights.put("key2", 1.5);
//
//        // When we try to build a query that matches exactly an ontology
//        Query query = instance.buildExactMatchOntologiesQueryFromTemplate(keys, values, weights);
//
//        // Then we get the expected query
//        String expected = "(+(ontology.label:value1)^1.0 +(ontology.label:value2)^1.5) (+(ontology.synonyms:value1)^1.0 +(ontology.synonyms:value2)^1.5)";
//        assertEquals(expected, query.toString());
//
//    }
}