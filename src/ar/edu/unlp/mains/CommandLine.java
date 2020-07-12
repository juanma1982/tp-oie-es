package ar.edu.unlp.mains;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import ar.edu.unlp.RelationExtractor;
import ar.edu.unlp.constants.Constants;
import ar.edu.unlp.entities.Relation;

public class CommandLine {

	public static boolean flagUseReverb = false;
	public static boolean flagOutputFile = false;
	public static boolean flagShowScore = false;
	public static boolean flagShowHelp = false;
	public static boolean flagShowFull = false;
	public static boolean flagScoreLimitOff = false;
	public static boolean flagPrintSentencesWithNoExtraction = false;
	public static String inputFile = "";
	public static String outputFile = "";
	public static String swneFile = "";
	public static BufferedWriter bw = null;
	public static FileWriter fw = null;
	public static BufferedWriter swneFileBuff = null;
	public static FileWriter swneFileFile = null;
	
	public static String EMPTY_SENTENCES_FILENAME = "emptySentences.txt";

	
	protected String filename = "";	
	protected BufferedReader br=null;
	protected int fileIndex = 0;

	
	public void startReading(String filename) throws FileNotFoundException {
		
		this.br = new BufferedReader(new FileReader(filename));
		
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
	
	public static void options(String[] args) throws Exception {
		
		for(int i=0;i<args.length;i++) {
			
			String param = args[i];
			
			switch (param) {
				case "-f":
					if(args.length<=(i+1)) {
						throw new Exception("Param -f expects a filepath name ");
					}
					inputFile = args[i+1];
					break;
				case "-o":
					flagOutputFile = true;
					if(args.length<=(i+1)) {
						throw new Exception("Param -o expects a filepath name ");
					}
					outputFile = args[i+1];
					
					fw = new FileWriter(outputFile);
		            bw = new BufferedWriter(fw);
					
					break;
				case "-reverb":
					flagUseReverb = true;
					break;
				case "-score":
					flagShowScore = true;
					break;
				case "-swne":
					flagPrintSentencesWithNoExtraction = true;					
					if(args.length<=(i+1)) {
						throw new Exception("Param -swne expects a filepath name ");
					}
					swneFile = args[i+1];
					swneFileFile = new FileWriter(swneFile);
					swneFileBuff = new BufferedWriter(swneFileFile);
					break;
				case "-full":
					flagShowFull = true;
					break;				
				case "help":
				case "-help":
				case "--help":
				case "-ayuda":
				case "--ayuda":
				case "ayuda":
				case "menu":
				case "-menu":
				case "--menu":
					flagShowHelp = true;
					break;
				case "-scoreLimitOff":
					flagScoreLimitOff = true;
					break;
				default:					
					break;
			}
		}
	}
	
	public static void outputSWNE(String line) throws IOException {
		swneFileBuff.write(line);					
		swneFileBuff.write("\n");	
	}
	
	public static void output(String line, List<Relation> relations) throws IOException {
		if(!flagOutputFile) {
			System.out.println(line);					
			for (Relation relation : relations) {				
				if(flagShowScore) {
					System.out.println(relation.toStringScore());
				}else if(flagShowFull) {
					System.out.println(relation.toStringFull());
				}else {
					System.out.println(relation.toString());
				}
			}
			System.out.println();
		}else {
			
			bw.write(line);					
			bw.write("\n");
			for (Relation relation : relations) {				
				if(flagShowScore) {
					 bw.write(relation.toStringScore());
				}else if(flagShowFull) {
					 bw.write(relation.toStringFull());
				}else {
					 bw.write(relation.toString());
				}
				bw.write("\n");
			}
			bw.write("\n");
		}
	}
	
	public static void main(String[] args) {
		
		try {
			CommandLine.options(args);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return;
		}
		
		if(flagShowHelp) {
			printHelp();
			return;
		}
		
		CommandLine cm = new CommandLine();
		
		RelationExtractor extractor;
		try {
			extractor = new RelationExtractor();
			//extractor.setUseReverb(flagUseReverb);
			extractor.setScoreFilter(!flagScoreLimitOff);
				
		} catch (Exception e1) {
			e1.printStackTrace();
			return;
		}
		
		try {
			cm.startReading(CommandLine.inputFile);
			String line = cm.readNextLine();
			 while(line!=null ){
				 List<Relation> relations = extractor.extractInformationFromParagraph(line);
				 if(relations!=null && !relations.isEmpty()){
					 output(line, relations);
				 }else{
					 System.err.println("No se extrajo relación");
					 if(flagPrintSentencesWithNoExtraction) {
						 outputSWNE(line);
					 }
					 //System.err.println("No relation extracted");
				 }
				 line = cm.readNextLine();
			 }		
			 cm.endRead();
		}catch(Exception e){
			e.printStackTrace();
		}finally {
            try {
                if (bw != null)
                    bw.close();

                if (fw != null)
                    fw.close();
                
                if(swneFileBuff != null)
                	swneFileBuff.close();

                if(swneFileFile != null)
                	swneFileFile.close();
                
            } catch (IOException ex) {
                System.err.format("IOException: %s%n", ex);
            }
        }

	}

	private static void printHelp() {
		System.out.println("TP-OIE-ES : Tree pattern - Open Information Extractor - Español"); 
		System.out.println("Ejemplo de uso:");
		System.out.println("	java -jar -Xmx4056m  -Xms1024m -ea tp-oie.jar -f /path/inputfile.txt");
		System.out.println("	java -jar -Xmx4056m  -Xms1024m -ea tp-oie.jar -f /path/inputfile.txt -o /path/output.file");
		System.out.println("	java -jar -Xmx4056m  -Xms1024m -ea tp-oie.jar <options> -f /path/inputfile.txt -o /path/output.file");
		System.out.println("");
		System.out.println("opciones disponibles: ");
		System.out.println(" -f  <file>: parametro obligatorio, indica un archivo de texto de entrada");
		System.out.println(" -o  <file>: indica cual será el  archivo de salida. Si no está presente se muestra la salida por pantalla");		
		System.out.println(" -score : imprime el puntaje que tiene la extracción");
		System.out.println(" -full : imprime el puntaje, el id, y si la relación es de tipo no-factica, indica su dependencia");
		System.out.println(" -scoreLimitOff : ignora el limite de score para mostrar una relación extraida. El limite es: "+Constants.SCORE_LIMIT);
		System.out.println(" -swne <file> : imprime en el archivo indicado las oraciones para las cuales no extrajo relaciones");
		System.out.println(" -help : imprime este menú");
	}

}
