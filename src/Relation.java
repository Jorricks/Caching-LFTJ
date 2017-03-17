/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src;
import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;

import java.util.NoSuchElementException;

/**
 *
 * @author s131061
 */
public class Relation implements Iterable<Integer>{
    private int[][] array;
    private int arrayDepth;
    private int depthSize;
    
    
    Relation(int[][] relArray) {
        this.array = relArray;
        this.arrayDepth = array.length;
    }

    @Override
    public RelationIterator<Integer> iterator() {
       RelationIterator<Integer> relIt;
        relIt = new RelationIterator<Integer>() {

            //not sure that both index and key are needed, but it seems to work fine
            private int indexDepth = 0; // From depth 0 to ?(often 1 as we would have just 2 data points)
            private int indexTuple = 0; // From tuple 0 to n
            // specifies for a certain depth where the tree's max is
            private int tupleLowerBound = 0;
            private int tupleUpperBound = (depthSize == 0? 0 : array[0].length-1);
            private int key = (depthSize == 0? 0 : array[indexDepth][indexTuple]);

            @Override
            public int key() {
                return key;
            }

            @Override
            public boolean hasNext() {
                return indexTuple < tupleUpperBound;
            }

            @Override
            public Integer next() {
                if(this.hasNext()) {
                    key = array[indexDepth][indexTuple];
                    indexTuple ++;
                    return key;
                }
                throw new NoSuchElementException();
            }

            //position iterator at least upper bound for seekKey as explained in the paper
            //or at the end if no such key exists
            @Override
            public void seek(int seekKey) {
                while (key < seekKey && indexTuple <= tupleUpperBound) {
                    if(this.hasNext()){
                        key = this.next();
                    } else {
                        return;
                    }
                }
            }

            @Override
            public boolean atEnd() {
                return (indexTuple >= tupleUpperBound);
            }

            //used for sorting
            @Override
            public int compareTo(RelationIterator<Integer> o) {
                return Integer.compare(this.key(), o.key());
            }

            //proceeds to the first element at the next depth
            @Override
            public void open(){
                if(indexDepth < array.length - 1){
                    indexDepth--;
                    findTupleLowerBound();
                    findTupleUpperBound();
                    indexTuple = tupleLowerBound;
                } else {
                    throw new NoSuchElementException();
                }
            }

            //returns to the parent key at the previous depth
            @Override
            public void up(){
                if(indexDepth > 0){
                    indexDepth++;
                    findTupleLowerBound();
                    findTupleUpperBound();
                    indexTuple = tupleLowerBound;
                } else {
                    throw new NoSuchElementException();
                }
            }

            //returns the lowest tuple index of all elements having the same parents(in the tree)
            void findTupleLowerBound(){
                if(indexDepth > 0) {
                    tupleLowerBound = 0;
                    int searchDepth = indexDepth - 1;
                    for (int i = searchDepth; i >= 0; i--) {
                        int levelKey = array[searchDepth][indexTuple];
                        for (int j = indexTuple; j >= 0 && array[searchDepth][j] > tupleLowerBound; j--) {
                            if (levelKey != array[searchDepth][j]) {
                                tupleLowerBound = j - 1;
                                break;
                            }
                        }
                    }
                } else{
                    tupleLowerBound = 0;
                }
            }

            //returns the lowest tuple index of all elements having the same parents(in the tree)
            void findTupleUpperBound(){
                if(indexDepth > 0){
                    tupleUpperBound = array[indexDepth].length-1;
                    int searchDepth = indexDepth - 1;
                    for(int i = searchDepth; i >= 0; i--){
                        int levelKey = array[searchDepth][indexTuple];
                        for(int j = indexTuple; j < array[searchDepth].length && array[searchDepth][j] < tupleUpperBound;
                            j++){
                            if (levelKey != array[searchDepth][j]){
                                tupleUpperBound = j-1;
                                break;
                            }
                        }
                    }
                } else {
                    tupleUpperBound = array[indexDepth].length;
                }
            }
        };

        return relIt;
    }
    
}
