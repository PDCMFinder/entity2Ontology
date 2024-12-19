package org.cancer_models.entity2ontology.common.mappers;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexableField;
import org.cancer_models.entity2ontology.common.model.TargetEntity;
import org.cancer_models.entity2ontology.common.model.TargetEntityDataFields;
import org.cancer_models.entity2ontology.common.model.TargetEntityFieldName;

import java.util.*;

/**
 * A class to map {@code TargetEntity}  to {@code Document} and vice versa.
 */
public class TargetEntityDocumentMapper {

    // Suppress default constructor for non-instantiability
    private TargetEntityDocumentMapper() {
        throw new AssertionError();
    }

    private static final List<String> MULTIVALUED_ATTRIBUTES = List.of("synonyms");

    public static Document targetEntityToDocument(TargetEntity targetEntity) {
        Document document = new Document();
        document.add(
            new StringField(TargetEntityFieldName.ID.getValue(), targetEntity.id(), Field.Store.YES));
        document.add(
            new StringField(TargetEntityFieldName.ENTITY_TYPE.getValue(), targetEntity.entityType(), Field.Store.YES));
        document.add(new StringField(TargetEntityFieldName.TARGET_TYPE.getValue(), targetEntity.targetType(), Field.Store.YES));
        document.add(new TextField(TargetEntityFieldName.LABEL.getValue(), targetEntity.label(), Field.Store.YES));
        document.add(new StringField(TargetEntityFieldName.URL.getValue(), targetEntity.url(), Field.Store.YES));

        // Add string data fields if any
        Map<String, String> stringFields = targetEntity.dataFields().getStringFields();
        if (stringFields != null) {
            stringFields.forEach((k, v) -> {
                String fieldName = targetEntity.targetType() + "." + k;
                document.add(new TextField(fieldName, v, Field.Store.YES));
            });
        }

        // Add list data fields if any
        Map<String, List<String>> listFields = targetEntity.dataFields().getListFields();
        if (listFields != null) {
            listFields.forEach((k, v) -> {
                for (var element : v) {
                    String fieldName = targetEntity.targetType() + "." + k;
                    document.add(new TextField(fieldName, element, Field.Store.YES));
                }
            });
        }

        return document;
    }

    public static TargetEntity documentToTargetEntity(Document document) {
        String id = document.get(TargetEntityFieldName.ID.getValue());
        String entityType = document.get(TargetEntityFieldName.ENTITY_TYPE.getValue());
        String targetType = document.get(TargetEntityFieldName.TARGET_TYPE.getValue());
        String label = document.get(TargetEntityFieldName.LABEL.getValue());
        String url = document.get(TargetEntityFieldName.URL.getValue());
        TargetEntityDataFields dataFields = extractDataMap(document);
        return new TargetEntity(id, entityType, targetType, dataFields, label, url);
    }

    // Builds a TargetEntityDataFields with the data stored in the document. An attribute is part of the data
    // section if its name has the format <entityType>.name. For example:
    // - rule.SampleDiagnosis
    private static TargetEntityDataFields extractDataMap(Document document) {
        TargetEntityDataFields dataFields = new TargetEntityDataFields();
        // Map with the data. The value is an object because in some scenarios it's not a primitive but a
        // collection, like the case of ontology.synonyms, which are expected to appear several times
        // as independent fields in the document, but must be transformed into a single entry:
        // <"synonyms", list of synonyms">

        Map<String, List<String>> multivaluedAttributesData = new HashMap<>();

        for (IndexableField field : document.getFields()) {

            if (!field.name().contains(".")) {
                continue;
            }

            String fieldValue = field.stringValue();
            int idx = field.name().lastIndexOf(".");
            String attributeName = field.name().substring(idx + 1);
            if (MULTIVALUED_ATTRIBUTES.contains(attributeName)) {
                if (multivaluedAttributesData.containsKey(attributeName)) {
                    multivaluedAttributesData.get(attributeName).add(fieldValue);
                } else {
                    List<String> values = new ArrayList<>();
                    values.add(fieldValue);
                    multivaluedAttributesData.put(attributeName,values);
                }
            } else {
                dataFields.addStringField(attributeName, fieldValue);
            }
        }
        if (!multivaluedAttributesData.isEmpty()) {
            multivaluedAttributesData.forEach(dataFields::addListField);
        }
        return dataFields;
    }
}
