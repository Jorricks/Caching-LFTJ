/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src;

import java.util.Iterator;

/**
 *
 * @author s131061
 */
public interface RelationIterator<Integer> extends Iterator, Comparable<RelationIterator<Integer>> {
    
    int key();
    
    void seek(int seekKey);
    
    boolean atEnd();

    void open();

    void up();
    
}
