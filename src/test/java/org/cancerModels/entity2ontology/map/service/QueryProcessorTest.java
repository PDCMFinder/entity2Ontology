package org.cancerModels.entity2ontology.map.service;

import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.cancerModels.entity2ontology.index.service.AnalyzerProvider;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class QueryProcessorTest {

    private AnalyzerProvider analyzerProvider = new AnalyzerProvider();
    private Searcher searcher = new Searcher(analyzerProvider);
    private QueryProcessor instance = new QueryProcessor(searcher);

    @Test
    void testExecuteQuery_validQuery() {
        String indexPath = "src/test/output/ontologies_searcher_index";

        String queryString = "(+(ontology.label:primary)^0.5 +(ontology.label:\"fusion-negative alveolar rhabdomyosarcoma\")^1.0) " +
            "(+(ontology.synonyms:primary)^0.5 +(ontology.synonyms:\"fusion-negative alveolar rhabdomyosarcoma\")^1.0)";
        queryString = "(+(ontology.label:primary)^0.5 +(ontology.label:fusion negative alveolar rhabdomyosarcoma)^1.0) (+(ontology.synonyms:primary)^0.5 +(ontology.synonyms:fusion negative alveolar rhabdomyosarcoma)^1.0)";
        try {
            // Initialize the analyzer and the query parser
            QueryParser queryParser = new QueryParser("", analyzerProvider.getAnalyzer());

            // Parse the query string to a Query object
            Query query = queryParser.parse(queryString);

            // You can then use this query object in a search
            System.out.println("Parsed Query: " + query);
            var x = instance.executeQuery(query, indexPath);
            System.out.println(x);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//        Query query = new Query("suggestion_finder_case1");
//        QueryParser queryParser = new QueryParser(
//            "(+(ontology.label:primary)^0.5 +(ontology.label:fusion-negative alveolar rhabdomyosarcoma)^1.0) (+(ontology.synonyms:primary)^0.5 +(ontology.synonyms:fusion-negative alveolar rhabdomyosarcoma)^1.0)",
//            analyzerProvider.getAnalyzer());
//        instance.executeQuery(queryParser.)
    }
}