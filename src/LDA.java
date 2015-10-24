import cc.mallet.examples.TopicModel;
import com.mongodb.*;
import org.bson.types.ObjectId;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;

/**
 * Created by rajat on 10/21/15.
 */
public class LDA {
    public static void main(String[] args) throws Exception {
        // Code to invoke LDA MALLET
        String l,line;
        String[] files = new String[1];
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

    }
}
