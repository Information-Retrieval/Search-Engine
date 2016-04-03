package textprocessor;
import java.util.*;
import java.io.IOException;

public class Anagrams {


	HashMap<String,String> indexWords=new HashMap<>(); // to store sorted character string as key and words containing those characters as values
	int uniquewordcount=0;                            // to calculate the size of unique dictionary words after combining two dictionaries with some overlap
	@SuppressWarnings("rawtypes")                     // to remove warnings
	public void loadDictionary() throws IOException
	{
		System.out.println("\n*******************************************************************");
		
		System.out.println("--------------------Tokens -> Anagrams------------------------------\n");
		
	Tokenize t=new Tokenize();
	char c[];
	List<String> words=new ArrayList<>(); 
	List<String> unique=new ArrayList<>();
	//words=t.generateTokens("F:\\wordsEn.txt");
	words=t.generateTokens("F:\\worddb.txt");         // combination of two dictionaries found from different sources with some repeated words
	//to generate unique words from dictionary
	HashMap<String,String> uniquewords=new HashMap<>(); //to get unique words of dictionary since I got two text files containing dictionary words
	for(String w:words)
	{
		uniquewords.put(w,w); // this will prevent duplicates // put(w,w) to access w using w maybe redundant
		
	}
	
	Iterator it = uniquewords.entrySet().iterator(); // to iterate over all the unique words stored in hashmap
    while (it.hasNext()) {
    	Map.Entry pair=(Map.Entry)it.next();
    	unique.add((String) pair.getValue()); // adding to List named "unique" to store all the unique dictionary words
    	uniquewordcount++;                     // counting unique words
    }
    
	String sortedChars;   // string with sorted characters to be used as key for hashmap
	for(String l:unique) //while all the strings in unique list
		{
		c=new char[l.length()];
		c=l.toCharArray();
		Arrays.sort(c);               // sorting the characters of the string to be stored as key in the dictionary hashmap
		sortedChars=new String(c);    // converted sorted char array to string
		
		if(indexWords.get(sortedChars)==null){   //if the sorted character string appears for first time in the dictionary hashmap
				indexWords.put(sortedChars,l);  // put the sorted version of the word as key and the original word as value which will be considered as anagram
			}
		else{
			// if one word already present in value append other to it
			if(!l.equals(indexWords.get(sortedChars)))
				{ 
				indexWords.put(sortedChars,indexWords.get(sortedChars)+", "+l); // if the sorted version of a string already exists just add the original word in the value (hence getting all the anagrams)
				}
			}
		
		}
	}
	//main function in MainHelper class calls this function
	public TreeMap<String,String> getAnagrams(List<String>tokens) throws IOException{
		HashSet<String> input=new HashSet<>();
		loadDictionary(); // to call the loadDictionary method to get the words from dictionary to find anagrams
		TreeMap<String,String> result=new TreeMap<>();
		String sortedString;
		for(String g: tokens) //tokens are generated from tokenize class
		{	
			input.add(g);          //adding to hashset to remove duplicates from input txt file
		}
		for (String s:input)    // for all unique tokens from input txt
		{	char c[];
			if(input.contains(s))
			{				
				c=new char[s.length()]; //converting each token to char array 
				c=s.toCharArray();
				Arrays.sort(c);			// to sort the chars to access its anagrams from the dictionary hashmap where key is sorted character words and corresponding words as anagrams as the value of the hashmap
				sortedString=new String(c);
				if(indexWords.get(sortedString)!=null)   // if the sorted character string found in dictionary hashmap
				{
					result.put(s,indexWords.get(sortedString));
					//System.out.println(s+" -> "+indexWords.get(sortedString)); // to display all the anagrams
				}
				//	else //if no such sorted character string found in the dictionary hashmap
					//System.out.println(s+" -> "+" ** \"ERROR: NO ANAGRAMS FOUND !\" ** "); // to not display null when no anagrams found
			}
		}
		printAnagrams(result);    // print the output
		return result;
		
		
	}
	public void printAnagrams(TreeMap<String,String> result){
		for(Map.Entry<String,String> entry: result.entrySet())
		{
			String key=entry.getKey();
			String value=entry.getValue();
			System.out.println("[Token: \""+key+"\" -> Anagrams: {"+value+"}]");
			//System.out.println("Size of the Dictionary: "+uniquewordcount);//  unique words by combining words from two text files found online
		}
	}
}
