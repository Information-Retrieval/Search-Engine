package textprocessor;

import java.util.*;
import java.util.Map.Entry;

public class NGram {
	
	int total=0;
	String ngmap;
	HashMap<String,Integer> hmap=new HashMap<>();
	
	public TreeMap<String,Integer> getThreeGram(List<String>l, int counter){
		System.out.println("\n**********************************************************************************************");
		System.out.println("--------------------3-Gram's Arranged in DECREASING order of frequency------------------------\n");
		
		int n=l.size();
		for (int i=0;i<n-3;i++){
			
			ngmap=l.get(i)+" "+l.get(i+1)+" "+l.get(i+2);  // storing the 3 words to form a 3-Gram
			if(hmap.get(ngmap)==null){

				hmap.put(ngmap,1); // storing the 3-grams to hashmap and this will also remove duplicates
			}
			else
				hmap.put(ngmap,hmap.get(ngmap)+1);  // if same 3-gram repeats we just increment the frequency
			total++;                             // total number of 3-Grams
		}
		
		TreeMap<String,Integer> sortedmap= sortByValue(hmap);
		 printGram(sortedmap,counter);   // print the output
		 return sortedmap;
	}
	public void printGram(TreeMap<String,Integer>sortedmap, int counter)
	{
		int count = 0;
		for(Entry<String, Integer>entry : sortedmap.entrySet())
			{
				String key=entry.getKey();
				Integer value=entry.getValue();
				System.out.println((count+1)+". [3-Gram:\""+key+"\" -> Frequency: "+value+"]");
				count++;
				if(count == counter)
					break;
			}

		System.out.println("\"Toatal nuber of 3-grams: "+ total+"\"");
	}
	public static TreeMap<String,Integer> sortByValue(HashMap<String,Integer>map){
			ValueComparator vc=new ValueComparator(map); //  sort the map in descending order using custom comparator
			TreeMap<String,Integer> sortedMap = new TreeMap<String,Integer>(vc);
			sortedMap.putAll(map);
			return sortedMap;
	}
		
	
	
}

