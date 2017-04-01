/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src;


import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;

import java.util.ArrayList;

/**
 *
 * @author s131061
 */
public class TreeRelation implements Iterable<Integer>{
    private TreeNode rootNode;
    private TreeNode currentNode;
    private int currentDepth = 0;
    private int uid; // UID

    public void setUid(int uid) {
        this.uid = uid;
    }


    TreeRelation(int[][] relArray, boolean debug) {
        if(debug){ System.out.println("------------------------ LOADING IN DATA ------------------------");}
        if(debug){ System.out.println("Add root =[-1]");}

        this.rootNode = new TreeNode(-1, -1,0);
        int j = relArray[0][0];

        if(debug){ System.out.println("Add parent =["+relArray[0][0]+"]");}
        currentNode = rootNode.addChild(relArray[0][0], 0, 0);

        int parentCounter = 1;
        int childCounter = 0;

        for(int i=0; i < relArray.length; i++){
            if(j == relArray[i][0]){
                if(debug){ System.out.println("Add child ["+currentNode.getKey()+"]["+relArray[i][1]+"]");}

                this.currentNode.addChild(relArray[i][1], 1,childCounter);
                childCounter++;

            } else {
                if(debug){ System.out.println("Add parent =["+relArray[i][0]+"]");}

                currentNode = rootNode.addChild(relArray[i][0], 0, parentCounter);
                parentCounter++;
                j = relArray[i][0];

                if(debug){ System.out.println("Add child ["+currentNode.getKey()+"]["+relArray[i][1]+"]");}

                childCounter = 0;
                this.currentNode.addChild(relArray[i][1], 1,childCounter);
                childCounter++;
            }
        }
        if(debug){ System.out.println("------------------------ FINISHED LOADING DATA ------------------------");}

        currentNode = rootNode;
    }


    @Override
    public RelationIterator<Integer> iterator() {
        RelationIterator<Integer> relIt = new RelationIterator<Integer>() {

            private boolean atEnd = false;

            //not sure that both index and key are needed, but it seems to work fine

            @Override
            public int key() {
                return currentNode.getKey();
            }

            @Override
            public boolean hasNext() {
                return currentNode.hasNext();
            }

            @Override
            public Integer next() {
                if(currentNode.getBornIndexOfThisChild()+1 >= currentNode.getParent().getAmountOfChildren()){
                    atEnd = true;
                } else {
                    currentNode = currentNode.next();
                }
                return currentNode.getKey();
            }

            //position iterator at least upper bound for seekKey as explained in the paper
            //or at the end if no such key exists
            @Override
            public void seek(int seekKey) {
                binarySearch(currentNode.getParent(), currentNode.getBornIndexOfThisChild(), seekKey);
            }

            @Override
            public boolean atEnd() {
                return atEnd;
            }

            //used for sorting
            @Override
            public int compareTo(RelationIterator<Integer> o) {
                return Integer.compare(this.key(), o.key());
            }

            //proceeds to the first element at the next depth
            @Override
            public void open(){
                currentDepth++;
                currentNode = currentNode.down();
            }

            //returns to the parent key at the previous depth
            @Override
            public void up(){
                atEnd = false;
                currentDepth--;
                currentNode = currentNode.up();
            }

            //returns the uid
            @Override
            public int getUid() {
                return uid;
            }

            private void binarySearch(TreeNode parentNode, int minIndex, int searchValue){
                int min = minIndex;
                int max = parentNode.getAmountOfChildren();
                while (max > min) {
                    int middle = (min + max) / 2;
                    if(parentNode.getChild(middle).getKey() == searchValue){
                        max = middle;
                    }
                    if(parentNode.getChild(middle).getKey() < searchValue){
                        min = middle + 1;
                    }
                    if(parentNode.getChild(middle).getKey() > searchValue){
                        max = middle - 1;
                    }
                }
                if(min >= parentNode.getAmountOfChildren()){
                    atEnd = true;
                    currentNode = parentNode.getChild(parentNode.getAmountOfChildren()-1);
                } else {
                    if(parentNode.getChild(min).getKey() == searchValue){
                        currentNode = parentNode.getChild(min);
                    }
                    if(parentNode.getChild(min).getKey() < searchValue){
                        int test = parentNode.getChild(min).getKey();
                        if(min+1 == parentNode.getAmountOfChildren()){
                            atEnd = true;
                            currentNode = parentNode.getChild(parentNode.getAmountOfChildren()-1);
                        } else {
                            currentNode = parentNode.getChild(min + 1);
                        }
                    }
                    if(parentNode.getChild(min).getKey() > searchValue){
                        int test = parentNode.getChild(min).getKey();
                        currentNode = parentNode.getChild(min);
                    }
                }
            }


            //return the complete value of a tuple that was found to be correct.
            public ArrayList<java.lang.Integer> giveResult(){
                return new ArrayList<>();
            }

            @Override
            public String debugString(){
                if(currentNode.getParent() != null){
                return "uid: " + uid + ", key: " + key() + ", depth: " + currentNode.getDepth() +
                        ", parentKey: " + currentNode.getParent().getKey() +
                        ", amount of children: "+ currentNode.getAmountOfChildren() +
                        ", get amount of brothers: "+ currentNode.getParent().getAmountOfChildren() +
                        ", compared to brothers at index: "+currentNode.getBornIndexOfThisChild();
                } else {
                    return "uid: " + uid + ", key: " + key() + ", depth: " + currentNode.getDepth() +
                            ", amount of children: "+ currentNode.getAmountOfChildren() +
                            ", This is the root note...";
                }
            }
        };

        return relIt;
    }

}
