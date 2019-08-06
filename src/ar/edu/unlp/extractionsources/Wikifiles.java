package ar.edu.unlp.extractionsources;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ar.edu.unlp.RelationExtractor;
import ar.edu.unlp.constants.Filenames;
import ar.edu.unlp.entities.Relation;

public class Wikifiles {
	
	protected int indexFile = 0;
	protected String filename = "";
	protected File[] listOfFiles = null;
	protected BufferedReader br=null; 
	public static final int MAX_CASES = 15;
	
	public Wikifiles(){
		File folder = new File(Filenames.WIKIPEDIA_FILES);
		this.listOfFiles = folder.listFiles();
		this.indexFile = 0;		
	}
	
	public static void extractInformationFromWikifiles() throws Exception{
		RelationExtractor extractor = new RelationExtractor();
		Wikifiles rwf = new Wikifiles();
		boolean quit = false;
		int count =0;		
		while(rwf.readNextFile() != null && !quit){
			String paragraphLine = rwf.readNextLine();			
			while(paragraphLine!=null && !quit){
				List<Relation> relations = extractor.extractInformationFromParagraph(paragraphLine);
				if(relations!=null && !relations.isEmpty()){
					count++;
					System.out.println();
					System.out.println(paragraphLine);
					for (Relation relation : relations) {
						System.out.println(relation.toString());
					}
				}
				if(count>=MAX_CASES){
					rwf.endRead();
					return;
				}
				paragraphLine = rwf.readNextLine();
			}//end while read line
		}//end while Read Files
	}
	
	public String readNextFile() throws FileNotFoundException {
		
		for (; this.indexFile < listOfFiles.length; this.indexFile++) {
		      if (listOfFiles[this.indexFile].isFile() &&  listOfFiles[this.indexFile].getName().contains("pages-articles")) {
		    	  filename=listOfFiles[this.indexFile].getName();
		          System.out.println("File " + filename);
		          this.br = new BufferedReader(new FileReader(Filenames.WIKIPEDIA_FILES+File.separator+this.filename));
		          return filename;
		      }
		 }
		return null;
	}
	
	public void endRead(){
		try {			
			this.br.close();
		} catch (IOException e) {
			System.out.println("fail to close BufferRead");
		}
	}
	
	public String readNextLine() throws IOException{
		
	    String line;
	    while ((line = br.readLine()) != null) {
	    	if(isValidLine(line)){
	    		return cleanLine(line);
	    	}
	    }
	    return null;
	}
	
	public boolean isValidLine(String line){
		if(line.startsWith("[")) return false;
		if(line.startsWith("*")) return false;
		if(line.startsWith("=")) return false;
		if(line.trim().isEmpty()) return false;
		
		return true;
	}
	
	public String cleanLine(String line){
		Pattern p = Pattern.compile("\\[\\[([^\\]]+)\\]\\]",Pattern.DOTALL | Pattern.CASE_INSENSITIVE);		    
		Pattern p2 = Pattern.compile("\\{\\{([^\\}]+)\\}\\}",Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(line);
		String cleanedtext = line;
		while (m.find()) {	
			
			String[] Ref = m.group(1).split("\\|");
			cleanedtext = cleanedtext.replace("[["+ m.group(1)+"]]", Ref[Ref.length-1]);
		}
		Matcher m2 = p2.matcher(cleanedtext);
		while (m2.find()) {			
			cleanedtext = cleanedtext.replace("{{"+ m2.group(1)+"}}", "");
		}
		return cleanedtext;
	}

}
