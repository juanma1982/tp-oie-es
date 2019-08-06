package ar.edu.unlp.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ar.edu.unlp.constants.PosTagDic;
import ar.edu.unlp.constants.Words;

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
	
	public String getPatternStrES() {		
		java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(Pattern.EXTRACT_BRACKET_CONTENT_REGEX);
		Matcher matcher = pattern.matcher(this.patternStr);
		String bracketContent = null;
		if (matcher.find()) {
			bracketContent = matcher.group(1);
		 if(bracketContent == null) return this.patternStr;
		}else {
			return this.patternStr;
		}
		String[] brackCont = bracketContent.split("=");
		String newValue = null;
		switch (brackCont[0]) {
			case "word":
				newValue = Words.WORDTRANSLATE.get(brackCont[1]);				
				break;
			case "tag":
				newValue = PosTagDic.PENN2UPT.get(brackCont[1] );
				if(newValue == null) {
					if(Words.WORDTRANSLATE.get(brackCont[1].toLowerCase()) == null) {
						System.err.println("Word: '"+brackCont[1]+"' has no translation");
						return patternStr;
					}
					newValue = Words.WORDTRANSLATE.get(brackCont[1].toLowerCase()).toUpperCase();
				}
				break;
			default:
				return patternStr;
		}
		if(newValue == null) {
			return patternStr;
		}
		if(newValue.contains("|")) {
			return patternStr.replaceAll(bracketContent, brackCont[0]+"~="+newValue);
		}
		
		return patternStr.replaceAll(bracketContent, brackCont[0]+"="+newValue);
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
	    return this.score-obj.score;
	}
	
	public Map<String, Pattern> getNextPatterns() {
		return nextPatterns;
	}

	public void setNextPatterns(Map<String, Pattern> nextPatterns) {
		this.nextPatterns = nextPatterns;
	}	
	
}
