package ar.edu.unlp.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Pattern implements Comparable<Pattern>{

	 public static final String EXTRACT_BRACKET_CONTENT_REGEX = "\\[([^\\]]+)\\]";
	
	protected String patternStr;
	protected int score = 0;
	protected boolean isLeaf = false;
	protected Map<String, Pattern> nextPatterns;
	
	public Pattern(){
		
	}
	
	public void setScore(int score) {
		this.score = score;
	}
	
	public Pattern(List<String> patternsAsStringList){
		
		this.nextPatterns = new HashMap<String, Pattern>();
		if(patternsAsStringList.size() >= 1){
			this.patternStr = patternsAsStringList.get(0);
		}
		if(patternsAsStringList.size() == 1){
			this.isLeaf = true;
		}else{
			Pattern child = new Pattern(patternsAsStringList.subList(1, patternsAsStringList.size()));
			this.nextPatterns.put(child.patternStr, child);
		}
	}
	
	public String getPatternStr() {
		return patternStr;
	}
		
	public void setPatternStr(String patternStr) {
		this.patternStr = patternStr;
	}
	public int getScore() {
		return score;
	}
	public void addOneToScore() {
		this.score++;
	}
	
	public boolean isLeaf() {
		return isLeaf;
	}
	public void setLeaf(boolean isLeaf) {
		this.isLeaf = isLeaf;
	}
	
	@Override
	public int hashCode() {
		return this.patternStr.hashCode();
	}

	public String toString() {
	
		 Gson gson = new GsonBuilder().setPrettyPrinting().create();
		 return gson.toJson(this);		
	}

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      Pattern other = (Pattern) obj;
      if (!this.patternStr.equals(other.patternStr))
         return false;
      return true;
   }

	public void addToListOfNext(List<String> patternsAsStringList) {
		if(patternsAsStringList.isEmpty()) return;
		if(this.nextPatterns.get(patternsAsStringList.get(0)) == null){
			Pattern pattern = new Pattern(patternsAsStringList);
			this.nextPatterns.put(pattern.patternStr, pattern);
		}else{
			Pattern existingPattern = this.nextPatterns.get(patternsAsStringList.get(0));
			if(patternsAsStringList.size() == 1){
				if(existingPattern.isLeaf){
					existingPattern.addOneToScore();
				}else{
					existingPattern.setLeaf(true);
				}				
			}else{
				existingPattern.addToListOfNext(patternsAsStringList.subList(1, patternsAsStringList.size()));
			}
		}
	}
	
	public List<Pattern> getListOfNext(){
		List<Pattern> nextPatternsSorted = new ArrayList<Pattern>(this.nextPatterns.values());
		Collections.sort(nextPatternsSorted);
		return nextPatternsSorted;
		
	}

	@Override
	public int compareTo(Pattern obj) {		
	    return obj.score-this.score;
	}
	
	public Map<String, Pattern> getNextPatterns() {
		return nextPatterns;
	}

	public void setNextPatterns(Map<String, Pattern> nextPatterns) {
		this.nextPatterns = nextPatterns;
	}	
	
}
