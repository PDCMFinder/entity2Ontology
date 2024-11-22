package org.cancer_models.entity2ontology.map.service;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.StoredFields;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.cancer_models.entity2ontology.common.utils.GeneralUtils;
import org.cancer_models.entity2ontology.map.model.Suggestion;
import org.cancer_models.entity2ontology.common.model.TargetEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

/**
 * A component responsible for executing Lucene queries and returning a list of {@link Suggestion}.
 */
@Component
public class QueryProcessor {

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
        if (query == null) {
            throw new IllegalArgumentException("query cannot be null");
        }
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
            TargetEntity targetEntity = docToEntity(doc);
            Suggestion suggestion = new Suggestion(targetEntity);
            suggestion.setRawScore(scoreDoc.score);
            suggestion.setTermLabel(targetEntity.getLabel());
            suggestion.setTermUrl(targetEntity.getUrl());
            suggestions.add(suggestion);
        }
        return suggestions;
    }

    private TargetEntity docToEntity(Document document) {
        TargetEntity targetEntity = new TargetEntity();
        targetEntity.setId(document.get("id"));
        targetEntity.setEntityType(document.get("entityType"));
        targetEntity.setTargetType(document.get("targetType"));
        targetEntity.setLabel(document.get("label"));
        targetEntity.setUrl(document.get("url"));
        // Now we can add the data
        Map<String, Object> data = new HashMap<>();
        for (IndexableField field : document.getFields()) {
            if (field.name().contains(".")) {
                String fieldValue = field.stringValue();
                int idx = field.name().lastIndexOf(".");
                String attributeName = field.name().substring(idx + 1);
                // If there is already a value for the attribute, then we are dealing with a list.
                // This happens for instance with synonyms, which are several values
                if (data.containsKey(attributeName)) {
                    Object currValue = data.get(attributeName);
                    if (currValue instanceof List) {
                        List<String> currList = GeneralUtils.castList(currValue, String.class);
                        currList.add(currValue.toString());
                    } else {
                        data.put(attributeName, Arrays.asList(currValue.toString(), fieldValue));
                    }
                } else {
                    data.put(attributeName, fieldValue);
                }
            }
        }
        targetEntity.setData(data);
        return targetEntity;
    }
}
