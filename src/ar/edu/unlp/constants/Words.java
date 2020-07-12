package ar.edu.unlp.constants;

import java.util.HashMap;
import java.util.Map;

public class Words {

	public static final String SPACE = " ";
	public class Chunks{
		
		public static final String B_NP = "B-NP";
		public static final String I_NP = "I-NP";
		public static final String B_VP = "B-VP";
		public static final String I_VP = "I-VP";
		public static final String B_ADVP = "B-ADVP";
		public static final String I_ADVP = "I-ADVP";
		public static final String BEGIN_LETTER = "B";
		public static final String CONTINUE_LETTER = "I";
	}
	
//	public static final String IN = "IN";
//	public static final String DT = "DT";
//	public static final String CC = "CC";
//	public static final String PRP$ = "PRP$";
	
	public static final String ADP = "ADP"; //ejemplos: con, en, durante
	public static final String DET = "DET";
	public static final String CCONJ = "CCONJ"; //ejemplos: "y" "o", "pero"
	public static final String PRON = "PRON"; //Possessive pronoun original
	public static final String PUNCT= "PUNCT";
	public static final String VERB= "VERB"; //Verbs
	public static final String PROPN = "PROPN";
	public static final String NOUN = "NOUN";
	public static final String ADJ = "ADJ";
	public static final String ADV = "ADV";
	public static final String DE = "DE";
	public static final String AUX = "AUX";
	

	//public static final String[] SAID_AND_SYNONYMS= {"said",  "told", "added", "announced", "asserted","believe","believed"};
	public static final String[] SAID_AND_SYNONYMS= {
			"dijo", "pensó", "contó", "añadió", "anunció", "aseveró","creyó", "sostuvo", "recalcó", "expresó","afirmó", "destacó", "aseguró",
			"dice", "piensa", "añade", "anuncia", "asevera","cree", "sostiene", "recalca", "expresa","afirma", "destaca", "asegura",
			"decía", "pensaba", "añadía", "anunciaba", "aseveraba","creía", "sostenía", "recalcaba", "expresaba","afirmaba", "destacaba", "aseguraba",
			"dijeron", "pensaron", "contaron", "añadieron", "anunciaron", "aseveraron","creyeron", "sostuvieron", "recalcaron", "expresaron","afirmaron",
			"destacaron", "aseguraron",
			"decían", "pensaban", "añadían", "anunciaban", "aseveraban","creían", "sostenían", "recalcaban", "expresaban","afirmaban", "destacaban", "aseguraban",
			"dicen", "piensan", "cuentan", "añaden", "anuncian", "aseveran","creyeren", "sostienen", "recalcan", "expresan","afirman"		
	}; //según"
	public static final String[] AFTER_SAID_AND_SYNONYMS= { ":","que"};
	public static final String[] SAID_END_POINTS= {", ",  ";", "\""}; //added the space to the como to avoid numbers
	
	
//	public static final String[] BAD_ENDINGS_WORDS_FOR_ARGUMENT= {"that",  "on", "at", "with", "a", "to", "and", "an", "by", "from", "in", "as"};
//	public static final String[] GOOD_START_WORDS_FOR_ARGUMENT= {"that", "on", "at", "with", "a", "an", "by", "in", "from", "to" };
//	public static final String[] PHRASAL_VERBS_COMMON_SECOND_WORDS = {"away", "by", "down", "for", "in", "into", "off", "on", "of", "out", "over", "up"};
//	public static final String[] ENDING_POSTAGS_TO_REMOVE= {".", ","};
//	public static final String[] NP_CONNECTORS= {"to", "of", "at"};
	public static final String[] BAD_ENDINGS_WORDS_FOR_ARGUMENT= {"como", "que", "en", "con", "a", "para", "y", "un", "por", "de", "desde", "dentro"};
	public static final String[] GOOD_START_WORDS_FOR_ARGUMENT = {"como", "que", "en", "con", "a", "para", "y", "un", "por", "de", "desde", "dentro"};	
	public static final String[] PHRASAL_VERBS_COMMON_SECOND_WORDS = {"se", "nos"};
	public static final String[] PHRASAL_VERBS_COMMON_FIRST_WORDS = {"se", "nos"};
	public static final String[] ENDING_POSTAGS_TO_REMOVE= {".", ","};
	public static final String[] NP_CONNECTORS= {"a", "de", "en", "los"};
	public static final String[] SUBJECTS_CONNECTORS= {"de", "en"};
	public static final String[] INTERROGATION_MARKS= {"?", "¿"};
	
	public static final String NER_OTHER = "O";
	public static final String WORD_WILDCARD_NER_FULL = "_";
	//public static final String PROPER_NOUN_POS_START_WITH= "NNP";
	public static final String PROPER_NOUN_POS_START_WITH= "PROPN";
	
	public static final String WILDCARD_QUOTED = "#QUOTED";
	public static final String WILDCARD_QUOTED_REVERB_SET = "# QUOTED";
	public static final String TACIT_SUBJECT_WILDCARD = "Él";
	public static final String TACIT_SUBJECT_WILDCARD_POS = "PRON";
	//public static final String WILDCARD_QUOTED_POS = "NN";
	public static final String WILDCARD_QUOTED_POS = "NOUN";
	//public static final String WILDCARD_ERROR_REPLACE = "something";
	public static final String WILDCARD_ERROR_REPLACE = "algo";
	public static final String WILDCARD_LEADING_ZEROES_FORMAT = "%03d";
	public static final int WILDCARD_LEADING_ZEROES_COUNT = 3;
	
	public static final String NEGATION = "no";
	
	public static final Map<String , String> WORDTRANSLATE = new HashMap<String , String>() {/**
		 * 
		 */
		private static final long serialVersionUID = -1123849607457813641L;

	{
		put("of","de");
		put("in","en");
		put("for","para|por");
		put("on","en");
		put("that","que");
		put("at","al|el");
		put("with","con");
		put("after","despues");
		put("before","antes");
		put("about","sobre");
		
	}};
	
	public static final Map<String , String> SIGNTRANSLATE = new HashMap<String , String>() {/**
		 * 
		 */
		private static final long serialVersionUID = -5968017843289154169L;

	{
		put("=LRB=","(");
		put("=RRB=",")");
	}};
	
	public class SpecialScoreWords{
		
		public static final String POR = "por";
		public static final String PARA = "para";
		public static final String EN = "en";
		public static final String HACIA = "hacia";
		public static final String DE = "de";
		public static final String A = "a";
	}
}
