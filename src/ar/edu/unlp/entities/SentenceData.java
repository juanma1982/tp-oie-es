package ar.edu.unlp.entities;

import java.util.Map;

import edu.stanford.nlp.semgraph.SemanticGraph;

public class SentenceData {
	
	protected String sentence = null;	
	protected String treeDependenciesLine = null;
	protected String patternLine = null; //Is a pattern, used to detect relations, between entities. 
	protected Map<String,String> wordNER = null;
	protected Map<String,String> wordPOSTAG = null;
	protected Map<String,String> wordPOSTAG_extended = null;
	protected SemanticGraph dependenciesGraph;	
	protected boolean useExtended = false; //extended, includes distinct POS for all words in POS "IN": by, of, at, in
	
	/*protected String sentenceAsPOSTags = null;
	protected String sentenceAsPOSTags_extended = null;
	protected String cleanSentence = null;*/
	
	protected String[] cleanSentenceArray = null;
	protected String[] sentenceAsPOSTagsArray = null;
	protected String[] sentenceAsPOSTagsExtendedArray = null;
	protected String[] chunkerTags = null;
	
	protected String rightChunkedSentence = null;
	protected String leftChunkedSentence = null;
	
	public String getSentence() {
		return sentence;
	}
	public void setSentence(String sentence) {
		this.sentence = sentence;
		//this.cleanSentence = this.sentence.replaceAll("'", " '").replaceAll("\\.", " .").replaceAll("\"", " \"").replaceAll(",", " ,").replaceAll(";", " ;,").replaceAll("  ", " ");
	}
	
	public String getCleanSentence(){
		return String.join(" ", this.cleanSentenceArray);
	}
	public void setCleanSentence(String[] cleanSentence){
		this.cleanSentenceArray = cleanSentence;
	}
	
	public String getTreeDependenciesLine() {
		return treeDependenciesLine;
	}
	public void setTreeDependenciesLine(String treeDependenciesLine) {
		this.treeDependenciesLine = treeDependenciesLine;
	}
	public Map<String, String> getWordNER() {
		return wordNER;
	}
	public void setWordNER(Map<String, String> wordNER) {
		this.wordNER = wordNER;
	}
	public Map<String, String> getWordPOSTAG() {
		if(useExtended) return wordPOSTAG_extended;
		return wordPOSTAG;
	}
	public void setWordPOSTAG(Map<String, String> wordPOSTAG) {
		this.wordPOSTAG = wordPOSTAG;
	}
	public String getPatternLine() {
		return patternLine;
	}
	public void setPatternLine(String patternLine) {
		this.patternLine = "root>"+patternLine;
	}
	public SemanticGraph getDependenciesGraph() {
		return dependenciesGraph;
	}
	public void setDependenciesGraph(SemanticGraph dependenciesGraph) {
		this.dependenciesGraph = dependenciesGraph;
	}
	public String getSentenceAsPOSTags() {
		if(useExtended) return getSentenceAsPOSTagsExtended();
		return String.join(" ", this.sentenceAsPOSTagsArray);
	}
	public void setSentenceAsPOSTags(String[] sentenceAsPOSTags) {
		this.sentenceAsPOSTagsArray = sentenceAsPOSTags;
	}
	public String getSentenceAsPOSTagsExtended() {
		return String.join(" ", this.sentenceAsPOSTagsExtendedArray);
	}
	
	public Map<String, String> getWordPOSTAG_extended() {
		return wordPOSTAG_extended;
	}
	public void setWordPOSTAG_extended(Map<String, String> wordPOSTAG_extended) {
		this.wordPOSTAG_extended = wordPOSTAG_extended;
	}
	
	public void setSentenceAsPOSTags_extended(String[] sentenceAsPOSTagsExtended) {
		this.sentenceAsPOSTagsExtendedArray = sentenceAsPOSTagsExtended;
	}
	public boolean isUseExtended() {
		return useExtended;
	}
	public void setUseExtended(boolean useExtended) {
		this.useExtended = useExtended;
	}
	public String[] getCleanSentenceArray() {
		return cleanSentenceArray;
	}
	public String[] getSentenceAsPOSTagsArray() {
		return sentenceAsPOSTagsArray;
	}
	public String[] getSentenceAsPOSTagsExtendedArray() {
		return sentenceAsPOSTagsExtendedArray;
	}
	public String[] getChunkerTags() {
		return chunkerTags;
	}
	public void setChunkerTags(String[] chunkerTags) {
		this.chunkerTags = chunkerTags;
	}
	public String getRightChunkedSentence() {
		return rightChunkedSentence;
	}
	public void setRightChunkedSentence(String rightChunkedSentence) {
		this.rightChunkedSentence = rightChunkedSentence;
	}
	public String getLeftChunkedSentence() {
		return leftChunkedSentence;
	}
	public void setLeftChunkedSentence(String leftChunkedSentence) {
		this.leftChunkedSentence = leftChunkedSentence;
	}
	

}
