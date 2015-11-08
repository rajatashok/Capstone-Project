import cc.mallet.examples.TopicModel;
import com.mongodb.*;
import org.bson.types.ObjectId;

import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Created by rajat on 10/21/15.
 */
public class LDA {
    public static void main(String[] args) throws Exception {
        // Code to invoke LDA MALLET
        String l,line;
/*        String[] files = new String[1];
        files[0] = "/home/rajat/IdeaProjects/Capstone-Project/src/ap.txt";
//        TopicModel.main(files);

//        Process p = Runtime.getRuntime().exec(new String[]{"bash", "-c", "/home/rajat/Desktop/mallet-2.0.8RC2/bin/mallet train-topics --input /home/rajat/IdeaProjects/Capstone-Project/src/ap2.txt --num-topics 100 --output-state topic-state.gz\n"});
//        Process p = Runtime.getRuntime().exec(new String[]{"bash", "-c", "/home/rajat/Desktop/mallet-2.0.8RC2/bin/mallet import-file --input /home/rajat/IdeaProjects/Capstone-Project/src/ap.txt --output opt.txt"});
//        Process p = Runtime.getRuntime().exec(new String[]{"bash", "-c", "/home/rajat/Desktop/mallet-2.0.8RC2/bin/mallet import-file --input /home/rajat/IdeaProjects/Capstone-Project/src/ap.txt --output opt.txt --keep-sequence --remove-stopwords"});

        Process p = Runtime.getRuntime().exec(new String[]{"bash", "-c", "/home/rajat/Desktop/mallet-2.0.8RC2/bin/mallet import-file --print-output --input /home/rajat/IdeaProjects/Capstone-Project/src/ap.txt --output web.mallet --keep-sequence --remove-stopwords --stoplist-file src/stopwords.txt"});
        Process p2 = Runtime.getRuntime().exec(new String[]{"bash", "-c", "/home/rajat/Desktop/mallet-2.0.8RC2/bin/mallet train-topics --input /home/rajat/IdeaProjects/Capstone-Project/web.mallet --num-topics 20 --num-top-words 1 --num-iterations 100 --output-doc-topics outputdoctopics.txt --output-topic-keys outputtopickeys.txt"});


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

        br = new BufferedReader(new FileReader("outputdoctopics.txt"));
        while ((line = br.readLine()) != null) {
            String[] str = line.split("\t");
            float max=0;
            int maxIndex=-1;
            for(int i=2; i<str.length; i++) {
                if(Float.parseFloat(str[i])>max) {
                    max = Float.parseFloat(str[i]);
                    maxIndex = i;
                }
            }
            System.out.println(str[0] + "  " + maxIndex + "  " + max);


            MongoClient mongoClient = new MongoClient("localhost");
            DB db = mongoClient.getDB("local");
            DBCollection collection = db.getCollection("users");

            BasicDBObject query = new BasicDBObject();
            query.put("_id", new ObjectId(str[1]));

            BasicDBObject update = new BasicDBObject();
            update.put("$set", new BasicDBObject("topic",hmap.get(maxIndex-2)));

            collection.update(query, update);
//            collection.update( { _id: ObjectId("561fdaa21c48820ce883921a") },{$set: {"topic": "xyz"}} )

        }

        in = new BufferedReader(new InputStreamReader(p2.getErrorStream()));
        while ((line = in.readLine()) != null) {
            System.out.println(line);
        }
        in.close();
*/
        Process p4 = Runtime.getRuntime().exec(new String[]{"bash", "-c", "cd /home/rajat/Desktop/stanford-corenlp-full-2015-04-20/; java -cp \"*\" -mx5g edu.stanford.nlp.sentiment.SentimentPipeline -file /home/rajat/IdeaProjects/Capstone-Project/src/ldaInput.txt > /home/rajat/IdeaProjects/Capstone-Project/src/outputFile.txt"});
        BufferedReader b = new BufferedReader(new InputStreamReader(p4.getErrorStream()));
        while(b.readLine()!=null) {System.out.println(b.readLine());}
/*
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
        int positive=0,negative=0;


        MongoClient mongoClient = new MongoClient("localhost");
        DB db = mongoClient.getDB("local");
        DBCollection collection = db.getCollection("data");

        String currentKey = br.readLine().split("\t")[0];
        while((line = br.readLine())!=null) {
            String key = line.split("\t")[0];

//            System.out.println("key= " + key + "  " + (positive-negative));
            if(hmap2.contains(key)) {
//                System.out.println("Found key= " + key + "  CurrentKey=" + currentKey + "  " + (positive-negative));

                int sentiment=0;
                if(positive-negative<-1) {
                    sentiment=-2;
                }
                else if(positive-negative<0 && positive-negative>=-1) {
                    sentiment=-1;
                }
                else if(positive-negative==0) {
                    sentiment=0;
                }
                else if(positive-negative>0 && positive-negative<=1) {
                    sentiment=1;
                }
                else if(positive-negative>1) {
                    sentiment=2;
                }

                BasicDBObject query = new BasicDBObject();
                query.put("_id", new ObjectId(currentKey));
                BasicDBObject update = new BasicDBObject();
                update.put("$set", new BasicDBObject("Sentiment Score",sentiment));
                collection.update(query, update);

                currentKey = key;
                positive=0;
                negative=0;
            }
            if(line.indexOf("Very Positive")>=0) {
                positive = positive+2;
            }
            else if(line.indexOf("Positive")>=0) {
                positive++;
            }
            else if(line.indexOf("Very Negative")>=0) {
                negative = negative+2;
            }
            else if(line.indexOf("Negative")>=0) {
                negative++;
            }
        }
        */

//        BufferedReader br2 = new BufferedReader(new FileReader("outputdoctopics.txt"));
//        while ((line = br.readLine()) != null) {
//            String[] str = line.split("\t");
//        }


//        Process p3 = Runtime.getRuntime().exec(new String[]{"bash", "-c", "cd /home/rajat/Desktop/stanford-corenlp-full-2015-04-20/; java -cp \"*\" -mx5g edu.stanford.nlp.sentiment.SentimentPipeline -stdin"});

        // Run one line at a time
        //Process p3 = Runtime.getRuntime().exec(new String[]{"bash", "-c", "cd /home/rajat/Desktop/stanford-corenlp-full-2015-04-20/; echo " + "Thanks! How to fix this problem! - goo.gl/QTdm4" +"| java -cp \"*\" -mx5g edu.stanford.nlp.sentiment.SentimentPipeline -stdin"});


//        Process p4 =Runtime.getRuntime().exec(new String[]{"bash", "-c", "cut -f3 /home/rajat/IdeaProjects/Capstone-Project/src/ap.txt"});
//        in = new BufferedReader(new InputStreamReader(p4.getInputStream()) );
//        while ((line = in.readLine()) != null) {
//            Process p3 = new ProcessBuilder(new String[]{"bash", "-c", "cd /home/rajat/Desktop/stanford-corenlp-full-2015-04-20/; java -cp \"*\" -mx5g edu.stanford.nlp.sentiment.SentimentPipeline -stdin"}).start();
//            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(p3.getOutputStream()));
//            out.write(line);
//            out.flush();
//            out.close();
//
//            System.out.println(line);

//        }
//        in.close();

    }
}
