	package indexer;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

import config.DatabaseConfig;



public class Queries {
public static void main(String[] args) throws SQLException {
	//getDistinctTerms();
	//updateIDF();
	updateTFIDF();
	//generateCompactInvertedIndex();
}

public static void getDistinctTerms() throws SQLException
{
	Connection c = (Connection) DriverManager.getConnection(DatabaseConfig.connection, DatabaseConfig.user, DatabaseConfig.password);
    
	PreparedStatement ps1 = (PreparedStatement) c.prepareStatement("SELECT count(distinct term) FROM inverted_index;");
    ResultSet rs;
	rs = ps1.executeQuery();
	
	while(rs.next())
	{
		Integer count = rs.getInt(1);
		System.out.println("Count = " + count);
	}	
}

public static void updateIDF() throws SQLException
{
	//Number of documents in collection
	int N = 68714;
	
	Connection c = (Connection) DriverManager.getConnection(DatabaseConfig.connection, DatabaseConfig.user, DatabaseConfig.password);
    
	PreparedStatement ps1 = (PreparedStatement) c.prepareStatement("Select term, count(*) from inverted_index group by term;");
    ResultSet rs;
	rs = ps1.executeQuery();
	
	while(rs.next())
	{
		String term = rs.getString(1);
		int df = rs.getInt(2);
		System.out.println("Term = " + term + " Document Frequency = " + df);
		
		Double idf =Math.log10(N/df);
		
		PreparedStatement ps2 = (PreparedStatement) c.prepareStatement("UPDATE inverted_index SET IDF = ? WHERE term = ?");
		ps2.setDouble(1, idf);
		ps2.setString(2, term);
    	ps2.executeUpdate();
		
	}		
}

public static void updateTFIDF() throws SQLException
{
	//time execution started
	DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	Date startDate = new Date();
	System.out.println("Execution started at : "+dateFormat.format(startDate));
	
	Connection dbConnection = null;
	PreparedStatement ps1,ps2 = null;
	
	try {
		dbConnection = (Connection) DriverManager.getConnection(DatabaseConfig.connection, DatabaseConfig.user, DatabaseConfig.password);
	    dbConnection.setAutoCommit(false);
		ps1 = (PreparedStatement) dbConnection.prepareStatement("SELECT id,TF,IDF FROM inverted_index WHERE id BETWEEN 1 and 500000;");
		ps2 = (PreparedStatement) dbConnection.prepareStatement("UPDATE inverted_index SET TF_IDF = ? WHERE id = ?");	   
		ResultSet rs;
		rs = ps1.executeQuery();
		//int count =1;
		int id;
		double tf,idf,tf_idf;
		
		Date writeDate = new Date();
		System.out.println("Started writes at "+ dateFormat.format(writeDate) );
		while(rs.next())
		{
			id = rs.getInt(1);
			tf = rs.getDouble(2);
			idf = rs.getDouble(3);
			//System.out.println("id = " + id + " tf = " + tf + " IDF = " + idf);
			//System.out.println("Count = " + count);
			//count = count + 1;
			
			tf_idf = tf*idf;
			
			//ps2 = (PreparedStatement) dbConnection.prepareStatement("UPDATE inverted_index SET TF_IDF = ? WHERE id = ?");
			ps2.setDouble(1, tf_idf);
			ps2.setInt(2, id);
	    	//ps2.executeUpdate();
			ps2.addBatch();
			
		}	
		ps2.executeBatch();
		dbConnection.commit();
		//time execution ended
		Date endDate = new Date();
		System.out.println("Execution started at: "+dateFormat.format(startDate)+" ended at : "+dateFormat.format(endDate));

		
	} catch (SQLException e) {
		dbConnection.rollback();
	}finally{
		if (dbConnection != null) {
			dbConnection.close();
		}

	}
	}

public static void generateCompactInvertedIndex() throws SQLException
{
	Connection c = (Connection) DriverManager.getConnection(DatabaseConfig.connection, DatabaseConfig.user, DatabaseConfig.password);
    
	PreparedStatement ps1 = (PreparedStatement) c.prepareStatement("SELECT * FROM inverted_index");
//	ps1.setString(1, "drop");
    ResultSet rs1;
	rs1 = ps1.executeQuery();
	
	while(rs1.next())
	{
		String term = rs1.getString(2);
		double idf = rs1.getDouble(8);
		int docid = rs1.getInt(3);
		int term_count = rs1.getInt(4);
		String positions = rs1.getString(5);
		double tf = rs1.getDouble(7);
		double tf_idf = rs1.getDouble(9);
		
		
		//Check if term is present in term_id table
		PreparedStatement ps2 = (PreparedStatement) c.prepareStatement("SELECT id FROM term_id where term = ?");
	    ps2.setString(1, term);
		ResultSet rs2;	
		rs2 = ps2.executeQuery();
		int termid =0;
		if(!rs2.next())
		{
			//Put term in term table
			String generatedKeys[]= {"id"};//auto-increment column
			PreparedStatement ps3 = (PreparedStatement) c.prepareStatement("INSERT INTO term_id (term, idf) VALUES(?,?)",generatedKeys);
			ps3.setString(1, term);
		    ps3.setDouble(2, idf);
		    ps3.executeUpdate();
			ResultSet rs3 = ps3.getGeneratedKeys();
			if (rs3 != null && rs3.next()) {
			    termid = rs3.getInt(1);
			}	
		}
		else
		{
			termid = rs2.getInt(1);
		}
		
		PreparedStatement ps4 = (PreparedStatement) c.prepareStatement("INSERT INTO compact_inverted_index (termid, docid, term_count, positions, TF, TF_IDF) VALUES(?,?,?,?,?,?)");
	    ps4.setInt(1, termid);
	    ps4.setInt(2, docid);
	    ps4.setInt(3, term_count);
	    ps4.setString(4, positions);
	    ps4.setDouble(5, tf);
	    ps4.setDouble(6, tf_idf);
		ps4.executeUpdate();		
		
	}		
}
}
