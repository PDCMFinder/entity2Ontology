package org.cancerModels.entity2ontology.map.service;

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

    private final RulesSearcher rulesSearcher;
    private final OntologiesSearcher ontologiesSearcher;


    SuggestionsFinder(RulesSearcher rulesSearcher, OntologiesSearcher ontologiesSearcher) {
        this.rulesSearcher = rulesSearcher;
        this.ontologiesSearcher = ontologiesSearcher;
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
    List<Suggestion> findSuggestions(SourceEntity entity,
        String indexPath,
        int maxNumSuggestions,
        MappingConfiguration config) throws IOException {

        boolean done = false;

        List<Suggestion> suggestions = new ArrayList<>();


        // Check if there are enough exact matches in rules
        done = collectResults(
            suggestions, rulesSearcher.findExactMatchingRules(entity, indexPath, config), maxNumSuggestions);

        // Check if there are enough similar matches in rules
        if (!done) {
            done = collectResults(
                suggestions, rulesSearcher.findSimilarRules(entity, indexPath, config), maxNumSuggestions);
        }

        // Check if there are enough exact matches in ontologies
        if (!done) {
            done = collectResults(
                suggestions, ontologiesSearcher.findExactMatchingOntologies(entity, indexPath, config),
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
