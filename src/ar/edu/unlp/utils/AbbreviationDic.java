package ar.edu.unlp.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ar.edu.unlp.constants.Filenames;

public class AbbreviationDic {	
	
	public static final String patternNumbers = "\\d+\\.\\d+";
	public static final String patternAcronyms = "(?:[a-zA-Z]\\.){2,}";
	public static final String patternAbbrevDoubles = "\\w+\\. \\w+.";
	public static final String patternAbbrevSingles = "\\w+\\.";
	public static final String replaceChar = "@#@";
	
	private Set<String> dictionary;	
	

	public AbbreviationDic() throws IOException{
		this.dictionary = new HashSet<String>();
		BufferedReader br = null;		
		try {
			br = new BufferedReader(new FileReader(Filenames.ABBREV_DIC));
		    String line = br.readLine();
		    while (line != null) {
		    	dictionary.add(line.trim());
		        line = br.readLine();
		    }		    		
		}finally {
		    br.close();
		}
	}
	
	private String replaceUsingPatterns(String pattern, String text){
		Pattern patron = Pattern.compile(pattern);
		Matcher match = patron.matcher(text);		
		while (match.find()) {	
			String aux = match.group(0).replaceAll("\\.", replaceChar);
			text = text.replace(match.group(0), aux);
		}
		return text;
	}
	
	private String replaceAbbrevUsingPattern(String pattern, String text){
		Pattern patron = Pattern.compile(pattern);
		Matcher match = patron.matcher(text);		
		while (match.find()) {
			String matchAbbrev = match.group(0);
			if(this.dictionary.contains(matchAbbrev)){
				String aux = matchAbbrev.replaceAll("\\.", replaceChar);
				text = text.replace(matchAbbrev, aux);
			}
		}
		return text;
	}
	
	private String replaceNumbers(String text){
		return this.replaceUsingPatterns(patternNumbers, text);
	}
	
	private String replaceAcronyms(String text){
		return this.replaceUsingPatterns(patternAcronyms, text);
	}
	
	private String replaceAbbrevDoubles(String text){
		return this.replaceAbbrevUsingPattern(patternAbbrevDoubles, text);
	}
	private String replaceAbbrevSingles(String text){
		return this.replaceAbbrevUsingPattern(patternAbbrevSingles, text);
	}
	
	public String replaceAbbrevDot(String text){
		text = replaceNumbers(text);
		text = replaceAcronyms(text);
		text = replaceAbbrevDoubles(text);
		text = replaceAbbrevSingles(text);
		return text;
	}
	public String removeAbbrevSpecialChar(String text){		
		return text.replaceAll(replaceChar, ".");
	}
	
	public String[] splitIntoSentences(String paragraph){
			String[] sentences = null;
			String[] sentencesReturn = null;
			paragraph = replaceNumbers(paragraph);
			paragraph = replaceAcronyms(paragraph);
			paragraph = replaceAbbrevDoubles(paragraph);
			paragraph = replaceAbbrevSingles(paragraph);
			sentences = paragraph.split(";|\\.");
			sentencesReturn = new String[sentences.length];
			int i=0;
			for (String sentence : sentences) {				
				sentencesReturn[i] = sentence.replaceAll(replaceChar, ".").concat(".").trim();
				i++;
			}
		
		return sentencesReturn;
	}
	
	
	public Set<String> getDictionary() {
		return dictionary;
	}

}
