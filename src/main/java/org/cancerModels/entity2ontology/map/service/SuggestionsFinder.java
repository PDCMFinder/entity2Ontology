package org.cancerModels.entity2ontology.map.service;

import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.cancerModels.entity2ontology.map.model.MappingConfiguration;
import org.cancerModels.entity2ontology.map.model.SourceEntity;
import org.cancerModels.entity2ontology.map.model.Suggestion;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Core logic of the mapping process. This class is in charge of applying the search strategy
 * to map entities with has the following steps (stop when getting the required number of suggestions):
 * - Search already existing rules (exact match)
 * - Search similar rules (fuzzy match)
 * - Search ontologies  (exact match: label or synonyms)
 * - Search similar ontologies (fuzzy match: label or synonyms)
 */
@Component
class SuggestionsFinder {

    // This allows us to create the different Lucene queries
    private final QueryBuilder queryBuilder;

    // This allows us to execute Lucene queries
    private Searcher searcher;

    // This allows us to process the result of Lucene queries
    private final QueryResultProcessor queryResultProcessor;


    SuggestionsFinder(QueryBuilder queryBuilder, Searcher searcher, QueryResultProcessor queryResultProcessor) {
        this.queryBuilder = queryBuilder;
        this.searcher = searcher;
        this.queryResultProcessor = queryResultProcessor;
    }

    /**
     * Generates a list of {@code maxNumSuggestions} suggestions for a given entity.
     *
     * @param entity            the source entity to be mapped
     * @param indexPath         the path of the index to use for the mapping
     * @param maxNumSuggestions the max number of suggestions to get
     * @param config            information about how to build the queries to find matches
     * @return a list of suggestions for the source entity
     */
    List<Suggestion> findSuggestions(
        SourceEntity entity,
        String indexPath,
        int maxNumSuggestions,
        MappingConfiguration config) throws IOException {
        System.out.println("Inside findSuggestions");

        boolean done;

        List<Suggestion> suggestions = new ArrayList<>();

        // Check if there are enough exact matches in rules
        done = collectResults(
            suggestions, findExactMatchingRules(entity, indexPath, maxNumSuggestions, config), maxNumSuggestions);

        // Check if there are enough similar matches in rules
        if (!done) {
            done = collectResults(
                suggestions, findSimilarRules(entity, indexPath, maxNumSuggestions, config), maxNumSuggestions);
        }

        // Check if there are enough exact matches in ontologies
        if (!done) {
            done = collectResults(
                suggestions, findExactMatchingOntologies(entity, indexPath, maxNumSuggestions, config),
                maxNumSuggestions);
        }

        // Check if there are enough similar matches in ontologies
        if (!done) {
            collectResults(
                suggestions, findSimilarOntologies(entity, indexPath, maxNumSuggestions, config),
                maxNumSuggestions);
        }
        System.out.println("***");
        System.out.printf("Wanted %d Suggestions found %d%n", maxNumSuggestions, suggestions.size());
        prettyPrintSuggestions(suggestions);
        return suggestions;
    }

    /**
     * Adds obtained suggestions with a specific method to the total of found suggestions.
     * Stops if the wanted number of results is reached, and returns true in order that we don't keep searching
     * for more matches
     */
    private boolean collectResults(List<Suggestion> all, List<Suggestion> newResults, int wanted) {
        boolean done = false;
        int found = all.size();

        if (!newResults.isEmpty()) {
            for (Suggestion suggestion : newResults) {
                // Only add new suggestions
                if (!all.contains(suggestion)) {
                    all.add(suggestion);
                    found++;
                    done = found == wanted;
                    if (done) {
                        break;
                    }
                }
            }
        }
        return done;
    }

    private List<Suggestion> executeQuery(Query query, String indexPath) throws IOException {
        TopDocs topDocs = searcher.search(query, indexPath);
        return queryResultProcessor.processTopDocs(topDocs, searcher.getIndexSearcher(indexPath));
    }

    private List<Suggestion> findExactMatchingRules(
        SourceEntity entity, String indexPath, int maxNumSuggestions, MappingConfiguration config) throws IOException {
        System.out.println("************************findExactMatchingRules*************************");
        System.out.println("entity: " + entity);
        System.out.println("indexPath: " + indexPath);
        System.out.println("maxNumSuggestions: " + maxNumSuggestions);
        System.out.println("config: " + config);
        Query query = queryBuilder.buildExactMatchRulesQuery(entity, config);
        System.out.println("Query: " + query);
        List<Suggestion> suggestions = executeQuery(query, indexPath);

        // Assign a `score` of 100 as results are perfect matches
        suggestions.forEach(suggestion -> suggestion.setScore(100));

        System.out.println("findExactMatchingRules==> " + suggestions.size());
        System.out.println("=======================================================================");
        return suggestions;
    }

    private List<Suggestion> findSimilarRules(
        SourceEntity entity, String indexPath, int maxNumSuggestions, MappingConfiguration config) throws IOException {
        //suggestions.add(new Suggestion());
        Query query = queryBuilder.buildSimilarMatchRulesQuery(entity, config);
        List<Suggestion> suggestions = executeQuery(query, indexPath);
        System.out.println("findSimilarRules==> " + suggestions.size());
        return suggestions;
    }

    private List<Suggestion> findExactMatchingOntologies(
        SourceEntity entity, String indexPath, int maxNumSuggestions, MappingConfiguration config) {
        List<Suggestion> suggestions = new ArrayList<>();
        //suggestions.add(new Suggestion());
        System.out.println("findExactMatchingOntologies==> " + suggestions.size());
        return suggestions;
    }

    private List<Suggestion> findSimilarOntologies(
        SourceEntity entity, String indexPath, int maxNumSuggestions, MappingConfiguration config) {
        List<Suggestion> suggestions = new ArrayList<>();
//        suggestions.add(new Suggestion());
//        suggestions.add(new Suggestion());
        System.out.println("findSimilarOntologies==> " + suggestions.size());
        return suggestions;
    }

    public void prettyPrintSuggestions(List<Suggestion> suggestions) {
        System.out.println("************************Suggestions***********************");
        System.out.println("Total suggestions: " + suggestions.size());
        for (Suggestion suggestion : suggestions) {
            System.out.println("suggestion::: {" + suggestion.getTermLabel() + "} raw score: " + suggestion.getRawScore());
            System.out.println("with detail " + suggestion);
        }
    }
}
