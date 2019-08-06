package test;

import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ar.edu.unlp.ExampleLoader;
import ar.edu.unlp.StanfordCoreParser;
import ar.edu.unlp.constants.Filenames;
import ar.edu.unlp.entities.Example;
import ar.edu.unlp.entities.ExampleList;
import ar.edu.unlp.entities.SentenceData;

public class TestXMLGenerator2 {
	
	protected StanfordCoreParser parser = null;
	
	@Before
	public void Init(){
		
		try {
			parser = new StanfordCoreParser();
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	
	@Test
	public void testSentence() {
		List<SentenceData> listOfParsedData = null;
		Example example = new Example();
		try {
			listOfParsedData = parser.doParser("Terms and conditions of the contract would be subject to approval of various regulatory bodies, including the U.S. Bankruptcy Court");
		} catch (Exception e) {
			fail(e.getMessage());
			e.printStackTrace();
		}
		example.setSentenceData(listOfParsedData.get(0));			
		System.out.println(example.getSentenceData().getTreeDependenciesLine());
	}

}
