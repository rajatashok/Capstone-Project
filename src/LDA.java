import cc.mallet.examples.TopicModel;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by rajat on 10/21/15.
 */
public class LDA {
    public static void main(String[] args) throws Exception {
        // Code to invoke LDA MALLET
        String line;
        String[] files = new String[1];
        files[0] = "/home/rajat/IdeaProjects/Capstone-Project/src/ap.txt";
//        TopicModel.main(files);

//        Process p = Runtime.getRuntime().exec(new String[]{"bash", "-c", "/home/rajat/Desktop/mallet-2.0.8RC2/bin/mallet train-topics --input /home/rajat/IdeaProjects/Capstone-Project/src/ap2.txt --num-topics 100 --output-state topic-state.gz\n"});
//        Process p = Runtime.getRuntime().exec(new String[]{"bash", "-c", "/home/rajat/Desktop/mallet-2.0.8RC2/bin/mallet import-file --input /home/rajat/IdeaProjects/Capstone-Project/src/ap.txt --output opt.txt"});
//        Process p = Runtime.getRuntime().exec(new String[]{"bash", "-c", "/home/rajat/Desktop/mallet-2.0.8RC2/bin/mallet import-file --input /home/rajat/IdeaProjects/Capstone-Project/src/ap.txt --output opt.txt --keep-sequence --remove-stopwords"});
        Process p = Runtime.getRuntime().exec(new String[]{"bash", "-c", "/home/rajat/Desktop/mallet-2.0.8RC2/bin/mallet import-file --print-output --input /home/rajat/IdeaProjects/Capstone-Project/src/ap.txt --output web.mallet --keep-sequence --remove-stopwords"});
//        /home/rajat/Desktop/mallet-2.0.8RC2/bin/mallet import-file --print-output --input /home/rajat/IdeaProjects/Capstone-Project/src/ap.txt --output web.mallet --keep-sequence --remove-stopwords
        BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()) );
        while ((line = in.readLine()) != null) {
            System.out.println(line);
        }
        in.close();

        in = new BufferedReader(new InputStreamReader(p.getErrorStream()) );
        while ((line = in.readLine()) != null) {
            System.out.println(line);
        }
        in.close();

    }
}
