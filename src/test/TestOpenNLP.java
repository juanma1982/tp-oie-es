package test;

import static org.junit.Assert.fail;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ar.edu.unlp.utils.Chunker;
import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.util.InvalidFormatException;

public class TestOpenNLP {

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
	public void testEn() {
		InputStream modelIn = null;
		ChunkerModel model = null;
		try {
			modelIn = new FileInputStream("opennlp-models/en-chunker.bin");
			model = new ChunkerModel(modelIn);
		}catch(Exception e){
			fail(e.getMessage());
		}
		

						
		ChunkerME chunker = new ChunkerME(model);
		

				  
		/*String sent[] = new String[] { "Rockwell", "International", "Corp.", "'s",
		    "Tulsa", "unit", "said", "it", "signed", "a", "tentative", "agreement",
		    "extending", "its", "contract", "with", "Boeing", "Co.", "to",
		    "provide", "structural", "parts", "for", "Boeing", "'s", "747",
		    "jetliners", "." };

		String pos[] = new String[] { "NNP", "NNP", "NNP", "POS", "NNP", "NN",
		    "VBD", "PRP", "VBD", "DT", "JJ", "NN", "VBG", "PRP$", "NN", "IN",
		    "NNP", "NNP", "TO", "VB", "JJ", "NNS", "IN", "NNP", "POS", "CD", "NNS",
		    "." };*/
		String sent[] = new String[] {"Albert", "Einstein", "was", "awarded", "the", "Nobel", "Prize", "in", "Sweden", "in", "1921", "." };
		String pos[] = new String[] { "NNP", "NNP", "VBD", "VBD", "DT", "NNP", "NNP", "IN", "NNP", "IN", "CD", "."} ;

		String tag[] = chunker.chunk(sent, pos);
		
		for (String string : tag) {
			System.out.println(string);	
		}
		

	}
	
	@Test
	public void test() {
		Chunker chunker;
		try {
			chunker = new Chunker();
			String sent[] = new String[] {"Albert", "Einstein", "recibió", "el", "Premio", "Nobel", "en", "Suecia", "en", "1921", "."};
			String pos[] = new String[] { "PROPN", "PROPN",     "VERB",    "DET", "PROPN", "PROPN", "ADP", "PROPN", "ADP", "NUM", "PUNCT"};
			String expectedTags[] = new String[]{"B-NP", "I-NP", "B-VP", "B-NP", "I-NP", "I-NP", "B-PP", "B-NP", "B-PP", "B-NP", "O"};
			String tags[]= chunker.universalPOSChunk(sent, pos);

			for (int i = 0; i < tags.length; i++) {
				System.out.println(tags[i]);
				if(!tags[i].equals(expectedTags[i])) {
					fail("not equal tag");
				}
			}
	
		} catch (InvalidFormatException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
				

	}

}
