package ar.edu.unlp.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PatternArgumentList {
	
	protected Set<PatternArgument> patternArgumentSet;
	
	public PatternArgumentList(){
		this.patternArgumentSet = new HashSet<PatternArgument>();
	}
	public Set<PatternArgument> getPatternArgumentSet() {
		return patternArgumentSet;
	}

	public void setPatternArgumentSet(Set<PatternArgument> patternArgumentSet) {
		this.patternArgumentSet = patternArgumentSet;
	}

	public List<PatternArgument> getSortedList(){
		List<PatternArgument> sortedList = new ArrayList<PatternArgument>(patternArgumentSet);
		Collections.sort(sortedList);
		return sortedList;
	}
	
	public PatternArgument addPatternFromString(String patternStr){
		
		PatternArgument patternArgument = new PatternArgument();
		patternArgument.setPattern(patternStr);
		patternArgument.setScore(1);
		return this.addPattern(patternArgument);
	}

	public PatternArgument addPattern(PatternArgument patternArgument){
		
		if(this.patternArgumentSet.contains(patternArgument)){
			 for (PatternArgument obj : this.patternArgumentSet) {
			        if (obj.equals(patternArgument)){ 
			        	obj.addOneScore();
			        	return obj;
			        }
		      }
		}
		patternArgument.setScore(1);
		this.patternArgumentSet.add(patternArgument);
		return patternArgument;
	}
}
