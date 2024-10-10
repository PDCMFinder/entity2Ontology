package org.cancerModels.entity2ontology.common.utils;

import java.util.Map;

public class MapUtils {

    /**
     * Retrieves the value associated with the given key from the map.
     * Throws an IllegalArgumentException if the key is not found.
     *
     * @param map the map from which to retrieve the value
     * @param key the key whose associated value is to be returned
     * @return the value associated with the specified key
     * @throws IllegalArgumentException if the key is not found in the map
     */
    public static <K, V> V getValueOrThrow(Map<K, V> map, K key, String mapDescription) {
        V value = map.get(key);
        if (value == null && !map.containsKey(key)) {
            String error;
            if (mapDescription != null) {
                error = "Key '" + key + "' not found in the map '" + mapDescription + "'.";
            } else {
                error = "Key '" + key + "' not found in the map.'";
            }
            throw new IllegalArgumentException(error);
        }
        return value;
    }
}
