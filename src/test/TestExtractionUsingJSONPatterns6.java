package test;

import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ar.edu.unlp.RelationExtractor;
import ar.edu.unlp.entities.Relation;

public class TestExtractionUsingJSONPatterns6 {
		
	
	public static final String[] ParagraphS = {"Early astronomers believed that the earth is the center of the universe."};
										  
	
	@Before
	public void setUp() throws Exception {
	
	}
	
	@Test
	public void test() {
		try{
		 System.out.println("Check Subject extraction");
		 RelationExtractor extractor = new RelationExtractor();

		 for (String string : ParagraphS) {
			 List<Relation> relations = extractor.extractInformationFromParagraph(string);
			 if(relations!=null && !relations.isEmpty()){
					System.out.println(string);					
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
