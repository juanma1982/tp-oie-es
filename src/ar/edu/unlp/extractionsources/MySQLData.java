package ar.edu.unlp.extractionsources;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;

import ar.edu.unlp.RelationExtractor;
import ar.edu.unlp.constants.Filenames;
import ar.edu.unlp.entities.Relation;
import ar.edu.unlp.entities.RelationForSQL;

public class MySQLData {

	public static final int BATCH_SIZE = 100;
	
	private Connection conn = null;
	private String host;
	private String username;
	private String password;
	private String database;
	private String sqlSelectReadText;
	private String sqlInsertSingleExtraction;
	private ResultSet rs=null;
	private String currentText;
	protected int currentIdText;
	protected int currentIdDatabase;

	//private ExtractionClassificatorBatch ec;	
	protected int countExtractions = 0;
	protected int countValidExtractions = 0;

	public MySQLData() throws IOException, SQLException{
		readProperties();
		connect();
		startQuerySelect();
	
	}
	
	protected void connect() throws SQLException{
		try {
		    conn =DriverManager.getConnection("jdbc:mysql://"+this.host+"/"+this.database+"?user="+this.username+"&password="+this.password);
		} catch (SQLException ex) {
		    // handle any errors
		    System.out.println("SQLException: " + ex.getMessage());
		    System.out.println("SQLState: " + ex.getSQLState());
		    System.out.println("VendorError: " + ex.getErrorCode());
		    throw ex;
		}
	}
	
	protected void readProperties() throws IOException{
		
		try {
		    //load a properties file from class path, inside static method
			File file = new File(Filenames.MYSQL_PROPERTIES);
			FileInputStream fileInput = new FileInputStream(file);
			Properties prop = new Properties();
			prop.load(fileInput);
			fileInput.close();
			this.host = prop.getProperty("mysql.host"); 
			this.username =prop.getProperty("mysql.username"); 
			this.password =prop.getProperty("mysql.password"); 
			this.database =prop.getProperty("mysql.database"); 
			this.sqlSelectReadText =prop.getProperty("mysql.sqlSelectReadText"); 
			this.sqlInsertSingleExtraction =prop.getProperty("mysql.sqlInsertSingleExtraction");
		}catch (IOException ex) {
			System.err.println("Unable to load property file");
		    ex.printStackTrace();
		    throw ex;
		}
	}
	
	protected void startQuerySelect() throws SQLException{

	      Statement st = conn.createStatement();
	      this.rs = st.executeQuery(this.sqlSelectReadText);
	}
	
	public String readNextLine() throws SQLException{
		
		if(this.rs.next()){
			this.currentIdDatabase	= rs.getInt("id_database");
			this.currentIdText 		= rs.getInt("id_text");
			this.currentText 		= rs.getString("Text");
			return this.currentText ;
		}
		this.rs.close();
		return null;
	}
	
	//(`id_metodo`,`id_articulo`, `score`, `entidad01`,`relacion`,`entidad02`) VALUES (2,?,?,?,?,?)
	public void insertRelationExtracted(RelationForSQL relation) throws SQLException{
		
		  PreparedStatement preparedStmt = conn.prepareStatement(this.sqlInsertSingleExtraction);
		  preparedStmt.setInt(1, relation.getIdText());
		  preparedStmt.setInt(2, relation.getScore());
	      preparedStmt.setString(3, relation.getEntity1());
	      preparedStmt.setString(4, relation.getRelation());
	      preparedStmt.setString(5, relation.getEntity2());
	      
	      

	      // execute the preparedstatement
	      preparedStmt.execute();
		
	}
	
	/*protected void processBatch(boolean insert,List<RelationForSQL> relationsBatch) throws SQLException {
		double[] results = this.evaluateExtractions(relationsBatch);
		for(int i=0;i<relationsBatch.size();i++) {
			this.countExtractions++;
			if(results!=null && ExtractionClassificatorBatch.INVALID == (int)results[i]) continue;
			this.countValidExtractions++;
			RelationForSQL relation = relationsBatch.get(i);			
			if(insert){
				this.insertRelationExtracted(relation);
			}
			System.out.println(relation.toString());
		}
		
	}*/
	
	public static void extractInformationFromTableAndInsert(boolean insert) throws Exception{
		
		MySQLData mysql = new MySQLData();
		String text = mysql.readNextLine();
		RelationExtractor extractor = new RelationExtractor();
		int count =0;
		
		while (text!=null ) {
			count++;
			System.out.println(text);
			List<Relation> relations = extractor.extractInformationFromParagraph(text);
			for (Relation relation : relations) {
				if(relation.getScore()<= 0) continue;
				RelationForSQL local = new RelationForSQL(relation);
				local.setIdDatabase(mysql.currentIdDatabase);
				local.setIdText(mysql.currentIdText);
				mysql.insertRelationExtracted(local);
			}			
			text = mysql.readNextLine();
		}		
		mysql.closeConnection();
		System.out.println();
		System.out.println("total sentences: "+count);
		System.out.println("total extractions: "+mysql.countExtractions);
		System.out.println("total valid extractions: "+mysql.countValidExtractions);
	}
	
	/*private double[] evaluateExtractions(List<RelationForSQL> relationsBatch) {

		for (RelationForSQL relation : relationsBatch) {			
			String bigrams = graphut.getBigramsAsString(relation.getFullExtractionAsPosTags());			
			try {
				ec.addCase(bigrams);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}			
		}
		try {
			return ec.evalCurrentCases();
		} catch (Exception e) {			
			e.printStackTrace();
			return null;
		}
	}*/

	public void closeConnection(){
		try {
			this.conn.close();
		} catch (SQLException e) {				
			System.err.println("The connection failed to closed");
			e.printStackTrace();
		}
	}
}
