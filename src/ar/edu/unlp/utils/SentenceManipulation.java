package ar.edu.unlp.utils;

import ar.edu.unlp.constants.Words;
import ar.edu.unlp.entities.ExtraSentenceData;
import ar.edu.unlp.entities.SentenceData;

public class SentenceManipulation {
	
	

	public String getTheRestOfTheNounPhrase(SentenceData sentenceData, String relationPart) {
		
		ExtraSentenceData extraSentenceData = new ExtraSentenceData();
		
		
		String str = sentenceData.getCleanSentence();
		extraSentenceData.rightText = this.getRightText(str, relationPart);
		if(extraSentenceData.rightText==null) return null;
		extraSentenceData.relStartWord = this.startWord(str,relationPart);
		extraSentenceData.relWordCount = this.countWords(relationPart);
		extraSentenceData.rightTextWordCount = this.countWords(extraSentenceData.rightText);
		
		String[] chunkTags = sentenceData.getChunkerTags();
		String[] words =sentenceData.getCleanSentenceArray();
		int indexWord = extraSentenceData.relStartWord+extraSentenceData.relWordCount;
		if(indexWord<0) return null;
		int wordCount=extraSentenceData.rightTextWordCount;
		if(chunkTags.length<(indexWord+wordCount)) return null;
		StringBuilder sb = new StringBuilder("");		
		for(int i=indexWord;i<indexWord+wordCount;i++){
			if(chunkTags[i].startsWith(Words.Chunks.CONTINUE_LETTER)) {
				sb.append(words[i]);
				sb.append(Words.SPACE);
			}else {
				break;
			}
		}
		
		return sb.toString().trim();
	}
	
	public String getChunkedSentenceAtRightOf(SentenceData sentenceData, String relationPart, ExtraSentenceData extraSentenceData) {
		if(extraSentenceData == null) {
			extraSentenceData = new ExtraSentenceData();
		}
		
		String str = sentenceData.getCleanSentence();
		extraSentenceData.rightText = this.getRightText(str, relationPart);
		if(extraSentenceData.rightText==null) return null;
		extraSentenceData.relStartWord = this.startWord(str,relationPart);
		extraSentenceData.relWordCount = this.countWords(relationPart);
		extraSentenceData.rightTextWordCount = this.countWords(extraSentenceData.rightText);
		return this.extractSubString(sentenceData.getChunkerTags(),extraSentenceData.relStartWord+extraSentenceData.relWordCount,extraSentenceData.rightTextWordCount);
	}
	
	public String getChunkedSentenceAtLeftOf(SentenceData sentenceData, String relationPart, ExtraSentenceData extraSentenceData) {
		if(extraSentenceData == null) {
			extraSentenceData = new ExtraSentenceData();
		}
		
		String str = sentenceData.getCleanSentence();
		extraSentenceData.leftText = this.getLeftText(str, relationPart);
		if(extraSentenceData.leftText == null) return null;
		extraSentenceData.leftTextWordCount = this.countWords(extraSentenceData.leftText);
		return this.extractSubString(sentenceData.getChunkerTags(),0,extraSentenceData.leftTextWordCount);
	}
	
	
	public String getRightText(String text, String relationText){
		int index = text.indexOf(relationText+Words.SPACE);
		if(index == -1) {
			index = text.indexOf(relationText);
			if(index == -1) return null;
		}
		
		return text.substring(index+relationText.length()).trim();
	}
	
	public int countWords(String input) {
		if (input == null || input.isEmpty()) { 
			return 0; 
		} 
		String[] words = input.split("\\s+"); 
		return words.length;
	}
	
	public String getLeftText(String text, String relationText){
		int index = text.indexOf(relationText+Words.SPACE);
		if(index == -1) {
			index = text.indexOf(relationText);
			if(index == -1) return null;
		}
		
		return text.substring(0,index).trim();
	}
	
	/**
	 * This function compares a text with a subtext (a substring) and returns the word index in wich substring start. In example:<br/>
	 *       text: "Albert Einstein was awarded the Nobel Prize in Sweden in 1921."<br/>
	 *       subText: "was awarded"<br/>
	 *       The return will be: 2, because "was" is the third word in the full text (Albert=0, Einstein=1, was=2 )
	 * @param text any String      
	 * @param subtext any sub strign of text
	 * @return the index word, or -1 if the subText is not in text
	 * 
	 * */
	public int startWord(String text, String subtext){
		String[] wordsInText = text.split("\\s+"); 
		String[] wordsInSubText = subtext.split("\\s+");
		int indexWord =-1;
		try{
			for(int i=0;i<wordsInText.length;i++){			
				if( wordsInText[i].equals(wordsInSubText[0])){
					indexWord = i;
					for(int j=0;j<wordsInSubText.length;j++){
						if(!wordsInText[i+j].equals(wordsInSubText[j])){
							break;
						}else if(j == (wordsInSubText.length-1) ){
							return indexWord;
						}
					}
				}
			}
		}catch(ArrayIndexOutOfBoundsException e){
			System.err.println("error at startWord: "+e.getMessage()+".. continues");
			System.err.println("text: "+text);
			System.err.println("subtext: "+subtext);
		}
		return indexWord;
	}
	
	/** 
	 * @desc This method will return a substring, using the starting word (indexWord) and the amount of words (wordCount)<br/>
	 * 		 In example:<br/>
	 * 		 text: "Albert Einstein was awarded the Nobel Prize in Sweden in 1921."<br/>
	 *       indexWord: 2<br/>
	 *       wordCount: 3<br/>
	 *       The return will be: "was awarded the". Because, was is the third word (index 2), and the substring has 3 words 
	 * @param text any String
	 * @param indexWord the number of word to start the substring, from 0 to (length-1)
	 * @param wordCount how many words will be in the returned subtring
	 * @return a substring, or null if the indexWord and wordCount are out of range
	 */
	public String extractSubString(String text, int indexWord, int wordCount){
		String[] wordsInText = text.split("\\s+");
		return extractSubString(wordsInText, indexWord, wordCount);
	}
	
	/** 
	 * @desc This method will return a substring, using the starting word (indexWord) and the amount of words (wordCount)<br/>
	 * 		 In example:<br/>
	 * 		 text: "Albert Einstein was awarded the Nobel Prize in Sweden in 1921."<br/>
	 *       indexWord: 2<br/>
	 *       wordCount: 3<br/>
	 *       The return will be: "was awarded the". Because, was is the third word (index 2), and the substring has 3 words 
	 * @param wordsInText an String array words in text
	 * @param indexWord the number of word to start the substring, from 0 to (length-1)
	 * @param wordCount how many words will be in the returned subtring
	 * @return a substring, or null if the indexWord and wordCount are out of range
	 */
	public String extractSubString(String[] wordsInText, int indexWord, int wordCount){		 
		if(wordsInText.length<(indexWord+wordCount)) return null;
		StringBuilder sb = new StringBuilder("");		
		for(int i=indexWord;i<indexWord+wordCount;i++){
			sb.append(wordsInText[i]);
			sb.append(Words.SPACE);
		}
		return sb.toString().trim();
	}
	
	
	public String getWordAtLeftOf(SentenceData sentenceData, String relationPart) {
		int relationStart = sentenceData.getCleanSentence().indexOf(relationPart);
		if(relationStart > 0 ) {
			int startAt = sentenceData.getCleanSentence().lastIndexOf(' ', relationStart-2);
			if(startAt==-1) {
				startAt=0;
			}else {
				startAt+=1;
			}
			return sentenceData.getCleanSentence().substring(startAt,relationStart-1);
		}
		return "";
	}
	
	public String getPOSTagAtLeftOf(SentenceData sentenceData, String relationPart) {
			String prevRelWord = getWordAtLeftOf(sentenceData,relationPart);
			if(prevRelWord!=null && !prevRelWord.isEmpty()) {
					String POS = sentenceData.getWordPOSTAG().get(prevRelWord);
					if(POS==null) {						
						return "";	
					}
					return POS;
					
			}
		return "";
	}
	
	public String getWordAtRightOf(SentenceData sentenceData, String relationPart) {
		int relationStart = sentenceData.getCleanSentence().indexOf(relationPart);
		if(relationStart > -1) {
			relationStart+= relationPart.length();
			int addOneForSpace = 1;
			if(relationPart.endsWith(Words.SPACE)) {
				addOneForSpace = 0; //the relation includes the space
			}
			int endsAt = sentenceData.getCleanSentence().indexOf(' ', relationStart+addOneForSpace);
			if(endsAt==-1) {
				endsAt=sentenceData.getCleanSentence().length();
			}
			if(relationStart == endsAt) return "";
			return sentenceData.getCleanSentence().substring(relationStart+addOneForSpace,endsAt);
			
		}
		return "";
	}
	
	public String getFirstWord(String text) {
		String[] words = text.split(Words.SPACE);
		if(words!=null && words.length >0) return words[0];		
		return "";
	}
	
	public String getPOSTagAtRightOf(SentenceData sentenceData, String relationPart) {
		String nextRelWord = getWordAtRightOf(sentenceData,relationPart);
		if(nextRelWord!=null && !nextRelWord.isEmpty()) {
			String POS = sentenceData.getWordPOSTAG().get(nextRelWord);
			if(POS==null) {						
				return "";	
			}
			return POS;
		}
		return "";
	}
	
	public String[] getNPOSTagAtRightOf(SentenceData sentenceData, String relationPart,int n) {
		String[] arrayOfNPosttags = new String[n];
		for (int i = 0; i < arrayOfNPosttags.length; i++) {
			arrayOfNPosttags[i]="";
		}
				
		int relationStart = sentenceData.getCleanSentence().indexOf(relationPart);
		if(relationStart > 0 ) {
			relationStart+= relationPart.length();
			int endsAt=sentenceData.getCleanSentence().length();
			if(relationStart == endsAt) return arrayOfNPosttags;
			String nextRelWord = sentenceData.getCleanSentence().substring(relationStart+1,endsAt);
			if(nextRelWord!=null && !nextRelWord.isEmpty()) {
				String[] words = nextRelWord.split(Words.SPACE);
				int end = n;
				if(words.length<n) {
					end=words.length;
				}
				for (int i = 0; i < end; i++) {
					String POS = sentenceData.getWordPOSTAG().get(words[i]);
					if(POS==null) {						
						return arrayOfNPosttags;	
					}else {
						arrayOfNPosttags[i]=POS;
					}
					
				}
				return arrayOfNPosttags;
			}
		}
		return arrayOfNPosttags;
	}	
}
