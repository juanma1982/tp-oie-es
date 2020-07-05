package ar.edu.unlp;

import java.util.ArrayList;
import java.util.List;

import ar.edu.unlp.constants.Words;
import ar.edu.unlp.entities.ExtraSentenceData;
import ar.edu.unlp.entities.NounPhrase;
import ar.edu.unlp.entities.SentenceData;

/**
 * @author Juan Manuel Rodríguez
 * */
public class ArgumentExtractorReverbStyle extends ArgumentExtractor{
	
	
	protected String[] sentArray = null;
	protected String[] posArray = null;
	protected String tag[] = null;
	protected String currentRelationStr;
	protected int relStartWord;
	protected int relWordCount;
	protected int rightTextWordCount;
	protected int leftTextWordCount;
	//protected String newRelation = "";
	
	public ArgumentExtractorReverbStyle(){
		
	}
	
	public String getCurrentRelationStr() {
		return this.currentRelationStr;
	}

	public String extractNounPhraseAtLeft(SentenceData sentenceData, String relationStr, String argument){
		String phraseAtLeft = "";
		String sentence = sentenceData.getCleanSentence();
		if( sentence.indexOf(relationStr) <sentence.indexOf(argument)) {
			ExtraSentenceData extraSentenceData = new ExtraSentenceData();
			String leftChunkedSentence = sentenceUtils.getChunkedSentenceAtLeftOf(sentenceData, relationStr,extraSentenceData);
			if(leftChunkedSentence==null || leftChunkedSentence.isEmpty()) return null;
			String extractionCandidate = extractAtLeft(leftChunkedSentence);
			if(extractionCandidate.contains(relationStr) || extractionCandidate.contains(argument)) {
				return phraseAtLeft;
			}
			phraseAtLeft =extractionCandidate;
		}
		
		return phraseAtLeft;
	
	}
	
	public List<String> argumentExtractorAll(SentenceData sentenceData, String relationStr, String entity01){
						
			List<String> listStr = new ArrayList<String>();
			if(sentenceData.getSentence().isEmpty()) return listStr;
			this.currentRelationStr = relationStr;
			String str = sentenceData.getCleanSentence();
			int totalWords = sentenceUtils.countWords(str);
			if(totalWords == 0) return listStr;
			ExtraSentenceData extraSentenceData = new ExtraSentenceData();
			
			String rightChunkedSentence = sentenceUtils.getChunkedSentenceAtRightOf(sentenceData, relationStr,extraSentenceData);
			String leftChunkedSentence=null;
			if(rightChunkedSentence==null) {
				leftChunkedSentence = sentenceUtils.getChunkedSentenceAtLeftOf(sentenceData, relationStr,extraSentenceData);
			}
			
			this.sentArray = sentenceData.getCleanSentenceArray();
			this.posArray  = sentenceData.getSentenceAsPOSTagsArray();
			this.tag = sentenceData.getChunkerTags();	
			
			if(rightChunkedSentence==null && leftChunkedSentence==null) {
				String newRelationStr = extractTextInTheMiddle(sentenceData.getCleanSentence(),relationStr);
				if(newRelationStr != null) {
					return argumentExtractorAll(sentenceData,newRelationStr, null);
				}
				String lastWord = getLastWord(relationStr);
				if(!lastWord.equals(relationStr)) return argumentExtractorAll(sentenceData, lastWord, null);
				return listStr;
			}
			
			this.relStartWord = extraSentenceData.relStartWord;
			this.relWordCount = extraSentenceData.relWordCount;
			this.rightTextWordCount = extraSentenceData.rightTextWordCount;
			this.leftTextWordCount  = extraSentenceData.leftTextWordCount;
			String leftText = extraSentenceData.leftText;
			String rightText= extraSentenceData.rightText;			
			
			/**********************************************************/			
			if(relationIsSaid(relationStr)) {
				listStr = saidExtraction(listStr,rightText);
				if(listStr.isEmpty() && leftText!=null) {
					listStr.add(leftText);
				} 
				return listStr;
			}
			if(rightText != null && !rightText.isEmpty()) {
				listStr = saidExtraction(listStr,rightText);
				return listStr;
			}		
//			
//			/****extract at  Right ***/
//			if(rightChunkedSentence!=null) {
//				listStr.addAll(extractAtRight(rightChunkedSentence));
//			}
//			if(!listStr.isEmpty()) return listStr;
			/********************extract at left******************************/
			if(leftChunkedSentence!=null) {
				String extractionCandidate = extractAtLeft(leftChunkedSentence);
				if(entity01 == null || (!extractionCandidate.contains(entity01) && !entity01.contains(extractionCandidate))) {
					listStr.add(extractionCandidate);
				}
			}
			return listStr;
	}

	/**
	 * Given a text and a list of strings, this function will add in the list a new argument taken from the rigth text 
	 * until the first puntuation mark
	 * 
	 * @param arguments
	 * @param rightText
	 * @return
	 */
	private List<String> saidExtraction(List<String> arguments, String rightText) {
		
		if(rightText!=null && !rightText.isEmpty()) {
			for(int k=0;k<Words.SAID_END_POINTS.length;k++){
				int index = rightText.indexOf(Words.SAID_END_POINTS[k]);
				if(index!=-1){
					arguments.add(rightText.substring(0, index));
					return arguments;
				}
			}
			if(rightText.endsWith(".")) {
				rightText = rightText.substring(0, rightText.length() - 1);
			}
			arguments.add(rightText);
		}
		
		
		return arguments;
	}

	public boolean relationIsSaid(String relationStr2) {
		for(int k=0;k<Words.SAID_AND_SYNONYMS.length;k++){
			if(relationStr2.toLowerCase().equals(Words.SAID_AND_SYNONYMS[k])){
				return true;
			}
		}
		return false;
	}

	protected String extractAtLeft(String leftChunkedSentence) {
		boolean foundNP = false;
		int startNP = 0;
		int lengthNP = 0;
		String[] ChunkedLeft = leftChunkedSentence.split(Words.SPACE);		
		int endNP = 0;
		lengthNP = 1;
		for (int i = ChunkedLeft.length-1; i >= 0; i--) {
			if(!foundNP && ChunkedLeft[i].equals(Words.Chunks.I_NP)) {
				foundNP=true;
				endNP = i;
			}else if(ChunkedLeft[i].equals(Words.Chunks.B_NP)) {
				startNP = i;
				if(endNP > 0) lengthNP = (endNP - startNP)+1;				
				break;
			}
		}
		return sentenceUtils.extractSubString(sentArray,startNP,lengthNP);
	}
		
	
	/**
	 * This method, is different from extractNextSingleNounPhraseStartingAt, because it method could extract 2 noun phrases joined by a connector<br/>
	 * like "to", "at" or "of"
	 * 
	 * @param ChunkedRight array of chunked text: [B-N, I-NP, etc.]. The text is text at right of the relation. 
	 * @param offset where to start lookin for an Noun Phrase in the ChunkedRight, i.e: 0
	 * @return NounPhrase, an object with the extracted NounPhrase, the start position and the end position within the ChunkedRight
	 */
	protected NounPhrase extractNextNounPhrasesStartingAt(String[] ChunkedRight,int offset) {
		NounPhrase np = extractNextSingleNounPhraseStartingAt(ChunkedRight,offset);
		NounPhrase np2=null;
		int localOffset =np.getStartIndex()+np.getLength();
		int nextIndex = relStartWord+relWordCount+localOffset;
		int nextIndexPlus = nextIndex+1;
		if(sentArray.length>nextIndexPlus && this.posArray.length>nextIndexPlus && this.tag.length>nextIndexPlus) {
			for (int i = 0; i < Words.NP_CONNECTORS.length; i++) {
				String connector = Words.NP_CONNECTORS[i];
				if(this.sentArray[nextIndex].equals(connector) && this.tag[nextIndexPlus].equals(Words.Chunks.B_NP)) {
					np2 = extractNextSingleNounPhraseStartingAt(ChunkedRight,localOffset);
					np2.setNounPhrase(np.getNounPhrase()+Words.SPACE+this.sentArray[nextIndex]+Words.SPACE+np2.getNounPhrase());
					np2.setLength(np.getLength()+np2.getLength()+1);
					np2.setStartIndex(np.getStartIndex());
					return np2;
				}
			}
			if(this.tag[nextIndex].equals(Words.Chunks.B_ADVP)){
				np2 = extractNextSingleADVPhraseStartingAt(ChunkedRight,localOffset);
				if(np2.getLength() > 0) {
					np2.setNounPhrase(np.getNounPhrase()+Words.SPACE+np2.getNounPhrase());
					np2.setLength(np.getLength()+np2.getLength());
					np2.setStartIndex(np.getStartIndex());
					return np2;
				}
			}
			
		}
		return np;
		
	}
	
	/**
	 * 
	 * @param ChunkedRight array of chunked text: [B-N, I-NP, etc.]. The text is text at right of the relation. 
	 * @param offset where to start lookin for an Noun Phrase in the ChunkedRight, i.e: 0 
	 * @return NounPhrase, an object with the extracted NounPhrase, the start position and the end position within the ChunkedRight
	 */
	protected NounPhrase extractNextSingleNounPhraseStartingAt(String[] ChunkedRight,int offset) {
		
		return extractNextSinglePhraseStartingAtWithTags(ChunkedRight,offset,Words.Chunks.B_NP,Words.Chunks.I_NP);
	}
	
	protected NounPhrase extractNextSingleADVPhraseStartingAt(String[] ChunkedRight,int offset) {

		return extractNextSinglePhraseStartingAtWithTags(ChunkedRight,offset,Words.Chunks.B_ADVP,Words.Chunks.I_ADVP);
	}
	
	protected NounPhrase extractNextSinglePhraseStartingAtWithTags(String[] ChunkedRight,int offset, String initTag, String continueTag) {
		NounPhrase np = new NounPhrase();
		boolean foundNP = false;
		int startNP = 0;
		int lengthNP = 0;
		for (int i = offset; i < ChunkedRight.length; i++) {
			if(!foundNP && ChunkedRight[i].equals(initTag)) {
				foundNP=true;
				startNP = i;
			}else if(foundNP && !ChunkedRight[i].equals(continueTag)) {
					lengthNP = i - startNP;
					break;
			}
		}
		if(foundNP && lengthNP==0) {
			lengthNP=ChunkedRight.length- startNP;
		}
		String extractionCandidate = sentenceUtils.extractSubString(sentArray,relStartWord+relWordCount+startNP,lengthNP);
		np.setNounPhrase(extractionCandidate);
		np.setLength(lengthNP);
		np.setStartIndex(startNP);
		return np;
	}
	
	
	protected List<String> extractAtRight(String rightChunkedSentence) {
		List<String> result = new ArrayList<String>();
		String[] ChunkedRight = rightChunkedSentence.split(Words.SPACE);
		
		NounPhrase np = extractNextNounPhrasesStartingAt(ChunkedRight,0);
		
		if(np.getNounPhrase().equals("")) return result;
		
		int prevIndex = relStartWord+relWordCount+np.getStartIndex()-1;		
		if(prevIndex >=0) {
			String wordBefore = sentenceUtils.extractSubString(sentArray,prevIndex,1);
			if(isAGoodWordToStartArgumnet(wordBefore) && !currentRelationStr.endsWith(wordBefore)) {
				np.setNounPhrase(wordBefore+Words.SPACE+np.getNounPhrase());
			}
		}
		result.add(np.getNounPhrase());
		boolean quit = false;
		do {
			int localOffset =np.getStartIndex()+np.getLength();
			int nextIndex = relStartWord+relWordCount+localOffset;
			int nextIndexPlus = nextIndex+1;
			if(sentArray.length>nextIndexPlus && this.posArray.length>nextIndexPlus && this.tag.length>nextIndexPlus) {
				if(this.posArray[nextIndex].equals(Words.ADP) && this.tag[nextIndexPlus].equals(Words.Chunks.B_NP)) { 
					np = extractNextNounPhrasesStartingAt(ChunkedRight,localOffset);
					result.add(result.get(result.size()-1)+Words.SPACE+this.sentArray[nextIndex]+Words.SPACE+np.getNounPhrase());
				}else if(this.posArray[nextIndex].equals(Words.DET) && this.tag[nextIndex].equals(Words.Chunks.B_NP)) {
					np = extractNextNounPhrasesStartingAt(ChunkedRight,localOffset);
					result.add(result.get(result.size()-1)+Words.SPACE+this.sentArray[nextIndex]+Words.SPACE+np.getNounPhrase());
				}else if(this.posArray[nextIndex].equals(Words.CCONJ) && this.tag[nextIndexPlus].equals(Words.Chunks.B_NP)) {
					np = extractNextNounPhrasesStartingAt(ChunkedRight,localOffset);
					result.add(this.sentArray[nextIndex]+Words.SPACE+np.getNounPhrase());
				}else{
					quit=true;
				}
			}else {
				quit=true;
			}
			
		}while(!quit);
		return result;
	}
	
	private boolean isAGoodWordToStartArgumnet(String wordBefore) {
		for(int k=0;k<Words.GOOD_START_WORDS_FOR_ARGUMENT.length;k++){
			if(wordBefore.toLowerCase().equals(Words.GOOD_START_WORDS_FOR_ARGUMENT[k])){
				return true;
			}
		}
		return false;
	}
	
	public String getLastWord(String line) {
		String[] words = line.split(Words.SPACE);
		if(words!=null) return words[words.length-1];
		return line;
	}
	
	/**
	 * This method will extract all text between the start and the end of a given relation.
	 * For example: 
	 *       sentence: "Albert Einstein was awarded the Nobel Prize in Sweden in 1921."<br/>
	 *       line: "was awarded in"<br/>
	 *       The return will be: was awarded the Nobel Prize in
	 * @param sentence any String      
	 * @param line String
	 * @return null or the full text between the words of the relation
	 */
	public String extractTextInTheMiddle(String sentence, String line) {
		String[] words = line.split(Words.SPACE);
		if(words!=null && words.length >1) {
			StringBuilder sb = new StringBuilder(" ");
			for(int i=0;i<(words.length-1);i++) {
				sb.append(words[i]);
				sb.append(Words.SPACE);
			}
			String auxSentence = Words.SPACE+sentence+Words.SPACE;
			int start = auxSentence.indexOf(sb.toString());
			int end = auxSentence.indexOf(Words.SPACE+words[words.length-1]+Words.SPACE)+1;
			if(start <= -1 || end <= 0 || (end <= start)) return null;
			return auxSentence.substring(start, end+(words[words.length-1].length())).trim();
		}
		return null;
	}
	
}
