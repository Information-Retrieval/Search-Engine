package crawler;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Map.Entry;

import textprocessor.*;

public class Answers 
{
	
	public void getAnswers() throws IOException
	{

/*
******************************************************************************************
		How many unique pages did you find in the entire domain? (Uniqueness is
				established by the URL)
******************************************************************************************
*/
		Tokenize tokenizer=new Tokenize();
		List<UrlToken> urlListTokens = tokenizer.generateUrlTokens("count.txt");
		System.out.println("Number of unique URLs visited: "+(urlListTokens.size()));
		
		
		
/*
 ************************************************************************************		
 		How many subdomains did you find? Submit the list of subdomains ordered
		alphabetically and the number of unique pages detected in each subdomain.
		The file should be called Subdomains.txt, and its content should be lines
		containing
		URL, number
		http://vision.ics.uci.edu, 10 ( not the actual number here)
		etc.
**************************************************************************************
*/
		
		List<String> subdomainListTokens = tokenizer.generateSubdomainTokens("subdomainlist.txt");
		TreeMap<String,Integer> tmap=new TreeMap<>();
		int sublength=subdomainListTokens.size();
		for(int i=0;i<sublength;i++)
		{
			String subdomain = subdomainListTokens.get(i);
			if(tmap.get(subdomain)==null){
				tmap.put(subdomain, 1);
			}
			else
				tmap.put(subdomain, tmap.get(subdomain) + 1);
		}
		PrintWriter write;
		write = new PrintWriter(new FileOutputStream("SubDomains.txt",true));
		System.out.println("Number of Subdomains found: " +tmap.size());
		for (Entry<String, Integer> entry : tmap.entrySet()) 
		{
			String key = entry.getKey();
			Integer value = entry.getValue();
			write.println("http://www." + key + ".uci.edu" + " contains "+ value + " pages");
		}
		write.close();

/*
 * ***************************************************************************		
		What is the longest page in terms of number of words? (HTML markup
				doesn’t count as words)
******************************************************************************				
*/
		
		Collections.sort
		(
				urlListTokens,new Comparator<UrlToken>() 
			{
				@Override
				public int compare(UrlToken a, UrlToken b)
				{
					if(a.count < b.count)
						return 1;
					else if(a.count == b.count)
						return 0;
					else return -1;
				}
			}
		);	
		
		System.out.println("Longest page in terms of number of words:\n URL: " + urlListTokens.get(0).url + "\n Count: " + urlListTokens.get(0).count);	

/*
**********************************************************************************		
		What are the 500 most common words in this domain? (Ignore English stop
		words, which can be found, for example, here) Submit the list of the 500
		most common words ordered by frequency. 
**********************************************************************************
*/
		
		//Get all words
		List<String> textTokens = tokenizer.generateTokens("text.txt");
		
		//Get all stop words
		List<String> stopWords = tokenizer.generateTokens("stopwords.txt");
		
		//Remove stop words from all words
		textTokens.removeAll(stopWords);
		
		//Remove words of length one and two 
		String pattern1="uc";
		String pattern2="ca";
		Iterator<String> it = textTokens.iterator();
		while(it.hasNext()) {
		    String s = it.next();
		    if (s.length()==1)
		    	it.remove();
		    else if(s.length() == 2 && !(s.equals(pattern1) || s.equals(pattern2)))
		    	it.remove();
		}
		
		
		//Make a hash map of remaining words to generate frequencies
		String s;
		HashMap<String,Integer> wordFrequencies = new HashMap<String,Integer>();
		for(int i=0;i<textTokens.size();i++)
		{
			s=textTokens.get(i);
			if(wordFrequencies.get(s)==null)
				wordFrequencies.put(s,1);
			else wordFrequencies.put(s,(wordFrequencies.get(s)+1));
		}
		
		//Sort hashmap by turning it into a list and using sort on list with a comparator
		List<Entry<String, Integer> > sorted_list = new ArrayList<Entry<String, Integer> >();
		for (Entry<String, Integer> t : wordFrequencies.entrySet())
		{
			sorted_list.add(t);
		}
		
		sorted_list.sort(new Comparator<Entry<String, Integer>>()
		{
			@Override
			public int compare(Entry<String, Integer> arg0,
					Entry<String, Integer> arg1) {
				if (arg0.getValue() == null)
					return 1;
				else
					return -1 * arg0.getValue().compareTo(arg1.getValue());
			}
		});
		
		//Print first 500 
		int counter=1;	
		for(Entry<String,Integer> word : sorted_list)
		{
			System.out.println(counter + ". Word: " + word.getKey() + "-> Frequency: " + word.getValue());
			counter++;
			if(counter > 500) 
				break;
		}
/*
 **************************************************************************************	
		What are the 20 most common 3-grams? (again ignore English stop words) A
	    2-gram, in this case, is a sequence of 2 words that aren’t stop words and that
		haven’t had a stop word in between them. Submit the list of 20 2-grams
		ordered by frequency. 
***************************************************************************************
 */		
		NGram ng = new NGram();
		TreeMap<String,Integer> threeGramFrequencies =  ng.getThreeGram(textTokens, 20);
		
	}
}
