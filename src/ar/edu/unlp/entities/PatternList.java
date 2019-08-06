package ar.edu.unlp.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class PatternList {
	protected Map<String, Pattern> subjects;
	protected Map<String, Pattern> relations;
	protected Map<String, Pattern> arguments;

	protected Map<Integer, Set<Pattern>> argumentsByRelationSubjectPattern = null;	
	protected Map<Integer, Set<Pattern>> subjectsByRelationPattern = null;
	
	public PatternList(){
		this.subjects = new HashMap<String, Pattern>();
		this.relations= new HashMap<String, Pattern>();
		this.subjectsByRelationPattern  = new HashMap<Integer, Set<Pattern>>();
		initArguments();
	}
	
	public void initArguments() {
		this.arguments= new HashMap<String, Pattern>();
		this.argumentsByRelationSubjectPattern = new HashMap<Integer, Set<Pattern>>();
	}
	
	protected Pattern addElementToMap(Map<String, Pattern> map, List<String> patternsAsStringList){
		Pattern pattern;
		
		if(patternsAsStringList.isEmpty()) return null;
		if(map.get(patternsAsStringList.get(0)) == null){
			pattern = new Pattern(patternsAsStringList);
			if(pattern.patternStr == null) {
				System.out.println("HERE"); //TODO FIX
			}
			map.put(pattern.patternStr, pattern);			
		}else{
			pattern = map.get(patternsAsStringList.get(0));
			if(patternsAsStringList.size() == 1){
				if(pattern.isLeaf){
					pattern.addOneToScore();
				}else{
					pattern.setLeaf(true);
				}				
			}else{
				pattern.addToListOfNext(patternsAsStringList.subList(1, patternsAsStringList.size()));
			}
		}
	
		return pattern;
	}
	
	protected void createRelationSubjectLink(Pattern patternSubject,List<String> relationPatternsAsStringList){
		String patternStr = this.stringListToString(relationPatternsAsStringList);
		int hashCodeIndex = patternStr.hashCode();
		
		if(patternSubject != null){
			if(this.subjectsByRelationPattern.get(hashCodeIndex) == null){
				this.subjectsByRelationPattern.put(hashCodeIndex, new HashSet<Pattern>());
			}
			Set<Pattern> listOfPattern =  this.subjectsByRelationPattern.get(hashCodeIndex);			
			if(!listOfPattern.contains(patternSubject)){
				listOfPattern.add(patternSubject);
			}
		}
	}
	
	protected void createRelationSubjectArgumentLink(Pattern patternArgument,List<String> relationPatternsAsStringList, List<String> subjectPatternsAsStringList){
		String patternStr = this.stringListToString(relationPatternsAsStringList) + this.stringListToString(subjectPatternsAsStringList);		
		
		int hashCodeIndex = patternStr.hashCode();
		if(patternArgument != null){
			if(this.argumentsByRelationSubjectPattern.get(hashCodeIndex) == null){
				this.argumentsByRelationSubjectPattern.put(hashCodeIndex, new HashSet<Pattern>());
			}
			Set<Pattern> listOfPattern =  this.argumentsByRelationSubjectPattern.get(hashCodeIndex);
			if(!listOfPattern.contains(patternArgument)){
				listOfPattern.add(patternArgument);
			}
		}
	}
	
	protected String stringListToString(List<String> list){
		StringBuilder sb = new StringBuilder("");
		for (String string : list) {
			sb.append(string);
			sb.append(" ");
		}
		return sb.toString().trim();
		
	}
	
	/**********
	public Pattern addPatternToSubjects(List<String> patternsAsStringList,Pattern patternArgument){
		return this.addElementToMap(this.subjects, patternsAsStringList,patternArgument,null);
	}
	public void addPatternToRelations(List<String> patternsAsStringList,Pattern patternSubject){
		this.addElementToMap(this.relations, patternsAsStringList, null,patternSubject);
	}
	public Pattern addPatternToArguments(List<String> patternsAsStringList){
		return this.addElementToMap(this.arguments, patternsAsStringList);
	}***/
	
	public void addPatterns(List<String> subjectPatternsAsStringList, List<String> relationPatternsAsStringList, List<String> argumentPatternsAsStringList){
		this.addElementToMap(this.relations, relationPatternsAsStringList);
		Pattern argPattern = this.addElementToMap(this.arguments, argumentPatternsAsStringList);		
		Pattern subPattern = this.addElementToMap(this.subjects, subjectPatternsAsStringList);
	
		this.createRelationSubjectLink(subPattern, relationPatternsAsStringList);
		this.createRelationSubjectArgumentLink(argPattern, relationPatternsAsStringList, subjectPatternsAsStringList);
	}
	
	public List<Pattern> getListOfSubjects(){
		return this.mapToSortedList(this.subjects.values());		
	}
	
	public List<Pattern> getListOfRelations(){	
		return this.mapToSortedList(this.relations.values());
		
	}
	
	public List<Pattern> getListOfArguments(){
		return this.mapToSortedList(this.arguments.values());
	}
	

	protected List<Pattern> mapToSortedList(Collection<Pattern> collection){
		List<Pattern> nextPatternsSorted = new ArrayList<Pattern>(collection);
		Collections.sort(nextPatternsSorted);
		return nextPatternsSorted;
	}

	public Map<String, Pattern> getSubjects() {
		return subjects;
	}


	public Map<String, Pattern> getRelations() {
		return relations;
	}


	public Map<String, Pattern> getArguments() {
		return arguments;
	}

	public void setSubjects(Map<String, Pattern> subjects) {
		this.subjects = subjects;
	}

	public void setRelations(Map<String, Pattern> relations) {
		this.relations = relations;
	}

	public void setArguments(Map<String, Pattern> arguments) {
		this.arguments = arguments;
	}

	
	public List<Pattern> getSubjectsByRelationPatternList(String relatonPatterns) {
		List<Pattern> patternList = new ArrayList<Pattern>();
		patternList.addAll(subjectsByRelationPattern.get(relatonPatterns.hashCode()));
		Collections.sort(patternList);
		return patternList;
	}
	public List<Pattern> getArgumentsByRelationSubjectPatternList(String relatonPatterns, String subjectPatterns) {
		String mixedPattrn = relatonPatterns+subjectPatterns;
		List<Pattern> patternList = new ArrayList<Pattern>();
		patternList.addAll(argumentsByRelationSubjectPattern.get(mixedPattrn.hashCode()));
		Collections.sort(patternList);
		return patternList;
	}
		
	public String toString(){
		 Gson gson = new GsonBuilder().setPrettyPrinting().create();
		 return gson.toJson(this);		
	}

	public Map<Integer, Set<Pattern>> getArgumentsByRelationSubjectPattern() {
		return argumentsByRelationSubjectPattern;
	}

	public void setArgumentsByRelationSubjectPattern(Map<Integer, Set<Pattern>> argumentsByRelationSubjectPattern) {
		this.argumentsByRelationSubjectPattern = argumentsByRelationSubjectPattern;
	}

	public Map<Integer, Set<Pattern>> getSubjectsByRelationPattern() {
		return subjectsByRelationPattern;
	}

	public void setSubjectsByRelationPattern(Map<Integer, Set<Pattern>> subjectsByRelationPattern) {
		this.subjectsByRelationPattern = subjectsByRelationPattern;
	}

	public void addScoreToAll(int scoreToAdd){
		List<Pattern> nextPatternsSorted = new ArrayList<Pattern>(this.subjects.values());
		for (Pattern pattern : nextPatternsSorted) {
			pattern.setScore(pattern.getScore()+scoreToAdd);
		}
		nextPatternsSorted = new ArrayList<Pattern>(this.relations.values());
		for (Pattern pattern : nextPatternsSorted) {
			pattern.setScore(pattern.getScore()+scoreToAdd);
		}
	}
	
}
