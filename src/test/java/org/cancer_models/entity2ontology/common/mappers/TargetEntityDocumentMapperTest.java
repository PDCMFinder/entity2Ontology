package org.cancer_models.entity2ontology.common.mappers;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.cancer_models.entity2ontology.common.model.TargetEntity;
import org.cancer_models.entity2ontology.common.model.TargetEntityDataFields;
import org.cancer_models.entity2ontology.common.model.TargetEntityType;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class TargetEntityDocumentMapperTest {

    private static final String ENTITY_ID_TEST = "entity_id";
    private static final String ENTITY_TYPE_TEST = "treatment";
    private static final TargetEntityType TARGET_TYPE_TEST = TargetEntityType.ONTOLOGY;
    private static final String LABEL_TEST = "Irinotecan/Temozolomide Regimen";
    private static final String URL_TEST = "http://purl.obolibrary.org/obo/NCIT_C11820";
    private static final String DESCRIPTION_TEST = "A regimen consisting of irinotecan and temozolomide that...";
    private static final String[] SYNONYMS_TEST = new String[]{
        "irinotecan-temozolomide",
        "irinotecan/temozolomide",
        "temozolomide-irinotecan",
        "temozolomide/irinotecan",
        "irinotecan and temozolomide",
        "temozolomide/irinotecan regimen"
    };


    @Test
    void givenValidTargetEntity_whenTargetEntityToDocument_thenReturnDocument() {
        List<String> synonyms = new ArrayList<>(Arrays.asList(SYNONYMS_TEST));
        TargetEntityDataFields dataFields = new TargetEntityDataFields();
        dataFields.addStringField("label", LABEL_TEST);
        dataFields.addStringField("description", DESCRIPTION_TEST);
        dataFields.addListField("synonyms", synonyms);
        TargetEntity targetEntity = new TargetEntity(
            ENTITY_ID_TEST, ENTITY_TYPE_TEST, TARGET_TYPE_TEST, dataFields, LABEL_TEST, URL_TEST);

        Document document = TargetEntityDocumentMapper.targetEntityToDocument(targetEntity);

        assertNotNull(document);
        String id = document.get("id");
        String entityType = document.get("entityType");
        String targetType = document.get("targetType");
        String label = document.get("label");
        String url = document.get("url");
        String[] synonymsData = document.getValues("ontology.synonyms");
        String descriptionData = document.get("ontology.description");
        String labelData = document.get("ontology.label");
        assertEquals(ENTITY_ID_TEST, id);
        assertEquals(ENTITY_TYPE_TEST, entityType);
        assertEquals(TARGET_TYPE_TEST.getValue(), targetType);
        assertEquals(LABEL_TEST, label);
        assertEquals(URL_TEST, url);
        assertEquals(LABEL_TEST, labelData);
        assertEquals(DESCRIPTION_TEST, descriptionData);
        assertArrayEquals(SYNONYMS_TEST, synonymsData);
    }

    @Test
    void givenValidDocument_whenDocumentToTargetEntity_thenReturnTargetEntity() {
        Document document = new Document();
        document.add(new StringField("id", ENTITY_ID_TEST, Field.Store.YES));
        document.add(new StringField("entityType", ENTITY_TYPE_TEST, Field.Store.YES));
        document.add(new StringField("targetType", TARGET_TYPE_TEST.getValue(), Field.Store.YES));
        document.add(new TextField("label", LABEL_TEST, Field.Store.YES));
        document.add(new StringField("url", URL_TEST, Field.Store.YES));
        document.add(new StringField("ontology.description", DESCRIPTION_TEST, Field.Store.YES));
        document.add(new StringField("ontology.label", LABEL_TEST, Field.Store.YES));
        for (String synonym : SYNONYMS_TEST) {
            document.add(new TextField("ontology.synonyms", synonym, Field.Store.YES));
        }

        TargetEntity targetEntity = TargetEntityDocumentMapper.documentToTargetEntity(document);

        TargetEntityDataFields dataFields = targetEntity.dataFields();

        assertTrue(dataFields.hasStringField("label"));
        assertEquals(LABEL_TEST, dataFields.getStringField("label"));
        assertTrue(dataFields.hasStringField("description"));
        assertEquals(DESCRIPTION_TEST, dataFields.getStringField("description"));
        assertTrue(dataFields.hasListField("synonyms"));
        assertArrayEquals(SYNONYMS_TEST, dataFields.getListField("synonyms").toArray());
    }

}
