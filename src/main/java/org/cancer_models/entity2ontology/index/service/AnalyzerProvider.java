package org.cancer_models.entity2ontology.index.service;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.springframework.stereotype.Component;

/**
 * Provides a centralised instance of a Lucene Analyser so all processes
 * requiring one (indexing and searching) use the same one.
 */
@Component
public class AnalyzerProvider {

  private Analyzer analyzer;

  /**
   * Returns the analyser to be used in the application.
   * @return {@link Analyzer} object configured with stop works for English.
   */
  public Analyzer getAnalyzer() {
    if (analyzer == null) {
      analyzer = generateNewAnalyzer();
    }
    return analyzer;
  }

  public Analyzer generateNewAnalyzer() {
    CharArraySet enStopSet = EnglishAnalyzer.ENGLISH_STOP_WORDS_SET;
    return new StandardAnalyzer(enStopSet);
  }
}
