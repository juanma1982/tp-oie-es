package ar.edu.unlp.entities;

import java.util.List;

public class Example {
	
	protected String sentence;
	protected List<Relation> relations;
	protected SentenceData sentenceData;
	
	public List<Relation> getRelations() {
		return relations;
	}
	public void setRelations(List<Relation> relations) {
		this.relations = relations;
	}
	public String getSentence() {
		return sentence;
	}
	public void setSentence(String sentence) {
		this.sentence = sentence;
	}
	public SentenceData getSentenceData() {
		return sentenceData;
	}
	public void setSentenceData(SentenceData sentenceData) {
		this.sentenceData = sentenceData;
	}
	
	public String toString(){
		return this.sentence;
	}
}
