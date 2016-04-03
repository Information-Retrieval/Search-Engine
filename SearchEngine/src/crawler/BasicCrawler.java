package crawler;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import textprocessor.Tokenize;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

import java.sql.DriverManager;
import java.sql.SQLException;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

import config.DatabaseConfig;

public class BasicCrawler extends WebCrawler 
{
	private static final Logger logger = LoggerFactory.getLogger(BasicCrawlController.class);
    private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|bmp|gif|jpe?g" 
            + "|png|tiff?|mid|mp2|mp3|mp4"
            + "|wav|avi|mov|mpeg|ram|m4v|pdf" 
            + "|rm|smil|wmv|swf|wma|zip|rar|gz))$");
	private static int htmlCount = 0;
  
	PrintWriter p1;
	BasicCrawlController bcc=new BasicCrawlController();
	
	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) 
	{
		String href = url.getURL().toLowerCase();
		
		if (FILTERS.matcher(href).matches()) 
		{
			return false;
		}
		if(href.indexOf("duttgroup.ics.uci.edu")!=-1)
		{
			return false;
		}
		if(href.indexOf("doku.php")!=-1)
		{
			return false;
		}
		if(href.indexOf("genomics.ics.uci.edu")!=-1)
		{
			return false;
		}
		if(href.indexOf("fano.ics.uci.edu/ca")!=-1)
		{
			return false;
		}
		if(href.indexOf("ics.uci.edu/~eppstein/pix") != -1)
		{
			return false;
		}
		if (href.indexOf("http://djp3-pc2.ics.uci.edu/LUCICodeRepository")!=-1)
		{
			return false;
		}
		if(href.indexOf("archive.ics.uci.edu")!=-1)
		{
			return false;
		}
		
		if(href.indexOf("ics.uci.edu/~dock")!=-1)
		{
			return false;
		}
		if(href.indexOf("ics.uci.edu/prospective")!=-1)
		{
			return false;
		}

		if(href.indexOf("luci.ics.uci.edu")!=-1)
		{
			return false;
		}
		
		if(href.indexOf("flamingo.ics.uci.edu/releases")!=-1)
		{
			return false;
		}
		if(href.indexOf("drzaius.ics.uci.edu/cgi-bin/cvsweb.cgi")!=-1)
		{
			return false;
		}
		if(href.indexOf("cbcl.ics.uci.edu/cgi-bin/cvsweb.cgi")!=-1)
		{
			return false;
		}
		if(href.indexOf("?")!=-1)
		{
			return false;
		}
		
    	return href.indexOf("ics.uci.edu")!=-1;
	}
  
	@Override
	public void visit(Page page) 
	{
		int docid = page.getWebURL().getDocid();
		String url = page.getWebURL().getURL();
		String domain = page.getWebURL().getDomain();
		String path = page.getWebURL().getPath();
		String subDomain = page.getWebURL().getSubDomain();
		String parentUrl = page.getWebURL().getParentUrl();
		String anchor = page.getWebURL().getAnchor();
		int wordCount=0;
		
		try
		{
			p1 = new PrintWriter(new FileOutputStream("urllist.txt",true));

			p1.println(url+" ");
			p1.close();
  	  	}
  	  	catch(Exception e)
		{
  	  		e.printStackTrace();
  	  	}


		logger.debug("Docid: {}", docid);
		logger.info("URL: {}", url);
		logger.debug("Domain: '{}'", domain);
		logger.debug("Sub-domain: '{}'", subDomain);
		logger.debug("Path: '{}'", path);
		logger.debug("Parent page: {}", parentUrl);
		logger.debug("Anchor text: {}", anchor);
		System.out.println("Url: "+url);
		
		try {
			p1=new PrintWriter(new FileOutputStream("subdomainlist.txt",true));
			String temp = subDomain;
			temp = temp.replaceAll("http://","");
			temp = temp.replaceAll("www.", "");
			temp = temp.replaceAll("https://","");
			
			p1.println(temp+" ");
			p1.close();
		} catch (FileNotFoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		if (page.getParseData() instanceof HtmlParseData) 
		{
			htmlCount++; 
			System.out.println("HTML Count: "+htmlCount);
			HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
			String text = htmlParseData.getText();
			
			Tokenize to=new Tokenize();
			try {
				List<String> textTokens=to.generateStringTokens(text);
				 wordCount=textTokens.size();
				 System.out.println("size : "+wordCount);
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			
			try 
			{
				p1=new PrintWriter(new FileOutputStream("text.txt",true));
			} 	
			catch(FileNotFoundException e1) 	
			{
				e1.printStackTrace();
			}
		
			p1.println(text+" ");
			p1.close();
		
			String html = htmlParseData.getHtml();
			Set<WebURL> links = htmlParseData.getOutgoingUrls();
			
			logger.debug("Text length: {}", text.length());
			
			try 
			{
				p1=new PrintWriter(new FileOutputStream("count.txt",true));
			} 
			catch(FileNotFoundException e) 	
			{
				e.printStackTrace();
			}
		
			p1.println(url+" "+wordCount+" ");
			p1.close();
			logger.debug("Html length: {}", html.length());
			logger.debug("Number of outgoing links: {}", links.size());
			
            try {
				Connection c = (Connection) DriverManager.getConnection(DatabaseConfig.connection, DatabaseConfig.user, DatabaseConfig.password);
	            PreparedStatement ps = (PreparedStatement) c.prepareStatement("INSERT INTO web_pages (docid,url, domain, subdomain, path, text, html, text_count, html_count, outgoing_urls, outgoing_urls_count, parent_url, anchor) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
	            ps.setInt(1, docid);
	            ps.setString(2, url);
	            ps.setString(3, domain);
	            ps.setString(4, subDomain);
	            ps.setString(5, path);
	            ps.setString(6, text);
	            ps.setString(7, html);
	            ps.setInt(8, wordCount);
	            ps.setInt(9, html.length());
	            ps.setString(10, links.toString());
	            ps.setInt(11, links.size());
	            ps.setString(12, parentUrl);
	            ps.setString(13, anchor);
	            ps.executeUpdate();
            } catch (SQLException e1) {
				e1.printStackTrace();
			}
		}

		Header[] responseHeaders = page.getFetchResponseHeaders();
		if(responseHeaders != null) 
		{
			logger.debug("Response headers:");
			for (Header header : responseHeaders) 
			{
				logger.debug("\t{}: {}", header.getName(), header.getValue());
			}
		}
		logger.debug("=============");
	}
}
