package org.cancerModels.entity2ontology.map.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.StoredFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.cancerModels.entity2ontology.AppConfig;
import org.cancerModels.entity2ontology.IndexTestCreator;
import org.cancerModels.entity2ontology.common.utils.FileUtils;
import org.cancerModels.entity2ontology.index.service.AnalyzerProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = AppConfig.class)
public class GeneralTest {

    @Autowired
    private AnalyzerProvider analyzerProvider;
    @Test
    public void test() throws IOException, ParseException {
        String indexLocation = IndexTestCreator.createIndex("input_data_small_diagnosis_index/data.json");
        System.out.println("Created index: " + indexLocation);
        Searcher searcher = new Searcher(analyzerProvider);

        QueryParser parser = new QueryParser("label", analyzerProvider.getAnalyzer());
//        Query query = parser.parse("\"fusion negative rhabdomyosarcoma\"");
        Query query = parser.parse("\"Fusionn Negative Alveolar Rhabdomyosarcoma\"");


        String sentence = "fusionn negatibe Alveolarr rhabdomyosarcoma";
        String[] words = sentence.split(" ");

        BooleanQuery.Builder builder = new BooleanQuery.Builder();
        for (String word : words) {
            FuzzyQuery fuzzyQuery = new FuzzyQuery(new Term("label", word), 2);
            builder.add(fuzzyQuery, BooleanClause.Occur.MUST); // Allow fuzzy matching on each word
        }

        BooleanQuery booleanQuery = builder.build();
        System.out.println(booleanQuery);

        TopDocs topDocs = searcher.search(booleanQuery, indexLocation);
        QueryResultProcessor queryResultProcessor = new QueryResultProcessor();
        queryResultProcessor.processTopDocs(topDocs, searcher.getIndexSearcher(indexLocation));
        System.out.println("FOUND " + topDocs.totalHits);

//        System.out.println("FOUND " + topDocs.totalHits);
//        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
//            Document doc = storedFields.document(scoreDoc.doc);
//            String id = doc.get("id");
//            String
//            System.out.println("doc=" + scoreDoc.doc + " score=" + scoreDoc.score);
//            System.out.println("DOC");
//            System.out.println(doc);
//            String id = doc.get("id");
//            System.out.println(id);
//            System.out.println("entityType "+doc.get("entityType"));
//            documents.add(doc);
//        }

        //FileUtils.deleteRecursively(new File(indexLocation));


    }

    @Test
    public void dummy() {
        String str1 = "hello world";
        String str2 = "hola world";
        String str3 = "hello world";

        // Create a LevenshteinDistance instance
        LevenshteinDistance levenshtein = new LevenshteinDistance();

        // Calculate the Levenshtein distance
        int distance = levenshtein.apply(str1, str2);
        int distance2 = levenshtein.apply(str1, str3);

        // Calculate the similarity percentage
        int maxLen = Math.max(str1.length(), str2.length());
        double similarity = (1.0 - (double) distance / maxLen) * 100;

        System.out.println("Similarity: " + similarity + "%");
        System.out.println("Similarity2: " + (1.0 - (double) distance2 / maxLen) * 100 + "%");
    }


}
