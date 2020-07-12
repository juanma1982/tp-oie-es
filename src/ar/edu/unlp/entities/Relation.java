package ar.edu.unlp.entities;

import ar.edu.unlp.constants.Words;

public class Relation {

	protected static int nextID = 0;
	protected String entity1;
	protected String entity2;
	protected String relation;
	protected String fullExtractionAsPosTags;
	protected int score;
	protected int id;
	protected int dependsOf=-1;
	protected boolean interogation=false;
	
	public Relation(){
		this.id=nextID;
		nextID++;
	}
	
	public Relation(Relation toCopy){
		this.id=nextID;
		nextID++;
		this.entity1 = toCopy.entity1;
		this.entity2 = toCopy.entity2;
		this.relation = toCopy.relation;
		this.score = toCopy.score;
		this.fullExtractionAsPosTags = toCopy.fullExtractionAsPosTags;
	}
		
	public String getEntity1() {
		return entity1;
	}
	public void setEntity1(String entity1) {
		this.entity1 = entity1.replace(" '", "'");
	}
	public String getEntity2() {
		return entity2;
	}
	public void setEntity2(String entity2) {
		this.entity2 = entity2.replace(" '", "'");
	}
	public String getRelation() {
		return relation;
	}
	public void setRelation(String relation) {
		this.relation = relation.replace(" '", "'");
	}
	public int getScore() {
		return score;
	}
	
	public void setScore(int score) {
		this.score = score;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getDependsOf() {
		return dependsOf;
	}

	public void setDependsOf(int dependsOf) {
		this.dependsOf = dependsOf;
	}

	public boolean isInterogation() {
		return interogation;
	}

	public void setInterogation(boolean interogation) {
		this.interogation = interogation;
	}

	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append(entity1);
		sb.append("; ");
		sb.append(relation);
		sb.append("; ");
		sb.append(entity2);
		sb.append(")");
		return sb.toString();
	}
	
	public String toStringScore(){
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append(entity1);
		sb.append("; ");
		sb.append(relation);
		sb.append("; ");
		sb.append(entity2);
		sb.append(")");
		sb.append(" => (");
		sb.append(this.score+") ");
		return sb.toString();
	}
	
	public String toStringFull() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.id);
		sb.append(" (");
		sb.append(entity1);
		sb.append("; ");
		sb.append(relation);
		sb.append("; ");
		sb.append(entity2);
		sb.append(")");
		sb.append(" => (");
		sb.append(this.score+") ");
		if(this.dependsOf >-1) {
			sb.append(" SUBJETIVA, DEPENDE DE ");
			sb.append(this.dependsOf);
		}
		if(this.interogation) {
			sb.append(" INTERROGACIÓN ");			
		}
		return sb.toString();
	}

	
	public String inRow(){
		StringBuilder sb = new StringBuilder();		
		sb.append(entity1);
		sb.append(Words.SPACE);
		sb.append(relation);
		sb.append(Words.SPACE);
		sb.append(entity2);		
		return sb.toString();
	}
	
	public boolean isComplete(){
		return entity1!=null && !entity1.isEmpty() && entity2!=null && 
				!entity2.isEmpty() && relation!=null && !relation.isEmpty(); 
	}
	public String getFullExtractionAsPosTags() {
		return fullExtractionAsPosTags;
	}

	public void setFullExtractionAsPosTags(String fullExtractionAsPosTags) {
		this.fullExtractionAsPosTags = fullExtractionAsPosTags;
	}
	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      Relation other = (Relation) obj;
      return this.toString().equals(other.toString());
   }

public boolean contains(Relation second) {
	if(!this.entity1.contains(second.entity1)) return false;
	if(!this.relation.contains(second.relation)) return false;
	if(!this.entity2.contains(second.entity2)) return false;
	return true;
}





   
}
