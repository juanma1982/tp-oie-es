package ar.edu.unlp.mains;

import ar.edu.unlp.extractionsources.MySQLData;

public class MySQLDataMain {

	public static boolean INSERT_IN_DB = true;
	public static void main(String[] args) {
		try{
			MySQLData.extractInformationFromTableAndInsert(INSERT_IN_DB);
		} catch (Exception e) {			
			e.printStackTrace();
		}
		System.out.println("Done");
	}

}
