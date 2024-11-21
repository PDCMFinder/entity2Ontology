package org.cancer_models.entity2ontology;

import org.cancer_models.entity2ontology.index.command.IndexCommand;
import org.cancer_models.entity2ontology.map.command.MapCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import picocli.CommandLine;

@Component
@CommandLine.Command(
    name = "Entity2Ontology",
    description = "Maps entities to ontology terms.",
    mixinStandardHelpOptions = true,
    subcommands = {MapCommand.class, IndexCommand.class})
public class Entity2Ontology {

    private final ApplicationContext context;

    @Autowired
    public Entity2Ontology(ApplicationContext context) {
        this.context = context;
    }

    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        Entity2Ontology app = context.getBean(Entity2Ontology.class);
        // Use the custom factory to allow Picocli to use Spring for creating command instances
        SpringCommandFactory factory = context.getBean(SpringCommandFactory.class);

        int exitCode = new CommandLine(app, factory)
            .setSubcommandsCaseInsensitive(true)
            .execute(args);
        System.exit(exitCode);
    }
}