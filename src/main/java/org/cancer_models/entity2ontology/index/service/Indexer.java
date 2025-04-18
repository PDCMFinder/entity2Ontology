package org.cancer_models.entity2ontology.index.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.cancer_models.entity2ontology.common.mappers.TargetEntityDocumentMapper;
import org.cancer_models.entity2ontology.common.model.TargetEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The {@code Indexer} class is responsible for indexing a collection of {@code TargetEntity} objects into a Lucene index.
 * It provides methods to add entities to the index and manage the indexing process.
 *
 * <p>
 * The {@code Indexer} class expects the following dependencies:
 * <ul>
 * <li>Lucene Core library</li>
 * </ul>
 * </p>
 *
 * <p>
 * Example usage:
 * <pre>
 * {@code
 * Indexer indexer = new Indexer(indexWriter);
 * indexer.indexEntities(targetEntities);
 * }
 * </pre>
 * </p>
 *
 * @see TargetEntity
 * @see org.apache.lucene.index.IndexWriter
 */
@Component
public class Indexer {

    private final Map<String, IndexWriter> indexes = new HashMap<>();

    private final AnalyzerProvider analyzerProvider = new AnalyzerProvider();

    private static final Logger logger = LogManager.getLogger(Indexer.class);

    /**
     * Creates a Lucene Index in {@code indexPath}. The path must be empty, so an exception will be
     * thrown if the directory contains data.
     * @param indexPath The path where the index will be created.
     * @return The Lucene {@link IndexWriter}
     * @throws IOException if there is an error creating the index
     */
    private IndexWriter createWriter(String indexPath) throws IOException {
        logger.info("Creating index at {}", indexPath);
        FSDirectory dir = FSDirectory.open(Paths.get(indexPath));
        IndexWriterConfig config = new IndexWriterConfig(analyzerProvider.getAnalyzer());
        return new IndexWriter(dir, config);
    }

    private IndexWriter getIndexWriter(String indexPath) throws IOException {
        if (!indexes.containsKey(indexPath)) {
            logger.info("Index {} not found. A new one will be created", indexPath);
            IndexWriter index = createWriter(indexPath);
            indexes.put(indexPath, index);
        }
        return indexes.get(indexPath);
    }

    /**
     * Indexes a list of {@code TargetEntity} objects into the Lucene index located at {@code indexPath}.
     *
     * @param entities the collection of {@code TargetEntity} objects to be indexed
     * @param indexPath the path where the data is going to be indexed
     * @throws IOException if there is an issue writing to the index
     */
    public void indexEntities(List<TargetEntity> entities, String indexPath) throws IOException {
        IndexWriter writer = getIndexWriter(indexPath);
        List<Document> documents = new ArrayList<>();
        for (TargetEntity entity : entities) {
            Document document = TargetEntityDocumentMapper.targetEntityToDocument(entity);
            documents.add(document);
        }
        logger.info("Start writing {} documents", documents.size());
        writer.addDocuments(documents);
        writer.commit();
        logger.info("Finished writing documents. Index closed.");
    }

    /**
     * Delete all documents which entityType is {@code entityType} and target type is {@code targetType}.
     * @param entityType The type of entity to delete (treatment or diagnosis, for instance).
     * @param indexPath Path of the index.
     */
    public void deleteAllByEntityTypeAndTargetType(
        String entityType, String targetType, String indexPath) throws IOException {
        IndexWriter writer = getIndexWriter(indexPath);
        // Create a BooleanQuery to match both entityType and targetType
        BooleanQuery.Builder queryBuilder = new BooleanQuery.Builder();
        queryBuilder.add(new TermQuery(new Term("entityType", entityType)), BooleanClause.Occur.MUST);
        queryBuilder.add(new TermQuery(new Term("targetType", targetType)), BooleanClause.Occur.MUST);

        // Delete documents matching the query
        writer.deleteDocuments(queryBuilder.build());
        writer.commit();
    }

    /**
     * Delete all documents in a given index {@code entityType}.
     * @param indexPath Path of the index.
     */
    public void deleteAll(String indexPath) throws IOException {
        IndexWriter writer = getIndexWriter(indexPath);
        writer.deleteAll();
        writer.commit();
        logger.info("All documents at {} deleted", indexPath);
    }

    /**
     * Checks if the given path contains a valid Lucene index.
     *
     * @param indexPath the path to the index
     * @return true if the path contains a valid index, false otherwise
     */
    public static boolean isValidLuceneIndex(String indexPath) {
        Path path = Paths.get(indexPath);
        if (!Files.exists(path)) {
            logger.error("Index not found at path: [{}] - directory is empty", indexPath);
            return false;
        }
        try (Directory directory = FSDirectory.open(path)) {
            DirectoryReader.open(directory).close();
            return true;
        } catch (IndexNotFoundException e) {
            logger.error("Index not found at path: [{}]", indexPath);
            return false;
        } catch (IOException e) {
            logger.error("IOException while checking index at path:: [{}]", indexPath);
            return false;
        }
    }
}
