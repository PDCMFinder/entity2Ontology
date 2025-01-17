package org.cancer_models.entity2ontology.map.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cancer_models.entity2ontology.exceptions.MalformedMappingConfigurationException;
import org.cancer_models.entity2ontology.exceptions.MappingException;
import org.cancer_models.entity2ontology.index.service.Indexer;
import org.cancer_models.entity2ontology.map.model.MappingConfiguration;
import org.cancer_models.entity2ontology.map.model.SourceEntity;
import org.cancer_models.entity2ontology.map.model.Suggestion;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Service class responsible for performing entity mappings.
 */
@Component
public class MappingService {

    private static final Logger logger = LogManager.getLogger(MappingService.class);

    private final SuggestionsFinder suggestionsFinder;

    // The maximum number of words an attribute value in an entity can be.
    private static final int MAX_NUM_WORDS = 30;
    // The maximum length of an attribute value in an entity.
    private static final int MAX_TEXT_LENGTH = 200;

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
     * @throws MalformedMappingConfigurationException  if the mapping configuration is not correct
     * @throws MappingException  if there were issues trying to map the entity
     */
    public List<Suggestion> mapEntity(
        SourceEntity entity, String indexPath, int maxNumSuggestions, MappingConfiguration config)
        throws MalformedMappingConfigurationException, MappingException {
        logger.info("Mapping entity: {}", entity);
        logger.info("Using index: [{}]", indexPath);
        logger.info("Using configuration: {}", config.getName());

        List<Suggestion> suggestions = new ArrayList<>();

        validateSourceEntity(entity);
        validateIndex(indexPath);
        validateMappingConfiguration(config);
        cleanSourceEntityData(entity);

        // Only process the entity if the text of its attributes is not too long
        if (!shouldIgnoreEntity(entity)) {
            suggestions = suggestionsFinder.findSuggestions(entity, indexPath, maxNumSuggestions, config);
        }

        return suggestions;
    }

    private void cleanSourceEntityData(SourceEntity entity) {
        entity.getData().forEach((key, value) -> {
            String cleanedValue = value.trim().replaceAll(" +", " ");
            entity.getData().put(key, cleanedValue);
        });
    }

    private void validateSourceEntity(SourceEntity entity) throws MappingException {
        if (entity == null) {
            throw new MappingException("Entity cannot be null");
        }
        if (entity.getId() == null) {
            throw new MappingException(String.format("Entity id cannot be null: %s", entity));
        }
        if (entity.getType() == null) {
            throw new MappingException(String.format("Entity type cannot be null: %s", entity));
        }
        if (entity.getData() == null) {
            throw new MappingException(String.format("Entity data cannot be null: %s", entity));
        }
    }

    private void validateIndex(String indexPath) throws MappingException {
        if (indexPath == null) {
            throw new MappingException("Index cannot be null");
        }
        if (!Indexer.isValidLuceneIndex(indexPath)) {
            throw new MappingException(String.format("Index [%s] is not a valid lucene index", indexPath));
        }
    }

    private void validateMappingConfiguration(MappingConfiguration config) throws MalformedMappingConfigurationException {
        Objects.requireNonNull(config);
        String errorMessage;
        List<MappingConfiguration. ConfigurationPerType> configsPerType = config.getConfigurations();

        if (configsPerType == null) {
            errorMessage = "Property `configurations` cannot be null";
            throw new MalformedMappingConfigurationException(errorMessage);
        }
        if (configsPerType.isEmpty()) {
            errorMessage = "Property `configurations` cannot be empty";
            throw new MalformedMappingConfigurationException(errorMessage);
        }
        for (MappingConfiguration.ConfigurationPerType confPerType : configsPerType) {
            validateConfigurationPerType(confPerType);
        }
    }

    private void validateConfigurationPerType(MappingConfiguration.ConfigurationPerType confPerType) throws MalformedMappingConfigurationException {
        String errorMessage;

        if (confPerType.getEntityType() == null) {
            errorMessage = "Property `configurations.entityType` cannot be null";
            throw new MalformedMappingConfigurationException(errorMessage);
        }
        List<MappingConfiguration.FieldConfiguration> fields = confPerType.getFields();
        if (fields == null) {
            errorMessage = "Property `configurations.fields` cannot be null";
            throw new MalformedMappingConfigurationException(errorMessage);
        }
        if (fields.isEmpty()) {
            errorMessage = "Property `configurations.fields` cannot be empty";
            throw new MalformedMappingConfigurationException(errorMessage);
        }
        for (MappingConfiguration.FieldConfiguration field : fields) {
            if (field.getName() == null) {
                errorMessage = "Property `configurations.fields.name` cannot be null";
                throw new MalformedMappingConfigurationException(errorMessage);
            }
            // Weight not checked as it is a double, so it will never be null
        }
        if (confPerType.getOntologyTemplates() == null) {
            errorMessage = "Property `configurations.ontologyTemplates` cannot be null";
            throw new MalformedMappingConfigurationException(errorMessage);
        }
        if (confPerType.getOntologyTemplates().isEmpty()) {
            errorMessage = "Property `configurations.ontologyTemplates` cannot be empty";
            throw new MalformedMappingConfigurationException(errorMessage);
        }
    }

    private boolean shouldIgnoreEntity(SourceEntity entity) {
        for (Map.Entry<String, String> entry : entity.getData().entrySet()) {
            if (isTextTooLong(entry.getValue())) {
                logger.info(
                    "Ignoring text too long. Attribute: {}, value: {}", entry.getKey(), entry.getValue());
                return true;
            }
        }
        return false;
    }

    private boolean isTextTooLong(String text) {
        if (text.length() > MAX_TEXT_LENGTH) {
            return true;
        }
        String[] arrayStr = text.split(" +");
        return arrayStr.length > MAX_NUM_WORDS;
    }

}
