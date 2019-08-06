package test;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ar.edu.unlp.utils.ParagraphStanford;

public class TestParagraphSplitter {
	
	public static final String PARAGRAPHTEST = "At first, the collectivists worked with the Marxists to push the First International in a more revolutionary socialist direction. Subsequently, the International became polarised into two camps, with Marx and Bakunin as their respective figureheads. Mikhail Bakunin characterised Marx's ideas as centralist and predicted that if a Marxist party came to power, its leaders would simply take the place of the ruling class they had fought against. Anarchist historian George Woodcock reports: \"The annual Congress of the International had not taken place in 1870 owing to the outbreak of the Paris Commune, and in 1871 the General Council called only a special conference in London. One delegate was able to attend from Spain and none from Italy, while a technical excuse – that they had split away from the Fédération Romande – was used to avoid inviting Bakunin's Swiss supporters. Thus only a tiny minority of anarchists was present, and the General Council's resolutions passed almost unanimously. Most of them were clearly directed against Bakunin and his followers\". In 1872, the conflict climaxed with a final split between the two groups at the Hague Congress, where Bakunin and James Guillaume were expelled from the International and its headquarters were transferred to New York. In response, the federalist sections formed their own International at the St. Imier Congress, adopting a revolutionary anarchist programme.";
	public static final String PARAGRAPHTEST02 = "\"It was clear to us when we visited there in October that the Romanians hated those who had left the country,\"said Liselotte Leicht, program director at the Helsinki Federation.";
	public static final int EXPECTED_SENTENCES = 9;
	public static final int EXPECTED_SENTENCES02= 2;
	public ParagraphStanford paragraph=null;
	
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
	public void testNOQuoted() {
		System.out.println("");
		System.out.println("Test 01: No quoted");
		try {
			this.paragraph = new ParagraphStanford();
		} catch (IOException e) {			
			e.printStackTrace();
			fail(e.getMessage());
		}	
		String[] sentences = paragraph.splitIntoSentences(TestParagraphSplitter.PARAGRAPHTEST);
		for (String line : sentences) { 
			System.out.println(line);
		}
		if(sentences.length<EXPECTED_SENTENCES){
			fail("Not the expected amount of sentences. Expected: "+EXPECTED_SENTENCES+" obtained: "+sentences.length);
		}else{
			System.out.println("obtained: "+sentences.length+" OK!");
		}
	}
	
	@Test
	public void testQuoted() {
		System.out.println("");
		System.out.println("Test 02: quoted");
		try {
			this.paragraph = new ParagraphStanford();
		} catch (IOException e) {			
			e.printStackTrace();
			fail(e.getMessage());
		}	
		String[] sentences = paragraph.splitIntoSentences(TestParagraphSplitter.PARAGRAPHTEST02);
		for (String line : sentences) { 
			System.out.println(line);
		}
		if(sentences.length<EXPECTED_SENTENCES02){
			fail("Not the expected amount of sentences. Expected: "+EXPECTED_SENTENCES02+" obtained: "+sentences.length);
		}else{
			System.out.println("obtained: "+sentences.length+" OK!");
		}
	}

}
