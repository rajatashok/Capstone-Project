import java.net.*;
import java.io.*;
import java.util.*;
import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Projects {
    public static void main(String[] args) throws Exception {
	ArrayList urlList = new ArrayList();

	Document doc = Jsoup.connect("http://stackoverflow.com/questions/4490439/facebook-api-search").timeout(0).userAgent("Mozilla").get();

	// Get Title
	Elements title = doc.select("div.container div.snippet-hidden div div h1 a.question-hyperlink");
	System.out.println(title.text());

	// Get Time
	// Time for question and answers
	Elements time = doc.select("span.relativetime");
	for (Element t : time) {
		System.out.println(t.attr("title"));
	}
	// Time for comments
	time = doc.select("span.relativetime-clean");
        for (Element t : time) {
		System.out.println(t.attr("title"));
	}

	// Get usernames
	Elements users = doc.select("div.user-details");
	for (Element user : users) {
		System.out.println(user.select("a").text() + "   Reputation= " + user.select("span.reputation-score").text());
	}
    // Usernames for comments
	users = doc.select("div.comment-body a");
  	for (Element user : users) {
		if(user.hasAttr("class")) {
			System.out.println(user.select("a").text() + "   Reputation= " + user.select("a").attr("title"));
		}
	}

    // Get text
    Elements text = doc.select("div.post-text");
    for (Element div : text) {
        System.out.println();
        Elements p = div.select("p");
        System.out.println(p.text());
    }

    text = doc.select("span.comment-copy");
    for (Element lines : text) {
        System.out.println();
        System.out.println(lines.text());
    }

	PrintWriter writer2 = new PrintWriter("Output.html", "UTF-8");
	writer2.println(doc);
	writer2.close(); 
   }
}
