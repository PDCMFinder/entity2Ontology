package org.cancer_models.entity2ontology.common.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents the data fields of a {@link TargetEntity} that are intended for indexing
 * in a Lucene document. This class provides functionality to manage and differentiate
 * between single-value fields (mapped as {@code String}) and multi-value fields
 * (mapped as {@code List<String>}).
 *
 * <p>The fields are stored in two separate maps:
 * <ul>
 *   <li>{@code stringFields} - A map for fields where the values are single strings.</li>
 *   <li>{@code listFields} - A map for fields where the values are lists of strings.</li>
 * </ul>
 *
 * <p>This structure simplifies the handling of different types of data and avoids
 * mixing single and multi-value fields in the same map.
 *
 * <h2>Example Usage:</h2>
 * <pre>
 * // Create an instance of TargetEntityDataFields
 * TargetEntityDataFields dataFields = new TargetEntityDataFields();
 *
 * // Add a single-value field
 * dataFields.addStringField("title", "My Document Title");
 *
 * // Add a multi-value field
 * dataFields.addListField("tags", List.of("tag1", "tag2", "tag3"));
 *
 * // Retrieve fields
 * String title = dataFields.getStringField("title");
 * List<String> tags = dataFields.getListField("tags");
 * </pre>
 *
 * @see TargetEntity
 */
public class TargetEntityDataFields {

    // Map to store fields with single string values
    private final Map<String, String> stringFields = new HashMap<>();

    // Map to store fields with list of string values
    private final Map<String, List<String>> listFields = new HashMap<>();

    /**
     * Adds a field with a single string value to the {@code stringFields} map.
     *
     * @param key   the name of the field
     * @param value the single string value associated with the field
     * @throws IllegalArgumentException if the key is already present in {@code listFields}
     */
    public void addStringField(String key, String value) {
        if (listFields.containsKey(key)) {
            throw new IllegalArgumentException("Field '" + key + "' already exists in listFields.");
        }
        stringFields.put(key, value);
    }

    /**
     * Adds a field with a list of string values to the {@code listFields} map.
     *
     * @param key    the name of the field
     * @param values the list of string values associated with the field
     * @throws IllegalArgumentException if the key is already present in {@code stringFields}
     */
    public void addListField(String key, List<String> values) {
        if (stringFields.containsKey(key)) {
            throw new IllegalArgumentException("Field '" + key + "' already exists in stringFields.");
        }
        listFields.put(key, values);
    }

    /**
     * Retrieves the value of a single-value field.
     *
     * @param key the name of the field
     * @return the value of the field, or {@code null} if the field does not exist
     */
    public String getStringField(String key) {
        return stringFields.get(key);
    }

    /**
     * Retrieves the value of a multi-value field.
     *
     * @param key the name of the field
     * @return the list of values associated with the field, or {@code null} if the field does not exist
     */
    public List<String> getListField(String key) {
        return listFields.get(key);
    }

    /**
     * Checks if a single-value field exists in {@code stringFields}.
     *
     * @param key the name of the field to check
     * @return {@code true} if the field exists, {@code false} otherwise
     */
    public boolean hasStringField(String key) {
        return stringFields.containsKey(key);
    }

    /**
     * Checks if a multi-value field exists in {@code listFields}.
     *
     * @param key the name of the field to check
     * @return {@code true} if the field exists, {@code false} otherwise
     */
    public boolean hasListField(String key) {
        return listFields.containsKey(key);
    }

    /**
     * Retrieves all single-value fields as an unmodifiable map.
     *
     * @return an unmodifiable view of {@code stringFields}
     */
    public Map<String, String> getStringFields() {
        return Map.copyOf(stringFields);
    }

    /**
     * Retrieves all multi-value fields as an unmodifiable map.
     *
     * @return an unmodifiable view of {@code listFields}
     */
    public Map<String, List<String>> getListFields() {
        return Map.copyOf(listFields);
    }
}
