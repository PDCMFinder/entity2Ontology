package org.cancerModels.entity2ontology.map.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.cancerModels.entity2ontology.index.service.AnalyzerProvider;

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
public class Searcher {

    // As there can exist several indexes, this structure keeps a reader per index, so they can be
    // used multiple times
    private final Map<String, IndexSearcher> readers = new HashMap<>();

    // This should be the same used to create the index
    private final AnalyzerProvider analyzerProvider;

    private static final Logger logger = LogManager.getLogger(Searcher.class);

    public Searcher(AnalyzerProvider analyzerProvider) {
        this.analyzerProvider = analyzerProvider;
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
        logger.info("Search with query: {\n{}\n}", query.toString());
        IndexSearcher indexSearcher = getOrCreateIndexSearcher(indexPath);
        return indexSearcher.search(query, 10);


        /*StoredFields storedFields = indexSearcher.storedFields();
        List<Document> documents = new ArrayList<>();
        System.out.println("FOUND " + topDocs.totalHits);
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            Document doc = storedFields.document(scoreDoc.doc);
            String id = doc.get("id");
            String
            System.out.println("doc=" + scoreDoc.doc + " score=" + scoreDoc.score);
            System.out.println("DOC");
            System.out.println(doc);
            String id = doc.get("id");
            System.out.println(id);
            System.out.println("entityType "+doc.get("entityType"));
            documents.add(doc);
        }
        return documents;*/
    }


}
