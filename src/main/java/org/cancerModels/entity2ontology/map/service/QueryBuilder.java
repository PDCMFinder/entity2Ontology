package org.cancerModels.entity2ontology.map.service;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.Query;
import org.cancerModels.entity2ontology.map.model.MappingConfiguration;
import org.cancerModels.entity2ontology.map.model.SourceEntity;
import org.springframework.stereotype.Component;

/**
 * The QueryBuilder class is responsible for constructing various Lucene {@link Query} objects
 * that are used in the entity-to-ontology mapping process.
 *
 * <p>
 * This class encapsulates the logic for creating different types of queries based on specific
 * mapping strategies, such as exact matches, fuzzy searches, and ontology lookups.
 * </p>
 *
 */
@Component
public class QueryBuilder {

    /**
     * Builds a Lucene {@link Query} for an exact match in already existing rules.
     *
     * <p>
     * This method creates a {@link BooleanQuery} that searches for documents rules that match
     * exactly the {@link SourceEntity}
     * </p>
     *
     * @param entity the {@link SourceEntity} to search
     * @param config information about the fields to use in the query
     * @return a {@link Query} that matches exact rules
     */
    public Query buildExactMatchRulesQuery(SourceEntity entity, MappingConfiguration config) {
        BooleanQuery.Builder builder = new BooleanQuery.Builder();
        MappingConfiguration.ConfigurationSection ruleConfiguration =
            config.getRuleConfigurationByEntityType(entity.getType());

        // With maxEdits as zero, each word in the sentence must match exactly
        int maxEdits = 0;

        for (var x : ruleConfiguration.getFields()) {
            String text = entity.getData().get(x.getName());

            Query query = buildPhraseQuery("rule."+x.getName(), text, maxEdits, BooleanClause.Occur.MUST);
            // The match must occur in all fields
            builder.add(query, BooleanClause.Occur.MUST);
        }

        return builder.build();
    }

    /**
     * Builds a Lucene {@link Query} for similar matches in already existing rules.
     *
     * <p>
     * This method creates a {@link BooleanQuery} that searches for documents rules that partially match
     * the {@link SourceEntity}
     * </p>
     *
     * @param entity the {@link SourceEntity} to search
     * @param config information about the fields to use in the query
     * @return a {@link Query} that matches similar rules
     */
    public Query buildSimilarMatchRulesQuery(SourceEntity entity, MappingConfiguration config) {
        BooleanQuery.Builder builder = new BooleanQuery.Builder();
        MappingConfiguration.ConfigurationSection ruleConfiguration =
            config.getRuleConfigurationByEntityType(entity.getType());

        // With maxEdits as zero, each word in the sentence must match exactly
        int maxEdits = 1;

        for (var x : ruleConfiguration.getFields()) {
            String text = entity.getData().get(x.getName());

            Query query = buildPhraseQuery("rule."+x.getName(), text, maxEdits, BooleanClause.Occur.SHOULD);
            // The match must occur in all fields
            builder.add(query, BooleanClause.Occur.SHOULD);
        }

        return builder.build();
    }

    private Query buildPhraseQuery(String field, String phrase, int maxEdits, BooleanClause.Occur occur) {
        String[] words = phrase.split(" ");
        BooleanQuery.Builder builder = new BooleanQuery.Builder();
        for (String word : words) {
            FuzzyQuery fuzzyQuery = new FuzzyQuery(new Term(field, word), maxEdits);
            builder.add(fuzzyQuery, occur); // Allow fuzzy matching on each word
        }
        BooleanQuery booleanQuery = builder.build();
        return booleanQuery;
    }
}
