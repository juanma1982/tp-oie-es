package ar.edu.unlp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ar.edu.unlp.constants.Words;
import ar.edu.unlp.entities.PatternArgument;
import ar.edu.unlp.entities.PatternArgumentList;
import ar.edu.unlp.entities.SentenceData;
import ar.edu.unlp.exceptions.ExceptionArgumentExtractor;

/**
 * @author Juan Manuel Rodríguez
 * */
public class ArgumentExtractorRegex extends ArgumentExtractor{

	protected PatternArgumentList patterns;
	
	public ArgumentExtractorRegex(){
		this.patterns = new PatternArgumentList();
	}
	public ArgumentExtractorRegex(PatternArgumentList patternArgumentList){
		this.patterns = patternArgumentList;
	}
	
	public PatternArgument extractArgumentPattern(SentenceData sentenceData, String relationStr, String argumentStr) throws ExceptionArgumentExtractor {
		PatternArgument patternArgument= new PatternArgument();
		StringBuilder patternRegex = new StringBuilder("");  
		String Str = sentenceData.getCleanSentence();
		String endingWord = null;
		int startingWord = 0;
		boolean flagStartBracket = false;
		
		String subText = sentenceUtils.getRightText(Str, relationStr);
		int indexOfSubtext = -1;
		if(subText != null) {
			indexOfSubtext =  subText.indexOf(argumentStr);
		}
		if(indexOfSubtext == -1){
			subText = sentenceUtils.getLeftText(Str, relationStr);
			if(subText == null) throw new ExceptionArgumentExtractor("Relation text is not present in the sentence");
			indexOfSubtext =  subText.indexOf(argumentStr);
			if(indexOfSubtext == -1) throw new ExceptionArgumentExtractor("Relation text is not present in the sentence");
			patternArgument.setLeftPattern(true);
		}
		
		String[] argumentWords  = argumentStr.split(" ");
		String[] subTextWords = subText.split(" ");
		
		if(indexOfSubtext == 0){
			patternRegex.append("^");
		}else{
			startingWord = sentenceUtils.startWord(subText, argumentStr);			
			if(startingWord>0){
				patternRegex.append("(");
				patternRegex.append(sentenceData.getWordPOSTAG().get(subTextWords[startingWord]));
				flagStartBracket=true;
			}
		}
		if(!flagStartBracket){
			patternRegex.append("(");
			flagStartBracket=true;			
		}
		
		if(subTextWords.length <= argumentWords.length){ //rightTextWords.length should always be gretaer (or equal) to argumentWords.length
			patternRegex.append(".+)$");
		}else{
			if(startingWord+argumentWords.length >= subTextWords.length) { //TODO: FIX, why this happens?
				throw new ExceptionArgumentExtractor("subTextWords is shorter than startingWord + argumentWords.length");
			}
			endingWord = subTextWords[startingWord+argumentWords.length];
			String endingPosTag = sentenceData.getWordPOSTAG().get(endingWord);
			if(endingPosTag==null) {
				throw new ExceptionArgumentExtractor("word: '"+endingWord+"' doesn't have a POSTag Assosiated ");
			}
			if(endingPosTag.equals(".")){
				endingPosTag = "\\.";
			}
			patternRegex.append(".*?)");
			patternRegex.append(endingPosTag);
		}
		patternArgument.setPattern(patternRegex.toString());
		this.patterns.addPattern(patternArgument);
		return patternArgument;
	}
	



	
	/**
	 * @desc this method will return the argument for a given relation and a given entity01.<br>
	 * 		 it will use the argument patterns list for that.<br/>
	 * @param sentenceData all the information from a parsed sentence
	 * @param relationStr the subtring which is the realtion
	 * @param entity01 the substring wich is the entity01
	 * @return the arguments for the given data if it found something or null otherwise
	 */
	public String argumentExtractor(SentenceData sentenceData, String relationStr, String entity01){
		List<String> list = argumentExtractorFull(sentenceData,relationStr, entity01, true );
		if(list.isEmpty()) return null;
		return list.get(0);
	}
	
	/**
	 * @desc this method will return a list of arguments for a given relation and a given entity01.<br>
	 * 		 it will use the argument patterns list for that.<br/>
	 * @param sentenceData all the information from a parsed sentence
	 * @param relationStr the subtring which is the realtion
	 * @param entity01 the substring wich is the entity01
	 * @retun string list of arguments for the given data if it found something or an empty list
	 */
	public List<String> argumentExtractorAll(SentenceData sentenceData, String relationStr, String entity01){
		return argumentExtractorFull(sentenceData,relationStr, entity01, false );
	}
	
	/**
	 * @desc this method will return a list of arguments for a given relation and a given entity01.<br>
	 * 		 it will use the argument patterns list for that.<br/>
	 * @param sentenceData all the information from a parsed sentence
	 * @param relationStr the subtring which is the realtion
	 * @param entity01 the substring wich is the entity01
	 * @param returnFirst just add the first argument founded to the returned list
	 * @retun string list of arguments for the given data if it found something or an empty list
	 */
	protected List<String> argumentExtractorFull(SentenceData sentenceData, String relationStr, String entity01, boolean returnFirst){
		List<String> arguments = argumentExtractorFullScore(sentenceData,relationStr,entity01,returnFirst,true);
		if(arguments.isEmpty()) {
			arguments = argumentExtractorFullScore(sentenceData,relationStr,entity01,returnFirst,false);
		}
		return arguments;
	}
	
	/**
	 * @desc this method will return a list of arguments for a given relation and a given entity01.<br>
	 * 		 it will use the argument patterns list for that.<br/>
	 * @param sentenceData all the information from a parsed sentence
	 * @param relationStr the subtring which is the realtion
	 * @param entity01 the substring wich is the entity01
	 * @param returnFirst just add the first argument founded to the returned list
	 * @param scorePositive use the patterns with score positive or negative (the score negative is given to prevent the use or bad patterns, but it could be used as a last solution)
	 * @retun string list of arguments for the given data if it found something or an empty list
	 */
	protected List<String> argumentExtractorFullScore(SentenceData sentenceData, String relationStr, String entity01, boolean returnFirst,boolean scorePositive){
		
		List<String> listStr = new ArrayList<String>();
		if(sentenceData.getSentence().isEmpty()) return listStr;
		String str = sentenceData.getCleanSentence();
		int totalWords = sentenceUtils.countWords(str);
		if(totalWords == 0) return listStr;
		
		boolean onlyLeft = false;
		boolean onlyRright = false;
		
		String rightText = sentenceUtils.getRightText(str, relationStr);
		String leftText = sentenceUtils.getLeftText(str, relationStr);
		if(rightText==null && leftText==null) return listStr;
		if(rightText==null) onlyLeft=true;
		if(leftText==null)  onlyRright=true;
		
		int relStartWord = sentenceUtils.startWord(str,relationStr);
		int relWordCount = sentenceUtils.countWords(relationStr);
		int rightTextWordCount = 0;		
		int leftTextWordCount = 0;
		
		if(!onlyLeft)   rightTextWordCount = sentenceUtils.countWords(rightText);		
		if(!onlyRright) leftTextWordCount = sentenceUtils.countWords(leftText);
		
		String rightTextPOSTag = null;
		String leftTextPOSTag = null;
		if(!onlyLeft)   rightTextPOSTag = sentenceUtils.extractSubString(sentenceData.getSentenceAsPOSTags(),relStartWord+relWordCount,rightTextWordCount);
		if(!onlyRright) leftTextPOSTag = sentenceUtils.extractSubString(sentenceData.getSentenceAsPOSTags(),0,leftTextWordCount);
		
		if(rightTextPOSTag == null && leftTextPOSTag == null){
			return listStr;
		}
		
		Set<String> setOfArguments = new HashSet<String>();
		List<PatternArgument> pattrns = patterns.getSortedList();
		for (PatternArgument patternArgument : pattrns) {
			
			if(scorePositive && patternArgument.getScore()<0) continue;
			if(!scorePositive && patternArgument.getScore()>0) continue;
						
			String textPOSTag = rightTextPOSTag;
			if(patternArgument.isLeftPattern()){
				if(onlyRright) continue;
				textPOSTag = leftTextPOSTag;
			} 
			if(!patternArgument.isLeftPattern()){
				if(onlyLeft) continue;
			}
			if(textPOSTag==null){
				return listStr;
			}
			
			Pattern r = Pattern.compile(patternArgument.getPattern());
			
			Matcher m = r.matcher(textPOSTag);
			if (m.find()) {
				   String argMatched = m.group(1);
				  
				   int argMatchedCoutnWord = sentenceUtils.countWords(argMatched);
				   String candidateArg = null;
				   if(patternArgument.isLeftPattern()){
					   candidateArg = sentenceUtils.extractSubString(str,0,argMatchedCoutnWord);
				   }else{
					   int argMatchedStartWord = relStartWord+relWordCount + sentenceUtils.startWord(textPOSTag,argMatched);
					   candidateArg = sentenceUtils.extractSubString(str,argMatchedStartWord,argMatchedCoutnWord);
				   }				   
				   if(candidateArg!=null && !candidateArg.isEmpty() && candidateArg.length()>1){
					   if(!setOfArguments.contains(candidateArg)){
						   //TODO: DELETE
						   //System.out.println("Pattern: "+patternArgument.toString()+" (left: "+patternArgument.isLeftPattern()+") = > "+candidateArg);
						   setOfArguments.add(candidateArg);
					   }
				   }
				   if(returnFirst){
					   return new ArrayList<String>(setOfArguments);
				   }
			}
		}
		listStr = new ArrayList<String>(setOfArguments);
		
		listStr = removeDuplicatedArguments(listStr,sentenceData);	
		return listStr;
	}
	
	
	/**
	 * @desc this method will remove arguments which contains other arguments<br/>
	 * 		 In example:<br/>
	 * 		 the sentence: "Albert Einstein was awarded the Nobel Prize in Sweden."<br/>
	 * 		 could have the following arguments: "the Nobel Prize in Sweden" and "in Sweden"<br/>
	 *  	 the argument: "the Nobel Prize in Sweden" should be removed<br/>
	 *  	 This method also remove the ending punctuation symbols like . or , at the end of a arguments
	 *  
	 * @return the input list modified. With the arguments wichs contains others deleted
	 */
	public List<String> removeDuplicatedArguments(List<String> argumentList,SentenceData sentenceData){
		
		Set<String> set = new HashSet<String>();		
		for(int i=0;i< argumentList.size();i++){
			String currentArgument = argumentList.get(i);
			for(int j=0;j< argumentList.size();j++){
				if(i==j) continue;
				if(currentArgument.contains(argumentList.get(j))){
					String secondArgument = argumentList.get(j).trim(); //.toLowerCase();
					
					String[] words = secondArgument.split(" ");
					String lastWordPOSTag = sentenceData.getWordPOSTAG().get(words[words.length-1]);
					
					if(lastWordPOSTag!= null && lastWordPOSTag.startsWith(Words.VERB_POS_FIRST_LETTER)){						
						currentArgument = secondArgument;
					}else{
						for(int k=0;k<Words.BAD_ENDINGS_WORDS_FOR_ARGUMENT.length;k++){
							if(secondArgument.toLowerCase().endsWith(Words.BAD_ENDINGS_WORDS_FOR_ARGUMENT[k])){
								currentArgument = secondArgument;
								break;
							}
						}
					}
					for(int k=0;k<Words.GOOD_START_WORDS_FOR_ARGUMENT.length;k++){
						if(secondArgument.toLowerCase().startsWith(Words.GOOD_START_WORDS_FOR_ARGUMENT[k])){
							if(!currentArgument.toLowerCase().startsWith(Words.GOOD_START_WORDS_FOR_ARGUMENT[k])) {
								currentArgument = secondArgument;
								break;
							}
						}
					}
					
					set.add(currentArgument);
				}
			}
		}
		List<String> returnedArgumentList = new ArrayList<String>();
		boolean discard = false;
		for (String argCandidate : argumentList) {
			if(!set.contains(argCandidate)){
				for(int k=0;k<Words.ENDING_POSTAGS_TO_REMOVE.length;k++){
					if(argCandidate.endsWith(Words.ENDING_POSTAGS_TO_REMOVE[k])){
						argCandidate =argCandidate.substring(0, argCandidate.length() - 2);
						break;
					}
				}
				discard=false;
				for (String argCandidate2 : returnedArgumentList) {
					if(argCandidate2.contains(argCandidate) || argCandidate.contains(argCandidate2)) {
						discard = true;
						break;
					}
				}
				if(!discard) {
					returnedArgumentList.add(argCandidate);
				}
		    }
		}
		
		/*for (Iterator<String> iter = argumentList.listIterator(); iter.hasNext(); ) {
		    String a = iter.next();
		    if(set.contains(a)){
		    	 iter.remove();
		    }
		}*/
		return returnedArgumentList;
	}

	public PatternArgumentList getPatterns() {
		return patterns;
	}

	public void setPatterns(PatternArgumentList patterns) {
		this.patterns = patterns;
	}
	
}
