package test;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ar.edu.unlp.ArgumentExtractorRegex;
import ar.edu.unlp.OIEPatternExtraction;
import ar.edu.unlp.StanfordCoreParser;
import ar.edu.unlp.entities.Example;
import ar.edu.unlp.entities.PatternArgumentList;
import ar.edu.unlp.entities.PatternContainer;
import ar.edu.unlp.entities.PatternList;
import ar.edu.unlp.entities.Relation;
import ar.edu.unlp.entities.SentenceData;

public class TestExampleToPatternAndSentence {

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
		StanfordCoreParser parser = null;
		try {
			parser = new StanfordCoreParser();
		} catch (IOException e) {			
			e.printStackTrace();
			fail(e.getMessage());
		}
		Example example = new Example();
		
		Relation relations01 = new Relation();
		relations01.setEntity1("that");
		relations01.setEntity2("revolutionary Come to think of it");
		relations01.setRelation("would be");
		Relation relations02 = new Relation();
		relations02.setEntity1("that");
		relations02.setEntity2("revolutionary");
		relations02.setRelation("would be");
		List<Relation> relations = new ArrayList<Relation>();
		
		relations.add(relations01);
		relations.add(relations02);
		
		example.setRelations(relations);
		example.setSentence("Come to think of it , that would be revolutionary .");
		
		
		List<SentenceData> listOfParsedData = null;
		try {
			listOfParsedData = parser.doParser(example.getSentence());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
		example.setSentenceData(listOfParsedData.get(0));
		
		PatternContainer patternContainer; 
		PatternList treePatternList;
		
		ArgumentExtractorRegex argumentExtractor = null;
		
		patternContainer = new PatternContainer();
		treePatternList = new PatternList();
		argumentExtractor = new ArgumentExtractorRegex();
		
		OIEPatternExtraction oiePatternExtraction = new OIEPatternExtraction();
		oiePatternExtraction.setArgumentExtractor(argumentExtractor);
		oiePatternExtraction.generateExtractionPatterns(example, treePatternList);
			
		treePatternList.initArguments(); //clean arguments, because is not longer used
		patternContainer.setTreePatterns(treePatternList);		
		patternContainer.setPatternsForArguments(new PatternArgumentList());
		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String jsonInString = gson.toJson(patternContainer);
		
		System.out.println(jsonInString);
	}

}
