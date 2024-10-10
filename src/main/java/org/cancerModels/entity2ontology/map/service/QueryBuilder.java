package org.cancerModels.entity2ontology.map.service;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.cancerModels.entity2ontology.map.model.MappingConfiguration;
import org.cancerModels.entity2ontology.map.model.SearchQueryItem;
import org.cancerModels.entity2ontology.map.model.SourceEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            String text = tryGetTextByFieldName(fieldName, entity);

            Query query = buildPhraseQuery(RULE_PREFIX + fieldName, text, maxEdits, BooleanClause.Occur.MUST);
            // The match must occur in all fields
            builder.add(query, BooleanClause.Occur.MUST);
        }

        return builder.build();
    }

    private String tryGetTextByFieldName(String fieldName, SourceEntity entity) {
        String text;
        if (entity.getData().containsKey(fieldName)) {
            text = entity.getData().get(fieldName);
        } else {
            throw new IllegalArgumentException("Field '" + fieldName + "' not found in source entity " + entity);
        }
        return text;
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
            String text = tryGetTextByFieldName(fieldName, entity);

            Query query = buildPhraseQuery(RULE_PREFIX + fieldName, text, maxEdits, BooleanClause.Occur.SHOULD);
            // The match must occur in all fields
            builder.add(query, BooleanClause.Occur.SHOULD);
        }

        return builder.build();
    }

    /**
     * Builds a Lucene {@link Query} for an exact match in indexed ontologies.
     *
     * <p>
     * This method creates a {@link BooleanQuery} that searches for ontology documents that match
     * exactly the {@link SourceEntity}
     * </p>
     *
     * @param entity the {@link SourceEntity} to search
     * @param config information about the fields to use in the query
     * @return a {@link Query} that matches exact ontologies
     */
    public Query buildExactMatchOntologiesQuery(SourceEntity entity, MappingConfiguration config) {
        BooleanQuery.Builder builder = new BooleanQuery.Builder();
        MappingConfiguration.ConfigurationPerType configuration = config.getConfigurationByEntityType(entity.getType());

        // With maxEdits as zero, each word in the sentence must match exactly
        int maxEdits = 0;

        System.out.println("configuration templates onto" + configuration.getOntologyTemplates());

        for (MappingConfiguration.FieldConfiguration field : configuration.getFields()) {
            String fieldName = field.getName();
            String text = tryGetTextByFieldName(fieldName, entity);

//            Query query = buildPhraseQuery("ontology."+fieldName, text, maxEdits, BooleanClause.Occur.MUST);
//            // The match must occur in all fields
//            builder.add(query, BooleanClause.Occur.MUST);
        }
        return null;
//        return builder.build();
    }

    /**
     * Builds a Lucene {@link Query} to perform an exact match search across ontology labels and synonyms
     * based on a templated query string, values, and weights.
     * <p>
     * The method takes a template containing keys in the form `${key}` and replaces each key with corresponding
     * values from the provided map. It constructs a {@link BooleanQuery} that searches both labels and synonyms
     * in the ontology, with terms boosted according to the weights provided for each key.
     *
     * <p>
     * The method creates two separate {@link BooleanQuery.Builder} objects for labels and synonyms, ensuring that:
     * <ul>
     *   <li>A MUST clause is added for each term in both labels and synonyms queries.</li>
     *   <li>Both the labels and synonyms queries are combined using a SHOULD clause, making the final query match
     *       documents that contain the values either in labels or synonyms.</li>
     * </ul>
     *
     * @param template A string containing placeholders in the form `${key}`, where `key` corresponds to terms
     *                 that will be searched in the ontology fields.
     * @param values A map where the key corresponds to a placeholder name (without `${}`), and the value is the
     *               string to be used for exact matching in the query.
     * @param weights A map where the key corresponds to the placeholder name and the value is a {@link Double}
     *                representing the weight (boost factor) to be applied to the query for that term.
     * @return A {@link Query} object representing the combined query for labels and synonyms with boosted terms.
     * @throws IllegalArgumentException if the template is null or if any value or weight for a key is missing.
     */
    public Query buildExactMatchOntologiesQueryFromTemplate(
        List<String> keys, Map<String, String> values, Map<String, Double> weights) {
        BooleanQuery.Builder labelQueryBuilder = new BooleanQuery.Builder();
        BooleanQuery.Builder synonymsQueryBuilder = new BooleanQuery.Builder();
        BooleanQuery.Builder booleanQueryBuilder = new BooleanQuery.Builder();


        for (String key : keys) {
            System.out.println("processing key: " + key);
            System.out.println("value: " + values.get(key));
            System.out.println("weight: " + weights.get(key));
            String value = values.get(key);
            float weight = weights.get(key).floatValue();

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

    public Query buildExactMatchOntologiesQuery( List<SearchQueryItem> searchQueryItems) {
        BooleanQuery.Builder labelQueryBuilder = new BooleanQuery.Builder();
        BooleanQuery.Builder synonymsQueryBuilder = new BooleanQuery.Builder();
        BooleanQuery.Builder booleanQueryBuilder = new BooleanQuery.Builder();

        for (SearchQueryItem searchQueryItem : searchQueryItems) {
            System.out.println("processing item: " + searchQueryItem);

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

//    private Query buildOntologyQuery(String value, float weight, BooleanClause.Occur occur) {
//        BooleanQuery.Builder builder = new BooleanQuery.Builder();
//    }
//
//    private Query buildOntologyQueryByField(String field, String value, float weight, BooleanClause.Occur occur) {
//        BooleanQuery.Builder builder = new BooleanQuery.Builder();
//    }

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

    /**
     * Extracts keys from a templated string.
     * <p>
     * The method looks for placeholders in the form of `${key}`, where `key` is any sequence of letters, and returns
     * a list of all the unique keys found in the template.
     *
     * @param template The string containing placeholders in the form `${key}`.
     * @return A list of keys (the content inside `${}`) found in the template.
     */
    public List<String> extractKeys(String template) {
        List<String> keys = new ArrayList<>();
        // Regular expression to match placeholders in the form ${key}
        Pattern pattern = Pattern.compile("\\$\\{([a-zA-Z][a-zA-Z0-9]*)\\}");
        Matcher matcher = pattern.matcher(template);

        // Find all matches and add the key (without ${}) to the list
        while (matcher.find()) {
            keys.add(matcher.group(1));  // group(1) captures the content inside ${}
        }

        return keys;
    }
}
