package ar.edu.unlp.utils;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ar.edu.unlp.constants.Words;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.WordToSentenceProcessor;

public class ParagraphStanford {
	
	private AbbreviationDic ad = null;
	public static String PATTERN_01 = "\\\"([^\\\"]+)\\\"";
	
	public ParagraphStanford() throws IOException{
		ad = new AbbreviationDic();
	}
	
	public String[] splitIntoSentences(String paragraph){
		
		return this.splitIntoSentences(paragraph, null);
	}
	
	public String[] splitIntoSentences(String paragraph, Map<String,String> wildcardReplacement){
		String lparagraph = ad.replaceAbbrevDot(paragraph);
		lparagraph = lparagraph.replaceAll(";", ".");
		List<String> sentenceList;
		List<CoreLabel> tokens = new ArrayList<CoreLabel>();
		PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<CoreLabel>(new StringReader(lparagraph), new CoreLabelTokenFactory(), "");
		while (tokenizer.hasNext()) {
		    tokens.add(tokenizer.next());
		}
		//// Split sentences from tokens
		List<List<CoreLabel>> sentences = new WordToSentenceProcessor<CoreLabel>().process(tokens);
		//// Join back together
		int end;
		int start = 0;
		sentenceList = new ArrayList<String>();
		int replaceCount=0;
		for (List<CoreLabel> sentence: sentences) {
		    end = sentence.get(sentence.size()-1).endPosition();
		    String subSentence = ad.removeAbbrevSpecialChar(lparagraph.substring(start, end).trim());		    
		    String subSentenceQuoted = extractQuoted(subSentence);
		    if(subSentenceQuoted!=null){
		    	String key = Words.WILDCARD_QUOTED+String.format(Words.WILDCARD_LEADING_ZEROES_FORMAT, replaceCount);;
		    	sentenceList.add(subSentence.replace(subSentenceQuoted, key).replaceAll("\"", ""));
		    	if(wildcardReplacement!=null){
		    		wildcardReplacement.put(key, subSentenceQuoted);
		    	}
		    	replaceCount++;
		    	sentenceList.add(subSentenceQuoted);
		    }else{
		    	sentenceList.add(subSentence);
		    }
		    start = end;
		}
		return sentenceList.toArray(new String[sentenceList.size()]);
	}
	
	public String extractQuoted(String line){
		Pattern r = Pattern.compile(PATTERN_01);
		
		Matcher m = r.matcher(line);
		if (m.find()) {
			String argMatched = m.group(1);
			if(argMatched!=null && !argMatched.isEmpty()){
				return argMatched;
			}
		}
		return null;
	}

}
