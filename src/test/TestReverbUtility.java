package test;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ar.edu.unlp.ReVerbExtractorUtility;
import ar.edu.unlp.ReadTestFile;
import ar.edu.unlp.entities.Relation;

public class TestReverbUtility {

	ReVerbExtractorUtility reverbExtractorUt = new ReVerbExtractorUtility();
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		
		List<Relation> relationList = new ArrayList<Relation>();
		ReadTestFile rtf = new ReadTestFile();
		try {
			rtf.startReading();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			fail(e.getMessage());
			
		}
		String line = null;
		try {
			line = rtf.readNextLine();
			while( line!=null ){
				relationList.addAll(reverbExtractorUt.extractRelationsFromLine(line));
				line = rtf.readNextLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
		
		for (Relation relation : relationList) {
			System.out.println(relation.toStringScore());
		}
	}

}
