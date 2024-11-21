package org.cancer_models.entity2ontology.index.command;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cancer_models.entity2ontology.index.service.IndexingRequestService;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

import java.io.IOException;

/**
 * Command to index data into a Lucene index.
 *
 * <p>This class is responsible for handling the indexing of source data into a Lucene index.
 * It reads the source data from the specified path and indexes it to facilitate efficient
 * searching and retrieval during the entity to ontology mapping process.
 *
 * <p>Usage example:
 * <pre>
 * {@code java -jar entity2Ontology-1.0-SNAPSHOT.jar index --source path/to/source/data}
 * </pre>
 *
 * <p>This command uses the Picocli library for command line parsing and execution.
 *
 */
@CommandLine.Command(
    name = "index",
    description = "Indexes data into a Lucene index.",
    mixinStandardHelpOptions = true //adds --help option to the command
    )
@Component
public class IndexCommand implements Runnable {

    private final IndexingRequestService indexingRequestService = new IndexingRequestService();

    private static final Logger logger = LogManager.getLogger(IndexCommand.class);
    /**
     * The JSON file containing the indexing request.
     */
    @CommandLine.Option(names = "--request", required = true, description = "Indexing request JSON file.")
    private String requestFile;

    @Override
    public void run() {

        try {
            // Read the mapping request
            indexingRequestService.processRequest(requestFile);
        } catch (IOException | IllegalArgumentException e) {
            logger.error("Failed to perform indexing: {}", e.getMessage());
            System.exit(1);
        }
    }
}
