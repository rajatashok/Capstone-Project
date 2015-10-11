import java.net.*;
import java.io.*;
import java.util.*;

import cc.mallet.examples.TopicModel;
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
import cc.mallet.util.*;
import cc.mallet.types.*;
import cc.mallet.pipe.*;
import cc.mallet.pipe.iterator.*;
import cc.mallet.topics.*;

public class Projects {
    public static void main(String[] args) throws Exception {
        ArrayList urlList = new ArrayList();

//        Document mainDocument = Jsoup.connect("http://stackoverflow.com/search?tab=relevance&pagesize=50&q=facebook%20api").timeout(0).userAgent("Mozilla").get();
//        Elements links = mainDocument.select("a[href]");
//        String pattern = "^http://stackoverflow.com/questions/[0-9]+/";

        // Create a Pattern object
//        Pattern regex = Pattern.compile(pattern);
//        for (Element src : links) {
//            Matcher m = regex.matcher(src.attr("abs:href"));
//            if( m.find() ) {
//                urlList.add(src.attr("abs:href"));
//                System.out.println(src.tagName() + "\t" + src.attr("abs:href"));
//            }
//        }

//        for(int i=0; i<urlList.size(); i++) {
        ArrayList datePosted = new ArrayList();
        ArrayList usernames = new ArrayList();
        ArrayList userReputation = new ArrayList();
        ArrayList posts = new ArrayList();

//        Document doc = Jsoup.connect("http://stackoverflow.com/questions/4490439/facebook-api-search").timeout(0).userAgent("Mozilla").get();
//            Document doc = Jsoup.connect((String) urlList.get(i)).timeout(0).userAgent("Mozilla").get();
        Document doc = Jsoup.connect("http://stackoverflow.com/questions/4691782/facebook-api-error-191").timeout(0).userAgent("Mozilla").get();
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
//            Elements users = doc.select("div.user-details:has(a)");
        Elements users = doc.select("div.user-details");
        for (Element user : users) {
            System.out.println(user.select("a").text() + "   1.Reputation= " + user.select("span.reputation-score").text());
//                if(!user.hasAttr("a")) {
//                    System.out.println("null");
//                    continue;
//                }
            usernames.add(user.select("a").text());
            userReputation.add(user.select("span.reputation-score").text());
        }
        // Usernames for comments
        users = doc.select("div.comment-body a");
        for (Element user : users) {
            if (user.hasAttr("class")) {
                System.out.println(user.select("a").text() + "   2.Reputation= " + user.select("a").attr("title"));
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

//            if (usernames.size() > posts.size()) {
//                usernames.remove(0);
//                datePosted.remove(0);
//            }
        // Database connection and insert
        MongoClient mongoClient = new MongoClient("localhost");
        List<String> databases = mongoClient.getDatabaseNames();
        DB db = mongoClient.getDB("local");
        DBCollection collection = db.getCollection("users");

        System.out.println("posts.size()= " + posts.size());
        System.out.println("datePosted.size()= " + datePosted.size());
        System.out.println("usernames.size()= " + usernames.size());

        // Removing edited values
        Elements edited = doc.select("div.user-action-time:has(a)");
        for (Element editedLines : edited) {
//                System.out.println(editedLines.select("a").text().substring(0,6));
            if(editedLines.select("a").text().substring(0, 6).equals("edited")) {
                System.out.println(editedLines.select("span").attr("title"));
                for (int j=0; j<datePosted.size(); j++) {
                    if((datePosted.get(j)).equals(editedLines.select("span").attr("title"))) {
                        datePosted.remove(j);
                        usernames.remove(j);
                        break;
                    }
                }
            }
        }

        for (int j = 0; j < usernames.size(); j++) {
            BasicDBObject document = new BasicDBObject("title", title.text()).append("posts", posts.get(j)).append("datePosted", datePosted.get(j)).append("user", usernames.get(j));
//                System.out.println(datePosted.get(j) + "x" + usernames.get(j) + "x" );
            collection.insert(document);
        }
//        }

        // Code to invoke LDA MALLET
        String[] files = new String[1];
        files[0] = "/home/rajat/IdeaProjects/Capstone-Project/src/ap.txt";
        TopicModel.main(files);

        PrintWriter writer2 = new PrintWriter("Output2.html", "UTF-8");
        writer2.println(doc);
        writer2.close();
    }
}