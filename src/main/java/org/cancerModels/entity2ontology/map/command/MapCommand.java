package org.cancerModels.entity2ontology.map.command;

import org.cancerModels.entity2ontology.map.service.MappingIO;
import org.cancerModels.entity2ontology.map.service.MappingRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

import java.io.IOException;

/**
 * Command class responsible for executing the mapping process.
 * <p>
 * The {@code MapCommand} class uses a mapping request JSON file to perform the mapping of source entities
 * to their respective target entities. The results of the mapping process are written to an output file.
 * This class is designed to be used with the Picocli command-line framework.
 * </p>
 *
 * <pre>
 * Example usage:
 * java -jar entity2Ontology.jar map --request mappingRequest.json --output output.json
 * </pre>
 *
 * <p>
 * Options:
 * </p>
 * <ul>
 *   <li>{@code --request}: Specifies the JSON file containing the mapping request.</li>
 *   <li>{@code --output}: Specifies the output file where the mapping results will be written.</li>
 * </ul>
 *
 * @see MappingIO
 * @see MappingRequestService
 */
@CommandLine.Command(
    name = "map",
    description = "Performs mapping using a mapping request JSON.",
    mixinStandardHelpOptions = true //adds --help option to the command
    )
@Component
public class MapCommand implements Runnable {

    private final MappingRequestService mappingRequestService;

    /**
     * The JSON file containing the mapping request.
     */
    @CommandLine.Option(names = "--request", required = true, description = "Mapping request JSON file.")
    private String requestFile;

    /**
     * The output file where the mapping results will be written.
     */
    @CommandLine.Option(names = "--output", required = true, description = "Output file to write the mapping results.")
    private String outputFile;

    @Autowired
    public MapCommand(MappingRequestService mappingRequestService) {
        this.mappingRequestService = mappingRequestService;
    }

    @Override
    public void run() {
        try {
            // Read the mapping request
            mappingRequestService.processMappingRequest(requestFile, outputFile);
        } catch (IOException e) {
            System.err.println("Failed to perform mapping: " + e.getMessage());
            System.exit(1);
        }
    }
}
