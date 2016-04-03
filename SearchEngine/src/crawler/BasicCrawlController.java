package crawler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class BasicCrawlController 
{
	private static final Logger logger = LoggerFactory.getLogger(BasicCrawlController.class);

	public static void main(String[] args) throws Exception 
	{
		String crawlStorageFolder = ".";
		int numberOfCrawlers = 7;
		CrawlConfig config = new CrawlConfig();
		config.getCrawlStorageFolder();
		config.setPolitenessDelay(1000);
		config.setMaxDepthOfCrawling(-1);
		config.setMaxPagesToFetch(-1);
		config.setIncludeBinaryContentInCrawling(false);
		config.setResumableCrawling(true);
		config.setCrawlStorageFolder(crawlStorageFolder);
		config.setUserAgentString("IR W16 WebCrawler");
		config.setSocketTimeout(50000);
		config.setConnectionTimeout(50000);
    
		PageFetcher pageFetcher = new PageFetcher(config);
		RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
		RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
		CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);
		
		controller.addSeed("http://www.ics.uci.edu");
		controller.addSeed("http://www.vision.ics.uci.edu");
		controller.addSeed("ics.uci.edu/~lopes");
		
		controller.start(BasicCrawler.class, numberOfCrawlers);
		
		Answers a = new Answers();
		a.getAnswers();
	}
}
