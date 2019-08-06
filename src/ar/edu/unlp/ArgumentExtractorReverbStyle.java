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
	protected String relationStr;
	protected int relStartWord;
	protected int relWordCount;
	protected int rightTextWordCount;		
	protected int leftTextWordCount;
	
	public ArgumentExtractorReverbStyle(){
		
	}
		
	
	//TODO: pasar a inglés
	public String extractNounPhraseAtLeft(SentenceData sentenceData, String relationStr, String argument){
		String phraseAtLeft = "";
		String sentence = sentenceData.getCleanSentence();
		if( sentence.indexOf(relationStr) <sentence.indexOf(argument)) {
			ExtraSentenceData extraSentenceData = new ExtraSentenceData();
			String leftChunkedSentence = sentenceUtils.getChunkedSentenceAtLeftOf(sentenceData, relationStr,extraSentenceData);
			if(leftChunkedSentence==null) return phraseAtLeft;
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
			this.relationStr = relationStr;
			String str = sentenceData.getCleanSentence();
			int totalWords = sentenceUtils.countWords(str);
			if(totalWords == 0) return listStr;
			ExtraSentenceData extraSentenceData = new ExtraSentenceData();
			
			String rightChunkedSentence = sentenceUtils.getChunkedSentenceAtRightOf(sentenceData, relationStr,extraSentenceData);
			String leftChunkedSentence=null;
			if(rightChunkedSentence==null) {
				leftChunkedSentence = sentenceUtils.getChunkedSentenceAtLeftOf(sentenceData, relationStr,extraSentenceData);
			}
			if(rightChunkedSentence==null && leftChunkedSentence==null) return listStr;
			
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
			
			this.sentArray = sentenceData.getCleanSentenceArray();			
			//this.posArray  = sentenceData.getSentenceAsPOSTagsExtendedArray();
			this.posArray  = sentenceData.getSentenceAsPOSTagsArray();
			this.tag = sentenceData.getChunkerTags();						
			
			/****extract at  Right ***/
			if(rightChunkedSentence!=null) {
				listStr.addAll(extractAtRight(rightChunkedSentence));
			}
			if(!listStr.isEmpty()) return listStr;
			/********************extract at left******************************/
			if(leftChunkedSentence!=null) {
				String extractionCandidate = extractAtLeft(leftChunkedSentence);
				if(entity01 == null || (!extractionCandidate.contains(entity01) && !entity01.contains(extractionCandidate))) {
					listStr.add(extractionCandidate);
				}
			}
			return listStr;
	}

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
		String[] ChunkedLeft = leftChunkedSentence.split(" ");		
		int endNP = 0;
		lengthNP = 0;
		for (int i = ChunkedLeft.length-1; i >= 0; i--) {
			if(!foundNP && ChunkedLeft[i].equals(Words.Chunks.I_NP)) {
				foundNP=true;
				endNP = i;
			}else if(foundNP && ChunkedLeft[i].equals(Words.Chunks.B_NP)) {
					startNP = i;
					lengthNP = (endNP - startNP)+1; //TODO: COrrección para el metodo en inglés
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
					np2.setNounPhrase(np.getNounPhrase()+" "+this.sentArray[nextIndex]+" "+np2.getNounPhrase());
					np2.setLength(np.getLength()+np2.getLength()+1);
					np2.setStartIndex(np.getStartIndex());
					return np2;
				}
			}//TODO: Pasar a ingles
			if(this.tag[nextIndex].equals(Words.Chunks.B_ADVP)){
				np2 = extractNextSingleADVPhraseStartingAt(ChunkedRight,localOffset);
				if(np2.getLength() > 0) {
					np2.setNounPhrase(np.getNounPhrase()+" "+np2.getNounPhrase());
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
		//TODO: Pasar a ingles
		return extractNextSinglePhraseStartingAtWithTags(ChunkedRight,offset,Words.Chunks.B_NP,Words.Chunks.I_NP);
	}
	//TODO: Pasar a ingles
	protected NounPhrase extractNextSingleADVPhraseStartingAt(String[] ChunkedRight,int offset) {

		return extractNextSinglePhraseStartingAtWithTags(ChunkedRight,offset,Words.Chunks.B_ADVP,Words.Chunks.I_ADVP);
	}
	//TODO: Pasar a ingles
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
		String[] ChunkedRight = rightChunkedSentence.split(" ");
		
		NounPhrase np = extractNextNounPhrasesStartingAt(ChunkedRight,0);
		
		if(np.getNounPhrase().equals("")) return result;
		
		int prevIndex = relStartWord+relWordCount+np.getStartIndex()-1;		
		if(prevIndex >=0) {
			String wordBefore = sentenceUtils.extractSubString(sentArray,prevIndex,1);
			if(isAGoodWordToStartArgumnet(wordBefore) && !relationStr.endsWith(wordBefore)) {
				np.setNounPhrase(wordBefore+" "+np.getNounPhrase());
			}
		}
		result.add(np.getNounPhrase());
		boolean quit = false;
		do {
			int localOffset =np.getStartIndex()+np.getLength();
			int nextIndex = relStartWord+relWordCount+localOffset;
			int nextIndexPlus = nextIndex+1;
			if(sentArray.length>nextIndexPlus && this.posArray.length>nextIndexPlus && this.tag.length>nextIndexPlus) {
				if(this.posArray[nextIndex].equals(Words.ADP) && this.tag[nextIndexPlus].equals(Words.Chunks.B_NP)) { //TODO: pasar a inglés
					np = extractNextNounPhrasesStartingAt(ChunkedRight,localOffset);
					result.add(result.get(result.size()-1)+" "+this.sentArray[nextIndex]+" "+np.getNounPhrase());
				}else if(this.posArray[nextIndex].equals(Words.DT) && this.tag[nextIndex].equals(Words.Chunks.B_NP)) {
					np = extractNextNounPhrasesStartingAt(ChunkedRight,localOffset);
					result.add(result.get(result.size()-1)+" "+this.sentArray[nextIndex]+" "+np.getNounPhrase());
				}else if(this.posArray[nextIndex].equals(Words.CCONJ) && this.tag[nextIndexPlus].equals(Words.Chunks.B_NP)) {
					np = extractNextNounPhrasesStartingAt(ChunkedRight,localOffset);
					result.add(this.sentArray[nextIndex]+" "+np.getNounPhrase());
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
	
}
