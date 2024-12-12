package org.cancer_models.entity2ontology.map.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cancer_models.entity2ontology.exceptions.MalformedMappingConfiguration;
import org.cancer_models.entity2ontology.index.service.Indexer;
import org.cancer_models.entity2ontology.map.model.MappingConfiguration;
import org.cancer_models.entity2ontology.map.model.SourceEntity;
import org.cancer_models.entity2ontology.map.model.Suggestion;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * Service class responsible for performing entity mappings.
 */
@Component
public class MappingService {

    private static final Logger logger = LogManager.getLogger(MappingService.class);

    private final SuggestionsFinder suggestionsFinder;

    public MappingService(SuggestionsFinder suggestionsFinder) {
        this.suggestionsFinder = suggestionsFinder;
    }

    /**
     * Generates a list of suggestions (sorted by score) for a given entity.
     *
     * @param entity            the source entity to be mapped
     * @param indexPath         the path of the index to use for the mapping
     * @param maxNumSuggestions the max number of suggestions to get
     * @param config            information about how to build the queries to find matches
     * @return a list of suggestions for the source entity
     */
    public List<Suggestion> mapEntity(
        SourceEntity entity, String indexPath, int maxNumSuggestions, MappingConfiguration config) throws IOException {
        logger.info("Mapping entity: {}", entity);
        logger.info("Using index: [{}]", indexPath);
        logger.info("Using configuration: {}", config.getName());

        validateSourceEntity(entity);
        validateIndex(indexPath);
        validateMappingConfiguration(config);

        return suggestionsFinder.findSuggestions(entity, indexPath, maxNumSuggestions, config);
    }

    private void validateSourceEntity(SourceEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }
        if (entity.getId() == null) {
            throw new IllegalArgumentException(String.format("Entity id cannot be null: %s", entity));
        }
        if (entity.getType() == null) {
            throw new IllegalArgumentException(String.format("Entity type cannot be null: %s", entity));
        }
        if (entity.getData() == null) {
            throw new IllegalArgumentException(String.format("Entity data cannot be null: %s", entity));
        }
    }

    private void validateIndex(String indexPath) {
        if (indexPath == null) {
            throw new IllegalArgumentException("Index cannot be null");
        }
        if (!Indexer.isValidLuceneIndex(indexPath)) {
            throw new IllegalArgumentException(String.format("Index [%s] is not a valid lucene index", indexPath));
        }
    }

    private void validateMappingConfiguration(MappingConfiguration config) {
        Objects.requireNonNull(config);
        String errorMessage;
        List<MappingConfiguration. ConfigurationPerType> configsPerType = config.getConfigurations();

        if (configsPerType == null) {
            errorMessage = "Property `configurations` cannot be null";
            throw new MalformedMappingConfiguration(errorMessage);
        }
        if (configsPerType.isEmpty()) {
            errorMessage = "Property `configurations` cannot be empty";
            throw new MalformedMappingConfiguration(errorMessage);
        }
        for (MappingConfiguration.ConfigurationPerType confPerType : configsPerType) {
            validateConfigurationPerType(confPerType);
        }
    }

    private void validateConfigurationPerType(MappingConfiguration.ConfigurationPerType confPerType) {
        String errorMessage;

        if (confPerType.getEntityType() == null) {
            errorMessage = "Property `configurations.entityType` cannot be null";
            throw new MalformedMappingConfiguration(errorMessage);
        }
        List<MappingConfiguration.FieldConfiguration> fields = confPerType.getFields();
        if (fields == null) {
            errorMessage = "Property `configurations.fields` cannot be null";
            throw new MalformedMappingConfiguration(errorMessage);
        }
        if (fields.isEmpty()) {
            errorMessage = "Property `configurations.fields` cannot be empty";
            throw new MalformedMappingConfiguration(errorMessage);
        }
        for (MappingConfiguration.FieldConfiguration field : fields) {
            if (field.getName() == null) {
                errorMessage = "Property `configurations.fields.name` cannot be null";
                throw new MalformedMappingConfiguration(errorMessage);
            }
            // Weight not checked as it is a double, so it will never be null
        }
        if (confPerType.getOntologyTemplates() == null) {
            errorMessage = "Property `configurations.ontologyTemplates` cannot be null";
            throw new MalformedMappingConfiguration(errorMessage);
        }
        if (confPerType.getOntologyTemplates().isEmpty()) {
            errorMessage = "Property `configurations.ontologyTemplates` cannot be empty";
            throw new MalformedMappingConfiguration(errorMessage);
        }
    }

}
