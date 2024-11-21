package org.cancer_models.entity2ontology.common.utils;

import java.util.List;
import java.util.stream.Collectors;

public class GeneralUtils {

    // Suppress default constructor for non-instantiability
    private GeneralUtils() {
        throw new AssertionError();
    }

    /**
     * Safely casts an {@link Object} to a {@link List} of the specified type.
     *
     * This method checks if the provided object is an instance of {@link List} and ensures that
     * all elements in the list are of the specified type {@code clazz}. If the object is not a list
     * or contains elements that cannot be cast to the specified type, a {@link ClassCastException} is thrown.
     *
     * @param <T>   The type of the elements in the list.
     * @param obj   The object to cast. This should be a {@link List} of elements of type {@code clazz}.
     * @param clazz The {@link Class} object representing the type to cast the list elements to.
     * @return A {@link List} of elements of type {@code T}.
     * @throws ClassCastException if the object is not a {@link List} or contains elements that are not of type {@code clazz}.
     */
    public static <T> List<T> castList(Object obj, Class<T> clazz) {
        if (obj instanceof List<?>) {
            return ((List<?>) obj).stream()
                .map(clazz::cast)
                .collect(Collectors.toList());
        }
        throw new ClassCastException("Object is not a List of " + clazz.getSimpleName());
    }
}
