package test;

import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ar.edu.unlp.StanfordCoreParser;
import edu.stanford.nlp.util.CoreMap;

public class TestStanfordCoreParser {

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
	public void test01() {
		System.out.println("basic pipeline test");
		try {
			StanfordCoreParser nerPosParser = new StanfordCoreParser();
			nerPosParser.doParser(TestConstants.SENTENCE_01);
			System.out.println("print: ");
			nerPosParser.print();
			System.out.println("prettyPrint: ");
			nerPosParser.prettyPrint();
			System.out.println("CoreMap: ");
			CoreMap sentence = nerPosParser.getFirstSentenceAsMap();
			System.out.println("The keys of the first sentence's CoreMap are:");
			System.out.println(sentence.keySet());
			System.out.println();
			System.out.println("The first sentence is:");
			System.out.println(sentence.toShorterString());
			System.out.println();
			//nerPosParser.toXML("test.xml");
			
		}catch(Exception e) {
			e.printStackTrace();
			fail(e.getMessage());	
		}
	}

}
