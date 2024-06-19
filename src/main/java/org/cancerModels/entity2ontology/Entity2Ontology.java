package org.cancerModels.entity2ontology;

import org.cancerModels.entity2ontology.index.command.IndexCommand;
import org.cancerModels.entity2ontology.map.command.MapCommand;
import picocli.CommandLine;

@CommandLine.Command(
    name = "Entity2Ontology",
    description = "Maps entities to ontology terms.",
    mixinStandardHelpOptions = true,
    subcommands = {MapCommand.class, IndexCommand.class})
public class Entity2Ontology {

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Entity2Ontology()).execute(args);
        System.exit(exitCode);
    }
}