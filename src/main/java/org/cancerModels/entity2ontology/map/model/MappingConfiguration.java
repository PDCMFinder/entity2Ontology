package org.cancerModels.entity2ontology.map.model;

import lombok.Data;

import java.util.List;

@Data
public class MappingConfiguration {
    private String name;
    private List<RuleConfiguration> ruleConfiguration;
    private OntologyConfiguration ontologyConfiguration;

    @Data
    static class RuleConfiguration {
        private String entityType;
        private List<RuleConfigurationField> fields;
    }

    @Data
    static class RuleConfigurationField {
        private String name;
        private double weight;
        private boolean main;
    }

    @Data
    static class OntologyConfiguration {
        private List<OntologyConfigurationField> fields;
    }

    @Data
    static class OntologyConfigurationField {
        private String name;
        private double weight;
    }
}
