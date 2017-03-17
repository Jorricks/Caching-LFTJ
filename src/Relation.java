/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src;
import java.util.NoSuchElementException;

/**
 *
 * @author s131061
 */
public class Relation implements Iterable<Integer>{
    private int[] array;
    private int arraySize;
    
    
    Relation(int[] relArray) {
        this.array = relArray;
        this.arraySize = array.length;
    }

    @Override
    public RelationIterator<Integer> iterator() {
       RelationIterator<Integer> relIt;
        relIt = new RelationIterator<Integer>() {

            //not sure that both index and key are needed, but it seems to work fine
            private int index = 1;
            private int key = (arraySize == 0 ? 0 : array[0]);

            @Override
            public int key() {
                return key;
            }

            @Override
            public boolean hasNext() {
                return index < arraySize;
            }

            @Override
            public Integer next() {
                if(this.hasNext()) {
                    key = array[index];
                    index ++;
                    return key;
                }
                throw new NoSuchElementException();
            }

            //position iterator at least upper bound for seekKey as explained in the paper
            //or at the end if no such key exists
            @Override
            public void seek(int seekKey) {
                while (key < seekKey) {
                    if(this.hasNext()){
                        key = this.next();
                    } else {
                        return;
                    }
                }
            }

            @Override
            public boolean atEnd() {
                return index > arraySize;
            }

            //used for sorting
            @Override
            public int compareTo(RelationIterator<Integer> o) {
                return Integer.compare(this.key(), o.key());
            }



        };

        return relIt;
    }
    
}
