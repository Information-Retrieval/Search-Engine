package indexer;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

import textprocessor.*;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

import config.DatabaseConfig;

public class InvertedIndex extends Thread {
	
	private Tokenize tokenizor = new Tokenize();
	private HashSet<String> stopWordsSet;
	private int start;
	private int end;
	public InvertedIndex()
	{
		
	}
	public InvertedIndex(int start, int end)
	{
		this.start=start;
		this.end=end;
	}
	
	
	//public void createIndex()  throws IOException {
	@Override
	public void run() {	
	try {
			
		   Thread t = Thread.currentThread();
		   String tname = t.getName();
		
			//Connect to Database
			Connection c = (Connection) DriverManager.getConnection(DatabaseConfig.connection, DatabaseConfig.user, DatabaseConfig.password);
	        
			//Fetch each web page - its docid, text and text count
			PreparedStatement ps1 = (PreparedStatement) c.prepareStatement("SELECT id,docid, text, text_count FROM web_pages WHERE id BETWEEN ? AND ? AND docid NOT IN (SELECT docid FROM inverted_index);");
			ps1.setInt(1, start);
			ps1.setInt(2, end);

	        ResultSet rs;
			rs = ps1.executeQuery();	
			//int doc_count = 1;

			//make hashset of stop words
			List<String> stopWords = null;
			try {
				stopWords = tokenizor.generateTokens("stopwords.txt");
			} catch (Exception e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			stopWordsSet = new HashSet<String>();
			for(String s : stopWords)
				stopWordsSet.add(s);
			
			PreparedStatement ps2 = (PreparedStatement) c.prepareStatement("INSERT INTO inverted_index (term, docid, term_count, positions, doc_token_count, TF) VALUES (?, ?, ?, ?, ?, ?) ");
			while(rs.next())
			{
				
				Integer docid = rs.getInt("docid");
				Integer id = rs.getInt("id");
				
				System.out.println("Thread : " + tname + " URL ID : " + id);
				List<String> tokens = null;
				try {
					tokens = tokenizor.generateStringTokens(rs.getString("text"));
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Integer word_count = rs.getInt("text_count");
	        	Double tf;
	        	HashMap<String, TokenInfo> hash = null;
				try {
					hash = generateTokenInfo(tokens);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        
	        	for(Entry<String,TokenInfo> termEntry : hash.entrySet())
	        	{	
		        	ps2.setString(1,termEntry.getKey());
		        	ps2.setInt(2,docid);
		        	ps2.setInt(3,termEntry.getValue().getFrequency());
		        	ps2.setString(4,termEntry.getValue().getPositions());
		        	ps2.setInt(5, word_count);
		        	//calculating Term weight from raw term frequency
		        	tf=1+Math.log10(termEntry.getValue().getFrequency());
		        	ps2.setDouble(6, tf);
		        	ps2.executeUpdate();
	        	}
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public  HashMap<String, TokenInfo> generateTokenInfo(List<String> tokens) throws IOException
	{	
			
		HashMap<String, TokenInfo> hash = new HashMap<String, TokenInfo>();
		Integer position=0;
		
		for (String s : tokens)
		{
			position++;
			if (stopWordsSet.contains(s) ||  (s.length() <= 2 && !s.equals("us") && !s.equals("ca")))
				continue;
			if(s.length() > 256)
				continue;
			TokenInfo t = hash.get(s);
			if (t == null)
				t = new TokenInfo();
			t.addPosition(position.toString());
			hash.put(s,t);
		}		
		return hash;
	}
}
