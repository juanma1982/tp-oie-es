package ar.edu.unlp;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import ar.edu.unlp.constants.Filenames;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.parser.nndep.DependencyParser;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.trees.GrammaticalStructure;

public class DeepNNDependeciesParser {

	protected String modelPath = null;
	protected String taggerPath = null;
	protected MaxentTagger tagger= null; 
	protected DependencyParser parser= null;
	protected Properties properties = null;    
	
	protected void loadProperties() throws IOException {
		this.properties = new Properties();
		InputStream input = null;
		input = new FileInputStream(Filenames.MODEL_PROPERTIES);
		this.properties.load(input);
		this.modelPath = this.properties.getProperty("model");
		this.taggerPath = this.properties.getProperty("tagger");
	}
	
	public DeepNNDependeciesParser() throws IOException{
		this.loadProperties();
	    this.tagger = new MaxentTagger(this.taggerPath);
	    this.parser = DependencyParser.loadFromModelFile(this.modelPath);
	}
	
	public List<GrammaticalStructure> generateDependencyGrammaticalStructureForParagrapths(String text){
	
		List<GrammaticalStructure> list = new ArrayList<GrammaticalStructure>();
		
	    DocumentPreprocessor tokenizer = new DocumentPreprocessor(new StringReader(text));
	    for (List<HasWord> sentence : tokenizer) {
	      List<TaggedWord> tagged = tagger.tagSentence(sentence);
	      GrammaticalStructure gs = parser.predict(tagged);	      
	      list.add(gs);
	    }
	    return list;
	}
	
	public GrammaticalStructure generateDependencyGrammaticalStructureForSentence(String text){
		
		return generateDependencyGrammaticalStructureForParagrapths(text).get(0);
	}
}