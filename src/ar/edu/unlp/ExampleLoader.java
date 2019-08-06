package ar.edu.unlp;

import java.io.FileNotFoundException;
import java.io.FileReader;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import ar.edu.unlp.constants.Filenames;
import ar.edu.unlp.entities.ExampleList;

public class ExampleLoader {

	public static ExampleList loadExamplesFromJson() throws FileNotFoundException{
		return loadExamplesFromJson(Filenames.JSON_EXAMPLES);
	}
	
	public static ExampleList loadExtraExamplesFromJson(String filename) throws FileNotFoundException{
		return loadExamplesFromJson(filename);
	}

	public static ExampleList loadExamplesFromJson(String filename) throws FileNotFoundException{
		Gson gson = new Gson();
		JsonReader reader = new JsonReader(new FileReader(filename));
		ExampleList exampleList = gson.fromJson(reader, ExampleList.class); // contains the whole reviews list
		return exampleList;
	}
}
