/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src;

public class TreeRelation implements Iterable<Integer>{
    private TreeNode currentNode; // Current node we are looking at.
    private int uid; // Unique identifier for each relation

    /**
     * Sets the UID of the relation.
     * @param uid Unique identifier for this relation. Easy while debugging.
     */
    void setUid(int uid) {
        this.uid = uid;
    }


    /**
     * Function which converts the data sets into a tree structure.
     * @param relArray Contains a double array containing all the data.
     * @param debug Specifies whether we want to output all information or not.
     * When? At initialization.
     * Modifies? currentNode to be equal to the root node of the tree.
     */
    TreeRelation(int[][] relArray, boolean debug) {
        if(debug){ System.out.println("------------------------ LOADING IN DATA ------------------------");}
        if(debug){ System.out.println("Add root =[-1]");}

        TreeNode rootNode = new TreeNode(-1, -1, 0);
        int j = relArray[0][0];

        if(debug){ System.out.println("Add parent =["+relArray[0][0]+"]");}
        currentNode = rootNode.addChild(relArray[0][0], 0, 0);

        int parentCounter = 1;
        int childCounter = 0;

        for (int[] aRelArray : relArray) {
            if (j == aRelArray[0]) {
                if (debug) {
                    System.out.println("Add child [" + currentNode.getKey() + "][" + aRelArray[1] + "]");
                }
                this.currentNode.addChild(aRelArray[1], 1, childCounter);
                childCounter++;

            } else {
                if (debug) {
                    System.out.println("Add parent =[" + aRelArray[0] + "]");
                }

                currentNode = rootNode.addChild(aRelArray[0], 0, parentCounter);
                parentCounter++;
                j = aRelArray[0];

                if (debug) {
                    System.out.println("Add child [" + currentNode.getKey() + "][" + aRelArray[1] + "]");
                }

                childCounter = 0;
                this.currentNode.addChild(aRelArray[1], 1, childCounter);
                childCounter++;
            }
        }
        if(debug){ System.out.println("------------------------ FINISHED LOADING DATA ------------------------");}

        currentNode = rootNode;
    }

    /**
     * The RelationIterator which allows us to sort our collections easily.
     */
    @Override
    public RelationIterator<Integer> iterator() {
        RelationIterator<Integer> relIt = new RelationIterator<Integer>() {

            private boolean atEnd = false; // Keeping track whether our iterator is at it's end.

            /**
             * @return the key of the currentNode.
             */
            @Override
            public int key() {
                return currentNode.getKey();
            }

            /**
             * @return boolean whether we can still do a valid next() operation.
             */
            @Override
            public boolean hasNext() {
                return currentNode.hasNext();
            }

            /**
             * Checks whether next is possible, if not we set end to true, if so we change our current node to the next
             * item.
             * @return key of the current node.
             */
            @Override
            public Integer next() {
                System.out.println("1- curnode "+currentNode+ "parent "+ currentNode.getParent());
                if(currentNode.getBornIndexOfThisChild()+1 >= currentNode.getParent().getAmountOfChildren()){
                    atEnd = true;
                } else {
                    currentNode = currentNode.next();
                    System.out.println("2- curnode "+currentNode+ " parent "+ currentNode.getParent());
                }
                return currentNode.getKey();
            }

            /**
             * Searches for a specific value, sets the current node to the least upper bound of seekKey.
             * If no such upper bound exists, we set atEnd true and set the current node to the last possible node.
             * @param seekKey Value we are searching for.
             */
            @Override
            public void seek(int seekKey) {
                System.out.println(" in seek " + currentNode + " "+currentNode.getParent() + " " +currentNode.getBornIndexOfThisChild());
                binarySearch(currentNode.getParent(), currentNode.getBornIndexOfThisChild(), seekKey);
            }

            /**
             * Specifies whether we reached the end of the current depth.
             * @return whether we reached the end of the current depth.
             */
            @Override
            public boolean atEnd() {
                return atEnd;
            }

            /**
             * Used for sorting our array lists.
             * @param o Containing some other iterator.
             * @return which iterator has a larger value, true if this iterator has a higher value.
             */
            @Override
            public int compareTo(RelationIterator<Integer> o) {
                return Integer.compare(this.key(), o.key());
            }

            /**
             * Proceeds to set our current node to the first child of this node.
             */
            @Override
            public void open(){
                currentNode = currentNode.down();
            }

            /**
             * Proceeds to set our current node to the parent of this node.
             */
            @Override
            public void up(){
                atEnd = false;
                currentNode = currentNode.up();
            }

            /**
             * Does a binary search for searchValue for all children's of parentNode. Starting from index minIndex
             * up to the amount of children's the parent node has..
             * Finally it will always set our current node to either the upper bound or the maximum possible element.
             * It sets atEnd true if the maximum possible element is less then the searchValue.
             * @param parentNode The node that is the parent of the current node when we called this function.
             * @param minIndex The index at the parent node compared to the current node's brothers.
             * @param searchValue The value we are searching for.
             * Complexity? log(n) as this is the standard binary search.
             */
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
                        if(min+1 == parentNode.getAmountOfChildren()){
                            atEnd = true;
                            currentNode = parentNode.getChild(parentNode.getAmountOfChildren()-1);
                        } else {
                            currentNode = parentNode.getChild(min + 1);
                        }
                    }
                    if(parentNode.getChild(min).getKey() > searchValue){
                        currentNode = parentNode.getChild(min);
                    }
                }
            }

            /**
             * Outputs a debug string. Used while debugging.
             * Kept in for people wanting to understand the process better.
             * @return a string containing information about this iterator.
             */
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

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };

        return relIt;
    }

}
