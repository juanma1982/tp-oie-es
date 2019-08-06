package ar.edu.unlp.entities;

public class NounPhrase {
	
	protected String nounPhrase;
	protected int startIndex; //in words
	protected int length; //in words

	public String getNounPhrase() {
		return nounPhrase;
	}
	public void setNounPhrase(String nounPhrase) {
		this.nounPhrase = nounPhrase;
	}
	public int getStartIndex() {
		return startIndex;
	}
	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}

}
