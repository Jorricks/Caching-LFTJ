package src;

import java.io.*;
import java.util.*;

public class DataImporter {
    private BufferedReader br;
    private boolean reverseOrder;

    /**
     * Constructor of this class
     * @param fileName Path to the file
     * @param reverseOrder Specifies whether the relation is stored in its original order
     * @throws IOException If fileName is specified incorrectly
     */
    DataImporter(String fileName, boolean reverseOrder) throws IOException {
        this.reverseOrder = reverseOrder;
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
                int fromEdge;
                int toEdge;
                if (!reverseOrder) {
                    fromEdge = Integer.parseInt(split[0]);
                    toEdge = Integer.parseInt(split[1]);
                } else {
                    fromEdge = Integer.parseInt(split[1]);
                    toEdge = Integer.parseInt(split[0]);
                }
                relArrayList.add(Arrays.asList(fromEdge, toEdge));
            }
        }

        // Sort TreeRelation ArrayList
        relArrayList = sortRelArray(relArrayList);

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

    /**
     * Sort a Relation ArrayList
     * @param relArrayList specifies the unsorted Relation ArrayList to sort
     * @return Sorted Relation ArrayList
     */
    private List<List<Integer>> sortRelArray(List<List<Integer>> relArrayList) {
        Collections.sort(relArrayList,new Comparator<List<Integer>>() {
            @Override
            public int compare(List<Integer> l1, List<Integer> l2) {
                for (int i = 0; i < l1.size(); i++) {
                    // If the a1 element compared at index i is smaller, return -1
                    if (l1.get(i) < l2.get(i)) {
                        return -1;
                    }
                    // If the a1 element compared at index 1 is larger, return 1
                    else if (l1.get(i) > l2.get(i)) {
                        return 1;
                    }
                }
                return 0;
            }
        });
        return relArrayList;
    }

    // Main method for testing purposes
    public static void main(String[] args) throws IOException {
        DataImporter di = new DataImporter("./data/test.txt", true);
        TreeRelation relation = di.getRelArray();
    }
}
