package org.cancerModels.entity2ontology.map.service;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.StoredFields;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
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

    public List<Suggestion> processTopDocs(TopDocs topDocs, IndexSearcher searcher) throws IOException {
        List<Document> documents = new ArrayList<>();

        List<Suggestion> suggestions = new ArrayList<>();

        System.out.println("FOUND " + topDocs.totalHits + " documents");
        StoredFields storedFields = searcher.storedFields();
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            System.out.println("Score: " + scoreDoc.score);
            Document doc = storedFields.document(scoreDoc.doc);
            TargetEntity targetEntity = docToEntity(doc);
            System.out.println("TargetEntity: " + targetEntity);
            Suggestion suggestion = new Suggestion();
            suggestion.setType(targetEntity.getTargetType());
            suggestion.setScore(scoreDoc.score);
            suggestion.setTargetId(targetEntity.getId());
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
