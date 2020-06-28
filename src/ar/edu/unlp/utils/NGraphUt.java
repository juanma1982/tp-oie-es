package ar.edu.unlp.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ar.edu.unlp.constants.Words;

public class NGraphUt {

	public List<String> getUnigrams(String sentence){
		return Arrays.asList(sentence.split(Words.SPACE));
	}
	
	public List<String> getBigrams(String sentence){
		String[] words = sentence.split(Words.SPACE);
		List<String> returnedList = new ArrayList<String>();
		String prevWord = "<BEGIN>";
		for (int i = 0; i < words.length; i++) {
			returnedList.add(prevWord+"-"+words[i]);
			prevWord = words[i];
		}
		returnedList.add(prevWord+"-<END>");
		
		return returnedList;
	}
	
	public String getBigramsAsString(String sentence){
		String[] words = sentence.split(Words.SPACE);
		StringBuilder sb = new StringBuilder("");
		String prevWord = "<BEGIN>";
		for (int i = 0; i < words.length; i++) {
			sb.append(prevWord);
			sb.append("-");
			sb.append(words[i]);
			sb.append(Words.SPACE);
			prevWord = words[i];
		}
		sb.append(prevWord+"-<END>");
		
		return sb.toString();
	}
	
	
}
