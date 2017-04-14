/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class LFTJ {

    int debug = 0; // Represents the amount of debugging. 0 = None, 3 = Extreme

    ArrayList<ArrayList<Integer>> result; // Result set: array with tuples
    ArrayList<ArrayList<RelationIterator<Integer>>> iteratorPerDepth; // Contains the iterator for each dept
    ArrayList<Integer> currentTuple = new ArrayList<>(); // Contains the key values of all parents
    int p = 0; // Current iterator pointer
    int numIters; // Number of iterators at this depth
    int depth = -1; // Current depth
    int maxDepth = 0; // How deep is our relation? R(x,y), T(y,z) yields 2.
    int key = 0; // Contains the key value when we find a matching search in leapfrogSearch.
    boolean atEnd; // Mirrors whether there is an iterator at his end.
    public enum CycleOrRoundsEnum { CYCLE, PATH } // Whether we want the relations to be a cycle or a path.

    long startTime, midTime, endTime; // Information about when the round started and finished.
    String resultCycleOrPath, resultAmountOfCycleorPath;

    /**
     * Constructor of this class
     */
    LFTJ(String dataSetPath, Enum CycleOrRounds, int amountOfPathOrCycle) throws IOException {
        startTime = System.nanoTime(); // Getting the time at the start

        if(CycleOrRounds == CycleOrRoundsEnum.CYCLE){ // Used for printing results.
            resultCycleOrPath = "Cycle";
        } else {
            resultCycleOrPath = "Path";
        }
        resultAmountOfCycleorPath = String.valueOf(amountOfPathOrCycle);

        initDataSets(dataSetPath, CycleOrRounds, amountOfPathOrCycle);
        result = new ArrayList<>(); //create an array that will hold the results

        midTime = System.nanoTime(); // Getting the time it took to initialize
    }

    /**
     * Function which initializes the data sets and converts the data sets into iterators.
     * @param fileName The path from this sources root to the data set.
     * @param CycleOrRounds Specifies whether we are looking for # rounds or a complete cycle.
     * @param amountOfPathOrCycle Specifies the # of paths or cycles specified in the query, i.e. 3-path, 3-cycle etc.
     * When? At the start of the program.
     * Calls? When all iterators are still 'alive' we call leapfrogSearch.
     */
    public void initDataSets(String fileName, Enum CycleOrRounds, int amountOfPathOrCycle) throws IOException{
        //for a cycle we have as many relations as amountOfPathOrCycle, i.e. a 4-cycle query gives 4 relations
        int amountOfRelations = amountOfPathOrCycle;
        //for a path we have one less, i.e. a 4-path query gives 3 relations
        if (CycleOrRounds == CycleOrRoundsEnum.PATH) {
            amountOfRelations--;
        }
        ArrayList<RelationIterator<Integer>> relIts = new ArrayList<>();
        int i;
        for(i = 1; i <= amountOfRelations; i++){
            DataImporter di;
            if(CycleOrRounds == CycleOrRoundsEnum.CYCLE && i == amountOfRelations) {
                di = new DataImporter(fileName, true, debug>1);
            } else {
                di = new DataImporter(fileName, false, debug>1);
            }
            TreeRelation rel = di.getRelArray();
            rel.setUid(i);
            RelationIterator<Integer> relIterator = rel.iterator();
            relIts.add(relIterator);
        }
        
        
        maxDepth = amountOfPathOrCycle - 1;
       
        iteratorPerDepth = new ArrayList<>();
        for (int j = 0; j <= maxDepth; j++) {
            ArrayList<RelationIterator<Integer>> intermedAListForIterators = new ArrayList<>();

            int a = Math.max(0, j - 1);
            int b = Math.min(j, maxDepth - 1);
            for (int k = a; k <= b; k++) {
                intermedAListForIterators.add(relIts.get(k));
            }
            
            //for a cycle query, we add for the first and last depth, the last iterator (this creates the cycle)
            if((CycleOrRounds == CycleOrRoundsEnum.CYCLE) && (j == 0 || j == maxDepth)) {
                intermedAListForIterators.add(relIts.get(maxDepth));
            }
            
            iteratorPerDepth.add(intermedAListForIterators);
            
        }
        
    }

    /**
     * Function which initializes the algorithm.
     * When? At the start after each time a key was found or we went a level higher.
     * Calls? When all iterators are still 'alive' we call leapfrogSearch.
     */
    public void multiJoin(){
        // Start out by initializing/starting the algorithm with leapfrogOpen. This is the main loop.
        leapfrogOpen();

        while (true){ // This is our search function
            if(debug>=2) { printDebugInfo("A - Continue with the true loop"); }

            if(atEnd){ // If we did not find the specific value we were looking for and an iterator is at an end.
                if(debug>=2) { printDebugInfo("B2 - We got one iterator that is at an end"); }

                if(depth==0){ // We stop because we are all the way at the end
                    break;
                }
                // We continue the search. At this depth we were at the end, so we go one up and go to the next value.
                leapfrogUp();
                leapfrogNext();
                if(debug>=2) { printDebugInfo("B3 - Executed leapfrogUp and leapfrogNext");}

            } else { // No iterator is at it's end.
                if(depth == maxDepth){// We got a winner
                    if(debug>=2) { printDebugInfo("C1 - We got a winner"); }
                    ArrayList<Integer> tuple = new ArrayList<>();
                    currentTuple.add(key);
                    tuple.addAll(currentTuple);
                    result.add(tuple);
                    tuple = null;
                    currentTuple.remove(currentTuple.size()-1);

                    key = -1;

                    if(debug>=1) {System.out.println(result); }
                    leapfrogNext();

                } else {// We can still go level deeper.
                    if(debug>=2) {System.out.println("C2 - Depth -> Level down"); }
                    leapfrogOpen();
                }
            }
        }
        endTime = System.nanoTime();
        printResults();
    }

    /**
     * Function which initializes the algorithm.
     * When? At the start after each time a key was found or we went a level higher.
     * Calls? When all iterators are still 'alive' we call leapfrogsearch.
     */
    public void leapfrogInit() {
        // Checking if any iterator is empty return (empty) result array
        for(RelationIterator<Integer> relIt : iteratorPerDepth.get(depth)) {
            if(relIt.atEnd()) {
                atEnd = true;
                return;
            }
        }

        // If all iterators are still 'alive' we make sure everything is sorted and start searching for the first
        // possible match.
        if(!atEnd) {
            atEnd = false;
            Collections.sort(iteratorPerDepth.get(depth));
            p = 0;
            leapfrogSearch();
        }
    }

    /**
     * Function which searches for a match in the leapfrogtreejoin. Created as in the paper.
     */
    public void leapfrogSearch() {
        // maxKeyIndex is the index of the maximal element we found and maxKey is the actual value.
        // (Correction needed since -1 % 3 returns -1 and not 2 as we want)
        int maxKeyIndex = ((p - 1) % numIters) + ((p - 1) < 0 ? numIters : 0);
//        int maxKeyIndex = numIters-1;
        maxKeyIndex = numIters == 1 ? 0 : maxKeyIndex; // Special case where maxKeyIndex = 1 while numIters = 1
        int maxKey = iteratorPerDepth.get(depth).get(maxKeyIndex).key();

        while (true) {
            int minKey = iteratorPerDepth.get(depth).get(p).key();

            if(debug>=2){ System.out.println("--- Searching --- Depth: " + depth + ", MaxKeyIndex: " + maxKeyIndex + ", NumIters: "
                    + numIters + ", maxKey: " + maxKey + ", minKey: " + minKey);}
            if(debug>=2) {System.out.println("curIt values: "+iteratorPerDepth.get(depth).get(p).debugString());}

            if (maxKey == minKey) {
                if(debug>=2) {System.out.println("Found key = "+minKey);}

                key = minKey;
                return;
            }
            else { // If no common key is found, update pointer of iterator
                if(debug>=2) {System.out.println("Key not equal, Searching for " + maxKey + " with minkey " + minKey);}

                if(debug>=2) {System.out.println("Seek with: "+iteratorPerDepth.get(depth).get(p).debugString());}

                iteratorPerDepth.get(depth).get(p).seek(maxKey);
                if(iteratorPerDepth.get(depth).get(p).atEnd()){ // The maxKey is not found
                    if(debug>=2) {System.out.println("key = -1");}
                    atEnd = true;
                    return;
                } else { // The maxKey is found and thus we check if the next iterator can also find it
                    maxKey = iteratorPerDepth.get(depth).get(p).key();
                    p = (p + 1) % numIters;
                }
            }
        }
    }

    /**
     * Function which sets the current iterator at the next value.
     * When? - This function is used when we just found a matching tuple which all iterators had.
     * Calls? - Calls leapfrogUp() until at the top.
     * Calls? - Sets the current iterator to the next.
     * Calls? - If the current iterator is not at the end, we set the next iterator and execute leapfrogSearch().
     */
    public void leapfrogNext(){
        atEnd = false;
        iteratorPerDepth.get(depth).get(p).next();
        if(iteratorPerDepth.get(depth).get(p).atEnd()){
            atEnd = true;
        } else {
            p = (p + 1) % numIters;
            leapfrogInit();
        }
    }

    /**
     * Function which seeks for a specific key in the current iterator.
     * @param seekKey - The specific value we are searching for.
     * When? - This function is stated in the paper, however, never used...
     * Calls? - Calls leapfrogSearch for the next iterator if we found the key.
     */
    public void leapfrogSeek(int seekKey){
        iteratorPerDepth.get(depth).get(p).seek(seekKey);
        if(iteratorPerDepth.get(depth).get(p).atEnd()) {
            atEnd = true;
        } else {
            p = (p + 1) % numIters;
            leapfrogSearch();
        }
    }

    /**
     * Function which opens up the next level(goes one level down) for all iterators.
     * When? - This function is used when we are going to the next level.
     * Calls? - Calls leapfrogInit afterwards to start up the next search.
     * Calls? - Calls updateIterPandNumIters which makes sure our p does not go out of bound and numIters is updated.
     * Modifies - currenTuple. Adds the current key to currentTuple and then proceeds with the opening.
     */
    public void leapfrogOpen(){
        if(depth > -1){ // Used to be able to report the currentTuple easily.
            currentTuple.add(iteratorPerDepth.get(depth).get(0).key());
        }
        depth = depth + 1;
        updateIterPandNumIters();
        for(RelationIterator relIt : iteratorPerDepth.get(depth) ) {
            relIt.open();
        }
        leapfrogInit();
    }

    /**
     * Function which goes up one level up for every iterator at this specific depth.
     * When? - This function is used when we are going to a lower depth.
     * Calls? - Calls leapfrogInit afterwards to start up the next search.
     * Calls? - Calls updateIterPandNumIters which makes sure our p does not go out of bound and numIters is updated.
     * Modifies - currentTuple. Removes the last added item which was the one from the depth just before we call up.
     */
    public void leapfrogUp(){
        if(depth > 0){
            currentTuple.remove(currentTuple.size()-1);
        }
        for(RelationIterator relIt : iteratorPerDepth.get(depth) ) {
            relIt.up();
        }
        depth = depth - 1;
        updateIterPandNumIters();
    }

    /**
     * Makes sure our p does not go out of bound and numIters is updated.
     * Modifies - numIters to match with this depth.
     * Modifies - p, if p is out of bound for the current depth, we set it to 0.
     */
    void updateIterPandNumIters(){
        numIters = iteratorPerDepth.get(depth).size();
        if(numIters<=p){
            p = 0;
        }
    }

    /**
     * Function to print the results in such a way with tabs that it can be reused.
     */
    private void printResults(){
        System.out.println("No caching" + "\t" +
                resultCycleOrPath + "\t" +
                resultAmountOfCycleorPath + "\t" +
                (midTime-startTime)/1000000 + "\t" +
                (endTime-midTime)/1000000 + "\t" +
                (endTime-startTime)/1000000 + "\t" +
                result.size() + "\t");
    }

    /**
     * Function to print debug information.
     */
    void printDebugInfo(String message){
        if (message.length()>=1){
            System.out.println("Message: "+message);
        }
        if(debug>=3) {
            if(depth>maxDepth){
                System.out.println("Our depth is "+ depth + " while our maxDepth is "+maxDepth+" hence no debuginfo");
            } else{
                for (int i = 0; i < iteratorPerDepth.get(depth).size(); i++) {
                    System.out.println("Info of iterator " + Integer.toString(i) + ": " +
                            iteratorPerDepth.get(depth).get(i).debugString());
                }
            }
        }
    }

    /**
     * Print information about the running time.
     */
    static void printRunningTimes(long startTime, long midTime, long endTime){
        long interTime = (midTime-startTime)/1000000;
        System.out.println("Time to load the data: "+interTime+" ms");
        interTime = (endTime-midTime)/1000000;
        System.out.println("Time to execute the algorithm: "+interTime+" ms");
        interTime = (endTime-startTime)/1000000;
        System.out.println("Time to execute both: "+interTime+" ms");
    }


    /**
     * @param args the command line arguments.
     */
    public static void main(String[] args) throws IOException {
        long startTime = System.nanoTime();
        LFTJ lftj = new LFTJ("./data/CA-GrQc.txt", CycleOrRoundsEnum.PATH, 4);
        // Create a LFTJ with cache, load the datasets and ready to rumble
        long midTime = System.nanoTime();
        lftj.multiJoin(); // We start the joins and count the cache
        long endTime = System.nanoTime();
        printRunningTimes(startTime, midTime, endTime);
    }
    
}
