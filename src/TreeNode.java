package src;

import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;
import java.util.ArrayList;

/**
 * A TreeNode can be either the root, a leaf or anything in between. There is no need to split them up.
 */
class TreeNode {
    private TreeNode parent = null; // For the root parent it is null, all other types it is equal to it's parent.
    private ArrayList<TreeNode> children; // Containing the list of all children's.
    private int key; // Containing the note's key value.
    private int childNumber = 0; // Containing how much children were born before him(with the same parent).
    private int depth; // The current depth we are at.

    /**
     * Constructor for the TreeNode, sets the parameters and initializes the children array.
     * @param key The key value of the node.
     * @param depth The depth at which this node is inserted.
     * @param childNumber The amount of siblings that were born before this node.
     */
    TreeNode(int key, int depth, int childNumber) {
        setKey(key);
        setDepth(depth);
        setChildNumber(childNumber);
        children = new ArrayList<>();
    }

    /**
     * We add a child to the current node.
     * @param childKey The key value of the child.
     * @param childDepth The depth at which this child is inserted.
     * @param childNumber The amount of siblings that were born before this child.
     * @return TreeNode of this child.
     */
    TreeNode addChild(int childKey, int childDepth, int childNumber){
        if(childDepth<0){
            throw new ValueException("ChildDepth has to be at least 0");
        }
        TreeNode child = new TreeNode(childKey, childDepth, childNumber);
        child.setParent(this);
        children.add(child);
        return child;
    }

    /**
     * @return parent of this node.
     */
    TreeNode up(){
        return parent;
    }

    /**
     * @return the first child of this node.
     */
    TreeNode down(){
        return children.get(0);
    }

    /**
     * @return whether we can still continue to call next.
     */
    boolean hasNext(){ // Also atEnd()
        return parent.children.size()-1 != childNumber;
    }

    /**
     * @return the sibling born after this node.
     */
    TreeNode next(){
        return parent.getChild(childNumber+1);
    }

    /**
     * @return key value of this node.
     */
    int getKey(){
        return key;
    }

    /**
     * @return depth of this node.
     */
    int getDepth(){
        return depth;
    }

    /**
     * @return the amount of children this node has.
     */
    int getAmountOfChildren(){
        return children.size();
    }

    /**
     * @return the amount of siblings that were born before this child.
     */
    int getBornIndexOfThisChild(){
        return childNumber;
    }

    /**
     * Gets the child at a specific index.
     * @param numberOfChild the index of the currentChild.
     * @return TreeNode of the child at the given index.
     */
    TreeNode getChild(int numberOfChild){
        return children.get(numberOfChild);
    }

    /**
     * Gets the parent at a specific index.
     * @return TreeNode of the parent.
     */
    TreeNode getParent(){
        return parent;
    }

    /**
     * Sets the child number.
     * @param childNumber How much siblings were born before this node.
     */
    private void setChildNumber(int childNumber){
        this.childNumber = childNumber;
    }

    /**
     * Sets the depth of this note.
     * @param depth of this note.
     */
    private void setDepth(int depth){
        this.depth = depth;
    }

    /**
     * Sets the key of this note.
     * @param key value of this note.
     */
    private void setKey(int key){
        this.key = key;
    }

    /**
     * Sets the parent of this note.
     * @param parent TreeNote of the parent.
     */
    private void setParent(TreeNode parent){
        this.parent = parent;
    }
}
