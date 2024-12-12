package org.cancer_models.entity2ontology.exceptions;

/**
 * This exception is thrown to indicate that there is an error
 * in the configuration represented by the {@code MappingConfiguration} object.
 *
 * <p>MalformedMappingConfiguration is an unchecked exception,
 * extending {@code RuntimeException}, to indicate an issue that
 * typically results from programming errors or invalid configuration.
 */
public class MalformedMappingConfiguration extends RuntimeException {

    /**
     * Constructs a new exception with {@code null} as its detail message.
     * The cause is not initialized and may be set later with {@link #initCause}.
     */
    public MalformedMappingConfiguration() {
        super();
    }

    /**
     * Constructs a new exception with the specified detail message.
     * The cause is not initialized and may be set later with {@link #initCause}.
     *
     * @param message the detail message, which can be retrieved later with {@link #getMessage()}
     */
    public MalformedMappingConfiguration(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     *
     * @param message the detail message, which can be retrieved later with {@link #getMessage()}
     * @param cause the cause of the exception, which can be retrieved later with {@link #getCause()}
     */
    public MalformedMappingConfiguration(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception with the specified cause and a detail message
     * of {@code (cause==null ? null : cause.toString())}.
     *
     * @param cause the cause of the exception, which can be retrieved later with {@link #getCause()}
     */
    public MalformedMappingConfiguration(Throwable cause) {
        super(cause);
    }
}