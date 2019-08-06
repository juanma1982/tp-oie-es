package ar.edu.unlp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import ar.edu.unlp.constants.Filenames;

public class ReadTestFile {

	
	protected String filename = "";	
	protected BufferedReader br=null;
	protected int fileIndex = 0;
		
	public ReadTestFile(){}
	public ReadTestFile(int fileIndex){
		this.fileIndex=fileIndex;
	}
	
	public void startReading() throws FileNotFoundException {
		
		switch (this.fileIndex) {
		case 0:
			this.br = new BufferedReader(new FileReader(Filenames.TESTFILE));
			break;
		default:
			this.br = new BufferedReader(new FileReader(Filenames.TESTFILE2));
			break;
		}
		
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
	    	return line;
	    }
	    return null;
	}
	
}
