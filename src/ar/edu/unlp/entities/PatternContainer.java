package ar.edu.unlp.entities;

public class PatternContainer {
	
	protected PatternArgumentList patternsForArguments;
	protected PatternList treePatterns;
	
	public PatternArgumentList getPatternsForArguments() {
		return patternsForArguments;
	}
	public void setPatternsForArguments(PatternArgumentList patternsForArguments) {
		this.patternsForArguments = patternsForArguments;
	}
	public PatternList getTreePatterns() {
		return treePatterns;
	}
	public void setTreePatterns(PatternList treePatterns) {
		this.treePatterns = treePatterns;
	}

}
