import cc.mallet.examples.TopicModel;

/**
 * Created by rajat on 10/21/15.
 */
public class LDA {
    public static void main(String[] args) throws Exception {
        // Code to invoke LDA MALLET
        String[] files = new String[1];
        files[0] = "/home/rajat/IdeaProjects/Capstone-Project/src/ap.txt";
        TopicModel.main(files);
    }
}
