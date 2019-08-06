package test;

import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ar.edu.unlp.RelationExtractor;
import ar.edu.unlp.entities.Relation;

public class TestExtractionUsingJSONPatterns3 {
		
	
	public static final String[] LINES ={ "He said \"Gandhi was vegetarian\"", 
										  "He said \"Gandhi was vegetarian\". She said \"Peter plays with the ball\"",
										  "She later revealed to Christina the late Sommers was a friend of her family and her father had appealed to Fey for help in getting Amanda a job at \"MODE\" after her career as an actress didn't pan out."};
	
	@Before
	public void setUp() throws Exception {
	
	}
	
	@Test
	public void test() {
		try{
		 System.out.println("Extract a wildcard quoted word");
		 RelationExtractor extractor = new RelationExtractor();

		 for (String line : LINES) {
			 List<Relation> relations = extractor.extractInformationFromParagraph(line);
			 if(relations!=null && !relations.isEmpty()){
					System.out.println(line);					
					for (Relation relation : relations) {
						System.out.println(relation.toStringScore());
					}
					System.out.println();
			 }else{
				 System.err.println("No relation extracted!");
				 fail("No relation extracted!");
			 }	
		}
			 		
			
		}catch(Exception e){
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}
