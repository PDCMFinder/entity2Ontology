package org.cancer_models.entity2ontology.index.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cancer_models.entity2ontology.index.model.OntologyLocation;
import org.cancer_models.entity2ontology.index.model.RuleLocation;
import org.cancer_models.entity2ontology.map.model.TargetEntity;

import java.io.IOException;
import java.util.List;

/**
 * Service class responsible for indexing rules and ontologies
 *
 * <p>This class provides methods to handle the indexing process based on various input data, such as rule sets,
 * and creates a Lucene index at the specified path.
 *
 * @see org.cancer_models.entity2ontology.index.model.IndexingRequest
 * @see RuleLocation
 * @see org.apache.lucene.index.IndexWriter
 */
public class IndexingService {

    private static final Logger logger = LogManager.getLogger(IndexingService.class);

    private static final RulesetExtractor rulesetExtractor = new RulesetExtractor();

    private static final OntologyExtractor ontologyExtractor = new OntologyExtractor();

    private static final Indexer indexer = new Indexer();

    /**
     * Indexes the given rule set target into a Lucene index at the specified path.
     *
     * <p>This method reads the rules from the specified {@link RuleLocation} and indexes them into a Lucene
     * index at the given {@code indexPath}. The method returns an integer representing the number of rules
     * successfully indexed.
     *
     * <p>Example usage:
     * <pre>
     * {@code
     * RuleLocation ruleLocation = new RuleLocation();
     * ruleLocation.setFilePath("/path/to/rules.json");
     * ruleLocation.setName("Example Rule Set");
     *
     * IndexingService indexingService = new IndexingService();
     * int indexedCount = indexingService.indexRuleSet(ruleLocation, "/path/to/index");
     * System.out.println("Number of rules indexed: " + indexedCount);
     * }
     * </pre>
     *
     * @param ruleLocation the target rule set containing the rules to be indexed
     * @param indexPath the path where the Lucene index will be created
     * @return the number of rules successfully indexed
     * @throws IOException if there is an error reading the rule set or writing to the index
     */
    public int indexRules(RuleLocation ruleLocation, String indexPath) throws IOException {
        logger.info("Processing rule location: {} ({})", ruleLocation.getFilePath(), ruleLocation.getName());
        logger.info("Rules will be indexed at {}", indexPath);
        List<TargetEntity> targetEntities = rulesetExtractor.extract(ruleLocation);
        logger.info("Deleting all rules documents with type '{}'", ruleLocation.getName());
        indexer.deleteAllByEntityType(ruleLocation.getName(), indexPath);
        indexer.indexEntities(targetEntities, indexPath);
        return targetEntities.size();
    }

    /**
     * Indexes the ontologies defined in the {@link OntologyLocation} into a Lucene index at the specified path.
     *
     * <p>This method downloads the ontologies from the specified {@link OntologyLocation} and indexes them into a Lucene
     * index at the given {@code indexPath}. The method returns an integer representing the number of ontologies
     * successfully indexed.
     *
     * <p>Example usage:
     * <pre>
     * {@code
     * OntologyLocation ontologyLocation = new OntologyLocation();
     * ontologyLocation.setOntoId("ncit");
     * ontologyLocation.setName("Treatment ncit ontologies");
     * ontologyLocation.setBranches(["NCIT_C9305"]);
     *
     * IndexingService indexingService = new IndexingService();
     * int indexedCount = indexingService.indexOntologies(ontologyLocation, "/path/to/index");
     * System.out.println("Number of ontologies indexed: " + indexedCount);
     * }
     * </pre>
     *
     * @param ontologyLocation the location of the ontologies to process
     * @param indexPath the path where the Lucene index will be created
     * @return the number of ontologies successfully indexed
     * @throws IOException if there is an error processing the ontologies or writing to the index
     */
    public int indexOntologies(OntologyLocation ontologyLocation, String indexPath) throws IOException {
        logger.info("Processing ontology location: {}", ontologyLocation.getName());
        logger.info("Ontologies will be indexed at {}", indexPath);
        List<TargetEntity> targetEntities = ontologyExtractor.extract(ontologyLocation);
        logger.info("Deleting all ontologies documents with type '{}'", ontologyLocation.getName());
        indexer.deleteAllByEntityType(ontologyLocation.getName(), indexPath);
        indexer.indexEntities(targetEntities, indexPath);
        return targetEntities.size();
    }
}
