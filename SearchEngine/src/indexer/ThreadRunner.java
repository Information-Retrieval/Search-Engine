package indexer;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ThreadRunner{
	public static void main(String[] args) throws IOException {

		//time execution started
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		Date startDate = new Date();
		System.out.println("Execution started at : "+dateFormat.format(startDate));
		
		//i.createIndex();
		 InvertedIndex t1 = new InvertedIndex(1,10000);
		 InvertedIndex t2 = new InvertedIndex(10001,20000);
		 InvertedIndex t3 = new InvertedIndex(20001,30000);
		 InvertedIndex t4 = new InvertedIndex(30001,40000);
		 InvertedIndex t5 = new InvertedIndex(40001,50000);
		 InvertedIndex t6 = new InvertedIndex(50001,60000);
		 InvertedIndex t7 = new InvertedIndex(60001,70000);
		 
		 t1.setName("Thread1");
		 t2.setName("Thread2");
		 t3.setName("Thread3");
		 t4.setName("Thread4");
		 t5.setName("Thread5");
		 t6.setName("Thread6");
		 t7.setName("Thread7");
		 
		 
		 t1.start();
		 t2.start();
		 t3.start();
		 t4.start();
		 t5.start();
		 t6.start();
		 t7.start();
		 
		 
		
	}
}