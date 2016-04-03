package ranker;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.Map.Entry;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

import config.DatabaseConfig;

public class PageRank 
{
	public static void main(String args[]) throws SQLException
	{
		HashMap<String,ArrayList<String>> graph = new HashMap<String,ArrayList<String>>();
		HashMap<String,Double> rank = new HashMap<String,Double>();
		HashMap<String,Double> lastrank = new HashMap<String,Double>();
		//ArrayList<String> keys = new ArrayList<String>();
		double d = 0.85;
		System.out.println("connecting to db...");
		//Connect to Database
		Connection c = (Connection) DriverManager.getConnection(DatabaseConfig.connection, DatabaseConfig.user, DatabaseConfig.password);
        System.out.println("connected!");
		//Fetch each web page - url,outgoing_urls
		PreparedStatement ps1 = (PreparedStatement) c.prepareStatement("SELECT url,outgoing_urls FROM web_pages");
		ResultSet rs;
		rs = ps1.executeQuery();	
		System.out.println("got the results from db!");
		//Inserting query for pagerank table
		PreparedStatement ps2 = (PreparedStatement) c.prepareStatement("INSERT INTO pagerank (url,rank) VALUES(?,?) ");
		
		while(rs.next())
		{	
			String url=rs.getString("url");
			String out_url=rs.getString("outgoing_urls");
			
			out_url = out_url.replaceAll("\\[", "").replaceAll("\\]","");
			String links[];
			links=out_url.split(", ");
			
			ArrayList<String> link_url=new ArrayList<String>();
			for(int i=0; i<links.length;i++)
			{
				if(link_url.contains(links[i]));
				else
					link_url.add(links[i]);
			}
			graph.put(url, link_url);
			rank.put(url, (double) 0);
			lastrank.put(url, (double) 1);
		}
		System.out.println("Starting ranking iterations...");
		ArrayList<String> outgoing = new ArrayList<String>();
		String url,outgoingurl;
		double sendrank=0,updaterank=0,x=0;
		for(int i=0;i<200;i++)
		{
			System.out.println(i);
			for (Entry<String, ArrayList<String>> urlEntry : graph.entrySet())
			{
				url=urlEntry.getKey();
				outgoing = urlEntry.getValue();
				sendrank = (lastrank.get(url)/outgoing.size());
				for(int k=0;k<outgoing.size();k++)
				{
					outgoingurl = outgoing.get(k);					
					if(graph.containsKey(outgoingurl))
					{
						updaterank = rank.get(outgoingurl) + sendrank;
						rank.put(outgoingurl,updaterank);
					}
				}
			}
			System.out.println("starting damping loop...");
			lastrank.clear();
			for (Entry<String, Double> rankEntry : rank.entrySet())
			{
				x = rankEntry.getValue();
				url = rankEntry.getKey();
				x = (1-d)+d*x;
				lastrank.put(url,x);
				rank.put(url,(double)0);
			}
		}
		
		
		System.out.println("inserting the results into db!");
		Double f_rank;
		Iterator it2 = lastrank.entrySet().iterator();
		while (it2.hasNext()) 
	    {	
	        Map.Entry e = (Map.Entry)it2.next();
	        url=(String) e.getKey();
	    	f_rank=(Double) e.getValue();
	        System.out.println("URL: "+e.getKey()+" Rank: "+e.getValue());
	        //enter into database
	        ps2.setString(1,url);
			ps2.setDouble(2, f_rank);
        	ps2.executeUpdate();
	        it2.remove();
	    }

	}
}