package ar.edu.unlp.mains;

import ar.edu.unlp.Train;
import ar.edu.unlp.constants.Filenames;

public class TrainMain {

	public static void main(String[] args) {
		
		Train train = null;
		try {
			train = new Train();
			train.doTraining();						
			train.doTraining(false,Filenames.JSON_SPANISH_EXAMPLES,new Integer(10));
			System.out.println("end training");
			
			/*Wikifiles.extractInformationFromWikifiles();*/
			
			
		} catch (Exception e) {			
			e.printStackTrace();
		}
		System.out.println("Done");
	}

}
