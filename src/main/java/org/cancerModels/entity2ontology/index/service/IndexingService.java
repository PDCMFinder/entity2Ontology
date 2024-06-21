package org.cancerModels.entity2ontology.index.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cancerModels.entity2ontology.index.model.RuleSetTarget;
import org.cancerModels.entity2ontology.map.model.TargetEntity;

import java.io.IOException;
import java.util.List;

/**
 * Service class responsible for indexing rules and ontologies
 *
 * <p>This class provides methods to handle the indexing process based on various input data, such as rule sets,
 * and creates a Lucene index at the specified path.
 *
 * @see com.yourdomain.entity2ontology.index.IndexingRequest
 * @see com.yourdomain.entity2ontology.rules.RuleSetTarget
 * @see org.apache.lucene.index.IndexWriter
 */
public class IndexingService {

    private static final Logger logger = LogManager.getLogger(IndexingService.class);

    private static final RulesetExtractor rulesetExtractor = new RulesetExtractor();

    /**
     * Indexes the given rule set target into a Lucene index at the specified path.
     *
     * <p>This method reads the rules from the specified {@link RuleSetTarget} and indexes them into a Lucene
     * index at the given {@code indexPath}. The method returns an integer representing the number of rules
     * successfully indexed.
     *
     * <p>Example usage:
     * <pre>
     * {@code
     * RuleSetTarget ruleSetTarget = new RuleSetTarget();
     * ruleSetTarget.setFilePath("/path/to/rules.json");
     * ruleSetTarget.setName("Example Rule Set");
     *
     * IndexingService indexingService = new IndexingService();
     * int indexedCount = indexingService.indexRuleSet(ruleSetTarget, "/path/to/index");
     * System.out.println("Number of rules indexed: " + indexedCount);
     * }
     * </pre>
     *
     * @param ruleSetTarget the target rule set containing the rules to be indexed
     * @param indexPath the path where the Lucene index will be created
     * @return the number of rules successfully indexed
     * @throws IOException if there is an error reading the rule set or writing to the index
     */
    public int indexRuleSet(RuleSetTarget ruleSetTarget, String indexPath) throws IOException {
        logger.info("Processing rule set target: {} ({})", ruleSetTarget.getFilePath(), ruleSetTarget.getName());
        // Read the ruleset
        List<TargetEntity> targetEntities = rulesetExtractor.extract(ruleSetTarget);
        System.out.println("Got " + targetEntities.size() + " target entities");
        return 0;
    }
}
