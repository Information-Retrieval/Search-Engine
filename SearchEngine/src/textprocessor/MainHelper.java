package textprocessor;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * This is a driver class which runs all the modules of the project
 * @author Arjun Bhadra
 *
 */
public class MainHelper {
	public static void main(String args[])throws IOException
	{	String fileName;
		List<String> l=new ArrayList<>();
		fileName="F:\\pg100.txt"; 					//input file from user
		
		
		//time execution started
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		Date startDate = new Date();
		//System.out.println("Execution started at : "+dateFormat.format(startDate));
		
		//Tokenizer class
		Tokenize t=new Tokenize();	
		 l=t.generateTokens(fileName);
		// t.printTokens(l);
		 //display tokens with their frequency
		 CountTokens c=new CountTokens();
		 c.count(l);
//		
		 //display 3-grams in order of their frequency
//		 NGram ng=new NGram();
//		 ng.getThreeGram(l);
//		 
		//display anagrams of a string		
//		Anagrams an=new Anagrams();
//		an.getAnagrams(l);
//		
		//time execution started
				
				Date endDate = new Date();
				System.out.println("Execution started at: "+dateFormat.format(startDate)+" ended at : "+dateFormat.format(endDate));
				
	}
}
