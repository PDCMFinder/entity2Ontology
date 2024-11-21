package org.cancer_models.entity2ontology.index.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cancer_models.entity2ontology.common.model.OntologyTerm;
import org.cancer_models.entity2ontology.common.utils.FileUtils;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Class responsible for downloading ontology terms from the OLS API.
 */
public class OntologyDownloader {
    private static final String BASE_URL = "https://www.ebi.ac.uk/ols4/api/ontologies/";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LogManager.getLogger(OntologyDownloader.class);

    /**
     * Downloads the ontology terms for a given ontology name and term ID.
     *
     * @param ontologyId The id of the ontology (e.g., "ncit").
     * @param termId       The ID of the root term (e.g., "NCIT_C35814").
     * @return A list of OntologyTerm objects containing the root term and its descendants.
     * @throws IOException if an I/O error occurs.
     */
    public Set<OntologyTerm> downloadOntologyTerms(
        String ontologyId, String termId, String type) throws IOException {
        validateInput(ontologyId, termId);
        Set<OntologyTerm> terms = new HashSet<>();
        String encodedTermId = URLEncoder.encode("http://purl.obolibrary.org/obo/" + termId, StandardCharsets.UTF_8);
        // This needs double encoding (OLS documentation)
        encodedTermId = URLEncoder.encode(encodedTermId, StandardCharsets.UTF_8);

        String rootUrl = BASE_URL + ontologyId + "/terms/" + encodedTermId;

        // Get the root term information
        String jsonResponse = FileUtils.getStringFromUrl(rootUrl);
        JsonNode jsonNode = objectMapper.readTree(jsonResponse);
        OntologyTerm rootTerm = parseTerm(jsonNode, type);
        terms.add(rootTerm);

        // Get all hierarchical descendants
        String descendantsUrl = getDescendantsUrl(jsonNode);
        if (descendantsUrl != null) {
            terms.addAll(getAllDescendants(descendantsUrl, type));
        }
        return terms;
    }

    private void validateInput(String ontologyName, String termId) {
        if (ontologyName == null) {
            throw new IllegalArgumentException("Ontology name cannot be null");
        }
        if (termId == null) {
            throw new IllegalArgumentException("termId cannot be null");
        }
        try {
            validateOntologyExists(ontologyName);
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format("The ontology %s does not exist in OLS", ontologyName));
        }
    }

    private void validateOntologyExists(String ontologyName) throws IOException {
        String r = FileUtils.getStringFromUrl(BASE_URL + ontologyName);
        if (r.contains("\"status\" : 500")) {
            throw new IllegalArgumentException();
        }
    }

    private String getDescendantsUrl(JsonNode jsonNode) throws IOException {
        String url = null;
        JsonNode links = jsonNode.path("_links");
        if (links.has("hierarchicalDescendants")) {
            JsonNode hierarchicalDescendantsNode = links.path("hierarchicalDescendants");
            url = hierarchicalDescendantsNode.get("href").asText() + "?size=1000";
        }

        return url;
    }

    private Set<OntologyTerm> getAllDescendants(String descendantsUrl, String ontologyType) {

        Set<OntologyTerm> ontologyTerms = new HashSet<>();

        //Start calling it with initial. Then keep until it needs to stop
        String nextUrl = descendantsUrl;

        String lastUrl = null;

        while (true) {
            try {

                String jsonResponse = FileUtils.getStringFromUrl(nextUrl);
                JsonNode jsonNode = objectMapper.readTree(jsonResponse);

                ontologyTerms.addAll(parseDescendantsResponseJson(jsonNode, ontologyType));

                if (ontologyTerms.size() % 1000 == 0) {
                    logger.info("{} records for {}", ontologyTerms.size(), nextUrl);
                }
                JsonNode links = jsonNode.path("_links");

                // Get last url if not yet defined
                if (lastUrl == null) {
                    if (links.has("last")) {
                        JsonNode lastUrlObject = links.path("last");
                        lastUrl = lastUrlObject.get("href").asText();
                    }
                    // If no last, there are no more pages so we can stop
                    else {
                        break;
                    }
                }

                if (nextUrl.equals(lastUrl)) {
                    break;
                }

                JsonNode nextUrlObject = links.path("next");
                nextUrl = nextUrlObject.get("href").asText();
            } catch (IOException e) {
                String error = e.getClass().getCanonicalName() + ": " + e.getMessage();
                logger.error(error);
            }
        }
        return ontologyTerms;
    }

    private OntologyTerm parseTerm(JsonNode jsonNode, String ontologyType) {

        String id = jsonNode.path("short_form").asText();
        String label = jsonNode.path("label").asText();
        String url = jsonNode.path("iri").asText();
        String description = "";

        if (jsonNode.has("description")) {
            JsonNode descriptionNode = jsonNode.path("description");
            if (descriptionNode.isArray() && !descriptionNode.isEmpty()) {
                description = descriptionNode.get(0).asText();
            }
        }

        List<String> synonyms = new ArrayList<>();
        if (jsonNode.has("synonyms")) {
            JsonNode synonymArray = jsonNode.path("synonyms");
            if (synonymArray.isArray()) {
                for (JsonNode synonymNode : synonymArray) {
                    synonyms.add(synonymNode.asText());
                }
            }
        }
        return new OntologyTerm(id, url, label, ontologyType, description, synonyms);
    }

    private Set<OntologyTerm> parseDescendantsResponseJson(JsonNode node, String ontologyType) {
        Set<OntologyTerm> ontologyTerms = new HashSet<>();

        if (!node.has("_embedded")) {
            return ontologyTerms;
        }
        JsonNode jsonObject = node.path("_embedded");
        JsonNode termArray = jsonObject.path("terms");
        if (termArray.isArray()) {
            for (JsonNode termNode : termArray) {
                ontologyTerms.add(parseTerm(termNode, ontologyType));
            }
        }
        return ontologyTerms;
    }
}
