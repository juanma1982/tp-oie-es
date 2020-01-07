package estest;

import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ar.edu.unlp.RelationExtractor;
import ar.edu.unlp.entities.Relation;

public class TestExtractionUsingJSONPatterns4 {
		
	
	public static final String[] LINES ={""};
	
	@Before
	public void setUp() throws Exception {
	
	}
	
	@Test
	public void test() {
		try{
		 System.out.println("Extract a wildcard quoted word");
		 RelationExtractor extractor = new RelationExtractor();
		 extractor.setUseReverb(false);
		 

		 for (String line : LINES) {
			 List<Relation> relations = extractor.extractInformationFromParagraph(line);
			 if(relations!=null && !relations.isEmpty()){
					System.out.println(line);					
					for (Relation relation : relations) {
						System.out.println(relation.toStringFull());
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
