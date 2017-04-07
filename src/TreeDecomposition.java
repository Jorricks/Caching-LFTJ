/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src;

import java.io.IOException;
import java.util.ArrayList;
import src.LFTJ.CycleOrRoundsEnum;

public class TreeDecomposition {
    int nrOfVariables;
    int nrOfBags;
    ArrayList<ArrayList<Integer>> bags = new ArrayList<>();
    ArrayList<ArrayList<Integer>> owned = new ArrayList<>();
    ArrayList<ArrayList<Integer>> adhesion = new ArrayList<>();
    ArrayList<Integer> owner = new ArrayList<>();
    
    /**
     * FConstructor of this class
     */
    public TreeDecomposition(Enum CycleOrRounds, int amountOfPathOrCycle) {
        nrOfVariables = amountOfPathOrCycle;
        nrOfBags = nrOfVariables - 1;
        createBags(CycleOrRounds);
        createAdhesion();
        createOwner();
        createOwned();
    }
    
    /**
     * Function that creates bags, in order to turn a graph into a tree decomposition.
     * This is done according to the paper, section 2.
     * Modifies? the arrayList bags to contain all bags of the tree.
     */
    private void createBags(Enum CycleOrRounds){
        //for a path query, bag 0 holds variables 0 and 1, bag 1  holds variables 1 and 2, etc..
        if (CycleOrRounds == CycleOrRoundsEnum.PATH){
            for(int i = 0; i < nrOfBags; i++) {
                ArrayList<Integer> tempBag = new ArrayList<>();
                tempBag.add(i);
                tempBag.add(i+1);
                bags.add(tempBag);
            }
        }
        printBags();
        
    }
    
   /**
     * Function that calculates the adhesion for each bag.
     * This is done according to the paper, section 2.
     * Modifies? the arrayList adhesion to contain, for each bag, the adhesion of the bag.
     */
    private void createAdhesion(){
        adhesion.add(new ArrayList(){});//adhesion for root is empty
        System.out.println("bag size "+bags.size());
        //for each bag, except the root, calculate the adhesion
        for(int i = 1; i < bags.size() ; i++){
            ArrayList<Integer> tempAdh = new ArrayList<>();
            //check for each item in the parent bag if it also occurs in the current bag, if so add to adhesion
            //System.out.println("bag subsize "+ i + " " +bags.get(i).size());
            for(int j = 0; j < bags.get(i).size(); j++) {
                int tempVar = bags.get(i).get(j);
                //System.out.println("tempvar: " + tempVar);
                if (bags.get(i-1).contains(tempVar)){
                    
                    tempAdh.add(tempVar);
                }
            }
            adhesion.add(tempAdh);
        }
        printAdhesion();
    }
    
    /**
     * Function that calculates the owner of each variable.
     * This is done according to the paper, section 2.
     * Modifies? the arrayList owner to contain, for each variable, which bag owns it.
     */
    private void createOwner(){
        //for all variables, check which bag is the owner
        for(int i = 0; i < nrOfVariables; i++) {
            for(int j = 0; j < bags.size(); j++) {
                if(bags.get(j).contains(i)) {
                    owner.add(j);
                    break;
                }
            }
        }
        printOwners();
    }
    
    /**
     * Function that calculates, for each bag, which variables it owns.
     * This is done according to the paper, section 2.
     * Modifies? the arrayList owned to contain, for each bag, which variables it owns.
     */
    private void createOwned(){
        //for all bags, checked which variables it owns
        for(int i = 0; i < bags.size(); i++){
            ArrayList<Integer> tempOwned = new ArrayList<>();
            for(int j = 0; j < nrOfVariables; j++) {
                if(owner.get(j) == i) {
                    tempOwned.add(j);
                }
            }
            owned.add(tempOwned);
        }
        printOwned();
    }
    
    /**
     * Function that prints all bags.
     */
    private void printBags(){
        //System.out.println("bag size "+bags.size());
        for(int i = 0; i < bags.size(); i++) {
            System.out.println(" Bag " + i+ " holds variable " + bags.get(i)+ "; ");
        }
    }
    
    /**
     * Function that prints all adhesions.
     */
    private void printAdhesion(){
        for(int i =0; i < bags.size(); i++) {
            System.out.println(" Adhesion " + i+ " is " + adhesion.get(i)+ "; ");
        }
    }
    
    /**
     * Function that prints for each variable which bag owns it.
     */
    private void printOwners(){
        for(int i = 0; i < nrOfVariables; i++){
            System.out.println("Owner of "+i+ " = " + owner.get(i) );
        }
    }
    
    /**
     * Function that prints for all bags which variables it owns.
     */
    private void printOwned(){
        for(int i = 0; i < nrOfBags; i++) {
            System.out.println("Bag " + i + " owns " + owned.get(i));
        }
    }
    
    /**
     * @param args the command line arguments.
     */
    public static void main(String[] args) throws IOException {
        TreeDecomposition td = new TreeDecomposition(CycleOrRoundsEnum.PATH, 4); 
        //td.multiJoin(); // We start the joins
    }
}

