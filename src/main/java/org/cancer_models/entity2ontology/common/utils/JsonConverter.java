package org.cancer_models.entity2ontology.common.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class JsonConverter {

    // Suppress default constructor for non-instantiability
    private JsonConverter() {
        throw new AssertionError();
    }

    private static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.registerModule(new JavaTimeModule());
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss a z");
        objectMapper.setDateFormat(df);
    }

    /**
     * Reads a JSON file and converts its content into an object of the specified type.
     *
     * <p>This method reads a JSON file from the given file path and deserializes it into an object of the specified type
     * using Jackson's ObjectMapper.</p>
     *
     * @param <T> the type of the object to be returned
     * @param jsonFile JSON file
     * @param valueType the class of the object to be returned
     * @return an object of type {@code T} deserialized from the JSON file
     * @throws IOException if an error occurs while reading the file or during deserialization
     */
    public static <T> T fromJsonFile(File jsonFile, Class<T> valueType) throws IOException {
        return objectMapper.readValue(jsonFile, valueType);
    }

    /**
     * Writes an object to a JSON file.
     *
     * <p>This method serializes the given object into JSON format and writes it to the specified file path
     * using Jackson's ObjectMapper.</p>
     *
     * @param <T> the type of the object to be written to the file
     * @param object the object to be serialized and written to the file
     * @param jsonFilePath the path to the file where the JSON content will be written
     * @throws IOException if an error occurs while writing to the file or during serialization
     */
    public static <T> void toJsonFile(T object, String jsonFilePath) throws IOException
    {
        File file = new File(jsonFilePath);
        objectMapper.writeValue(file, object);
    }
}
