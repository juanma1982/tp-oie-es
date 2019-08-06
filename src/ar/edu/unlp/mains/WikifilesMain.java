package ar.edu.unlp.mains;

import ar.edu.unlp.extractionsources.Wikifiles;

public class WikifilesMain {

	public static void main(String[] args) {
		
		try{
			Wikifiles.extractInformationFromWikifiles();
			
			
		} catch (Exception e) {			
			e.printStackTrace();
		}
		System.out.println("Done");
	}

}
