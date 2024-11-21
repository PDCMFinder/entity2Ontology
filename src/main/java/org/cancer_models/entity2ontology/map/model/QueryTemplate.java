package org.cancer_models.entity2ontology.map.model;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A string with the format "${key1} ${key2} ${keyN}" which is used to specify the fields (and their order) to be used
 * in a "phrase" query. The values for key1, key2, etc. correspond to fields in indexed documents.
 * An example of a template:
 * - ${TumorType} ${SampleDiagnosis} ${OriginTissue}
 */
public class QueryTemplate {
    /**
     * Regexp pattern to identify the placeholders in the template. A placeholder example is ${key1}.
     */
    private static final String PLACEHOLDER_PATTERN = "\\$\\{([a-zA-Z][a-zA-Z0-9]*)\\}";

    /**
     * The text of the template.
     */
    private final String text;

    public QueryTemplate(String templateText) {
        this.text = templateText;
        validateTemplateText(templateText);
    }

    private void validateTemplateText(String templateText) {
        if (templateText == null) {
            throw new IllegalArgumentException("Template text cannot be null");
        }
        if (templateText.isEmpty()) {
            throw new IllegalArgumentException("Template text cannot be empty");
        }
        if (extractKeys().isEmpty()) {
            throw new IllegalArgumentException(
                "The template does not contain any valid keys. A valid template has the format: ${key1} ${key2} ${keyN}");
        }
    }

    /**
     * Gets the text of the template.
     *
     * @return String with the text of the template.
     */
    public String getText() {
        return text;
    }

    /**
     * Extracts keys from a templated string.
     * <p>
     * The method looks for placeholders in the form of `${key}`, where `key` is any sequence of letters, and returns
     * a list of all the unique keys found in the template.
     *
     * @return A list of keys (the content inside `${}`) found in the template.
     */
    public List<String> extractKeys() {
        List<String> keys = new ArrayList<>();
        // Regular expression to match placeholders in the form ${key}
        Pattern pattern = Pattern.compile(PLACEHOLDER_PATTERN);
        Matcher matcher = pattern.matcher(text);

        // Find all matches and add the key (without ${}) to the list
        while (matcher.find()) {
            keys.add(matcher.group(1));  // group(1) captures the content inside ${}
        }

        return keys;
    }
}
