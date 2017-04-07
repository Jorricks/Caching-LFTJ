/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Cache {
    Set<VariableAssignment> vas; //
    VariableAssignment lastChecked; // The last checked variable assignment
    ArrayList<ArrayList<Integer>> Owns; // All the tuple key values that it owns.

    
    /**
     * Constructor of this class
     */
    Cache(){
        vas = new HashSet<>();
    }
    
    /**
     * Function which adds a variable assignment to the current set vas.
     * @param ass Contains the variable assignment to be added to the set
     * Modifies? set vas to contain ass.
     */
    void addAssignment(VariableAssignment ass){
        vas.add(ass);
    }
    
    /**
     * Function which removes a variable assignment from the current set vas.
     * @param ass Contains the variable assignment to be removed from the set
     * Modifies? set vas to vas without ass.
     */
    public void removeAssignment(VariableAssignment ass){
        vas.remove(ass);
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
