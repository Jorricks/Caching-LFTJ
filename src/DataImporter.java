package src;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataImporter {
    private BufferedReader br;

    DataImporter(String fileName) throws IOException {
        try {
            File file = new File(fileName);
            br = new BufferedReader((new FileReader(file)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    TreeRelation getRelArray() throws IOException {
        List<List<Integer>> relArrayList = new ArrayList<>();

        // While we have not reached the end of file
        while (br.ready()) {
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

        // Convert TreeRelation ArrayList to Array
        int[][] relArray = new int[relArrayList.size()][];
        for (int i = 0; i < relArrayList.size(); i++) {
            relArray[i] = new int[relArrayList.get(i).size()];
            for (int j = 0; j < relArrayList.get(i).size(); j++) {
                relArray[i][j] = relArrayList.get(i).get(j);
            }
        }

        return new TreeRelation(relArray, true);
    }

    // Main method for testing purposes
    public static void main(String[] args) throws IOException {
        DataImporter di = new DataImporter("./data/test.txt");
        TreeRelation relation = di.getRelArray();
    }
}
