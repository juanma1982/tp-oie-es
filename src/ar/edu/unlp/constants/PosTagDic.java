package ar.edu.unlp.constants;

import java.util.HashMap;
import java.util.Map;

public class PosTagDic {

	public static final Map<String , String> UPT2PENN = new HashMap<String , String>() {/**
		 * 
		 */
		private static final long serialVersionUID = 836008216758066348L;

	{
		put("CCONJ", "CC");
		put("NUM", "CD");
		put("DET", "DT");
		put("X", "FW");
		put("ADP", "IN");
		put("ADJ", "JJ");
		put("NOUN", "NN");
		put("PROPN", "NNP");
		put("PART", "POS");
		put("PRON", "PRP");
		put("ADV", "RB");
		put("SYM", "SYM");
		put("INTJ", "UH");
		put("VERB", "VB");
		put("AUX", "VB");
		put("PUNCT", ".");
	}};
	
	public static final Map<String , String> PENN2UPT = new HashMap<String , String>() {/**
		 * 
		 */
		private static final long serialVersionUID = -1108324410854126515L;

	{
		put("#", "SYM");
		put("$", "SYM");
		put("''", "PUNCT");
		put(",", "PUNCT");
		put("-LRB-", "PUNCT");
		put("-RRB-", "PUNCT");
		put(".", "PUNCT");
		put(":", "PUNCT");
		put("AFX", "ADJ");
		put("CC", "CCONJ");
		put("CD", "NUM");
		put("DT", "DET");
		put("EX", "PRON");
		put("FW", "X");
		put("HYPH", "PUNCT");
		put("IN", "ADP");
		put("JJ", "ADJ");
		put("JJR", "ADJ");
		put("JJS", "ADJ");
		put("LS", "X");
		put("MD", "VERB");
		put("NIL", "X");
		put("NN", "NOUN");
		put("NNP", "PROPN");
		put("NNPS", "PROPN");
		put("NNS", "NOUN");
		put("PDT", "DET");
		put("POS", "PART");
		put("PRP", "PRON");
		put("PRP$", "DET");
		put("RB", "ADV");
		put("RBR", "ADV");
		put("RBS", "ADV");
		put("RP", "ADP");
		put("SYM", "SYM");
		put("TO", "PART");
		put("UH", "INTJ");
		put("VB", "VERB");
		put("VBD", "VERB");
		put("VBG", "VERB");
		put("VBN", "VERB");
		put("VBP", "VERB");
		put("VBZ", "VERB");
		put("WDT", "DET");
		put("WP", "PRON");
		put("WP$", "DET");
		put("WRB", "ADV");
		put(".", "PUNCT");
	}};
}
