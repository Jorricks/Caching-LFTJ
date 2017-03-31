/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src;


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

    TreeRelation(int[][] relArray) {
        this.rootNode = new TreeNode(0, -1,0);
        int j = relArray[0][0];
        currentNode = rootNode.addChild(relArray[0][0], 0, 0);
        int parentCounter = 1;
        int childCounter = 0;
        for(int i=0; i < relArray.length; i++){
            if(j == relArray[i][0]){
                System.out.println("Add child =["+relArray[i][0]+"]["+relArray[i][1]+"] to parent "+currentNode.getKey());
                this.currentNode.addChild(relArray[i][1], 1,childCounter);
                childCounter++;
            } else {
                System.out.println("Add parent =["+relArray[i][0]+"]["+relArray[i][1]+"]");
                currentNode = rootNode.addChild(relArray[i][0], 0, parentCounter);
                parentCounter++;
                j = relArray[0][0];
                System.out.println("Add child =["+relArray[i][0]+"]["+relArray[i][1]+"] to parent "+currentNode.getKey());
                this.currentNode.addChild(relArray[i][1], 1,childCounter);
                childCounter++;
            }
        }
        currentNode = rootNode;
    }


    @Override
    public RelationIterator<Integer> iterator() {
        RelationIterator<Integer> relIt = new RelationIterator<Integer>() {

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
                currentNode = currentNode.next();
                return currentNode.getKey();
            }

            //position iterator at least upper bound for seekKey as explained in the paper
            //or at the end if no such key exists
            @Override
            public void seek(int seekKey) {
                int[] rangeOfSeekValue = exponentialSearch(currentNode.getParent(),seekKey);
                binarySearch(currentNode.getParent(), rangeOfSeekValue[0], rangeOfSeekValue[1], seekKey);
            }

            @Override
            public boolean atEnd() {
                return currentNode.hasNext();
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
                currentDepth--;
                currentNode = currentNode.up();
            }

            //returns the uid
            @Override
            public int getUid() {
                return uid;
            }

            private int[] exponentialSearch(TreeNode parentNode, int searchValue){
                int min = 1;
                int max = parentNode.getAmountOfChildren();
                int currentValue = parentNode.getChild(0).getKey();
                while (currentValue < searchValue && min * 2 < max){
                    min = min * 2;
                    currentValue = parentNode.getChild(min-1).getKey();
                }
                return new int[]{min, Math.min(min*2, max)};
            }

            private void binarySearch(TreeNode parentNode, int minIndex, int maxIndex, int searchValue){
                int min = minIndex;
                int max = maxIndex;
                while (max > min) {
                    int middle = (min + max) / 2;
                    if(parentNode.getChild(middle-1).getKey() == searchValue){
                        max = middle;
                    }
                    if(parentNode.getChild(middle-1).getKey() < searchValue){
                        min = middle + 1;
                    }
                    if(parentNode.getChild(middle-1).getKey() > searchValue){
                        max = middle - 1;
                    }
                }
                currentNode = parentNode.getChild(max-1);
            }


            //return the complete value of a tuple that was found to be correct.
            public ArrayList<java.lang.Integer> giveResult(){
                return new ArrayList<>();
            }

            @Override
            public String debugString(){
                return "";
            }
        };

        return relIt;
    }

}
