/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author s131061
 */
public class LFTJCacheCount extends LFTJ {

    private ArrayList<Cache> caches;
    private int counter[];
    private int totalCacheHits;
    private int v = 0;
    private ArrayList<Integer> owned = new ArrayList<>();
    private ArrayList<Integer> adhesion = new ArrayList<>();
    private TreeDecomposition td;
    private int cacheHitJumpCounter = 0;
    private int numberOfCacheResults = 0;
    private int numberOfComputedResults = 0;
    Enum CycleOrPaths;

    private LFTJCacheCount() throws IOException {
        // Executes the init method of LFTJ..
    }

    /**
     * Function which initializes the data sets and converts the data sets into
     * iterators.
     *
     * @param fileName The path from this sources root to the data set.
     * @param CycleOrPaths Specifies whether we are looking for #paths or
     * #cycles.
     * @param amountOfPathOrCycle Specifies the # of paths or cycles specified
     * in the query, i.e. 3-path, 3-cycle etc. 
     * When? At the start of the program. 
     * Calls? When all iterators are still 'alive' we call
     * leapfrogSearch.
     */
    @Override
    public void initDataSets(String fileName, Enum CycleOrPaths, int amountOfPathOrCycle) throws IOException {
        this.CycleOrPaths = CycleOrPaths;
        caches = new ArrayList<>();
        //create tree decomposition
        td = new TreeDecomposition(CycleOrPaths, amountOfPathOrCycle, debug > 1);
        //create cache and counter for each bag and initialize to zero
        counter = new int[td.nrOfBags];
        for (int i = 0; i < td.nrOfBags; i++) {
            caches.add(new Cache());
            counter[i] = 0;
        }
        totalCacheHits = 0;

        // For a cycle we have as many relations as amountOfPathOrCycle, i.e. a 4-cycle query gives 4 relations
        int amountOfRelations = amountOfPathOrCycle;
        // For a path we have one less, i.e. a 4-path query gives 3 relations
        if (CycleOrPaths == LFTJ.CycleOrPathsEnum.PATH) {
            amountOfRelations--;
        }
        ArrayList<RelationIterator<Integer>> relIts = new ArrayList<>();
        int i;
        for (i = 1; i <= amountOfRelations; i++) {
            DataImporter di;
            //for a cycle, the last relation is inverted
            if (CycleOrPaths == LFTJ.CycleOrPathsEnum.CYCLE && i == amountOfRelations) {
                di = new DataImporter(fileName, true, debug > 1);
            } else {
                di = new DataImporter(fileName, false, debug > 1);
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
                if (debug > 0) {
                    System.out.println("added at depth " + j + " iterator " + k);
                }
            }

            //for a cycle query, we add for the first and last depth, the last iterator (this creates the cycle)
            if ((CycleOrPaths == LFTJ.CycleOrPathsEnum.CYCLE) && (j == 0 || j == maxDepth)) {
                intermedAListForIterators.add(relIts.get(maxDepth));
                if (debug > 0) {
                    System.out.println("added at depth " + j + " iterator " + (maxDepth));
                }
            }

            iteratorPerDepth.add(intermedAListForIterators);

        }

    }

    /**
     * Function which initializes the algorithm. When? At the start after each
     * time a key was found or we went a level higher. Calls? When all iterators
     * are still 'alive' we call leapfrogSearch.
     */
    @Override
    public void multiJoin() {
        // Start out by initializing/starting the algorithm with leapfrogOpen. This is the main loop.
        leapfrogOpen();

        while (true) { // This is our search function
            if (debug >= 2) {
                printDebugInfo("A - Continue with the true loop");
            }

            if (atEnd) { // If we did not find the specific value we were looking for and an iterator is at an end.
                if (debug >= 2) {
                    printDebugInfo("B2 - We got one iterator that is at an end");
                }

                if (depth == 0) { // We stop because we are all the way at the end
                    break;
                }
                // We continue the search. At this depth we were at the end, so we go one up and go to the next value.
                //Before we go up, check if depth is first in bag, if so store cache (lines 22,23 from algorithm in paper)
                if (v != 0 && depth == td.owned.get(v).get(0)) {

                    VariableAssignment va;
                    if (CycleOrPaths == LFTJ.CycleOrPathsEnum.PATH) {
                        va = new VariableAssignment(adhesion.get(0), currentTuple.get(adhesion.get(0)));
                    } else {
                        va = new VariableAssignment(adhesion.get(0), currentTuple.get(adhesion.get(0)), adhesion.get(1), currentTuple.get(adhesion.get(1)));
                    }
                    va.counter = counter[v];
                    caches.get(v).addAssignment(va);
                    

                    if (debug >= 2) {
                        if (CycleOrPaths == CycleOrPathsEnum.PATH) {
                            System.out.println("At depth: " + depth + " currentTuple: " + currentTuple
                                    + " checking for assignment " + adhesion.get(0) + " = " + (currentTuple.get(adhesion.get(0))));
                            System.out.println("Current bag: " + v + " adhesion: " + adhesion
                                    + " assignment: " + (currentTuple.get(adhesion.get(0))));
                            System.out.println(currentTuple);
                            System.out.println("Created assignment for key: " + va.assignment1 + ", variable: " + va.variable1
                                    + " in depth: " + depth);
                        } else {
                            System.out.println("At depth: " + depth + " currentTuple: " + currentTuple
                                    + " checking for assignment " + adhesion.get(0) + " = " + (currentTuple.get(adhesion.get(0)))
                                    + " and " + adhesion.get(1) + " = " + (currentTuple.get(adhesion.get(1))));
                            System.out.println("Current bag: " + v + " adhesion: " + adhesion
                                    + " assignment: " + (currentTuple.get(adhesion.get(0))) + ", " + (currentTuple.get(adhesion.get(1))));
                            System.out.println(currentTuple);
                            System.out.println("Created assignment for key: " + va.assignment1 + ", variable: " + va.variable1
                                    + " and key: " + va.assignment2 + ", variable: " + va.variable2 + " in depth: " + depth);
                        }

                    }

                    // Create the values for the cache such that they come out in the results.
                    ArrayList<ArrayList<Integer>> tempTupleList = new ArrayList<>();
                    for (int i = result.size() - 1; i >= 0; i--) {
                        if (currentTuple.equals(result.get(i).subList(0, depth))) {
                            ArrayList<Integer> tempTuple = new ArrayList<>();
                            tempTuple.addAll(result.get(i).subList(depth, result.get(i).size()));
                            tempTupleList.add(tempTuple);

                            if (debug >= 2) {
                                System.out.println("Added single item to cache: " + tempTuple);
                            }
                        } else {
                            break;
                        }
                    }
                    for (int i = tempTupleList.size() - 1; i >= 0; i--) {
                        caches.get(v).addOwnedKeyResults(va, tempTupleList.get(i));
                    }

                    if (debug >= 2) {
                        System.out.println();
                        System.out.println("Added to cache is: ");
                        if (CycleOrPaths == CycleOrPathsEnum.PATH) {
                            System.out.println(caches.get(v).returnOwnedKeysPath(va.assignment1));
                        } else {
                            System.out.println(caches.get(v).returnOwnedKeysCycle(va.assignment1, va.assignment2));
                        }
                        System.out.println();
                        printDebugInfo("B3 - Executed leapfrogUp and leapfrogNext");
                    }
                }
                // Then go up..
                leapfrogUp();
                // And after we go up, check if depth is last in bag, if so update counter.
                // As specified in the caching paper on lines 18-20 from the algorithm
                if (v != 0 && depth == td.owned.get(v).get(td.owned.get(v).size() - 1)) {
                    counter[v] = counter[v] + counter[v + 1];
                }
                leapfrogNext();

            } else { // No iterator is at it's end.
                if (depth == maxDepth) {// We got a winner
                    ArrayList<Integer> tuple = new ArrayList<>();
                    currentTuple.add(key);
                    tuple.addAll(currentTuple);
                    result.add(tuple);
                    numberOfComputedResults++;
                    currentTuple.remove(currentTuple.size() - 1);

                    key = -1;

                    //incrementer counter of bag
                    counter[v] = counter[v] + 1;

                    if (debug >= 2) {
                        System.out.println("We found a match!");
                        System.out.println("");
                        System.out.println(result);
                        System.out.println("");
                    }
                    leapfrogNext();
                } else {// We can still go level deeper.
                    if (debug >= 2) {
                        System.out.println("C2 - Depth -> Level down");
                    }
                    leapfrogOpen();
                }
            }
        }

        System.out.println("Total number of cache hits: " + totalCacheHits);
        System.out.println("Total number of results: " + result.size() + " of which " + numberOfCacheResults + " are from cache and " + numberOfComputedResults + " are computed");
        //System.out.println(result);

    }

    /**
     * Function which initializes the algorithm. When? At the start after each
     * time a key was found or we went a level higher. Calls? When all iterators
     * are still 'alive' we call leapfrogsearch.
     */
    @Override
    public void leapfrogInit() {
        for (RelationIterator<Integer> relIt : iteratorPerDepth.get(depth)) {
            if (relIt.atEnd()) {
                atEnd = true;
                return;
            }
        }
        //initialize bag counter
        // @TODO Shouldn't we put this in the part where this is not at the end?
        // Seems like this could avoid initializing bags that shouldn't be initialized, right?
        // --- Not sure what you mean here, but this follows the structure of the algorithm in the paper
        if (v != 0 && depth == owned.get(0)) {
            counter[v] = 0;

            if (debug >= 2) {
                if (CycleOrPaths == CycleOrPathsEnum.PATH) {
                    System.out.println("At depth: " + depth + " currentTuple: " + currentTuple
                            + " checking for assignment " + adhesion.get(0) + " = " + (currentTuple.get(adhesion.get(0))));
                    System.out.println("Current bag: " + v + " adhesion: " + adhesion
                            + " assignment: " + (currentTuple.get(adhesion.get(0))));
                } else {
                    System.out.println("At depth: " + depth + " currentTuple: " + currentTuple
                            + " checking for assignment " + adhesion.get(0) + " = " + (currentTuple.get(adhesion.get(0)))
                            + " and " + adhesion.get(1) + " = " + (currentTuple.get(adhesion.get(1))));
                    System.out.println("Current bag: " + v + " adhesion: " + adhesion
                            + " assignment: " + (currentTuple.get(adhesion.get(0))) + ", " + (currentTuple.get(adhesion.get(1))));
                }
            }

            //check for cache hit
            Boolean cacheHit = false;
            if (CycleOrPaths == CycleOrPathsEnum.PATH) {
                if(caches.get(v).containsAssignmentPath(adhesion.get(0), currentTuple.get(adhesion.get(0)))) {
                    cacheHit = true;
                }
            } else {
                if (caches.get(v).containsAssignmentCycle(adhesion.get(0), currentTuple.get(adhesion.get(0)), adhesion.get(1), currentTuple.get(adhesion.get(1)))) {
                    cacheHit = true;
                }
            }
            
            //if cache hit..
            if(cacheHit) {

                if (debug >= 1) {
                    System.out.println("cache hit");
                }

                totalCacheHits++;
                // Adding the results to our arrayList result
                ArrayList<ArrayList<Integer>> allCacheResults;
                if(CycleOrPaths == CycleOrPathsEnum.PATH){
                    allCacheResults = caches.get(v).returnOwnedKeysPath(currentTuple.get(adhesion.get(0)));
                } else {
                    allCacheResults = caches.get(v).returnOwnedKeysCycle(currentTuple.get(adhesion.get(0)), currentTuple.get(adhesion.get(1)));
                }
                for (ArrayList<Integer> allCacheResult : allCacheResults) {
                    ArrayList<Integer> tempTuple = new ArrayList<>();
                    tempTuple.addAll(currentTuple);
                    tempTuple.addAll(allCacheResult);
                    result.add(tempTuple);
                    numberOfCacheResults++;
                }

                if (debug >= 1) {
                    System.out.println();
                    System.out.println(result);
                    System.out.println();
                }

                // Updating the counter
                counter[v] = caches.get(v).lastChecked.counter;
                int m = 0;
                for (int i = 0; i < td.nrOfVariables; i++) {
                    if (td.owner.get(i) >= v) {
                        m = i;
                    }
                }

                cacheHitJumpCounter = m - depth;
                leapfrogUp();
                leapfrogNext();
                return;
            }

        }
        // If all iterators are still 'alive' we make sure everything is sorted and start searching for the first
        // possible match.
        if (!atEnd) {
            atEnd = false;
            Collections.sort(iteratorPerDepth.get(depth));
            p = 0;
            leapfrogSearch();
        }
    }

    /**
     * Function which opens up the next level(goes one level down) for all
     * iterators. When? - This function is used when we are going to the next
     * level. Calls? - Calls leapfrogInit afterwards to start up the next
     * search. Calls? - Calls updateIterPandNumIters which makes sure our p does
     * not go out of bound and numIters is updated. Modifies - currenTuple. Adds
     * the current key to currentTuple and then proceeds with the opening.
     */
    @Override
    public void leapfrogOpen() {
        if (depth > -1) { // Used to be able to report the currentTuple easily.
            currentTuple.add(iteratorPerDepth.get(depth).get(0).key());
        }
        depth = depth + 1;
        if (depth <= maxDepth) {
            if (debug >= 2) {
                System.out.println("depth <= maxDepth");
            }
            updateBag();
            updateIterPandNumIters();
            for (RelationIterator relIt : iteratorPerDepth.get(depth)) {
                if (debug >= 2) {
                    printDebugInfo("Extra iterator info for debug ");
                }
                relIt.open();
                if (debug >= 2) {
                    printDebugInfo("Extra iterator info for debug ");
                }
            }
            leapfrogInit();
        }
    }

    /**
     * Function which goes up one level up for every iterator at this specific
     * depth. When? - This function is used when we are going to a lower depth.
     * Calls? - Calls leapfrogInit afterwards to start up the next search.
     * Calls? - Calls updateIterPandNumIters which makes sure our p does not go
     * out of bound and numIters is updated. Modifies - currentTuple. Removes
     * the last added item which was the one from the depth just before we call
     * up.
     */
    @Override
    public void leapfrogUp() {
        if (depth <= currentTuple.size() && depth > 0) {
            currentTuple.remove(currentTuple.size() - 1);
        }
        if (depth <= maxDepth) {
            for (RelationIterator relIt : iteratorPerDepth.get(depth)) {
                relIt.up();
            }
        }
        depth = depth - 1;
        if (depth >= 0 && depth <= maxDepth) {
            updateBag();
            updateIterPandNumIters();
        }

    }

    /**
     * Function which updates the appropriate variables when the current bag 
     * may be changed. 
     * When? - This function is called when depth is changed and thus another 
     * bag may be selected.
     * Modifies? v to contain the current bag
     * Modifies? owned to contain the set of variables the new bag owns
     * Modifies? adhesion to contain the adhesion of the new bag
     */
    private void updateBag() {
        v = td.owner.get(depth);
        owned = td.owned.get(v);
        adhesion = td.adhesion.get(v);
    }

    /**
     * @param args the command line arguments.
     */
    public static void main(String[] args) throws IOException {
        long startTime = System.nanoTime();
        LFTJCacheCount lftjcc = new LFTJCacheCount(); // Create a LFTJ with cache, load the datasets and ready to rumble
        long midTime = System.nanoTime();
        lftjcc.multiJoin(); // We start the joins and count the cache
        long endTime = System.nanoTime();
        printRunningTimes(startTime, midTime, endTime);
    }

}
