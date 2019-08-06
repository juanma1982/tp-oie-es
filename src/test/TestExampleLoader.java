package test;

import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import ar.edu.unlp.ExampleLoader;
import ar.edu.unlp.constants.Filenames;
import ar.edu.unlp.entities.Example;
import ar.edu.unlp.entities.ExampleList;
import ar.edu.unlp.entities.Relation;

public class TestExampleLoader {

	@Test
	public void test() {
		ExampleList el=null;
		try {
			el = ExampleLoader.loadExamplesFromJson(Filenames.JSON_TEST_EXAMPLES);
			List<Example> examples = el.getExamples();
			Assert.assertNotNull(examples);
			if(examples.size()<1) fail("examples is empty");
			Assert.assertNotNull(el.getLang());
			Assert.assertNotEquals(el.getLang().length(), 0);
			for (Example example : examples) {				
				Assert.assertNotNull(example.getSentence());
				Assert.assertNotEquals(example.getSentence().length(), 0);
				System.out.println(example.getSentence());
				List<Relation> relPatt = example.getRelations();
				Assert.assertNotNull(relPatt);
				Assert.assertNotEquals(relPatt.size(), 0);
				for (Relation relationPatterns : relPatt) {
					Assert.assertNotNull(relationPatterns.getEntity1());
					Assert.assertNotEquals(relationPatterns.getEntity1().length(), 0);
					
					Assert.assertNotNull(relationPatterns.getEntity2());
					Assert.assertNotEquals(relationPatterns.getEntity2().length(), 0);
					
					Assert.assertNotNull(relationPatterns.getRelation());
					Assert.assertNotEquals(relationPatterns.getRelation().length(), 0);
					
					System.out.print("("+relationPatterns.getEntity1()+", ");					
					System.out.print(relationPatterns.getRelation()+", ");
					System.out.println(relationPatterns.getEntity2()+")");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		} 
		System.out.println("fin "+el);
	}

}
