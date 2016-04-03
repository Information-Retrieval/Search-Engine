package textprocessor;

import java.util.*;
import java.io.*;
/**
 * This class tokenizes the file whose name is passed to it and returns the list of tokens
 * @author Arjun Bhadra
 *
 */
public class Tokenize {
	 public boolean isNumeric(String str) {
		             if (str == null) {
		                 return false;
		            }
		             int sz = str.length();
		             for (int i = 0; i < sz; i++) {
		                 if (Character.isDigit(str.charAt(i)) == false) {
		                     return false;
		                 }
		             }
		             return true;
		         }
		   
	public List<String> generateTokens(String fileName) throws IOException{
		int wordcount=0;
		List<String> tokenList=new ArrayList<>();
		Scanner sc=null;String s;
		FileInputStream fstream=null;
		
		try{		
			fstream=new FileInputStream(fileName);
			sc = new Scanner(fstream, "UTF-8"); 
			while(sc.hasNextLine())
			{
				s=sc.nextLine();
				String smallS=s.toLowerCase();
				smallS=smallS.replaceAll("[^a-z0-9']", " "); // remove all the characters other than a-z 0-9 and '
				smallS=smallS.replaceAll("[']", "");         // remove '
				StringTokenizer st = new StringTokenizer(smallS," ");  // tokenizes on witespace
			     while(st.hasMoreTokens())
			     {
			    	 
			    	 tokenList.add(st.nextToken());
			    	 wordcount++;      // keeps track of number of tokens 
			     }
				
			}
			//System.out.println("TokenCount: "+ wordcount+" tokens\n");
			
			if (sc.ioException()!=null){ // just to prevent exceptions with scannner class
				
				throw sc.ioException();
			}
			
		}finally {
			
			if(fstream!=null){    // to close file stream reader
				fstream.close();
			}
			if(sc!=null){
				sc.close(); // to close scanner
			}
		}
		//printTokens(tokenList); //to print tokens implicitly when the tokenizer method is called
		return tokenList;
			
		
	}
	public List<UrlToken> generateUrlTokens(String fileName) throws IOException{
		
		List<UrlToken> urlTokenList=new ArrayList<UrlToken>();
		Scanner sc=null;
		//FileInputStream fstream=null;
		File file = new File(fileName);
		String url;
		int count = 0;
		
		try{		
			//fstream=new FileInputStream(fileName);
			sc = new Scanner(file); 
			sc.useDelimiter(" ");
				
			while(sc.hasNext())
			{
				url = sc.next();
				if(sc.hasNext())
					count = Integer.parseInt(sc.next());
				else break;	
				UrlToken ut=new UrlToken(url,count);
				//System.out.println(ut.url+" -- count --"+ut.count+" !!!");
				urlTokenList.add(ut);
			}
			sc.close();
			
			/*				//s=s.replaceAll("[^a-Z0-9.:/]", ""); // remove all the characters other than a-z 0-9 and '
				StringTokenizer st = new StringTokenizer(s," ");  // tokenizes on witespace
			     while(st.hasMoreTokens())
			     {
			    	 UrlToken ut=new UrlToken(st.nextToken(),Integer.parseInt(st.nextToken()));
			    	 urlTokenList.add(ut);
			    	 
			     }
			     */
		
		}
		catch(FileNotFoundException e)
		{
			e.printStackTrace();
		}
		return urlTokenList;
	}
public List<String> generateSubdomainTokens(String fileName) throws IOException{
		
		List<String> subdomainList=new ArrayList<>();
		Scanner sc=null;
		String s;
		FileInputStream fstream=null;
		
		try{		
			fstream=new FileInputStream(fileName);
			sc = new Scanner(fstream, "UTF-8"); 
			while(sc.hasNextLine())
			{
				s=sc.nextLine();
				s=s.replaceAll("[^a-zA-Z0-9.:/]", ""); // remove all the characters other than a-z 0-9 and '
				
				StringTokenizer st = new StringTokenizer(s," ");  // tokenizes on witespace
			     while(st.hasMoreTokens())
			     {
			    	subdomainList.add(st.nextToken());	 
			     }
			}
			if (sc.ioException()!=null){ // just to prevent exceptions with scannner class
				throw sc.ioException();
			}	
		}finally {
			
			if(fstream!=null){    // to close file stream reader
				fstream.close();
			}
			if(sc!=null){
				sc.close(); // to close scanner
			}
		}
		return subdomainList;
	}
	
	
	//string tokenizer
	@SuppressWarnings("resource")
	public List<String> generateStringTokens(String crawledText) throws IOException{
		List<String> stringTokenList=new ArrayList<>();
		String s=crawledText;	
		String smallS=s.toLowerCase();
		smallS=smallS.replaceAll("[^a-z0-9'-]", " "); // remove all the characters other than a-z 0-9 and '
		smallS=smallS.replaceAll("['-]", "");         // remove '
		StringTokenizer st = new StringTokenizer(smallS," ");  // tokenizes on witespace
		while(st.hasMoreTokens())
		{
			String temp = st.nextToken();
			int len=temp.length();
			if(isNumeric(temp) &&(len!=4||len!=10))
				continue;
			stringTokenList.add(temp);		
		}
		return stringTokenList;
	}
	public  List<String> generateStringTokens2(String crawledText) throws IOException{
		// Reading File line by line
		List<String> token_list = new ArrayList<String>();
		// Removing Punctuation's, Symbols and keeping only alphanumeric character
		crawledText=crawledText.toLowerCase();
		//String[] arr=crawledText.split("[\\p{P}\\p{S}\\s]");	
		String[] arr=crawledText.split("\\W+");
		Collections.addAll(token_list,arr);
		token_list.removeAll(Collections.singleton(""));
		return token_list;
	}
	public void printTokens(List<String>l)
	{	System.out.print("Tokens: [");
		for(String s:l)
		{
			System.out.println("\""+s+"\"");
		}
		System.out.println("]");
	}
}

