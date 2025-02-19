package org.cancer_models.entity2ontology.map.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.cancer_models.entity2ontology.index.service.AnalyzerProvider;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * The Searcher class is responsible for managing a Lucene {@link IndexSearcher} and executing queries
 * against a Lucene index.
 *
 * <p>
 * This class provides functionality to initialize an IndexSearcher and perform search operations using various
 * Lucene queries. It abstracts the complexities of interacting directly with the Lucene API, making it easier
 * to perform search operations within the application.
 * </p>
 *
 * <p>
 * Example usage:
 * <pre>
 *     Searcher searcher = new Searcher(indexDirectoryPath);
 *     Query query = new TermQuery(new Term("fieldName", "searchTerm"));
 *     TopDocs results = searcher.executeQuery(query);
 * </pre>
 * </p>
 *
 * @see IndexSearcher
 * @see Query
 * @see TopDocs
 */
@Component
public class Searcher {

    // As there can exist several indexes, this structure keeps a reader per index, so they can be
    // used multiple times
    private final Map<String, IndexSearcher> readers = new HashMap<>();

    private final QueryParser queryParser;

    // Number of results to retrieve from the search
    private static final int NUM_RESULTS = 50;

    private static final Logger logger = LogManager.getLogger(Searcher.class);

    public Searcher(AnalyzerProvider analyzerProvider) {
        // This should be the same used to create the index
        queryParser = new QueryParser("", analyzerProvider.getAnalyzer());
    }

    private IndexSearcher createSearcher(String indexPath) throws IOException {
        logger.info("Creating searcher for index at {}", indexPath);
        Directory dir = FSDirectory.open(Paths.get(indexPath));
        IndexReader reader = DirectoryReader.open(dir);
        return new IndexSearcher(reader);
    }

    private IndexSearcher getOrCreateIndexSearcher(String indexPath) throws IOException {
        if (!readers.containsKey(indexPath)) {
            logger.info("Index searcher for {} not found. A new one will be created", indexPath);
            IndexSearcher indexSearcher = createSearcher(indexPath);
            readers.put(indexPath, indexSearcher);
        }
        return readers.get(indexPath);
    }

    public IndexSearcher getIndexSearcher(String indexPath) {
        return readers.get(indexPath);
    }

    public TopDocs search(Query query, String indexPath) throws IOException {
        logger.info("Search with query: {\n{}\n}", query);
        // To make sure the queries use the same analyser used to index, we rebuild the query by parsing the string version
        // or the original one
        String queryAsString = query.toString();
        Query reparsedQuery;
        try {
            reparsedQuery = queryParser.parse(queryAsString);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
        IndexSearcher indexSearcher = getOrCreateIndexSearcher(indexPath);
        return indexSearcher.search(reparsedQuery, NUM_RESULTS);
    }
}
