package test;

import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ar.edu.unlp.ReadTestFile;
import ar.edu.unlp.RelationExtractor;
import ar.edu.unlp.entities.Relation;

public class TestExtractionUsingJSONPatterns2 {
		
	@Before
	public void setUp() throws Exception {
	
	}
	
	@Test
	public void test() {
		try{
		 RelationExtractor extractor = new RelationExtractor();
		 
		 ReadTestFile rtf = new ReadTestFile(1);
		 rtf.startReading();
		 String line = rtf.readNextLine();
		 while(line!=null ){
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
			 line = rtf.readNextLine();
		 }		
		 rtf.endRead();
			
		}catch(Exception e){
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}
