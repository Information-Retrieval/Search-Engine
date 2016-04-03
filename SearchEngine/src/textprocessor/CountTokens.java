package textprocessor;

import java.util.*;
import java.util.Map.Entry;
/**
 * This module counts the frequency of the tokens generated in Tokenize class
 *  and sorts them in decreasing order of frequency
 * @author Arjun Bhadra
 *
 */
public class CountTokens{
	HashMap<String,Integer> hmap;int c=0;
	
	public TreeMap<String,Integer> count(List<String> l) {
		System.out.println("\n***************************************************************************************************");
		System.out.println("--------------------Tokens Arranged in DECREASING order of frequency-------------------------------\n");
		 hmap=new HashMap<String,Integer>(); // to store all the tokens to hashamp to remove duplicates
		
		 /* stores tokens into hashmap and counts frequency*/
		for(String s:l)
			{	
			if((hmap.get(s))==null)   // if the token is not present in hashmap enter it and put its frequency as 1
				{
					hmap.put(s, 1);
				}
			else
				hmap.put(s,hmap.get(s)+1); // increment frequency (value) if the token is already exists in the hashmap
				
			}
		
		/*sorts the tokens in decreasing order of frequency*/
		TreeMap<String,Integer> sortedmap= sortByValue(hmap);
		
		printSortedTokens(sortedmap);  // print the output
		return sortedmap;
		}
		
		public static TreeMap<String,Integer> sortByValue(HashMap<String,Integer>map){
			ValueComparator vc=new ValueComparator(map);
			TreeMap<String,Integer> sortedMap = new TreeMap<String,Integer>(vc);
			sortedMap.putAll(map);
			return sortedMap;
		}
		
		public void printSortedTokens(TreeMap<String,Integer> sortedmap)
		{
			for(Entry<String, Integer>entry : sortedmap.entrySet())
			{	if(c<10){
				String key=entry.getKey();
				Integer value=entry.getValue();
				System.out.println("[Token: \""+key+"\" -> Frequency: "+value+"]");
				c++;
				}
			}
		}
			
}
