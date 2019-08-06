package ar.edu.unlp;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import ar.edu.unlp.constants.Filenames;
import ar.edu.unlp.entities.PatternContainer;

public class PatternLoader {

	public static void savePatternsIntoJson(PatternContainer patterns) throws JsonIOException, IOException{		
		savePatternsIntoJson(patterns,Filenames.JSON_PATTERNS);
	}
	public static void savePatternsIntoJson(PatternContainer patterns, String filename) throws JsonIOException, IOException{
				
		try (Writer writer = new FileWriter(filename)) {
		    Gson gson = new GsonBuilder().setPrettyPrinting().create();
		    gson.toJson(patterns, writer);
		}
	}
	
	public static PatternContainer loadPatternsFromJson() throws FileNotFoundException{		
		return loadPatternsFromJson(Filenames.JSON_PATTERNS);
	}
	public static PatternContainer loadPatternsFromJson(String filename) throws FileNotFoundException{
		Gson gson = new Gson();
		JsonReader reader = new JsonReader(new FileReader(filename));
		
		Type listType = new TypeToken<PatternContainer>(){}.getType();
		
		PatternContainer patterns = gson.fromJson(reader, listType); // contains the whole reviews list
		return patterns;
	}
	
	
}
