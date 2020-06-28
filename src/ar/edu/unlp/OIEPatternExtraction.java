package ar.edu.unlp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import ar.edu.unlp.constants.Constants;
import ar.edu.unlp.constants.Words;
import ar.edu.unlp.entities.Example;
import ar.edu.unlp.entities.PatternList;
import ar.edu.unlp.entities.Relation;
import ar.edu.unlp.entities.SentenceData;
import ar.edu.unlp.exceptions.ExceptionArgumentExtractor;
import ar.edu.unlp.exceptions.ExceptionWordNotInSentence;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.util.MapFactory;

public class OIEPatternExtraction {

	private static final MapFactory<IndexedWord, IndexedWord> wordMapFactory = MapFactory.hashMapFactory();
	
	protected SemanticGraph dependencies = null;
	protected Stack<String> nodePathStack = null;
	protected Map<String,String> wordToXMLPath = null;
	protected ArgumentExtractorRegex argumentExtractor = null;

	public OIEPatternExtraction(){		
	}
	
	protected void recToString(IndexedWord curr, CoreLabel.OutputFormat wordFormat, StringBuilder sb, int offset,
			Set<IndexedWord> used) {
		used.add(curr);
		List<SemanticGraphEdge> edges = dependencies.outgoingEdgeList(curr);
		Collections.sort(edges);
		for (SemanticGraphEdge edge : edges) {
			IndexedWord target = edge.getTarget();
			addPathToWord(edge.getRelation().toString(), target.toString(wordFormat));
			if (!used.contains(target)) { // recurse
				recToString(target, wordFormat, sb, offset + 1, used);
			}			
			this.nodePathStack.pop();
		}
	}
	
	protected void addPathToWord(String nodeName, String word){
		nodeName = correctNodeName(nodeName);
		nodePathStack.push(nodeName);
		StringBuilder sb = new StringBuilder();
		for (String node : nodePathStack) {
			sb.append(node).append(Words.SPACE);
		}
		wordToXMLPath.put(word, sb.toString().trim());
	}

	private String correctNodeName(String nodeName) {
		return nodeName.replace(":", "_");
	}
	
	protected void pathTree(){
		CoreLabel.OutputFormat wordFormat = CoreLabel.OutputFormat.VALUE;
		Collection<IndexedWord> rootNodes = dependencies.getRoots();
		if (rootNodes.isEmpty()) {
			return;
		}

		StringBuilder sb = new StringBuilder();
		Set<IndexedWord> used = wordMapFactory.newSet();
		for (IndexedWord root : rootNodes) {
			this.addPathToWord("root", root.toString(wordFormat) );			
			recToString(root, wordFormat, sb, 1, used);
			this.nodePathStack.pop();
		}
	}

	public void generateExtractionPatterns(Example example, PatternList patterns) {
		this.nodePathStack = new Stack<String>();
		this.dependencies = example.getSentenceData().getDependenciesGraph() ;		
		this.wordToXMLPath = new HashMap<String, String>();
		this.pathTree();		
		for (Relation relation : example.getRelations()) {
			extractPatternForRelation(example.getSentenceData(), relation, patterns);
		}
	}

	protected void extractPatternForRelation(SentenceData sentenceData, Relation relation, PatternList patterns) {
		
		String entityStr   = relation.getEntity1().replaceAll("'", " '").replaceAll("  ", Words.SPACE);
		String argumentStr = relation.getEntity2().replaceAll("'", " '").replaceAll("  ", Words.SPACE);
		String relationStr = relation.getRelation().replaceAll("'", " '").replaceAll("  ", Words.SPACE);
		
		String[] wordsInEntity1  = entityStr.split("\\s+");
		String[] wordsInEntity2  = argumentStr.split("\\s+");
		String[] wordsInRelation = relationStr.split("\\s+");
		
		List<String> relationPatterns = new ArrayList<String>();
		List<String> entity1Patterns  = new ArrayList<String>();
		List<String> entity2Patterns  = new ArrayList<String>();
		
		try {
			for (String word : wordsInRelation) {
				relationPatterns.add(this.generateStringPattern(word, sentenceData.getWordNER(),sentenceData.getWordPOSTAG()));
			}
			for (String word : wordsInEntity1) {
				entity1Patterns.add(this.generateStringPattern(word, sentenceData.getWordNER(),sentenceData.getWordPOSTAG()));
			}
			for (String word : wordsInEntity2) {
				entity2Patterns.add(this.generateStringPattern(word, sentenceData.getWordNER(),sentenceData.getWordPOSTAG()));
			}
		} catch (ExceptionWordNotInSentence e) {
			System.err.println(e.getMessage()+" for sentence: "+sentenceData.getSentence());
			return;
		}
		patterns.addPatterns(entity1Patterns, relationPatterns, entity2Patterns);
		//We will extract argument patterns as a separate process
		if(Constants.BE_SPECIFIC_WITH_POSTAG_ADP){
			sentenceData.setUseExtended(true);	
		}
		try {
			argumentExtractor.extractArgumentPattern(sentenceData, relationStr, argumentStr);
		} catch (ExceptionArgumentExtractor e) {
			System.err.println(e.getMessage()+" for sentence: "+sentenceData.getSentence());
			return;
		}
	}
	
	public ArgumentExtractorRegex getArgumentExtractor() {
		return argumentExtractor;
	}

	public void setArgumentExtractor(ArgumentExtractorRegex argumentExtractor) {
		this.argumentExtractor = argumentExtractor;
	}

	protected String generateStringPattern(String word,Map<String,String> wordNER,Map<String,String> wordPOSTAG) throws ExceptionWordNotInSentence{
		
		String wordNer = wordNER.get(word);
		String wordPos = wordPOSTAG.get(word);		
		if(wordPos==null){
			throw new ExceptionWordNotInSentence("The word: '"+word+"' is not present in the sentence");
			
		}
		
		String typeSelector;
		String path = this.wordToXMLPath.get(word);
		if(wordNer != null && !wordNer.equals("O")){
			typeSelector="ner="+wordNer;
		}else if(path.endsWith("case") && (wordPos.equals("IN") || wordPos.equals("TO"))){
			typeSelector="word="+word.replaceAll("'", "&apos;").replaceAll(Words.SPACE, "_");
		}else{
			typeSelector="tag="+wordPOSTAG.get(word);
		}
		StringBuilder sb = new StringBuilder();		
		sb.append(path);
		sb.append("[");
		sb.append(typeSelector);			
		sb.append("]");
		return sb.toString();
		
	}

}
