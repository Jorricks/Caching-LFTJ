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
    private boolean atEnd = false;
    
    private LFTJ() {
        //create some fictive relations (later to be replaced with existing datasets)
        //note: it only works if these arrays are sorted
        //either we have to always sort these arrays first or we need to change the seek method in relation
        Relation relA = new Relation(new int[][]{{1,1,1,2,2,3,3},{3,4,6,3,6,4,7}});
        Relation relB = new Relation(new int[][]{{1,1,1,1,2,3,3},{2,4,7,8,3,4,7}});
        Relation relC = new Relation(new int[][]{{1,2,3},{4,3,7}});

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
    private void unaryJoin(){
        
        //start leapfrogging
        leapfrogInit();
        
        //after initializing, search for common keys in all relations and output them to the result set
        if(!atEnd) {
            leapfrogSearch();
        }
        //print the result set
        System.out.println(result);
        
        //no idea why we need leapfrog-seek as of now?!?!
        
    }
    
    private void leapfrogInit() {
        
        //if any iterator is empty return (empty) result array
        for(RelationIterator<Integer> relIt : relIts) {
            if(relIt.atEnd()) {
                atEnd = true;
                return;
            }
        }
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
        
        //testing
//        System.out.println( "After sorting");
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
            
            //get minimum key (from the first spot in the sorted array)
            int minKey = curIt.key();

            //if they are equal a common key is found, write it to the result set
            if (maxKey == minKey) {
                ArrayList<Integer> tuple = new ArrayList<>();
                for(RelationIterator relIt : relIts ) {
                    tuple.add(relIt.key());
                }
                //System.out.println(result);
                result.add(tuple);
                
                //continue looking for more tuples (if the current iterator is not at the end of its relation)
                leapfrogNext();
            } 
            //if no common key is found, update pointer of iterator
            else {
                leapfrogSeek(maxKey);
                if(relIts.get(p).atEnd()){
                    atEnd = true;
                    return;
                } else {
                    maxKeyIndex = ((p - 1) % numIters) + ((p - 1) < 0 ? numIters : 0);
                    maxKey = relIts.get(maxKeyIndex).key();
                    p = (p + 1) % numIters;
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

    private void triejoinOpen(){
        depth = depth + 1;
        for(RelationIterator relIt : relIts ) {
            relIt.open();
        }
        leapfrogInit();
    }

    private void triejoinUp(){
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
        lftj.unaryJoin();
    }
    
}
