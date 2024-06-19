package org.cancerModels.entity2ontology.index.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cancerModels.entity2ontology.map.model.SourceEntity;
import org.cancerModels.entity2ontology.map.model.Suggestion;

import java.util.ArrayList;
import java.util.List;

/**
 * Service class responsible for indexing data mappings.
 */
public class IndexingService {

    private static final Logger logger = LogManager.getLogger(IndexingService.class);

    /**
     * Generates a list of suggestions (sorted by score) for a given entity.
     *
     * @param entity the source entity to be mapped
     * @param indexPath the path of the index to use for the mapping
     * @param maxNumSuggestions the max number of suggestions to get
     * @return a list of suggestions for the source entity
     */
    public List<Suggestion> mapEntity(
        SourceEntity entity,
        String indexPath,
        int maxNumSuggestions) {
        logger.info("Mapping entity {} using index {}", entity, indexPath);
        // For now let's assign a list with a dummy result
        List<Suggestion> dummy = new ArrayList<>();
        Suggestion suggestion1 = new Suggestion();
        suggestion1.setTargetId("reference_1");
        suggestion1.setType("type_1");
        //suggestion1.setSourceEntity(entity);
        suggestion1.setScore(5);
        suggestion1.setTermLabel("label_term_1");
        suggestion1.setTermUrl("term_url_1");
        dummy.add(suggestion1);

        Suggestion suggestion2 = new Suggestion();
        suggestion2.setTargetId("reference_2");
        suggestion2.setType("type_2");
        //suggestion2.setSourceEntity(entity);
        suggestion2.setScore(0);
        suggestion2.setTermLabel("label_term_2");
        suggestion2.setTermUrl("term_url_2");
        dummy.add(suggestion2);
        return dummy;
    }
}
