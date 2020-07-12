package ar.edu.unlp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;

import ar.edu.unlp.constants.Constants;
import ar.edu.unlp.constants.Words;
import ar.edu.unlp.entities.Pattern;
import ar.edu.unlp.entities.PatternArgumentList;
import ar.edu.unlp.entities.PatternContainer;
import ar.edu.unlp.entities.PatternList;
import ar.edu.unlp.entities.Relation;
import ar.edu.unlp.entities.SentenceData;
import ar.edu.unlp.utils.ParagraphStanford;
import ar.edu.unlp.utils.RelationFeaturesScore;
import ar.edu.unlp.utils.SentenceManipulation;
import ar.edu.unlp.utils.StringLengthComparator;
import ar.edu.unlp.utils.WordsUtils;

public class RelationExtractor {
	
	protected StanfordCoreParser parser = null;	
	protected ParagraphStanford paragraph=null;
	public static final boolean USE_ARGUMENT_PATTERNS = true;
	public static final boolean GET_RELATION_POSTAGS = false;
	public static final boolean CALCULATE_SCORE = true;
	public static final boolean CHECK_NON_FACTUAL = true;
	protected PatternContainer patternContainer; 
	protected PatternList treePatternList;
	protected PatternArgumentList argumentPatternList;
	protected RelationFeaturesScore scoreCalculator = null;
	protected SentenceManipulation sentenceManipulation = null;
	protected ReVerbExtractorUtility reverbExtractor = null;
	protected boolean useReverb = false;
	protected boolean lookForTacitSubject = true;
	protected int scoreLimit = Constants.SCORE_LIMIT;
	protected boolean scoreFilter = true;
		
	protected ArgumentExtractorReverbStyle argumentExtractor = null;
	
	public RelationExtractor() throws Exception{
		parser = new StanfordCoreParser();
		paragraph = new ParagraphStanford();
		scoreCalculator =  new RelationFeaturesScore();
		sentenceManipulation = new SentenceManipulation();		
		loadPatterns();
				
	}
	
	public void loadPatterns() throws Exception{
		this.patternContainer = PatternLoader.loadPatternsFromJson();	
		this.treePatternList = this.patternContainer.getTreePatterns();
		this.argumentPatternList = this.patternContainer.getPatternsForArguments();
		//argumentExtractor = new ArgumentExtractor(this.argumentPatternList);		
		argumentExtractor = new ArgumentExtractorReverbStyle();
	}
	
	public List<Relation> extractInformationFromParagraph(String paragraphLine) throws Exception{
		List<Relation> relations = new ArrayList<Relation>();		
		if(paragraphLine == null) return relations;
		
		Map<String, String> mapOfReplacement = new HashMap<String,String>();
		String[] sentences = paragraph.splitIntoSentences(paragraphLine,mapOfReplacement);
		
		for (int i=0;i<sentences.length;i++) {
			String line = sentences[i];
			 List<Relation> relationsInLine = extractInformationFromLine(line);
			 if(relationsInLine.isEmpty() && lookForTacitSubject) {
				 relationsInLine = extractInformationFromLineUsingTacitSubject(line);
			 }
			relations.addAll(relationsInLine);
		}
		this.replacedQuotedInRelations(relations,mapOfReplacement);	
		if(CHECK_NON_FACTUAL) {
			checkNonFactualExtractions(relations);
		}		
		return relations;
	}
	
	private void replacedQuotedInRelations(List<Relation> relations,Map<String, String> mapOfReplacement) {
		
		for (Iterator<Relation> iter = relations.listIterator(); iter.hasNext(); ) {
			Relation relation = iter.next();
			if(relation.getEntity1().contains(Words.WILDCARD_QUOTED)){
		    	 iter.remove();
		    	 continue;
		    }
			if(relation.getRelation().contains(Words.WILDCARD_QUOTED)){
		    	 iter.remove();
		    	 continue;
		    }
			int index = relation.getEntity2().indexOf(Words.WILDCARD_QUOTED);
			if(index > -1){				
				String keyWord = relation.getEntity2().substring(index,index+Words.WILDCARD_QUOTED.length()+Words.WILDCARD_LEADING_ZEROES_COUNT);
				/*String[] newKeyArray =keyWord.split(Words.SPACE);
				String newKeyWord =newKeyArray[0]+newKeyArray[1];
				relation.setEntity2(relation.getEntity2().replace(keyWord, "\""+mapOfReplacement.get(newKeyWord)+"\""));*/
				relation.setEntity2(relation.getEntity2().replace(keyWord, "\""+mapOfReplacement.get(keyWord)+"\""));
		    }
		}
	}

	public List<Relation> extractInformationFromLineUsingTacitSubject(String line) throws Exception{
		if(line==null) return Collections.emptyList();
		List<Relation> relations = new ArrayList<Relation>();
		StringBuilder sb = new StringBuilder(Words.TACIT_SUBJECT_WILDCARD);
		sb.append(Words.SPACE);
		sb.append(line.substring(0, 1).toLowerCase());
		sb.append(line.substring(1));
		
		List<SentenceData> listOfParsedData = parser.doParser(sb.toString());
		for (SentenceData sentenceData : listOfParsedData) {
			Set<Relation> relationsAux = extractInformationFromXMLTree(sentenceData);
			if(relationsAux.size() > 0) {
				relations.addAll(relationsAux) ;
			}
		}
		removeDuplicatedExtractions(relations);
		for (Relation relation : relations) {
			relation.setEntity1(relation.getEntity1().replace(Words.TACIT_SUBJECT_WILDCARD, ""));
			relation.setEntity2(relation.getEntity2().replace(Words.TACIT_SUBJECT_WILDCARD, ""));
		}
		return relations;
	}
	
	public List<Relation> extractInformationFromLine(String line) throws Exception{
		if(line==null) return Collections.emptyList();
		List<Relation> relations = new ArrayList<Relation>();
		List<SentenceData> listOfParsedData = parser.doParser(line);
		for (SentenceData sentenceData : listOfParsedData) {
			Set<Relation> relationsAux = extractInformationFromXMLTree(sentenceData);
			if(relationsAux.size() > 0) {
				relations.addAll(relationsAux) ;
			}else if(this.useReverb) {
				List<Relation> relationsAuxReverb = reverbExtractor.extractRelationsFromLine(line);
				if(CALCULATE_SCORE) {
					for (Relation relation : relationsAuxReverb) {
						addExtractedRelationToSet(relation,relations,sentenceData);
					}
				}else {
					relations.addAll(relationsAuxReverb) ;
				}
			}
		}
		
		
		removeDuplicatedExtractions(relations);	
		return relations;
	}
	
	/**
	 * This method removes the extractions that are identical, but also those that are similar, that is, extractions that are different but if they are read continuously: entity 1, relation, entity 2 are identical.
		for example:
		(AE; won; the Nobel Prize) and (AE; won the; Nobel Prize)
		in this second case, she will try to keep the one with the best score or a longer relationship.
	 */
	private void removeDuplicatedExtractions(List<Relation> relations) {
		
		Set<Integer> positionsToDelete = new HashSet<Integer>();
		for (int i = 0; i < relations.size(); i++) {
			for (int j = 0; j < relations.size(); j++) {
				if(i==j) continue;				
				Relation first = relations.get(i);
				Relation second = relations.get(j);
				if(positionsToDelete.contains(i)) continue;
				if(positionsToDelete.contains(j)) continue;
				if(first.contains(second)) {
					positionsToDelete.add(j);
				}else {
					//delete similar extractions					
					if(first.inRow().equals(second.inRow())) {
						if(first.getScore() > second.getScore()) positionsToDelete.add(j);
						else if(first.getScore() < second.getScore()) positionsToDelete.add(i);
						else {
							if(first.getRelation().length()<second.getRelation().length()) positionsToDelete.add(i);
							else positionsToDelete.add(j);
						}
					}else {
						if((first.getEntity1().contains(second.getEntity1()) || second.getEntity1().contains(first.getEntity1())) &&
						   (first.getEntity2().contains(second.getEntity2()) || second.getEntity2().contains(first.getEntity2())) &&
						   (first.getRelation().contains(second.getRelation()) || second.getRelation().contains(first.getRelation()))) {
							if(first.getRelation().length()<second.getRelation().length()) positionsToDelete.add(i);
							else if(first.getScore() < second.getScore()) positionsToDelete.add(i);
							else positionsToDelete.add(j);
						}
					}
				}
			}
		}
	
		List<Integer> list = new ArrayList<Integer>(positionsToDelete);
		Collections.sort(list, Collections.reverseOrder());
		
		for (Integer index : list) {	
			relations.remove(index.intValue());
		}
	}

	private void checkNonFactualExtractions(List<Relation> relations) {
		int id=-1;
		String argument = "";
		for (Relation relation : relations) {
			
			String relationAsString = relation.toString();			
			for (String imark : Words.INTERROGATION_MARKS) {
				if(relationAsString.contains(imark)) {
					relation.setInterogation(true);
					break;
				}
			}
			
			
			if(this.argumentExtractor.relationIsSaid(relation.getRelation())) {
				id= relation.getId();
				argument = relation.getEntity2();
				break;
			}
		}
		if(id>-1) {
			for (Relation relation : relations) {
				if(relation.getId() == id) continue;
				if(argument.contains(relation.getEntity1()) && argument.contains(relation.getRelation())
						&& argument.contains(relation.getEntity2())) {
					relation.setDependsOf(id);
					break;
				}
			}
		}
		
		
	}

	
	private void extractInformation(Pattern pattern, Document doc, Map<String, String> listOfExtractions, String words, String patterStr){
		if(words == null){
			words = "";
		}
		if(patterStr == null){
			patterStr = "";
		}
		Pattern currentPattern = pattern;
		Element element = doc.select(currentPattern.getPatternStr()).first();
		if(element != null){
			if(!words.isEmpty()){
				words = words+ Words.SPACE;
			}
			if(!patterStr.isEmpty()){
				patterStr = patterStr+ Words.SPACE;
			}
			words = words+ element.attr("word").replaceAll("&apos;", "'");
			patterStr = patterStr+ currentPattern.getPatternStr();			
			if(pattern.isLeaf()){			
				listOfExtractions.put(patterStr, words);
			}
			
			List<Pattern> nextPatterns =  pattern.getListOfNext();
			if(nextPatterns != null){
				for (Pattern nextPattern : nextPatterns) {
					this.extractInformation(nextPattern, doc,listOfExtractions, words, patterStr);
				}
			}
		}
	}
	
	/**
	 * Make some validations in the extracted subject (entity 1), in order to improve it:
	 * 
	 * 1. If the subject has only one word of, and the word is part of an entity (NER) => return the full entity
	 * 2. Add missing words in the middle, for example: "a spokeman" => "a defense spokeman"
	 * 3. Add the article at left, for example the word "The" in english or "El" in spanish
	 * 4. Connect with the following noun phrase if the word at rigth is "de" or "en" (spanish)
	 * 5. If the word at rigth or at left is an entity (NER), then it will be added to the subject 
	 * @param subjectCanidate
	 * @param sentenceData
	 * @return
	 */
	public String validateSubject(String subjectCanidate,SentenceData sentenceData, Relation relation){
		if(subjectCanidate == null) return null;
		if(!subjectCanidate.contains(Words.SPACE)){ //It means: contain only one word 
			String newSubject = sentenceData.getWordNER().get(subjectCanidate+Words.WORD_WILDCARD_NER_FULL);
			if(newSubject!=null) return newSubject;
		}
		if(!sentenceData.getCleanSentence().contains(subjectCanidate)){
			subjectCanidate = completeSubject(subjectCanidate,sentenceData.getCleanSentence());
		}
		if(subjectCanidate == null) return null;
		//Add the article at left, for example the word "The" in english or "El" in spanish
		String WordAtLeft = this.sentenceManipulation.getWordAtLeftOf(sentenceData, subjectCanidate);
		String POSTtAtLeft =null;
		if(WordAtLeft!=null && !WordAtLeft.isEmpty()) {
			POSTtAtLeft = sentenceData.getWordPOSTAG().get(WordAtLeft);
		}
		if(POSTtAtLeft!=null && POSTtAtLeft.equals(Words.DET)) {
			subjectCanidate =WordAtLeft+Words.SPACE+subjectCanidate;
		}
		///////////verify if it belongs to chunked noun phrase////////////

		if(!subjectCanidate.isEmpty()) {
			String nounPhraseAtRigth = this.sentenceManipulation.getTheRestOfTheNounPhrase(sentenceData, subjectCanidate);
			if(nounPhraseAtRigth != null && !nounPhraseAtRigth.isEmpty()) {
				subjectCanidate = subjectCanidate+Words.SPACE+nounPhraseAtRigth;
			}
		}else {
			return null;
		}		
		
		String wordAtRight = this.sentenceManipulation.getWordAtRightOf(sentenceData, subjectCanidate);
		String relationfirstWord = this.sentenceManipulation.getFirstWord(relation.getRelation());
		String argumentfirstWord = this.sentenceManipulation.getFirstWord(relation.getEntity2());
		if(wordAtRight!=null && !wordAtRight.equals(relationfirstWord) && !wordAtRight.equals(argumentfirstWord)
				&& WordsUtils.contains(Words.SUBJECTS_CONNECTORS,wordAtRight)) {
			String wordAtRightAtRight = this.sentenceManipulation.getWordAtRightOf(sentenceData, subjectCanidate+Words.SPACE+wordAtRight);
			subjectCanidate = subjectCanidate+Words.SPACE+wordAtRight+Words.SPACE+wordAtRightAtRight;
			String nounPhraseAtRigth = this.sentenceManipulation.getTheRestOfTheNounPhrase(sentenceData, subjectCanidate);
			if(!nounPhraseAtRigth.isEmpty()) {
				subjectCanidate = subjectCanidate+Words.SPACE+nounPhraseAtRigth;
			}
		}else if(!Words.NER_OTHER.equals(sentenceData.getWordNER().get(wordAtRight))) {
			subjectCanidate = subjectCanidate+Words.SPACE+wordAtRight;
		}else if(!Words.NER_OTHER.equals(sentenceData.getWordNER().get(WordAtLeft))) {
			subjectCanidate = WordAtLeft+Words.SPACE+subjectCanidate;
		}
		
		return subjectCanidate;
	}
	
	/**
	 * When a subject is extracted, but it is a string which not appears in the text, ie: "A spokeman", and the full text is:<br/>
	 * "A defense spokesman added that British officials ..."<br/>
	 * This method creates a pattern in the form: "A(.+)spokeman" to get the full the subject: "A defense spokesman"
	 *   
	 * @param subjectCanidate
	 * @param fullText
	 * @return the improved subjectCanidate
	 */
	public String completeSubject(String subjectCanidate,String fullText){
		
		
		//String[] fulltextWords = fullText.split(Words.SPACE);
		String[] subjectWords = subjectCanidate.split(Words.SPACE);
		StringBuilder sb = new StringBuilder();
		int count = 0;
		for (int j = 0; j < subjectWords.length; j++) {
			sb.append(subjectWords[j]);
			if(j < (subjectWords.length-1)){
				count++;
				sb.append("(.+)");	
			}
		}
		java.util.regex.Pattern r = java.util.regex.Pattern.compile(sb.toString());
		java.util.regex.Matcher m = r.matcher(fullText);		
		if (m.find()) {
			for (int i = 1; i <= count; i++) {
				String argMatched = m.group(i);
				if (argMatched == null || argMatched.isEmpty()) return null;
				if(argMatched.trim().split(Words.SPACE).length > Constants.MIN_WORD_DISTANCE+1) return null;
			}
			return m.group(0);
		}
		return null;
		
	}
	
	/***
	 * This method improves the extracted relation in several ways:
	 * 
	 * 1. if the next word after the extacted relation is "se" or "nos", then tis word will be concatenated at the end of the relation
	 * 2. Do the same if the word "se" or "nos" is at the left of the relation, then this word is added at the begining of the relation itself
	 * 3. if the last word in the relation is a verb and the next word in the sentence after that is also a verb => join the verb at the end of the relation
	 * 4. if the fist word in the relation is a verb and the next word at left (in the sentence is also a verb) => join the verb at the begining of the relation
	 * 5. If the relation conatins an NounPhrase at the begining, and then a verbalPhrase and there is no other word at left in sentence, the relation will be reduced only to the verbalPhrase 
	 * 
	 * @param sentenceData
	 * @param currentRelationExtraction
	 */
	public void improveExtractedRelations(SentenceData sentenceData, Map<String, String> currentRelationExtraction) {
		
		for (String key : currentRelationExtraction.keySet()) {
			String relation = currentRelationExtraction.get(key);
			String[] relationWords = relation.split(Words.SPACE);
			String lastPOS = sentenceData.getWordPOSTAG().get(relationWords[relationWords.length-1]);
			String wAtLeft = sentenceManipulation.getWordAtLeftOf(sentenceData, relation);
			String wAtRigth = sentenceManipulation.getWordAtRightOf(sentenceData, relation+Words.SPACE);
			
			//if last POS tag in the relation is a Verb
			if(lastPOS!=null && !lastPOS.isEmpty() && lastPOS.equals(Words.VERB)) {
				if(wAtRigth!=null && !wAtRigth.isEmpty()) {
					if(Words.VERB.equals(sentenceData.getWordPOSTAG().get(wAtRigth)) || 
							Words.AUX.equals(sentenceData.getWordPOSTAG().get(wAtRigth))) {
						currentRelationExtraction.put(key, relation+Words.SPACE+wAtRigth);
					}else {
						for (int i = 0; i < Words.PHRASAL_VERBS_COMMON_SECOND_WORDS.length; i++) {
							if(wAtRigth.equals(Words.PHRASAL_VERBS_COMMON_SECOND_WORDS[i])) {
								currentRelationExtraction.put(key, relation+Words.SPACE+wAtRigth);
								break;
							}
						}
					}
				}
				/***********/

				if(wAtLeft!=null && !wAtLeft.isEmpty()) {					
					for (int i = 0; i < Words.PHRASAL_VERBS_COMMON_FIRST_WORDS.length; i++) {
						if(wAtLeft.equals(Words.PHRASAL_VERBS_COMMON_FIRST_WORDS[i])) {
							currentRelationExtraction.put(key, wAtLeft+Words.SPACE+relation);
							break;
						}
					}
				}
								
			}else if(lastPOS!=null && !lastPOS.isEmpty() && lastPOS.equals(Words.AUX)) { //if last POS tag in the relation is an AUX (example: "has", "have")
				if(wAtRigth!=null && !wAtRigth.isEmpty() && Words.VERB.equals(sentenceData.getWordPOSTAG().get(wAtRigth))) {
					currentRelationExtraction.put(key, relation+Words.SPACE+wAtRigth);
				}
			}
			/****************************/
			String firstPOS = sentenceData.getWordPOSTAG().get(relationWords[0]);
			if(Words.VERB.equals(firstPOS) && (Words.VERB.equals(sentenceData.getWordPOSTAG().get(wAtLeft)) ||
					Words.AUX.equals(sentenceData.getWordPOSTAG().get(wAtLeft)) ||
					Words.NEGATION.equals(wAtLeft.toLowerCase())) ) { //if first POS in the relation is AUX or VERB
				currentRelationExtraction.put(key, wAtLeft+Words.SPACE+relation);
			}
			///Check if the relation contains part of the subject
			if(sentenceData.getCleanSentence().startsWith(relation)) {
				int wordsinRelation = relationWords.length;
				StringBuilder newRelationCandidate = new StringBuilder("");				
				for(int i=0;i< wordsinRelation;i++) {
					if(Words.Chunks.B_VP.equals(sentenceData.getChunkerTags()[i]) || Words.Chunks.I_VP.equals(sentenceData.getChunkerTags()[i])) {
						newRelationCandidate.append(relationWords[i]);
						newRelationCandidate.append(Words.SPACE);
					}
				}
				if(newRelationCandidate.length() > 0) {
					currentRelationExtraction.put(key,newRelationCandidate.toString().trim());
				}
			}
		}//end for
	}
	
	private Set<Relation> extractInformationFromXMLTree(SentenceData sentenceData) throws Exception {
		
		String treeDependenciesLine = sentenceData.getTreeDependenciesLine();
		Document doc = Jsoup.parse(treeDependenciesLine, "", Parser.xmlParser());
		Set<Relation> set = new HashSet<Relation>();
		//RelationExtractor extractor = new RelationExtractor();
		
		/*************Relation extraction******************/
		
		Map<String, String> currentRelationExtraction = new HashMap<String, String>();
		for (Pattern pattern : this.treePatternList.getListOfRelations()) { //Get each pattern from the list of patterns for extract relations
		
			extractInformation(pattern,doc,currentRelationExtraction,"",""); //Extract the relations, using each pattern
		} //END We get each pattern from the list of patterns for extract relations
		
		/*******Delete duplicated relations*************/
		deleteDuplicatedRelations(currentRelationExtraction, sentenceData.getCleanSentence());
		improveExtractedRelations(sentenceData, currentRelationExtraction);
		
		for(String keyRelation: currentRelationExtraction.keySet()){ //for each relation candidate, obtained
				List<Pattern> subjectPatterns = this.treePatternList.getSubjectsByRelationPatternList(keyRelation); //We get the patterns to extract subjects (entity01), according with the extracted relation
				Map<String, String> subjectCurrentExtractionAll = new HashMap<String, String>();				
				for (Pattern subjectPattern : subjectPatterns) { //for each pattern for extract subjects
					/*************Entity1 / Subject extraction******************/
					Map<String, String>  subjectCurrentExtraction = new HashMap<String, String>();					
					extractInformation(subjectPattern,doc,subjectCurrentExtraction,"",""); //extract subjects for given relations
					subjectCurrentExtractionAll.putAll(subjectCurrentExtraction);					
				} //END for each pattern for extract subjects
				
					
					Relation relation = new Relation();
					relation.setRelation(currentRelationExtraction.get(keyRelation));
					if(Constants.BE_SPECIFIC_WITH_POSTAG_ADP){
						sentenceData.setUseExtended(true);
					}
					List<String> arguments = this.argumentExtractor.argumentExtractorAll(sentenceData, relation.getRelation(), null); //extract all arguments candidates, (entity02)
	
					for (String argument : arguments) {
						relation.setEntity2(argument);
						relation.setRelation(this.argumentExtractor.getCurrentRelationStr());
						for(String keySubject: subjectCurrentExtractionAll.keySet()){
							String subject = this.validateSubject(subjectCurrentExtractionAll.get(keySubject), sentenceData, relation);
							if(subject == null) continue;
							relation.setEntity1(subject);
														
							if(relation.isComplete() && SemanticRelationValidator.isValid(relation)){
								if(GET_RELATION_POSTAGS) {
									String words[] = relation.inRow().split(Words.SPACE);
									StringBuilder sb = new StringBuilder();
									for (int i = 0; i < words.length; i++) {										
										sb.append(sentenceData.getWordPOSTAG_extended().get(words[i]));
										sb.append(Words.SPACE);
									}
									relation.setFullExtractionAsPosTags(sb.toString().trim());
								}								
								addExtractedRelationToSet(relation,set,sentenceData);
								relation = new Relation(relation);
							}
						}//Termino el ciclo de subject.
						if(set.isEmpty() && relation.getRelation()!=null && relation.getEntity2()!=null ) {
							String subject = this.argumentExtractor.extractNounPhraseAtLeft(sentenceData,  relation.getRelation(),  relation.getEntity2());
							subject = this.validateSubject(subject, sentenceData, relation);
							if(subject != null) {
								relation.setEntity1(subject);
								addExtractedRelationToSet(relation,set,sentenceData);
							}
						}
					}
					

			} // END for each relation candidate, obtained
	
		return set;
	}

	
	protected void addExtractedRelationToSet(Relation relation,Collection<Relation> set,SentenceData sentenceData) {
		if(CALCULATE_SCORE) {
			relation.setScore(scoreCalculator.calculate(sentenceData, relation));
			if(this.scoreFilter) {
				if(relation.getScore()>=this.scoreLimit) {
					set.add(relation) ;
				}
			}else {
				set.add(relation) ;
			}
			
		}else {
			set.add(relation);
		}
	}

	private void deleteDuplicatedRelations(Map<String, String> currentRelationExtraction, String sentence) {
		
		Set<String> relationsSet = new HashSet<String>();
		Set<String> relationsToDelete = new HashSet<String>(); 
		
		for (String key : currentRelationExtraction.keySet()) {
			String relation = currentRelationExtraction.get(key);
			if(sentence.contains(relation)) {
				relationsSet.add(relation);
			}
		}
		
		List<String> relations = new ArrayList<String>();
		relations.addAll(relationsSet);		
		java.util.Collections.sort(relations, new StringLengthComparator());
		
		for (int i = 0; i < relations.size(); i++) {
			String relation = relations.get(i);
			for (int j = i+1; j < relations.size(); j++) {
				String relation2 = relations.get(j);
				if(relation2.contains(relation)) {
					relationsToDelete.add(relation);
				}
			}
		}
		Iterator<String> it = currentRelationExtraction.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			if (relationsToDelete.contains(currentRelationExtraction.get(key))) {
				it.remove();
			}
		}
	}

	public boolean isUseReverb() {
		return useReverb;
	}

	public void setScoreLimit(int score) {
		this.scoreLimit = score;
	}
	public void setScoreFilter(boolean scoreFilter) {
		this.scoreFilter = scoreFilter;
	}
}
