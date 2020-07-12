package test;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ar.edu.unlp.StanfordCoreParser;
import ar.edu.unlp.entities.SentenceData;

public class TestSentenceDataExtracted {

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
		System.out.println("Test the extracted information from a Sentence");
		String testStentence = TestConstants.SENTENCE_04;		
		try {
			StanfordCoreParser nerPosParser = new StanfordCoreParser();
			List<SentenceData> sentenceDataList = nerPosParser.doParser(testStentence);
			SentenceData firstSentenceData = sentenceDataList.get(0);
			
			System.out.println("Pattern line:");
			System.out.println(firstSentenceData.getPatternLine());
			System.out.println();
			
			
			String[] listOfWords =  testStentence.replaceAll("'", " '").replaceAll("\\.", " .").split(" ");
			StringBuilder sb = new StringBuilder();
			for (String word : listOfWords) {
				sb.append(firstSentenceData.getWordPOSTAG().get(word));
				sb.append(" ");
			}
			
			System.out.println("Sentence as POS Tag:");
			System.out.println(firstSentenceData.getSentenceAsPOSTags());
			Assert.assertNotEquals(firstSentenceData.getSentenceAsPOSTags().length(), 0);
			if(!sb.toString().trim().equals(firstSentenceData.getSentenceAsPOSTags())){
				System.out.println("   Debria ser:");
				System.out.println(sb.toString().trim());
				fail("Sentence as POS Tag is not well formed");
			}			
			System.out.println();
						
			System.out.println("Tree dependencies Line:");
			System.out.println(firstSentenceData.getTreeDependenciesLine());
			Assert.assertNotEquals(firstSentenceData.getTreeDependenciesLine().length(), 0);
			System.out.println();
			
			System.out.println("Dependencies Graph:");
			System.out.println(firstSentenceData.getDependenciesGraph().toString());
			Assert.assertNotEquals(firstSentenceData.getDependenciesGraph().toString().length(), 0);
			System.out.println();
			
			System.out.println("MAPA DE POSTAG:");
			for (String key : firstSentenceData.getWordPOSTAG().keySet()) {
				System.out.println(key+" = > "+firstSentenceData.getWordPOSTAG().get(key));
			}
			Assert.assertNotEquals(firstSentenceData.getWordPOSTAG().keySet().size(), 0);
			System.out.println();
			System.out.println("MAPA DE NER:");
			for (String key : firstSentenceData.getWordNER().keySet()) {
				System.out.println(key+" = > "+firstSentenceData.getWordNER().get(key));
			}
			Assert.assertNotEquals(firstSentenceData.getWordNER().keySet().size(), 0);
			
			
		}catch(Exception e) {
			e.printStackTrace();
			fail(e.getMessage());	
		}
	}

}
