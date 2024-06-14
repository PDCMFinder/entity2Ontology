package org.cancerModels.entity2ontology;

import org.cancerModels.entity2ontology.map.command.MapCommand;
import picocli.CommandLine;

@CommandLine.Command(name = "Entity2Ontology", subcommands = {MapCommand.class})
public class Entity2Ontology implements Runnable {

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Entity2Ontology()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public void run() {
        System.out.println("Use a subcommand: index or map");
    }
}