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

public class TestXMLGenerator {
	
	protected StanfordCoreParser parser = null;
	public static final String RESULT = 
			"<root word='awarded' tag='VBN' ner='O' >\n"+
					"  <nsubjpass word='Einstein' tag='NNP' ner='PERSON' >\n"+
					"    <compound word='Albert' tag='NNP' ner='PERSON' >\n"+
					"    </compound>\n"+
					"  </nsubjpass>\n"+
					"  <auxpass word='was' tag='VBD' ner='O' >\n"+
					"  </auxpass>\n"+
					"  <dobj word='Prize' tag='NNP' ner='MISC' >\n"+
					"    <det word='the' tag='DT' ner='O' >\n"+
					"    </det>\n"+
					"    <compound word='Nobel' tag='NNP' ner='MISC' >\n"+
					"    </compound>\n"+
					"    <nmod word='Sweden' tag='NNP' ner='COUNTRY' >\n"+
					"      <case word='in' tag='IN' ner='O' >\n"+
					"      </case>\n"+
					"    </nmod>\n"+
					"  </dobj>\n"+
					"  <nmod word='1921' tag='CD' ner='DATE' >\n"+
					"    <case word='in' tag='IN' ner='O' >\n"+
					"    </case>\n"+
					"  </nmod>\n"+
					"  <punct word='.' tag='.' ner='O' >\n"+
					"  </punct>\n"+
					"</root>\n";

	
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
	public void testExamples() {

		ExampleList examples = null;
		try {
			examples = ExampleLoader.loadExamplesFromJson(Filenames.JSON_TEST_EXAMPLES);
		} catch (FileNotFoundException e) {			
			e.printStackTrace();
			fail(e.getMessage());
		}
		for (Example example : examples.getExamples()) {
			List<SentenceData> listOfParsedData = null;
			try {
				listOfParsedData = parser.doParser(example.getSentence());
			} catch (Exception e) {
				fail(e.getMessage());
				e.printStackTrace();
			}
			example.setSentenceData(listOfParsedData.get(0));
			if(!RESULT.equals(example.getSentenceData().getTreeDependenciesLine())){
				System.out.println(example.getSentenceData().getTreeDependenciesLine());
				fail("Tree is not as expcted");
			}
			System.out.println(example.getSentenceData().getTreeDependenciesLine()); 
			
		}
	}
	
	@Test
	public void testSentence() {
		List<SentenceData> listOfParsedData = null;
		Example example = new Example();
		try {
			listOfParsedData = parser.doParser(TestConstants.SENTENCE_03);
		} catch (Exception e) {
			fail(e.getMessage());
			e.printStackTrace();
		}
		example.setSentenceData(listOfParsedData.get(0));			
		System.out.println(example.getSentenceData().getTreeDependenciesLine());
	}

}
