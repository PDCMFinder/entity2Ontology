package org.cancerModels.entity2ontology.map.service;

import org.cancerModels.entity2ontology.map.model.SourceEntity;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MappingServiceTest {
    @Test
    public void shouldFailIfNullEntity() {
        // When we try to map an entity that is null
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
        {
            MappingService mappingService = new MappingService();
            mappingService.mapEntity(null, "", 0);
        });

        // Then we get an IOException
        assertEquals("Entity cannot be null", exception.getMessage());
    }

    @Test
    public void shouldFailIfNullEntityId() {
        // When we try to map an entity that is null
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
        {
            MappingService mappingService = new MappingService();
            SourceEntity sourceEntity = new SourceEntity();
            mappingService.mapEntity(sourceEntity, "", 0);
        });

        // Then we get an IOException
        assertEquals("Entity id cannot be null", exception.getMessage());
    }

    @Test
    public void shouldFailIfNullEntityType() {
        // When we try to map an entity that is null
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
        {
            MappingService mappingService = new MappingService();
            SourceEntity sourceEntity = new SourceEntity();
            sourceEntity.setId("1");
            mappingService.mapEntity(sourceEntity, "", 0);
        });

        // Then we get an IOException
        assertEquals("Entity type cannot be null", exception.getMessage());
    }

    @Test
    public void shouldFailIfNullEntityData() {
        // When we try to map an entity that is null
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
        {
            MappingService mappingService = new MappingService();
            SourceEntity sourceEntity = new SourceEntity();
            sourceEntity.setId("1");
            sourceEntity.setType("type");
            mappingService.mapEntity(sourceEntity, "", 0);
        });

        // Then we get an IOException
        assertEquals("Entity data cannot be null", exception.getMessage());
    }

    @Test
    public void shouldFailIfNullIndexName() {
        // When we try to map an entity without specifying the index
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
        {
            MappingService mappingService = new MappingService();
            SourceEntity sourceEntity = new SourceEntity();
            sourceEntity.setId("1");
            sourceEntity.setType("type");
            sourceEntity.setData(new HashMap<>());
            mappingService.mapEntity(sourceEntity, null, 0);
        });

        // Then we get an IOException
        assertEquals("Index cannot be null", exception.getMessage());
    }

    @Test
    public void shouldFailIfIndexNotExists() {
        // When we try to map an entity using an invalid index
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
        {
            MappingService mappingService = new MappingService();
            SourceEntity sourceEntity = new SourceEntity();
            sourceEntity.setId("1");
            sourceEntity.setType("type");
            sourceEntity.setData(new HashMap<>());
            mappingService.mapEntity(sourceEntity, "unknown", 0);
        });

        // Then we get an IOException
        assertEquals("Index [unknown] is not a valid lucene index", exception.getMessage());
    }
}
