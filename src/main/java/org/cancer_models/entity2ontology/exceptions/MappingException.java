package org.cancer_models.entity2ontology.exceptions;

import org.cancer_models.entity2ontology.map.model.SourceEntity;

/**
 * This exception is thrown to indicate that there was an error
 * trying to execute the mapping process for a {@link SourceEntity} .
 *
 * <p>MappingException is a checked exception,
 * extending {@code Exception}, to indicate an issue in the mapping process.
 */
public class MappingException extends Exception {

    /**
     * Constructs a new exception with {@code null} as its detail message.
     * The cause is not initialized and may be set later with {@link #initCause}.
     */
    public MappingException() {
        super();
    }

    /**
     * Constructs a new exception with the specified detail message.
     * The cause is not initialized and may be set later with {@link #initCause}.
     *
     * @param message the detail message, which can be retrieved later with {@link #getMessage()}
     */
    public MappingException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     *
     * @param message the detail message, which can be retrieved later with {@link #getMessage()}
     * @param cause the cause of the exception, which can be retrieved later with {@link #getCause()}
     */
    public MappingException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception with the specified cause and a detail message
     * of {@code (cause==null ? null : cause.toString())}.
     *
     * @param cause the cause of the exception, which can be retrieved later with {@link #getCause()}
     */
    public MappingException(Throwable cause) {
        super(cause);
    }
}