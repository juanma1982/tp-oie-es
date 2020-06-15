package ar.edu.unlp.utils;

public class WordsUtils {
	
	
	public static final boolean contains(String[] array,String text) {
		if(array == null || array.length==0) return false;
		if(text == null || text.isEmpty()) return false;
		for (String string : array) {
			if(string.equals(text)) return true;
		}
		return false;
	}

}
