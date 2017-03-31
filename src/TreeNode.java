package src;

import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;
import java.util.ArrayList;

/**
 * Created by Administrator on 3/31/2017.
 */
class TreeNode {
    private TreeNode parent = null;
    private ArrayList<TreeNode> children;
    private int key;
    private int childNumber = 0;
    private int depth;

    // Only for the root node, the root node does not have a key
    TreeNode(int key, int depth, int childNumber) {
        setKey(key);
        setDepth(depth);
        setChildNumber(childNumber);
        children = new ArrayList<>();
    }

    TreeNode addChild(int childKey, int childDepth, int childNumber){
        if(childDepth<0){
            throw new ValueException("ChildDepth has to be at least 0");
        }
        TreeNode child = new TreeNode(childKey, childDepth, childNumber);
        child.setParent(this);
        children.add(child);
        return child;
    }

    // Return the parent
    TreeNode up(){
        return parent;
    }

    // Return the first child
    TreeNode down(){
        return children.get(0);
    }

    // Return whether there is another one left;
    boolean hasNext(){ // Also atEnd()
        return parent.children.size()-1 != childNumber;
    }

    // When calling next, we ask the parent for the next child.
    TreeNode next(){
        return parent.parentGetNextNode(childNumber);
    }
    private TreeNode parentGetNextNode(int currentChild){
        return children.get(currentChild+1);
    }

    int getKey(){
        return key;
    }

    int getDepth(){
        return depth;
    }

    int getAmountOfChildren(){
        return children.size();
    }

    TreeNode getChild(int numberOfChild){
        return children.get(numberOfChild);
    }

    TreeNode getParent(){
        return parent;
    }

    private void setChildNumber(int childNumber){
        this.childNumber = childNumber;
    }

    private void setDepth(int depth){
        this.depth = depth;
    }

    private void setKey(int key){
        this.key = key;
    }

    private void setParent(TreeNode parent){
        this.parent = parent;
    }
}
