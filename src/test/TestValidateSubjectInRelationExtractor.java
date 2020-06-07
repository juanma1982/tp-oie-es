package test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ar.edu.unlp.RelationExtractor;
import ar.edu.unlp.constants.Constants;

public class TestValidateSubjectInRelationExtractor {

	protected  RelationExtractor extractor;
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {		
	}

	@Before
	public void setUp() throws Exception {
		this.extractor = new RelationExtractor();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCase01() {
		System.out.println("Case 01: distance between words is > 0 but <= Min distance ("+Constants.MIN_WORD_DISTANCE+") ");
		try{			 
			 String subject = extractor.completeSubject("Bakunin Guillaume", TestConstants.SENTENCE_05);
			 System.out.println(subject);
			 if(!subject.equals("Bakunin y James Guillaume")){
				 fail("Extraction is not as expected");
			 }
		}catch(Exception e){
			fail("Fail");
		}
		
	}
	
	@Test
	public void testCase02() {
		System.out.println("Case 02: distance between words is 0");
		try{			 
			 String subject = extractor.completeSubject("James Guillaume", TestConstants.SENTENCE_05);
			 System.out.println(subject);
			 if(!subject.equals("James Guillaume")){
				 fail("Extraction is not as expected");
			 }
		}catch(Exception e){
			fail("Fail");
		}
		
	}
	
	@Test
	public void testCase03() {
		System.out.println("Case 03: distance between words is > Min distance ("+Constants.MIN_WORD_DISTANCE+") ");
		try{			 
			 String subject = extractor.completeSubject("Guillaume International", TestConstants.SENTENCE_05);
			 if(subject == null){
				 System.out.println("es null");
			 }
			 if(subject != null){
				 fail("Extraction is not as expected");
			 }
		}catch(Exception e){
			fail("Fail");
		}
		
	}

}
