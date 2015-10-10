import java.net.*;
import java.io.*;
import java.util.*;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
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
    ArrayList datePosted = new ArrayList();
    ArrayList usernames = new ArrayList();
    ArrayList userReputation = new ArrayList();
    ArrayList posts = new ArrayList();


        Document doc = Jsoup.connect("http://stackoverflow.com/questions/10281557/signaturedoesnotmatch-amazon-s3-api").timeout(0).userAgent("Mozilla").get();

    // Get Title
	Elements title = doc.select("div.container div.snippet-hidden div div h1 a.question-hyperlink");
	System.out.println(title.text());

    // Get Time
	// Time for question and answers
	Elements time = doc.select("span.relativetime");
	for (Element t : time) {
		System.out.println(t.attr("title"));
        datePosted.add(t.attr("title"));
	}
	// Time for comments
	time = doc.select("span.relativetime-clean");
    for (Element t : time) {
		System.out.println(t.attr("title"));
        datePosted.add(t.attr("title"));
	}

	// Get usernames
    // Usernames for posts
	Elements users = doc.select("div.user-details");
	for (Element user : users) {
		System.out.println(user.select("a").text() + "   Reputation= " + user.select("span.reputation-score").text());
        usernames.add(user.select("a").text());
        userReputation.add(user.select("span.reputation-score").text());
    }
    // Usernames for comments
	users = doc.select("div.comment-body a");
  	for (Element user : users) {
		if(user.hasAttr("class")) {
			System.out.println(user.select("a").text() + "   Reputation= " + user.select("a").attr("title"));
            usernames.add(user.select("a").text());
            userReputation.add(user.select("a").attr("title"));
		}
	}

    // Get text
    // Text for posts
    Elements text = doc.select("div.post-text");
    for (Element div : text) {
        System.out.println();
        Elements p = div.select("p");
        System.out.println(p.text());
        posts.add(p.text());
    }
    // Text for comments
    text = doc.select("span.comment-copy");
    for (Element lines : text) {
        System.out.println();
        System.out.println(lines.text());
        posts.add(lines.text());
    }

    // Database connection and insert
    MongoClient mongoClient = new MongoClient("localhost");
    List<String> databases = mongoClient.getDatabaseNames();
    DB db=mongoClient.getDB("local");;
    DBCollection collection=db.getCollection("users");
    for(int i=0; i<usernames.size(); i++) {
        BasicDBObject document = new BasicDBObject("title", title.text()).append("posts",posts.get(i)).append("datePosted", datePosted.get(i)).append("user",usernames.get(i));
        collection.insert(document);
    }

	PrintWriter writer2 = new PrintWriter("Output2.html", "UTF-8");
	writer2.println(doc);
	writer2.close(); 
   }
}