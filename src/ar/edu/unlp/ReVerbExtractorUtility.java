package ar.edu.unlp;

import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import ar.edu.unlp.constants.Words;
import ar.edu.unlp.entities.Relation;
import ar.edu.unlp.entities.SentenceData;
import edu.washington.cs.knowitall.extractor.ReVerbExtractor;
import edu.washington.cs.knowitall.extractor.conf.ConfidenceFunction;
import edu.washington.cs.knowitall.extractor.conf.ConfidenceFunctionException;
import edu.washington.cs.knowitall.extractor.conf.ReVerbOpenNlpConfFunction;
import edu.washington.cs.knowitall.nlp.ChunkedSentence;
import edu.washington.cs.knowitall.nlp.ChunkedSentenceReader;
import edu.washington.cs.knowitall.nlp.extraction.ChunkedBinaryExtraction;
import edu.washington.cs.knowitall.util.DefaultObjects;

public class ReVerbExtractorUtility {
	
	protected ChunkedSentenceReader sentReader;
	protected ConfidenceFunction scoreFunc;
	protected ReVerbExtractor extractor;
	
	public ReVerbExtractorUtility() {
		

        System.err.print("Initializing extractor...");
        extractor = new ReVerbExtractor();
        System.err.println("Done.");

        System.err.print("Initializing confidence function...");
        scoreFunc = null;
		try {
			scoreFunc = new ReVerbOpenNlpConfFunction();
		} catch (ConfidenceFunctionException | IOException e) {
			e.printStackTrace();
		}
        System.err.println("Done.");

        System.err.print("Initializing NLP tools...");
        sentReader = null;
		
	}
	
	public List<Relation> extractRelationsFromLine(String line) {

		BufferedReader reader = null;
		List<Relation> relationList = new ArrayList<Relation>();
	      
    	Reader inputString = new StringReader(line);
    	reader = new BufferedReader(inputString);
    	
		try {
			sentReader = DefaultObjects.getDefaultSentenceReader(reader);
		} catch (IOException e) {				
			e.printStackTrace();
			fail(e.getMessage());
		}
        System.err.println("Done.");

       
        for (ChunkedSentence sent : sentReader.getSentences()) {

           //String sentString = sent.getTokensAsString();
            
            for (ChunkedBinaryExtraction extr : extractor.extract(sent)) {

                double score = scoreFunc.getConf(extr);
                Relation rel = new Relation();
                rel.setEntity1(extr.getArgument1().toString().replaceAll(Words.WILDCARD_QUOTED_REVERB_SET, Words.WILDCARD_QUOTED));
                rel.setRelation(extr.getRelation().toString().replaceAll(Words.WILDCARD_QUOTED_REVERB_SET, Words.WILDCARD_QUOTED));
                rel.setEntity2(extr.getArgument2().toString().replaceAll(Words.WILDCARD_QUOTED_REVERB_SET, Words.WILDCARD_QUOTED));
                rel.setScore((int)(score*100));
                relationList.add(rel);
            }
        }
        return relationList;
	}

}
