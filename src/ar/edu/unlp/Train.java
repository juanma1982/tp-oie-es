package ar.edu.unlp;

import java.util.ArrayList;
import java.util.List;

import ar.edu.unlp.constants.Words;
import ar.edu.unlp.entities.Example;
import ar.edu.unlp.entities.ExampleList;
import ar.edu.unlp.entities.PatternArgumentList;
import ar.edu.unlp.entities.PatternContainer;
import ar.edu.unlp.entities.PatternList;
import ar.edu.unlp.entities.Relation;
import ar.edu.unlp.entities.SentenceData;
import ar.edu.unlp.utils.ParagraphStanford;

public class Train {

	protected StanfordCoreParser parser = null;
	protected PatternContainer patternContainer; 
	protected PatternList treePatternList;
	protected PatternArgumentList argumentPatternList;
	public boolean REUSE_SAMPLES = false; //this will create new sentences joining the entities and the relation 
	public ParagraphStanford paragraph=null;
	protected ArgumentExtractorRegex argumentExtractor = null;
	
	
	public Train() throws Exception{
		parser = new StanfordCoreParser();
	}
	
	
	/**
	 * Generates patterns using examples. The examples should be in a JSON file.
	 * 
	 * @param fromScratch if its true will override all the current patterns, otherwise the patterns will be<br/> 
	 * 					  added to existing 
	 * @throws Exception
	 */
	public void doTraining(boolean fromScratch, String filename, Integer score) throws Exception{
		
		if(!fromScratch) {
			loadPatterns();
			if(score != null) {
				treePatternList.addScoreToAll(score.intValue());
			}
		}else {
			this.patternContainer = new PatternContainer();
			treePatternList = new PatternList();
			argumentExtractor = new ArgumentExtractorRegex();
		}
		
		ExampleList examples = null;
		if(filename==null) {
			examples = ExampleLoader.loadExamplesFromJson();
		}else {
			examples = ExampleLoader.loadExtraExamplesFromJson(filename);
		}
		
		for (Example example : examples.getExamples()) {
			List<SentenceData> listOfParsedData = parser.doParser(example.getSentence());
			example.setSentenceData(listOfParsedData.get(0));
			this.generateExtractionPatternsFromExample(example);
			if(REUSE_SAMPLES){
				List<Example> listOfNewGeneratedExamples = this.generateExamplesFromRelations(example);
				for (Example exampleNew : listOfNewGeneratedExamples) {
					List<SentenceData> newListOfParsedData = parser.doParser(exampleNew.getSentence());
					exampleNew.setSentenceData(newListOfParsedData.get(0));
					this.generateExtractionPatternsFromExample(exampleNew);
				}
			}
		}
		
		treePatternList.initArguments(); //clean arguments, because is not longer used
		this.patternContainer.setTreePatterns(treePatternList);
		//this.patternContainer.setPatternsForArguments(this.argumentPatternList);
		this.patternContainer.setPatternsForArguments(new PatternArgumentList()); //initialice empty, because is not longer used
		PatternLoader.savePatternsIntoJson(this.patternContainer);		
	}
	
	public void doTraining() throws Exception{
		this.doTraining(true,null,null);
	}
	
	public void loadPatterns() throws Exception{
		this.patternContainer = PatternLoader.loadPatternsFromJson();	
		this.treePatternList = this.patternContainer.getTreePatterns();
		this.argumentPatternList = this.patternContainer.getPatternsForArguments();
		argumentExtractor = new ArgumentExtractorRegex(this.argumentPatternList);
	}

	
	protected List<Example> generateExamplesFromRelations(Example example){
		List<Example> fakeExampleList = new ArrayList<Example>();
		for(Relation relation:example.getRelations()){
			Example fakeExample = new Example();
			StringBuilder sb = new StringBuilder();
			sb.append(relation.getEntity1());
			sb.append(Words.SPACE);
			sb.append(relation.getRelation());
			sb.append(Words.SPACE);
			sb.append(relation.getEntity2());
			
			fakeExample.setSentence(sb.toString());
			
			List<Relation> relationsFake = new ArrayList<Relation>();
			relationsFake.add(relation);			
			fakeExample.setRelations(relationsFake);
			fakeExampleList.add(fakeExample);
		}
		return fakeExampleList;
	}

	
	protected void generateExtractionPatternsFromExample(Example example) {

		OIEPatternExtraction oiePatternExtraction = new OIEPatternExtraction();
		oiePatternExtraction.setArgumentExtractor(this.argumentExtractor);
		oiePatternExtraction.generateExtractionPatterns(example, this.treePatternList);
		this.argumentPatternList = oiePatternExtraction.getArgumentExtractor().getPatterns();
	}
	
}
