package ar.edu.unlp.entities;

public class PatternArgument implements Comparable<PatternArgument>{
	
	protected String pattern;
	protected int score;
	protected boolean leftPattern=false;
		
	public PatternArgument(){
		this.score = 0;
	}
	
	public String getPattern() {
		return pattern;
	}
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	public int getScore() {
		return score;
	}
	
	public void addOneScore() {
		this.score++;
	}
	
	public void setScore(int score) {
		this.score = score;
	}
	
	public boolean isLeftPattern() {
		return leftPattern;
	}

	public void setLeftPattern(boolean leftPattern) {
		this.leftPattern = leftPattern;
	}
	
	@Override
	public int compareTo(PatternArgument o) {		
		return this.score-o.score;
	}
	
	@Override
	public int hashCode() {
		return this.pattern.hashCode();
	}

	public String toString() {
	
		 return  this.pattern;	
	}
	

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      PatternArgument other = (PatternArgument) obj;
      if (!this.pattern.equals(other.pattern))
         return false;
      return true;
   }



}
