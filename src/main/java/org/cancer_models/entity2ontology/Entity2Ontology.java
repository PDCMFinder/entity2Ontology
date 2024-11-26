package org.cancer_models.entity2ontology;

import org.cancer_models.entity2ontology.index.command.IndexCommand;
import org.cancer_models.entity2ontology.map.command.MapCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import picocli.CommandLine;

/**
 * Main class for the project, providing a command-line interface for mapping entities to ontology terms.
 */
@Component
@CommandLine.Command(
    name = "Entity2Ontology",
    description = "Maps entities to ontology terms.",
    mixinStandardHelpOptions = true,
    subcommands = {MapCommand.class, IndexCommand.class})
public class Entity2Ontology {

    /**
     * The application context, used to manage the application's dependencies.
     */
    private final ApplicationContext context;

    /**
     * Constructor for the Entity2Ontology class, injecting the application context.
     *
     * @param context the application context
     */
    public Entity2Ontology(ApplicationContext context) {
        this.context = context;
    }

    /**
     * The main entry point for the application, responsible for executing the command-line interface.
     *
     * @param args the command-line arguments
     */
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