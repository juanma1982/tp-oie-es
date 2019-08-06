package test;

import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ar.edu.unlp.RelationExtractor;
import ar.edu.unlp.entities.Relation;

public class TestExtractionUsingJSONPatterns4 {
		
	
	public static final String[] LINES ={ "In his \"2006 Movie Guide\", Leonard Maltin gives the film 3.5 stars (out of four) and calls it \"probably Malle\'s best early film.\" ",
										  "He chose not to do that.",
										  "It relied on collective work, and gave the amateurs of the time a chance to compensate to some extent for their lack of skill."};
										  
	
	@Before
	public void setUp() throws Exception {
	
	}
	
	@Test
	public void test() {
		try{
		 System.out.println("Check Subject extraction");
		 RelationExtractor extractor = new RelationExtractor();

		 for (String line : LINES) {
			 List<Relation> relations = extractor.extractInformationFromParagraph(line);
			 if(relations!=null && !relations.isEmpty()){
					System.out.println(line);					
					for (Relation relation : relations) {
						System.out.println(relation.toStringScore());
						if(relation.getEntity1().equals("Maltin")){
							System.err.println("extract only one word in the name!");
							fail("extract only one word in the name!");
						}
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
