package org.cancer_models.entity2ontology.map.service;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class QueryProcessorTest {

    @Mock
    private Searcher searcherMock;

    private final QueryProcessor queryProcessor = new QueryProcessor(searcherMock);

    @Test
    void givenNullQuery_whenExecuteQuery_thenFails() {
        // Given a null query is used
        // When we try to process the query
        NullPointerException exception = assertThrows(NullPointerException.class, () ->
        {
            queryProcessor.executeQuery(null, "null");
        });
        // Then we get an NullPointerException
        assertEquals("query cannot be null", exception.getMessage());
    }

    @Test
    void givenNullIndexPath_whenExecuteQuery_thenFails() {
        // Given a null indexPath is used
        Query dummyQuery = new TermQuery(new Term(""));
        // When we try to process the query;
        NullPointerException exception = assertThrows(NullPointerException.class, () ->
        {
            queryProcessor.executeQuery(dummyQuery, null);
        });
        // Then we get an NullPointerException
        assertEquals("indexPath cannot be null", exception.getMessage());
    }
}
