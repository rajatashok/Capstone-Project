import cc.mallet.examples.TopicModel;
import com.mongodb.*;
import org.bson.BSONObject;
import org.bson.types.ObjectId;

import java.io.*;
import java.util.*;

/**
 * Created by rajat on 10/21/15.
 */
public class LDA {
    public static void main(String[] args) throws Exception {
//        storeOriginalUserReputation();
        generateUserRanking();
//        generateSentimentScore();
//        generatePostScore();
//        generateTopicSentimentScore();
//        analyzeTopicSentiments();
        System.exit(0);

        // Code to invoke LDA MALLET
        String l, line;

        HashSet stopWordsHash = new HashSet();
        BufferedReader reader = new BufferedReader(new FileReader("/home/rajat/IdeaProjects/Capstone-Project/src/stopwords.txt"));
        while ((line = reader.readLine()) != null) {
            stopWordsHash.add(line);
        }
        System.out.println(stopWordsHash);

//        String[] files = new String[1];
//        files[0] = "/home/rajat/IdeaProjects/Capstone-Project/src/ap.txt";
//        TopicModel.main(files);

//        Process p = Runtime.getRuntime().exec(new String[]{"bash", "-c", "/home/rajat/Desktop/mallet-2.0.8RC2/bin/mallet train-topics --input /home/rajat/IdeaProjects/Capstone-Project/src/ap2.txt --num-topics 100 --output-state topic-state.gz\n"});
//        Process p = Runtime.getRuntime().exec(new String[]{"bash", "-c", "/home/rajat/Desktop/mallet-2.0.8RC2/bin/mallet import-file --input /home/rajat/IdeaProjects/Capstone-Project/src/ap.txt --output opt.txt"});
//        Process p = Runtime.getRuntime().exec(new String[]{"bash", "-c", "/home/rajat/Desktop/mallet-2.0.8RC2/bin/mallet import-file --input /home/rajat/IdeaProjects/Capstone-Project/src/ap.txt --output opt.txt --keep-sequence --remove-stopwords"});

        Process p = Runtime.getRuntime().exec(new String[]{"bash", "-c", "/home/rajat/Desktop/mallet-2.0.8RC2/bin/mallet import-file --print-output --input /home/rajat/IdeaProjects/Capstone-Project/src/ldaInput.txt --output web.mallet --keep-sequence --remove-stopwords --stoplist-file /home/rajat/IdeaProjects/Capstone-Project/src/stopwords.txt"});
        Process p2 = Runtime.getRuntime().exec(new String[]{"bash", "-c", "/home/rajat/Desktop/mallet-2.0.8RC2/bin/mallet train-topics --input /home/rajat/IdeaProjects/Capstone-Project/web.mallet --num-topics 30 --num-top-words 1 --num-iterations 100 --output-doc-topics outputdoctopics.txt --output-topic-keys outputtopickeys.txt"});


//        Needed because the first time program is executed, read happens before writing above process
//        try {
//            Thread.sleep(2000);
//        } catch(InterruptedException ex) {
//            Thread.currentThread().interrupt();
//        }

        BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()) );
        while ((line = in.readLine()) != null) {
            System.out.println(line);
        }
        in.close();

        BufferedReader br = new BufferedReader(new FileReader("outputtopickeys.txt"));
        HashMap<Integer,String> hmap = new HashMap<Integer,String>();
        while ((line = br.readLine()) != null) {
            String[] str = line.split("\t");
            hmap.put(Integer.parseInt(str[0]),str[2]);
        }
        System.out.println(hmap);

        MongoClient mongoClient = new MongoClient("localhost");
        DB db = mongoClient.getDB("local");
        DBCollection collection = db.getCollection("data");

        br = new BufferedReader(new FileReader("outputdoctopics.txt"));
        while ((line = br.readLine()) != null) {
            String[] str = line.split("\t");
            float max=0;
            int maxIndex=-1;
            for(int i=2; i<str.length; i++) {
                if(Float.parseFloat(str[i])>max && !stopWordsHash.contains(hmap.get(i-2).replace(" ",""))) {
                    max = Float.parseFloat(str[i]);
                    maxIndex = i;
                }
            }
            System.out.println(str[0] + "  " + maxIndex + "  " + max + "  hmap= " + hmap.get(maxIndex-2));

            BasicDBObject query = new BasicDBObject();
            query.put("_id", new ObjectId(str[1]));

            BasicDBObject update = new BasicDBObject();
            update.put("$set", new BasicDBObject("topic",hmap.get(maxIndex-2).replace(" ","")));

            collection.update(query, update);

//            collection.update( { _id: ObjectId("561fdaa21c48820ce883921a") },{$set: {"topic": "xyz"}} )

        }

        in = new BufferedReader(new InputStreamReader(p2.getErrorStream()));
        while ((line = in.readLine()) != null) {
            System.out.println(line);
        }
        in.close();

        /*
        TO GENERATE SENTIMENT AND STORE IN outputFile.txt
        Process p4 = Runtime.getRuntime().exec(new String[]{"bash", "-c", "cd /home/rajat/Desktop/stanford-corenlp-full-2015-04-20/; java -cp \"*\" -mx5g edu.stanford.nlp.sentiment.SentimentPipeline -file /home/rajat/IdeaProjects/Capstone-Project/src/ldaInput.txt > /home/rajat/IdeaProjects/Capstone-Project/src/outputFile.txt"});
        java -cp \"*\" -mx5g edu.stanford.nlp.sentiment.SentimentPipeline -file /home/rajat/IdeaProjects/Capstone-Project/src/ldaInput.txt > outputlatentdirichlet.txt
        BufferedReader b = new BufferedReader(new InputStreamReader(p4.getErrorStream()));
        while(b.readLine()!=null) {System.out.println(b.readLine());}
         */


//        BufferedReader br2 = new BufferedReader(new FileReader("outputdoctopics.txt"));
//        while ((line = br.readLine()) != null) {
//            String[] str = line.split("\t");
//        }


//        Process p3 = Runtime.getRuntime().exec(new String[]{"bash", "-c", "cd /home/rajat/Desktop/stanford-corenlp-full-2015-04-20/; java -cp \"*\" -mx5g edu.stanford.nlp.sentiment.SentimentPipeline -stdin"});
        // Run one line at a time
        //Process p3 = Runtime.getRuntime().exec(new String[]{"bash", "-c", "cd /home/rajat/Desktop/stanford-corenlp-full-2015-04-20/; echo " + "Thanks! How to fix this problem! - goo.gl/QTdm4" +"| java -cp \"*\" -mx5g edu.stanford.nlp.sentiment.SentimentPipeline -stdin"});
    }

    public static void generateSentimentScore() throws IOException {
        String line="";
        // HashMap
        BufferedReader br = new BufferedReader(new FileReader("/home/rajat/IdeaProjects/Capstone-Project/src/ldaInput.txt"));
        HashSet<String> hmap2 = new HashSet<String>();
        while ((line = br.readLine()) != null) {
            String[] str = line.split("\t");
            hmap2.add(str[0]);
        }
        System.out.println("hmap done");
        //        System.out.println(hmap2);
        br = new BufferedReader(new FileReader("/home/rajat/IdeaProjects/Capstone-Project/src/outputFile.txt"));
        int positive = 0, negative = 0;

        MongoClient mongoClient = new MongoClient("localhost");
        DB db = mongoClient.getDB("local");
        DBCollection collection = db.getCollection("data");

        String currentKey = br.readLine().split("\t")[0];
        while ((line = br.readLine()) != null) {
            String key = line.split("\t")[0];

//            System.out.println("key= " + key + "  " + (positive-negative));
            if (hmap2.contains(key)) {
                System.out.println("Found key= " + key + "  CurrentKey=" + currentKey + "  " + (positive-negative));
                int sentiment = 0;
                if (positive - negative < -1) {
                    sentiment = -2;
                } else if (positive - negative < 0 && positive - negative >= -1) {
                    sentiment = -1;
                } else if (positive - negative == 0) {
                    sentiment = 0;
                } else if (positive - negative > 0 && positive - negative <= 1) {
                    sentiment = 1;
                } else if (positive - negative > 1) {
                    sentiment = 2;
                }

                BasicDBObject query = new BasicDBObject();
                query.put("_id", new ObjectId(currentKey));
                BasicDBObject update = new BasicDBObject();
                update.put("$set", new BasicDBObject("Sentiment Score", sentiment));
                collection.update(query, update);

                currentKey = key;
                positive = 0;
                negative = 0;
            }
            if (line.indexOf("Very Positive") >= 0) {
                positive = positive + 2;
            } else if (line.indexOf("Positive") >= 0) {
                positive++;
            } else if (line.indexOf("Very Negative") >= 0) {
                negative = negative + 2;
            } else if (line.indexOf("Negative") >= 0) {
                negative++;
            }
            else if (line.indexOf("?") >= 0) {
                negative--;
            }
        }

        // Sentiment Output for last key
        int sentiment = 0;
        if (positive - negative < -1) {
            sentiment = -2;
        } else if (positive - negative < 0 && positive - negative >= -1) {
            sentiment = -1;
        } else if (positive - negative == 0) {
            sentiment = 0;
        } else if (positive - negative > 0 && positive - negative <= 1) {
            sentiment = 1;
        } else if (positive - negative > 1) {
            sentiment = 2;
        }
        BasicDBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(currentKey));
        BasicDBObject update = new BasicDBObject();
        update.put("$set", new BasicDBObject("Sentiment Score", sentiment));
        collection.update(query, update);
    }

    public static void generatePostScore() {
        MongoClient mongoClient = new MongoClient("localhost");
        DB db = mongoClient.getDB("local");
//        DBCollection collection = db.getCollection("data");
        DBCollection collection = db.getCollection("updateReputationDb");

        BasicDBObject query = new BasicDBObject();
        BasicDBObject fields = new BasicDBObject();
        fields.put("$exists", 1);
        query.put("Sentiment Score", fields);
        query.put("datePosted", fields);

        // Performing this query:  db.data.find({"Sentiment Score": { $exists: 1}},{});
        DBCursor cursor = collection.find(query);
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        while (cursor.hasNext()) {
            BasicDBObject obj = (BasicDBObject) cursor.next();
            System.out.println(obj.getString("_id"));
            if(obj.getString("datePosted").length()<4) {
                continue;
            }
            if ( (obj.getString("datePosted").charAt(0)!='1' && obj.getString("datePosted").charAt(0)!='2')) {
                continue;
            }
            int postAge = currentYear - Integer.parseInt(obj.getString("datePosted").substring(0,4)) + 1;
            System.out.println("\n" + obj.get("reputation") + "  " + obj.get("top%rank") + "  " + obj.get("upvotes") + "  " + obj.get("goldBadges") + "  " + obj.get("silverBadges") + "  " + obj.get("bronzeBadges") + "  " + postAge);
            System.out.println("Post Score = " +  (Float.parseFloat(obj.get("reputation").toString())/Float.parseFloat(obj.get("top%rank").toString()) + Float.parseFloat(obj.get("upvotes").toString())+ 2*(Float.parseFloat(obj.get("goldBadges").toString())) + Float.parseFloat(obj.get("silverBadges").toString()) + (0.5 * Float.parseFloat(obj.get("bronzeBadges").toString()))));
            float postScore = (float)((Float.parseFloat(obj.get("reputation").toString())/Float.parseFloat(obj.get("top%rank").toString()) + Float.parseFloat(obj.get("upvotes").toString())+ 2*(Float.parseFloat(obj.get("goldBadges").toString())) + Float.parseFloat(obj.get("silverBadges").toString()) + (0.5 * Float.parseFloat(obj.get("bronzeBadges").toString())))/postAge);
            float totalSentimentScore = postScore*Float.parseFloat(obj.get("Sentiment Score").toString());
//            System.out.println("Post Score = " +  (Float.parseFloat(obj.get("reputation").toString())/Float.parseFloat(obj.get("top%rank").toString()) + Float.parseFloat(obj.get("upvotes").toString())+ 2*(Float.parseFloat(obj.get("goldBadges").toString())) + Float.parseFloat(obj.get("silverBadges").toString()) + (0.5 * Float.parseFloat(obj.get("bronzeBadges").toString())))/postAge);
            System.out.println("Post Score = " +  postScore + "   Sentiment Value= " + Float.parseFloat(obj.get("Sentiment Score").toString()) + "  TotalSentimentScore= " + totalSentimentScore);


            BasicDBObject updateQuery = new BasicDBObject();
            updateQuery.put("_id", obj.get("_id"));
            BasicDBObject updateScore = new BasicDBObject();
            updateScore.put("$set", new BasicDBObject("PostScore", postScore));
            collection.update(updateQuery, updateScore);
            updateScore.put("$set", new BasicDBObject("TotalSentimentScore", totalSentimentScore));
            collection.update(updateQuery, updateScore);

        }
    }

    public static void generateTopicSentimentScore() {
        MongoClient mongoClient = new MongoClient("localhost");
        DB db = mongoClient.getDB("local");
        DBCollection collection = db.getCollection("data");

        // Performing this query:  db.data.distinct("topic");
        List topicsList = collection.distinct("topic");
        for(int i=0; i<topicsList.size(); i++) {

            BasicDBObject query = new BasicDBObject();
            BasicDBObject fields = new BasicDBObject();
            query.put("topic", topicsList.get(i));

            // Performing this query:  db.data.find({"Sentiment Score": { $exists: 1}},{});
            DBCursor cursor = collection.find(query);

            System.out.println(topicsList.get(i));
        }
    }

    public static void analyzeTopicSentiments() {
        MongoClient mongoClient = new MongoClient("localhost");
        DB db = mongoClient.getDB("local");
//        DBCollection collection = db.getCollection("data");
        DBCollection collection = db.getCollection("updateReputationDb");

        // db.data.find({ WebService: "Amazon S3 API", topic: "server", TotalSentimentScore: { $eq: 0 } }).count();
        BasicDBObject query = new BasicDBObject();
        query.put("WebService", "Amazon S3 API");
        List webServiceslist = collection.distinct("WebService");
        List list = collection.distinct("topic");

        for(int j=0; j<webServiceslist.size(); j++) {
            System.out.println("\n\n" + webServiceslist.get(j) + "  Total:");
            query.put("WebService", webServiceslist.get(j));
            // Total sentiments
            // Neutral
            BasicDBObject fields = new BasicDBObject();
            fields.put("$eq", 0);
            query.put("Sentiment Score", fields);
            System.out.print("\t" + collection.find(query).count());
            //Positive
            fields.remove("$eq");
            fields.put("$gt", 0);
            query.put("Sentiment Score", fields);
            System.out.print("\t" + collection.find(query).count());
            // Negative
            fields.remove("$gt");
            fields.put("$lt", 0);
            query.put("Sentiment Score", fields);
            System.out.print("\t" + collection.find(query).count());
            fields.remove("$lt");
            query.remove("Sentiment Score");

            for(int i=0; i<list.size(); i++) {
                query.put("topic", list.get(i));
//                System.out.println("\nTopic= " + list.get(i));
                System.out.print("\n" + list.get(i));

                // Sentiment neutral
//                BasicDBObject fields = new BasicDBObject();
                fields.put("$eq", 0);
                query.put("TotalSentimentScore", fields);
                System.out.print("\t" + collection.find(query).count());
//                System.out.print("Neutral= " + collection.find(query).count());

                // Sentiment positive
                fields.remove("$eq");
                fields.put("$gt", 0);
                query.put("TotalSentimentScore", fields);
                System.out.print("\t" + collection.find(query).count());
//                System.out.println("Positive= " + collection.find(query).count());

                // Sentiment negative
                fields.remove("$gt");
                fields.put("$lt", 0);
                query.put("TotalSentimentScore", fields);
                System.out.print("\t" + collection.find(query).count());
                fields.remove("$lt");
//                System.out.println("Negative= " + collection.find(query).count());
            }
            query.clear();
        }

    }

    public static void storeOriginalUserReputation() {
        MongoClient mongoClient = new MongoClient("localhost");
        DB db = mongoClient.getDB("local");
        DBCollection collection = db.getCollection("data");
        DBCollection collectionUsers = db.getCollection("users");

        DBCursor cursor = collection.find();
        while (cursor.hasNext()) {
            BasicDBObject obj = (BasicDBObject) cursor.next();
            BasicDBObject update = new BasicDBObject();
            BasicDBObject query = new BasicDBObject();
            query.put("user",obj.get("user"));

            update.put("user",obj.get("user"));
            update.put("reputation",obj.get("reputation"));

            collectionUsers.update(query, update, true, false);
        }

    }
    public static void generateUserRanking() {
        // db.data.find({WebService: "Amazon S3 API", owner: 1}).pretty();

        MongoClient mongoClient = new MongoClient("localhost");
        DB db = mongoClient.getDB("local");
        DBCollection collection = db.getCollection("updateReputationDb");
        DBCollection collectionUsers = db.getCollection("users");

        HashMap hashMap = new HashMap();

        BasicDBObject query = new BasicDBObject();
        query.put("WebService", "Amazon S3 API");
        // Getting all users for that WebService and storing in HashMap
        DBCursor cursor = collection.find(query);
        while (cursor.hasNext()) {
            BasicDBObject obj = (BasicDBObject) cursor.next();
            hashMap.put(obj.get("user"), Integer.parseInt(obj.get("reputation").toString()));
        }
//        System.out.println(hashMap);
        sortHashMap(hashMap);

        System.out.println(hashMap.size());
        query.put("owner", 1);
        cursor = collection.find(query);
        while (cursor.hasNext()) {
            BasicDBObject obj = (BasicDBObject) cursor.next();
            String title = obj.get("title").toString();
//            System.out.print(obj.get("user") + "  " + obj.get("title"));
            System.out.print(obj.get("user") + "  ");
            query.put("owner", 0);
            query.put("title", obj.get("title"));
            DBCursor cursor2 = collection.find(query);

//            Points of User x = Reputation of Y * (Number of answers by X to Y / Total number of answers received by Y)
//            New Reputation of User x = Reputation of user x + Points accumulated

            int ownerReputation = Integer.parseInt(obj.get("reputation").toString());
            while (cursor2.hasNext()) {
                BasicDBObject obj2 = (BasicDBObject) cursor2.next();
                int userPoints = ownerReputation / cursor2.size();
                hashMap.put(obj2.get("user"), Integer.parseInt(hashMap.get(obj2.get("user")).toString()) + userPoints);

//                System.out.print(obj2.get("user") + "\t");
            }
//            System.out.println();
        }
        System.out.println("\nUpdating DB");
        Iterator iterator = hashMap.entrySet().iterator();
        while(iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();

            BasicDBObject userQuery = new BasicDBObject();
            userQuery.put("user",entry.getKey());
            userQuery.put("WebService","Amazon S3 API");

            BasicDBObject updateUserRep = new BasicDBObject();
            updateUserRep.put("$set", new BasicDBObject("reputation", entry.getValue()));

            collection.updateMulti(userQuery, updateUserRep);

        }

        sortHashMap(hashMap);
    }
    public static void sortHashMap(HashMap hashMap) {
        // Sorting hashMap
        List list = new LinkedList(hashMap.entrySet());
        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o2)).getValue())
                        .compareTo(((Map.Entry) (o1)).getValue());
            }
        });

        Map result = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry)it.next();
            result.put(entry.getKey(), entry.getValue());
        }
        System.out.println("\n" + result);
//        System.out.println("\n" + hashMap);
    }
}
