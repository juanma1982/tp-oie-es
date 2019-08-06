package test;

import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.washington.cs.knowitall.extractor.ReVerbExtractor;
import edu.washington.cs.knowitall.extractor.conf.ConfidenceFunction;
import edu.washington.cs.knowitall.extractor.conf.ConfidenceFunctionException;
import edu.washington.cs.knowitall.extractor.conf.ReVerbOpenNlpConfFunction;
import edu.washington.cs.knowitall.nlp.ChunkedSentence;
import edu.washington.cs.knowitall.nlp.ChunkedSentenceReader;
import edu.washington.cs.knowitall.nlp.extraction.ChunkedBinaryExtraction;
import edu.washington.cs.knowitall.util.DefaultObjects;

public class TestReverb {

	public static final String LINE ="Albert Einstein was awarded the Nobel Prize in Sweden in 1921.";
	public static final String Paragraph = "U.S. farmers who in the past have grown oats for their own use but failed to certify to the government that they had done so probably will be allowed to continue planting that crop and be eligible for corn program benefits, an aide to Agriculture Secretary Richard Lyng said. Currently a farmer, to be eligible for corn program benefits, must restrict his plantings of other program crops to the acreage base for that crop. Several members of Congress from Iowa have complained that farmers who inadvertantly failed to certify that they had grown oats for their own use in the past now are being asked to halt oats production or lose corn program benefits. USDA likely will allow historic oats farmers to plant oats but not extend the exemption to all farmers, Lyng's aide said.";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		 	BufferedReader reader = null;
	      
        	Reader inputString = new StringReader(Paragraph);
        	reader = new BufferedReader(inputString);
			

	        int sentenceCount = 0;
	        int extractionCount = 0;

	        System.err.print("Initializing extractor...");
	        ReVerbExtractor extractor = new ReVerbExtractor();
	        System.err.println("Done.");

	        System.err.print("Initializing confidence function...");
	        ConfidenceFunction scoreFunc = null;
			try {
				scoreFunc = new ReVerbOpenNlpConfFunction();
			} catch (ConfidenceFunctionException | IOException e) {
				e.printStackTrace();
			}
	        System.err.println("Done.");

	        System.err.print("Initializing NLP tools...");
	        ChunkedSentenceReader sentReader = null;
			try {
				sentReader = DefaultObjects.getDefaultSentenceReader(reader);
			} catch (IOException e) {				
				e.printStackTrace();
				fail(e.getMessage());
			}
	        System.err.println("Done.");

	       
	        for (ChunkedSentence sent : sentReader.getSentences()) {

	            sentenceCount++;

	            String sentString = sent.getTokensAsString();
	            System.out.println(String.format("sentence\t%s\t%s", sentenceCount, sentString));

	            for (ChunkedBinaryExtraction extr : extractor.extract(sent)) {

	                double score = scoreFunc.getConf(extr);

	                String arg1 = extr.getArgument1().toString();
	                String rel = extr.getRelation().toString();
	                String arg2 = extr.getArgument2().toString();

	                String extrString = sentenceCount+"\t("+arg1+", "+ rel+", "+ arg2+") score:"+ score;

	                System.out.println("extraction\t" + extrString);

	                extractionCount++;
	            }
	        }

	        System.err.println(String.format("Got %s extractions from %s sentences.", extractionCount, sentenceCount));
	}

}
