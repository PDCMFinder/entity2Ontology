package org.cancerModels.entity2ontology.map.service;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.StoredFields;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.cancerModels.entity2ontology.map.model.MappingConfiguration;
import org.cancerModels.entity2ontology.map.model.SourceEntity;
import org.cancerModels.entity2ontology.map.model.Suggestion;
import org.cancerModels.entity2ontology.map.model.TargetEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class QueryResultProcessor {

    /**
     * Generates a non-null list of {@code Suggestion} based on the top documents in a search.
     *
     * @param topDocs           results of a search in Lucene
     * @param searcher          the {@code IndexSearcher} used in the search
     * @return a non-null list of {@code Suggestion} with the score Lucene gave to the results ('score' will be zero
     * as it is not calculated by Lucene)
     */
    public List<Suggestion> processQueryResponse(TopDocs topDocs, IndexSearcher searcher) throws IOException {
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
                int idx = field.name().lastIndexOf(".");
                String attributeName = field.name().substring(idx + 1);
                data.put(attributeName, document.get(field.name()));
            }
        }
        targetEntity.setData(data);
        return targetEntity;
    }
}
