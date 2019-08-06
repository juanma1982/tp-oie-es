# TP-OIE-ES
Método de extracción de relaciones semánticas para la Web (OIE) en español

##Steps to compile TP-OIE-ES application##

    1. user must have Java 1.8 or greater installed and Maven (https://maven.apache.org/install.html). (tested with maven 3.5.2 and 3.3.9)
    
 		mvn clean compile assembly:single

	This step will download all needed libraries and then will build TP-OIE from source code.
    2. When finish you should see a “BUILD SUCCESS” message similar to the following:
[INFO] --------------
[INFO] BUILD SUCCESS
[INFO] --------------

    3. The application should be created into “target” directory. Please move the jar file to the parent directory and rename as “tp-oie-es.jar”, you can do that executing the following command:

	mv target/TP-OIE-ES-1.0-jar-with-dependencies.jar tp-oie-es.jar
	
##Steps to run TP-OIE-ES##

    1. If you are in a Linux environment, you can execute the file “runTP-OIE-ES.sh”, the available options are:

	 -f : mandatory parameter, indicates the input text file
	 -o : indicates the output file. If not present, the result will be printed in console
	 -reverb : Use reverb for sentences without extractions 
	 -score : also prints the score of the extraction
	 -full : prints score, id, and if the relation is non factual its dependency
	 -help : prints a help menu

	for example, you can execute:

	./runTP-OIE-ES.sh -f testFile.txt -full -reverb


    2. If you are under Windows environment or another SO, you must run the application with the following command:

	java -jar -Xmx4056m  -Xms1024m -ea tp-oie.jar 

	the parameters are the same, for example:

	java -jar -Xmx4056m  -Xms1024m -ea tp-oie.jar -f testFile.txt -full -reverb

