package org.cancer_models.entity2ontology.exceptions;

/**
 * This exception is thrown to indicate that there is an error
 * in the configuration represented by the {@code MappingConfiguration} object.
 *
 * <p>MalformedMappingConfigurationException is a checked exception,
 * extending {@code Exception}, to indicate an issue with the mapping configuration that needs to be fixed.
 */
public class MalformedMappingConfigurationException extends Exception {

    /**
     * Constructs a new exception with {@code null} as its detail message.
     * The cause is not initialized and may be set later with {@link #initCause}.
     */
    public MalformedMappingConfigurationException() {
        super();
    }

    /**
     * Constructs a new exception with the specified detail message.
     * The cause is not initialized and may be set later with {@link #initCause}.
     *
     * @param message the detail message, which can be retrieved later with {@link #getMessage()}
     */
    public MalformedMappingConfigurationException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     *
     * @param message the detail message, which can be retrieved later with {@link #getMessage()}
     * @param cause the cause of the exception, which can be retrieved later with {@link #getCause()}
     */
    public MalformedMappingConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception with the specified cause and a detail message
     * of {@code (cause==null ? null : cause.toString())}.
     *
     * @param cause the cause of the exception, which can be retrieved later with {@link #getCause()}
     */
    public MalformedMappingConfigurationException(Throwable cause) {
        super(cause);
    }
}