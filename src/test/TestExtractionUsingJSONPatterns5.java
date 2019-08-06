package test;

import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ar.edu.unlp.RelationExtractor;
import ar.edu.unlp.entities.Relation;

public class TestExtractionUsingJSONPatterns5 {
		
	
	public static final String Paragraph = "Baker also said he still sees \"ominous\" signs of pressure for protectionist trade legislation \"and this pressure for protectionism is coming from new areas of society.\"";
										  
	
	@Before
	public void setUp() throws Exception {
	
	}
	
	@Test
	public void test() {
		try{
		 System.out.println("Check Subject extraction");
		 RelationExtractor extractor = new RelationExtractor();


			 List<Relation> relations = extractor.extractInformationFromParagraph(Paragraph);
			 if(relations!=null && !relations.isEmpty()){
					System.out.println(Paragraph);					
					for (Relation relation : relations) {
						System.out.println(relation.toStringScore());						
					}
					System.out.println();
			 }else{
				 System.err.println("No relation extracted!");
				 fail("No relation extracted!");
			 }	

			 		
			
		}catch(Exception e){
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}
