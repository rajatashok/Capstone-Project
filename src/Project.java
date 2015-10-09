import java.io.*;
import java.util.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Project {
    public static void main(String[] args) throws Exception {
	ArrayList urlList = new ArrayList();

	Document doc = Jsoup.connect("http://stackoverflow.com/search?tab=relevance&pagesize=50&q=Amazon%20S3%20API").timeout(0).userAgent("Mozilla").get();
	PrintWriter writer2 = new PrintWriter("Output.html", "UTF-8");
	writer2.println(doc);

        Elements links = doc.select("a[href]");
        Elements media = doc.select("[src]");
        Elements imports = doc.select("link[href]");
	
	String pattern = "^http://stackoverflow.com/questions/[0-9]+/";

	// Create a Pattern object
	Pattern regex = Pattern.compile(pattern);

	for (Element src : links) {
       //   System.out.println(src.attr("abs:href"));
	  Matcher m = regex.matcher(src.attr("abs:href"));
	  if( m.find() ) {
		  urlList.add(src.attr("abs:href"));
	          System.out.println(src.tagName() + "\t" + src.attr("abs:href"));
	  }
	}

	PrintWriter writer = new PrintWriter("OutputAmazon.txt", "UTF-8");
	for (Object url : urlList) {
		doc = Jsoup.connect(url.toString()).timeout(0).userAgent("Mozilla").get();
		//System.out.println(doc);		
		links = doc.select("a[href]");
		media = doc.select("[src]");

		Elements text = doc.select("p");
		for(Element p : text) {
		    writer.println(p.text());
		}
	}
	writer.close(); 
   }
}
