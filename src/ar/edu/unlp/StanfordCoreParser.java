package ar.edu.unlp;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import ar.edu.unlp.constants.Constants;
import ar.edu.unlp.constants.Filenames;
import ar.edu.unlp.constants.Words;
import ar.edu.unlp.entities.SentenceData;
import ar.edu.unlp.utils.Chunker;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.BasicDependenciesAnnotation;
/*import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;*/
import edu.stanford.nlp.util.CoreMap;

public class StanfordCoreParser {

	protected StanfordCoreNLP pipeline = null;
	protected Annotation annotation;
	protected SemanticGraphFormatter sgm = null;
	protected Chunker chunker = null;
		
	public StanfordCoreParser() throws IOException {
		Properties properties = new Properties();
		InputStream input = null;
		input = new FileInputStream(Filenames.MODEL_PROPERTIES);
		properties.load(input);
		this.pipeline = new StanfordCoreNLP(properties);
		this.sgm = new SemanticGraphFormatter();
		this.chunker = new Chunker();
	}
	
	public List<SentenceData> doParser(String text) throws Exception{
		
		annotation = new Annotation(text);
		pipeline.annotate(annotation);
	    List<CoreMap> sentences = annotation.get(SentencesAnnotation.class);

	    List<SentenceData> sentenceDataList = new ArrayList<SentenceData>();
	    for(CoreMap cm_sentence: sentences) {
	      Map<String,String> wordNer = new HashMap<String,String>(); 
		  Map<String,String> wordPos = new HashMap<String,String>();
		  Set<String> setOfNerFullString = new HashSet<String>(); 
		  Map<String,String> wordPos_extended = null;
		  StringBuilder POSSentence_extended = null;
		  if(Constants.BE_SPECIFIC_WITH_POSTAG_ADP){
			  wordPos_extended = new HashMap<String,String>();
			  POSSentence_extended = new StringBuilder("");
		  }
		  StringBuilder POSSentence = new StringBuilder("");
		  String prevNer = Words.NER_OTHER;
		  StringBuilder sbNERFull = new StringBuilder("");
		  StringBuilder cleanSentence = new StringBuilder("");
		  boolean wildcardsplitted = false;
	      for (CoreLabel token: cm_sentence.get(TokensAnnotation.class)) {
	        String word = token.get(TextAnnotation.class);
	        if(word.equals(Words.WILDCARD_QUOTED)){
	        	wildcardsplitted = true;
	        	continue;
	        }else if(wildcardsplitted) {
	        	wildcardsplitted = false;
	        	word = Words.WILDCARD_QUOTED+word;
	        }
	        
	        String tokenPOS = null;
	        if(Words.SIGNTRANSLATE.get(word) != null) {
	        	word = Words.SIGNTRANSLATE.get(word);
	        	tokenPOS = Words.PUNCT;
	        }
	        
	        cleanSentence.append(word);
	        cleanSentence.append(Words.SPACE);
	        
	        if(tokenPOS == null) {
	        	if(word.startsWith(Words.WILDCARD_QUOTED)){
		        	tokenPOS = Words.WILDCARD_QUOTED_POS;	
		        }else{
		        	tokenPOS = token.get(PartOfSpeechAnnotation.class);
		        }	
        	}
	        String tokenNer = token.get(NamedEntityTagAnnotation.class);
	        if(!tokenNer.equals(Words.NER_OTHER)){
	        	if(!tokenNer.equals(prevNer)){
	        		sbNERFull.setLength(0);
	        	}
	        	sbNERFull.append(word);
	        	sbNERFull.append(Words.SPACE);
	        }else if(sbNERFull.length()!=0){
	        	setOfNerFullString.add(sbNERFull.toString().trim());
	        	sbNERFull.setLength(0);
	        }
	        prevNer = tokenNer;
	        	
	        
	        wordNer.put(word, tokenNer);
	        wordPos.put(word, tokenPOS);
	        if(Constants.BE_SPECIFIC_WITH_POSTAG_ADP){
	        	if(tokenPOS.equals(Words.ADP)){
	        		wordPos_extended.put(word, word.toUpperCase());
	        		POSSentence_extended.append(word.toUpperCase());
	        	}else{
	        		wordPos_extended.put(word, tokenPOS);
	        		POSSentence_extended.append(tokenPOS);
	        	}
	        	POSSentence_extended.append(Words.SPACE);
			}
	        POSSentence.append(tokenPOS);
	        POSSentence.append(Words.SPACE);
	      }
	      // this is the parse tree of the current sentence
	     // Tree tree = cm_sentence.get(TreeAnnotation.class);

	      // this is the Stanford dependency graph of the current sentence
	      SemanticGraph dependencies = cm_sentence.get(BasicDependenciesAnnotation.class);
	      this.sgm.setWordNER(wordNer);	      
	      SentenceData data = new SentenceData();	      
	      data.setTreeDependenciesLine(this.sgm.toXMLString(dependencies));
	      
	      data.setWordPOSTAG(wordPos);
	      data.setDependenciesGraph(dependencies);
	      data.setSentenceAsPOSTags(POSSentence.toString().trim().split(Words.SPACE));
	      data.setSentence(text);
	      data.setCleanSentence(cleanSentence.toString().trim().split(Words.SPACE));
	      
	      //Add the full NER String,
	      for (String nerStringWord : setOfNerFullString) {
	    	  String[] nerWords = nerStringWord.split(Words.SPACE);
	    	  for (int i = 0; i < nerWords.length; i++) {
	    		  wordNer.put(nerWords[i]+Words.WORD_WILDCARD_NER_FULL,nerStringWord);
			}
	      }
	      data.setWordNER(wordNer);
	      
	      if(Constants.BE_SPECIFIC_WITH_POSTAG_ADP){
	    	  data.setSentenceAsPOSTags_extended(POSSentence_extended.toString().trim().split(Words.SPACE));
	    	  data.setWordPOSTAG_extended(wordPos_extended);
	      }
	      
	      //Add the chunker
	      //String tags[]= chunker.getChunker().chunk(data.getCleanSentenceArray(), data.getSentenceAsPOSTagsExtendedArray());
	      String tags[]= chunker.universalPOSChunk(data.getCleanSentenceArray(), data.getSentenceAsPOSTagsArray());
	      data.setChunkerTags(tags);
	      sentenceDataList.add(data);
	    }
	    
	    return sentenceDataList;
	}
	
	public void print() {
		PrintWriter out;
		out = new PrintWriter(System.out);
		out.println(annotation.toShorterString());
	}
	
	public void prettyPrint() {
		PrintWriter out;
		out = new PrintWriter(System.out);
		pipeline.prettyPrint(annotation, out);
	}
	
	public Annotation getAnnotation() {
		return this.annotation;
	}
	
	public List<CoreMap> getSentencesAsMapList(){
		return annotation.get(CoreAnnotations.SentencesAnnotation.class);
	}
	
	public CoreMap getFirstSentenceAsMap(){
		return annotation.get(CoreAnnotations.SentencesAnnotation.class).get(0);
	}

}