package src;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Brent on 21-Mar-17.
 */
public class DataImporter {
    private BufferedReader br;

    public DataImporter(String fileName) throws IOException {
        try {
            File file = new File(fileName);
            br = new BufferedReader((new FileReader(file)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Relation getRelArray() throws IOException {
        List<List<Integer>> relArrayList = new ArrayList<List<Integer>>();

        // While we have not reached the end of file
        while (br.ready() == true) {
            String line = br.readLine();
            // Ignore comment lines that start with #
            if (line.charAt(0) != '#') {
                // Split on space
                String[] split = line.split("\\s+");
                int fromEdge = Integer.parseInt(split[0]);
                int toEdge = Integer.parseInt(split[1]);
                relArrayList.add(Arrays.asList(fromEdge, toEdge));
            }
        }

        // Convert Relation ArrayList to Array
        int[][] relArray = new int[relArrayList.size()][];
        for (int i = 0; i < relArrayList.size(); i++) {
            relArray[i] = new int[relArrayList.get(i).size()];
            for (int j = 0; j < relArrayList.get(i).size(); j++) {
                relArray[i][j] = relArrayList.get(i).get(j);
            }
        }

        Relation result = new Relation(relArray);
        return result;
    }

    // Main method for testing purposes
    public static void main(String[] args) throws IOException {
        DataImporter di = new DataImporter("./data/test.txt");
        Relation relation = di.getRelArray();
    }
}
