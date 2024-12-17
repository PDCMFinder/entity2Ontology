package org.cancer_models.entity2ontology.map.service;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.StoredFields;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.cancer_models.entity2ontology.common.mappers.TargetEntityDocumentMapper;
import org.cancer_models.entity2ontology.map.model.Suggestion;
import org.cancer_models.entity2ontology.common.model.TargetEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

/**
 * A component responsible for executing Lucene queries and returning a list of {@link Suggestion}.
 */
@Component
class QueryProcessor {

    private final Searcher searcher;


    public QueryProcessor(Searcher searcher) {
        this.searcher = searcher;
    }

    /**
     * Executes a Lucene query on the specified index and returns the matching suggestions.
     *
     * @param query     The Lucene query to execute.
     * @param indexPath The path to the Lucene index.
     * @return A list of suggestions based on the query results.
     * @throws IOException If an error occurs while searching the index.
     */
    public List<Suggestion> executeQuery(Query query, String indexPath) throws IOException {
        Objects.requireNonNull(query, "query cannot be null");
        Objects.requireNonNull(indexPath, "indexPath cannot be null");
        TopDocs topDocs = searcher.search(query, indexPath);
        return processQueryResponse(topDocs, searcher.getIndexSearcher(indexPath));
    }

    /**
     * Generates a non-null list of {@code Suggestion} based on the top documents in a search.
     *
     * @param topDocs           results of a search in Lucene
     * @param searcher          the {@code IndexSearcher} used in the search
     * @return a non-null list of {@code Suggestion} with the score Lucene gave to the results ('score' will be zero
     * as it is not calculated by Lucene)
     */
    private List<Suggestion> processQueryResponse(TopDocs topDocs, IndexSearcher searcher) throws IOException {
        List<Suggestion> suggestions = new ArrayList<>();

        StoredFields storedFields = searcher.storedFields();
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            Document doc = storedFields.document(scoreDoc.doc);
            TargetEntity targetEntity = TargetEntityDocumentMapper.documentToTargetEntity(doc);
            Suggestion suggestion = new Suggestion(targetEntity);
            suggestion.setRawScore(scoreDoc.score);
            suggestion.setTermLabel(targetEntity.label());
            suggestion.setTermUrl(targetEntity.url());
            suggestions.add(suggestion);
        }
        return suggestions;
    }
}
