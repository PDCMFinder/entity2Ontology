package org.cancerModels.entity2ontology.map.service;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.cancerModels.entity2ontology.common.utils.MapUtils;
import org.cancerModels.entity2ontology.map.model.MappingConfiguration;
import org.cancerModels.entity2ontology.map.model.SearchQueryItem;
import org.cancerModels.entity2ontology.map.model.SourceEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * The QueryBuilder class is responsible for constructing various Lucene {@link Query} objects
 * that are used in the entity-to-ontology mapping process.
 *
 * <p>
 * This class encapsulates the logic for creating different types of queries based on specific
 * mapping strategies, such as exact matches, fuzzy searches, and ontology lookups.
 * </p>
 */
@Component
public class QueryBuilder {

    // Rule exclusive fields in a document have this prefix.
    private static final String RULE_PREFIX = "rule.";

    // Rule exclusive fields in a document have this prefix.
    private static final String ONTOLOGY_PREFIX = "ontology.";

    // Name of the label field in indexed documents
    private static final String LABEL_FIELD = "label";

    // Name of the synonyms field in indexed documents
    private static final String SYNONYMS_FIELD = "synonyms";

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
        MappingConfiguration.ConfigurationPerType configuration = config.getConfigurationByEntityType(entity.getType());

        // With maxEdits as zero, each word in the sentence must match exactly
        int maxEdits = 0;

        for (MappingConfiguration.FieldConfiguration field : configuration.getFields()) {
            String fieldName = field.getName();
            String text = MapUtils.getValueOrThrow(entity.getData(), fieldName, "source entity data");

            Query query = buildPhraseQuery(RULE_PREFIX + fieldName, text, maxEdits, BooleanClause.Occur.MUST);
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
        MappingConfiguration.ConfigurationPerType configuration = config.getConfigurationByEntityType(entity.getType());

        // With maxEdits as zero, each word in the sentence must match exactly
        int maxEdits = 1;

        for (MappingConfiguration.FieldConfiguration field : configuration.getFields()) {
            String fieldName = field.getName();
            String text = MapUtils.getValueOrThrow(entity.getData(), fieldName, "source entity data");


            Query query = buildPhraseQuery(RULE_PREFIX + fieldName, text, maxEdits, BooleanClause.Occur.SHOULD);
            // The match must occur in all fields
            builder.add(query, BooleanClause.Occur.SHOULD);
        }

        return builder.build();
    }

    /**
     * Builds a Lucene query that performs an exact match search on ontology labels and synonyms
     * based on a list of {@link SearchQueryItem}. Each item contains the search term and its associated weight.
     * <p>
     * The method creates two subqueries: one for matching the labels and one for matching synonyms of ontologies.
     * Each subquery contains terms with corresponding weights (boost factors). The two subqueries are then combined
     * into a Boolean query where both label and synonym matches are considered.
     * </p>
     *
     * @param searchQueryItems the list of {@link SearchQueryItem} objects, each containing a field, value, and weight.
     *                         These items represent the terms and their respective weights (boosts) for querying.
     *                         The field is used to determine the specific label or synonym to search.
     * @return a {@link Query} object combining the label and synonym exact matches, where either a label match
     * or a synonym match will satisfy the query.
     * @throws IllegalArgumentException if {@code searchQueryItems} is null or empty.
     */
    public Query buildExactMatchOntologiesQuery(List<SearchQueryItem> searchQueryItems) {
        BooleanQuery.Builder labelQueryBuilder = new BooleanQuery.Builder();
        BooleanQuery.Builder synonymsQueryBuilder = new BooleanQuery.Builder();
        BooleanQuery.Builder booleanQueryBuilder = new BooleanQuery.Builder();

        for (SearchQueryItem searchQueryItem : searchQueryItems) {

            String value = searchQueryItem.getValue();
            float weight = (float) searchQueryItem.getWeight();

            Term labelTerm = new Term(ONTOLOGY_PREFIX + LABEL_FIELD, value);
            Query labelQuery = new BoostQuery(new TermQuery(labelTerm), weight);
            labelQueryBuilder.add(labelQuery, BooleanClause.Occur.MUST);

            Term synonymsTerm = new Term(ONTOLOGY_PREFIX + SYNONYMS_FIELD, value);
            Query synonymsQuery = new BoostQuery(new TermQuery(synonymsTerm), weight);
            synonymsQueryBuilder.add(synonymsQuery, BooleanClause.Occur.MUST);
        }
        booleanQueryBuilder.add(labelQueryBuilder.build(), BooleanClause.Occur.SHOULD);
        booleanQueryBuilder.add(synonymsQueryBuilder.build(), BooleanClause.Occur.SHOULD);

        return booleanQueryBuilder.build();
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
