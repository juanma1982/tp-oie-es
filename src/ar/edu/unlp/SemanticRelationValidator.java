package ar.edu.unlp;

import ar.edu.unlp.entities.Relation;

public class SemanticRelationValidator {
	
	
	public static boolean isValid(Relation relation){
		
		if(relation.getEntity2().contains(relation.getRelation())) return false;
		if(relation.getEntity2().contains(relation.getEntity1())) return false;
		
		return true;
		
	}

}
