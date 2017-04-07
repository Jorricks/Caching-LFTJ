/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src;

import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Cache {
    private ArrayList<VariableAssignment> vas; //
    VariableAssignment lastChecked; // The last checked variable assignment
    // For a given vas, which is at the same index as it would be at vas.
    // It contains the lists of all lists.
    private ArrayList<ArrayList<ArrayList<Integer>>> ownedKeys;
    
    /**
     * Constructor of this class
     */
    Cache(){
        vas = new ArrayList<>();
        ownedKeys = new ArrayList<>(new ArrayList<>());
    }
    
    /**
     * Function which adds a variable assignment to the current set vas.
     * @param ass Contains the variable assignment to be added to the set
     * Modifies? set vas to contain ass.
     */
    void addAssignment(VariableAssignment ass){
        vas.add(ass);
        ArrayList<ArrayList<Integer>> emptyArrayList = new ArrayList<>(new ArrayList<>());
        ownedKeys.add(emptyArrayList);
    }
    
    /**
     * Function which removes a variable assignment from the current set vas.
     * @param ass Contains the variable assignment to be removed from the set
     * Modifies? set vas to vas without ass.
     */
    public void removeAssignment(VariableAssignment ass){
        ownedKeys.remove(getAssKey(ass));
        vas.remove(ass);
    }


   /* public void addOwnedKey(VariableAssignment ass, ArrayList<Integer> keys, int newKey){
        boolean found = false;
        for (ArrayList<Integer> keyList : ownedKeys.get(getAssKey(ass))){
            if (keyList.equals(prefixOwnedKeys)){
                keyList.add(newKey);
                found = true;
            }
        }
        if(!found){*/
    public void addOwnedKeyResults(VariableAssignment ass, ArrayList<Integer> keys){
        ownedKeys.get(getAssKey(ass)).add(keys);
    }

    public ArrayList<ArrayList<Integer>> returnOwnedKeys(int key){
        return ownedKeys.get(getAssKey(key));
    }

    private int getAssKey(VariableAssignment ass){
        int result = 0;
        for(VariableAssignment vasItem: vas){
            if(ass.equals(vasItem)){
                return result;
            }
            result++;
        }
        System.out.println("Could not find VariableAssignment in current bag.");
        return -1;
    }

    private int getAssKey(int key){
        int result = 0;
        for(int i = 0; i < vas.size(); i++){
            if(vas.get(i).assignment == key){
                return result;
            }
            result++;
        }
        System.out.println("Could not find VariableAssignment in current bag.");
        return -1;
    }
    
    /**
     * Function which checks if the current set vas contains a specific variable assignment.
     * @param var Contains the variable value of the variable assignment we want to check.
     * @param ass Contains the assignment value of the variable assignment we want to check.
     * Modifies? lastChecked to the variable assignment in vas that matches var and ass.
     * This assignment can then be retrieved immediately after calling this function in order
     * to obtain the actual variable assignment.
     * @return true if vas contains a variable assignment with variable=var and 
     * assignment=ass, false otherwise
     */
    public boolean containsAssignment(int var, int ass){
        for(VariableAssignment va : vas) {
            if (va.variable == var && va.assignment == ass) {
                lastChecked = va;
                return true;
            }
        }
        return false;
    }
}
