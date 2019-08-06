package ar.edu.unlp.entities;

public class RelationForSQL extends Relation {

	protected int idText;
	protected int idDatabase;
	
	public RelationForSQL(Relation relation) {
		super(relation);
	}
	
	public int getIdText() {
		return idText;
	}
	public void setIdText(int currentIdText) {
		this.idText = currentIdText;
	}
	public int getIdDatabase() {
		return idDatabase;
	}
	public void setIdDatabase(int currentIdDatabase) {
		this.idDatabase = currentIdDatabase;
	}
}
