import java.net.*;
import java.io.*;
import java.util.*;

import cc.mallet.examples.TopicModel;
import com.mongodb.*;
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
        HashMap<String, String[]> hashmap = new HashMap<String, String[]>();

        // Getting List of Popular APIs
        Document programmableWeb = Jsoup.connect("http://www.programmableweb.com/category/all/apis?order=field_popularity").timeout(0).userAgent("Mozilla").get();
        Elements apiList = programmableWeb.getElementsByClass("views-field-title");
//        for (Element apis : apiList.select("a")) {
        //   System.out.println(apis.text());

        //  Document mainDocument = Jsoup.connect("http://stackoverflow.com/search?tab=relevance&pagesize=50&q=facebook%20api").timeout(0).userAgent("Mozilla").get();
//            Document mainDocument = Jsoup.connect("http://stackoverflow.com/search?tab=relevance&pagesize=50&q=" + apis.text().replace(" ","+")).timeout(0).followRedirects(false).userAgent("Mozilla").get();
        for (int pagenum = 1; pagenum <= 50; pagenum++) {
            System.out.println("Pagenum= " + pagenum);
            Document mainDocument = Jsoup.connect("http://stackoverflow.com/search?tab=relevance&pagesize=50&q=amazon+s3+api&page=" + pagenum).timeout(0).userAgent("Mozilla").get();
            Elements links = mainDocument.select("a[href]");
            String pattern = "^http://stackoverflow.com/questions/[0-9]+/";

            ArrayList urlList = new ArrayList();
            // Create a Pattern object
            Pattern regex = Pattern.compile(pattern);
            for (Element src : links) {
                Matcher m = regex.matcher(src.attr("abs:href"));
                if (m.find()) {
                    urlList.add(src.attr("abs:href"));
                    // System.out.println(src.tagName() + "\t" + src.attr("abs:href"));
                }
            }

            //Page 8
//            Title= SHA-1 in Amazon Api Authorization in Javascript
//            Title= How do I use AWS S3 to store user uploaded pictures?

            for (int i = 0; i < urlList.size(); i++) {
                ArrayList datePosted = new ArrayList();
                ArrayList upvoteCount = new ArrayList();
                ArrayList usernames = new ArrayList();
                ArrayList usernamesLink = new ArrayList();
                ArrayList posts = new ArrayList();
                ArrayList owner = new ArrayList();

//                        Document doc = Jsoup.connect("http://stackoverflow.com/questions/4490439/facebook-api-search").timeout(0).followRedirects(false).userAgent("Mozilla").get();
//                        Document doc = Jsoup.connect("http://stackoverflow.com/questions/1544739/google-maps-api-v3-how-to-remove-all-markers").timeout(0).followRedirects(false).userAgent("Mozilla").get();

//                Document doc = Jsoup.connect("http://stackoverflow.com/questions/10281557/signaturedoesnotmatch-amazon-s3-api").timeout(0).followRedirects(false).userAgent("Mozilla").get();
                Document doc = Jsoup.connect((String) urlList.get(i)).timeout(0).followRedirects(false).userAgent("Mozilla").get();
                // Get Title
                Elements title = doc.select("div.container div.snippet-hidden div div h1 a.question-hyperlink");
                System.out.println("Title= " + title.text());

                // Get details of posts and comments
                Elements details = doc.select("td.comment-text:has(a.comment-user)");
                // For comments
                for (Element element : details) {
                    usernames.add(element.select("a.comment-user").text());
                    upvoteCount.add(0);
                    owner.add(0);
                    datePosted.add(element.select("span.relativetime-clean").attr("title"));
                    posts.add(element.select("span.comment-copy").text());
                    String userurl = "";
                    Elements atag = element.select("a");
                    for (Element element2 : atag) {
                        if (element2.select("a").text().equals(element.select("a.comment-user").text())) {
                            usernamesLink.add("http://stackoverflow.com" + element2.select("a").attr("href"));
//                            userurl = element2.select("a").attr("href");
                            break;
                        }
                    }
//                    System.out.println(userurl + "  x" + element.select("a.comment-user").text()+ "x   " + element.select("span.relativetime-clean").attr("title")+ "   " + element.select("span.comment-copy").text());
                }

                // For posts
                details = doc.select("td.post-signature:has(div.user-details:has(a))");
                for (Element element : details) {
//                    System.out.println("details.attr(\"class\")= " + details.select("div.user-action-time").text());

                    if (element.select("div.user-action-time a").text().length() >= 6 && element.select("div.user-action-time a").text().substring(0, 6).equals("edited")) {
                        continue;
                    }
//                    System.out.println("div.user-action-time=  " + element.select("div.user-action-time").text());

                    // Setting Owner of post
                    if (element.select("div.user-action-time").text().length() >= 5 && element.select("div.user-action-time").text().substring(0, 5).equals("asked")) {
                        owner.add(1);
//                        System.out.println("Owner= " + element.select("div.user-details a").text());
//                        System.out.println("Href= http://stackoverflow.com/" + element.select("div.user-details a").attr("href"));

                    } else {
                        owner.add(0);
                    }
                    if (element.select("div.user-details a").text().indexOf("users") != -1 && element.select("div.user-details a").text().indexOf("revs") != -1) {
                        Document userRevisionsdoc = Jsoup.connect("http://stackoverflow.com" + element.select("a").attr("href")).timeout(0).followRedirects(false).userAgent("Mozilla").get();
                        datePosted.add(userRevisionsdoc.select("span.relativetime").get(0).text());
                        usernames.add(userRevisionsdoc.select("div.user-details a").get(0).text());
                        usernamesLink.add("http://stackoverflow.com" + element.select("div.user-details a").attr("href"));
//                        System.out.println("Href= http://stackoverflow.com/" + element.select("div.user-details a").attr("href"));
                        continue;
                    }
                    usernames.add(element.select("div.user-details a").text());
                    usernamesLink.add("http://stackoverflow.com" + element.select("div.user-details a").attr("href"));
                    datePosted.add(element.select("div.user-action-time span").attr("title"));
                    //  System.out.println(element.select("div.user-details a").text() + "    " + element.select("div.user-action-time span").attr("title"));
                }
                // Adding Post text
                Elements text = doc.select("div.post-text");
                for (Element div : text) {
                    Elements p = div.select("p");
                    posts.add(p.text());
                }
                // Adding upvotes for Posts
                Elements upvote = doc.select("span.vote-count-post");
                for (Element element : upvote) {
                    upvoteCount.add(element.text());
                }

                // Database connection and insert
                MongoClient mongoClient = new MongoClient("localhost");
                List<String> databases = mongoClient.getDatabaseNames();
                DB db = mongoClient.getDB("local");
                DBCollection collection = db.getCollection("data");

/*                PrintWriter writer2 = new PrintWriter("Output2.html", "UTF-8");
                writer2.println(doc);
                writer2.close();
*/
                for (int j = 0; j < usernames.size(); j++) {
                    String[] userDet = new String[5];
                    Document userDetailsDoc = Jsoup.connect("" + usernamesLink.get(j)).timeout(0).followRedirects(false).userAgent("Mozilla").get();
//                    Document userDetailsDoc = Jsoup.connect("http://stackoverflow.com/users/1919054/charles-engelke").timeout(0).followRedirects(false).userAgent("Mozilla").get();

//                    PrintWriter writer2 = new PrintWriter("UserOutput.html", "UTF-8");
//                    writer2.println(userDetailsDoc);
//                    writer2.close();
                    userDet[0] = (userDetailsDoc.select("div.badges").select("span.badge1-alternate").select("span.badgecount").text().length() == 0) ? "0" : userDetailsDoc.select("div.badges").select("span.badge1-alternate").select("span.badgecount").text();
                    userDet[1] = (userDetailsDoc.select("div.badges").select("span.badge2-alternate").select("span.badgecount").text().length() == 0) ? "0" : userDetailsDoc.select("div.badges").select("span.badge2-alternate").select("span.badgecount").text();
                    userDet[2] = (userDetailsDoc.select("div.badges").select("span.badge3-alternate").select("span.badgecount").text().length() == 0) ? "0" : userDetailsDoc.select("div.badges").select("span.badge3-alternate").select("span.badgecount").text();
                    userDet[3] = (userDetailsDoc.select("div.reputation").text().split(" ")[0]).length() == 0 ? "0" : userDetailsDoc.select("div.reputation").text().split(" ")[0].replace(",", "");
                    userDet[4] = ((userDetailsDoc.select("div.user-card").select("span.top-badge b").text()).length() == 0) ? "100" : userDetailsDoc.select("span.top-badge b").text().replace("%", "");

//                    System.out.println("b= " + userDetailsDoc.select("div.user-card").select("span.top-badge b").toString());

//                    System.out.println(usernamesLink.get(j) + "  " + usernames.get(j) + " upvotes= " + upvoteCount.get(j) + "  Gold= " + userDet[0] + "  Silver= " + userDet[1] + "  Bronze= " + userDet[2] + "  Reputation= " + userDet[3] + "  topRank= " + userDet[4]);
                    BasicDBObject document = new BasicDBObject("title", title.text()).append("posts", posts.get(j)).append("upvotes", upvoteCount.get(j)).append("datePosted", datePosted.get(j)).append("user", usernames.get(j)).append("goldBadges", userDet[0]).append("silverBadges", userDet[1]).append("bronzeBadges", userDet[2]).append("reputation", userDet[3]).append("top%rank", userDet[4]).append("owner", owner.get(j));
                    BasicDBObject update = new BasicDBObject();
                    update.put("$set", document);

                    BasicDBObject query = new BasicDBObject();
                    query.put("posts", posts.get(j));
                    query.put("title", title.text());

                    BasicDBObject upsert = new BasicDBObject();
                    upsert.put("$upsert", true);

//                    break;

                    collection.update(query, update, true, false);
//                    collection.insert(document);

                }

//                for (int j = 0; j < usernames.size(); j++) {
//                    BasicDBObject document = new BasicDBObject("title", title.text()).append("posts", posts.get(j)).append("upvotes", upvoteCount.get(j)).append("datePosted", datePosted.get(j)).append("user", usernames.get(j)).append("reputation",userReputation.get(j)).append("goldBadges",userDet[0]);
//                                    System.out.println(datePosted.get(j) + "x" + usernames.get(j) + "x" );
//                    collection.insert(document);
//                }
//                break;
            }
        }
//    }

        MongoClient mongoClient = new MongoClient("localhost");
        List<String> databases = mongoClient.getDatabaseNames();
        DB db = mongoClient.getDB("local");
        DBCollection collection = db.getCollection("data");
        DBObject dbObject = collection.findOne();
        List list = collection.distinct("title");


        BasicDBObject allQuery = new BasicDBObject();
        BasicDBObject fields = new BasicDBObject();
        fields.put("_id", 1);
        fields.put("posts", 1);

        DBCursor cursor = collection.find(allQuery, fields);
        PrintWriter writer2 = new PrintWriter("src/ap.txt", "UTF-8");
        while (cursor.hasNext()) {
            BasicDBObject obj = (BasicDBObject) cursor.next();
            writer2.println(obj.getString("_id") + "\tX\t" + obj.getString("posts"));
        }
        writer2.close();

    }
}