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
    private int ip = 0; //iterator pointer
    private int numIters; //number of iterators
    private boolean lfDone = false;
    
    private LFTJ() {
        //create some fictive relations (later to be replaced with existing datasets)
        //note: it only works if these arrays are sorted
        //either we have to always sort these arrays first or we need to change the seek method in relation
        Relation relA = new Relation(new int[]{1, 3, 4, 6, 8, 11});
        Relation relB = new Relation(new int[]{2, 3, 4, 5, 7, 8, 9, 10, 11});
        //Relation relC = new Relation(new int[]{1, 2, 3, 5, 6, 8, 10, 11});
        Relation relC = new Relation(new int[]{3,5,8,10});

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
        if(!lfDone) {
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
                lfDone = true;
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
        int maxKeyIndex = ((ip - 1) % numIters) + ((ip - 1) < 0 ? numIters : 0);
        
        //get maximum key (from the last spot in the sorted array)
        int maxKey = relIts.get(maxKeyIndex).key();
        
        while (true) {
            curIt = relIts.get(ip);
            
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
                //this is, I think, where leapfrog-next comes in, but it doesn't seem necessary to me at first glance to create a new method for this
                if (curIt.hasNext()) {
                    curIt.next();
                    maxKey = curIt.key();
                    //re-sort the iterators based on their keys
                    Collections.sort(relIts);
                    //reset ip to 0, it will lead to the minimum key value again after the sort
                    ip = 0;
                } else {
                    lfDone = true;
                    return;
                }             
            } 
            //if no common key is found, update pointer of iterator
            else {
                if(curIt.hasNext()) {
                    curIt.seek(maxKey);
                    maxKey = curIt.key();
                    //move on to next iterator
                    ip = (ip + 1) % numIters;
                } else {
                    lfDone = true;
                    return;
                }
            }
        }
        
    }
    
    //used to test the functionality of the iterator interface
    public void testIterator() {
        //create fictive relation for testing purposes 
        int[] relArray = new int[]{1, 4, 6, 8, 9};
        Relation rel = new Relation(relArray);
        
        //create iterator for the relation
        RelationIterator<Integer> relIt = rel.iterator();
        
        //test next() 
        for(Integer key : rel) {
            System.out.print(key + " ");
        }
        System.out.println();
        
        //test seek(int seekKey) and key()
        relIt.seek(5);
        System.out.println("seek 5 gives " +relIt.key());
        
        //test seek(int seekKey) and atEnd()
        relIt.seek(9);
        System.out.println("with seek 9, atEnd is " +relIt.atEnd());
        
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
