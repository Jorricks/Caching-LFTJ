/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src;

import java.util.ArrayList;
import java.util.Collections;
/**
 *
 * @author s131061
 */
public class LFTJ {
    
    private ArrayList<RelationIterator<Integer>> relIts; //array of iterators, one for each relation
    private ArrayList<ArrayList<Integer>> result; //result set: array with tuples
    private int p = 0; //iterator pointer
    private int numIters; //number of iterators
    private int depth = 0;
    private int maxDepth = 0; // how deep is our relation? R(x,y), T(y,z) yields 2.
    private int key = 0;
    private ArrayList<Integer> currentTuple = new ArrayList<>();
    private boolean atEnd;
    
    private LFTJ() {
        //create some fictive relations (later to be replaced with existing datasets)
        //note: it only works if these arrays are sorted
        //either we have to always sort these arrays first or we need to change the seek method in relation
        Relation relA = new Relation(new int[][]{{1,1,1},{3,5,6}});
        Relation relB = new Relation(new int[][]{{1,1,1,2,2,3,3},{4,5,7,8,3,4,7}});
        Relation relC = new Relation(new int[][]{{1,1,2,3},{2,5,3,7}});

        maxDepth = 2;

        //create iterators for each relation and put all iterators in an array
        relIts = new ArrayList<>();
        relIts.add(relA.iterator());
        relIts.add(relB.iterator());
        relIts.add(relC.iterator());
        numIters = relIts.size();

        //create an array that will hold the results
        result = new ArrayList<>();
    }
    
    
    //main function to call for a unary join 
    //will also be the building block for relations with arity > 1 later on
    private void multiJoin(){
        
        //start leapfrogging
        System.out.println(relIts.get(0).debugString());
        System.out.println(relIts.get(1).debugString());
        System.out.println(relIts.get(2).debugString());
        System.out.println(depth+" - "+key);
        leapfrogInit();
        System.out.println(relIts.get(0).debugString());
        System.out.println(relIts.get(1).debugString());
        System.out.println(relIts.get(2).debugString());
        System.out.println(depth+" - "+key);
        while (true){
            if(key == -1){
                System.out.println("A1");
                if(depth==0){
                    System.out.println("A2");
                    break;
                }
                leapfrogUp();
                leapfrogInit();
                System.out.println("A3");
                System.out.println(relIts.get(0).debugString());
                System.out.println(relIts.get(1).debugString());
                System.out.println(relIts.get(2).debugString());
                System.out.println(depth+" - "+key);
            } else {
                System.out.println("B1");
                if(depth == maxDepth-1){
                    System.out.println("B2");
                    currentTuple.add(key);
                    System.out.println("ADDED =[" + currentTuple.get(0)+"]["+ currentTuple.get(1)+"] - "
                            +currentTuple.size());
                    result.add(currentTuple);
                    currentTuple.remove(currentTuple.size()-1);
                    leapfrogUp();
                    System.out.println("START ");
                    System.out.println(relIts.get(0).debugString());
                    System.out.println(relIts.get(1).debugString());
                    System.out.println(relIts.get(2).debugString());
                    System.out.println(depth+" - "+key);
                    leapfrogInit();
                } else {
                    System.out.println("B3");
                    leapfrogOpen();
                }
            }
        }
        System.out.println(relIts.get(0).debugString());
        System.out.println(relIts.get(1).debugString());
        System.out.println(relIts.get(2).debugString());
        System.out.println(depth+" - "+key);
        leapfrogNext();
        System.out.println(relIts.get(0).debugString());
        System.out.println(relIts.get(1).debugString());
        System.out.println(relIts.get(2).debugString());
        System.out.println(depth+" - "+key);
        
//        //after initializing, search for common keys in all relations and output them to the result set
//        if(!atEnd) {
//            leapfrogSearch();
//        }
        //print the result set
        System.out.println(result);
    }
    
    private void leapfrogInit() {
        //if any iterator is empty return (empty) result array
        for(RelationIterator<Integer> relIt : relIts) {
//            System.out.println(counter + " - " + relIt.key() + " = " + relIt.debugString());
            if(relIt.atEnd()) {
                atEnd = true;
                System.out.println("kkey = -1");
//                key = -1;
            }
        }
        atEnd = false;
        //else sort the iterators on their key values
        
        //testing 
//        System.out.println("Before sorting");
//        for(RelationIterator<Integer> relIt : relIts) {
//           System.out.println(relIt.key());
//        }
        
        //actual sorting
        Collections.sort(relIts);
        p = 0;
        leapfrogSearch();
//        System.out.println(key);
        
        //testing
//        System.out.println("After sorting");
//        for(RelationIterator<Integer> relIt : relIts) {
//            System.out.println(relIt.key());
//        }
        
    }

    private void leapfrogSearch() {
        //current iterator
        RelationIterator curIt;
        
        //get maxKey index 
        //correction needed since -1 % 3 returns -1 and not 2 as we want
        int maxKeyIndex = ((p - 1) % numIters) + ((p - 1) < 0 ? numIters : 0);
        
        //get maximum key (from the last spot in the sorted array)
        int maxKey = relIts.get(maxKeyIndex).key();
        
        while (true) {
            curIt = relIts.get(p);
//            System.out.println(p + " - " + relIts.get(p).key());
            
            //get minimum key (from the first spot in the sorted array)
            int minKey = curIt.key();
            System.out.println("Ja "+curIt.debugString());

            //if they are equal a common key is found, write it to the result set
            if (maxKey == minKey) {
                key = minKey;
                System.out.println(relIts.get(0).debugString());
                System.out.println(relIts.get(1).debugString());
                System.out.println(relIts.get(2).debugString());
                System.out.println("key = "+key);
                return;
            } 
            //if no common key is found, update pointer of iterator
            else {
                relIts.get(p).seek(maxKey);
                if(relIts.get(p).atEnd()){
                    atEnd = true;
                    key = -1;
                    System.out.println("key = -1");
                    return;
                } else {
                    p = (p + 1) % numIters;
                    leapfrogSearch();
                }
            }
        }
        
    }

    private void leapfrogNext(){
        relIts.get(p).next();
        if(relIts.get(p).atEnd()){
            atEnd = true;
        } else {
            p = (p + 1) % numIters;
            leapfrogSearch();
        }
    }

    private void leapfrogSeek(int seekKey){
        relIts.get(p).seek(seekKey);
        if(relIts.get(p).atEnd()) {
            //move on to next iterator
            atEnd = true;
        } else {
            p = (p + 1) % numIters;
            leapfrogSearch();
        }
    }

    private void leapfrogOpen(){
        if(depth > -1){
            currentTuple.add(relIts.get(0).key());
        }
        depth = depth + 1;
        for(RelationIterator relIt : relIts ) {
            relIt.open();
        }
        System.out.println(relIts.get(0).debugString());
        System.out.println(relIts.get(1).debugString());
        System.out.println(relIts.get(2).debugString());
        System.out.println(depth+" - "+key);
        leapfrogInit();
    }

    private void leapfrogUp(){
        currentTuple.remove(currentTuple.size()-1);
        for(RelationIterator relIt : relIts ) {
            relIt.up();
        }
        depth = depth - 1;
    }

   
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        LFTJ lftj = new LFTJ();
        //lftj.testIterator();
        lftj.multiJoin();
    }
    
}
