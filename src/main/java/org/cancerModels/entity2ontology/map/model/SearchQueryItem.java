package org.cancerModels.entity2ontology.map.model;

import lombok.*;

/**
 * Encapsulates information about a term to be used in a Lucene query.
 * <p>
 * This class holds key attributes required for constructing a Lucene query, including the field name,
 * the value to search for, the maximum number of edits allowed for fuzzy matching (fuzziness), and
 * the boost factor (weight) applied to the query term.
 * <p>
 * By encapsulating these elements in a single class, it simplifies query construction and improves
 * code readability when working with complex search queries.
 */
@Getter
@Setter
@Builder
@ToString
public class SearchQueryItem {
    /**
     * The name of the field in the document where the term will be searched.
     * <p>
     * This field cannot be null.
     */
    @NonNull
    private String field;

    /**
     * The value to be searched in the specified field.
     * <p>
     * This field cannot be null.
     */
    @NonNull
    private String value;

    /**
     * The boost factor (weight) to apply to this query term.
     * Defaults to 1.
     */
    @Builder.Default
    private double weight = 1.0;

    /**
     * The maximum number of edits (fuzziness) allowed when performing fuzzy matching for this term.
     * This allows the query to match terms that are similar to the value but not identical.
     * Defaults to 0 (exact match).
     */
    @Builder.Default
    private double maxEdits = 0.0;
}
