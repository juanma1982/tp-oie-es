package ar.edu.unlp.utils;

public class StringLengthComparator implements java.util.Comparator<String> {

    

    public StringLengthComparator() {
        super();

    }

    @Override
    public int compare(String s1, String s2) {
    	 return s1.length() - s2.length();
    }
    
}