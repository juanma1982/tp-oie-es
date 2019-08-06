package ar.edu.unlp.entities;

import java.util.List;

public class ExampleList {
	protected String lang;
	protected List<Example> examples;
	
	public String getLang() {
		return lang;
	}
	public void setLang(String lang) {
		this.lang = lang;
	}
	public List<Example> getExamples() {
		return examples;
	}
	public void setExamples(List<Example> examples) {
		this.examples = examples;
	}
	
}
