/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src;

import java.io.IOException;
/**
 *
 * @author s131061
 */
public class TestRealTrees {
    private TestRealTrees(){
        TreeRelation rel1 = new TreeRelation(
                new int[][]{{1,4},{1,5},{1,7},{2,4},{2,8},{3,4},{3,7},{5,5},{6,1},{7,1}}, true);
        rel1.setUid(1);
        RelationIterator<Integer> rel1it = rel1.iterator();

        System.out.println("RootKey = "+rel1it.key()); // 0
        System.out.println("Opening up");
        rel1it.open();
        System.out.println("Key of first node = "+rel1it.key()); // 1
        System.out.println("We should still have a next value = "+rel1it.hasNext()); // True
        System.out.println("We open up the next level");
        rel1it.open();
        System.out.println("Key of the first child of the first node = "+rel1it.key()); // 4
        System.out.println("Key of the first child of the first node = "+rel1it.debugString()); // info
        rel1it.next();
        System.out.println("The key of the second child of the first node = "+rel1it.key()); // 5
        System.out.println("We should still have a next value = "+rel1it.hasNext()); // True
        rel1it.next();
        System.out.println("The key of the third child of the first node = "+rel1it.key()); // 7
        System.out.println("We should now not have a next value anymore = "+rel1it.hasNext()); // False
        rel1it.up();
        rel1it.next();
        System.out.println("Key of the second parent = "+rel1it.key()); // 2
        rel1it.next();
        System.out.println("Key of the third parent = "+rel1it.key()); // 3
        rel1it.next();
        System.out.println("Key of the fourth parent = "+rel1it.key()); // 5
        rel1it.next();
        System.out.println("Key of the fifth parent = "+rel1it.key()); // 6
        rel1it.next();
        System.out.println("Key of the fifth parent = "+rel1it.key()); // 7
        rel1it.up();
        rel1it.open();
        rel1it.seek(6);
        System.out.println("Key of the parent after seek for 6 = "+rel1it.key()); // 6
        rel1it.open();
        rel1it.seek(2);
        System.out.println("Key of the child after seek for 2 = "+rel1it.key()); // 1
        rel1it.up();
        System.out.println("Key of the parent of the last visited = "+rel1it.key()); // 6




    }


    public static void main(String[] args) throws IOException {
        // Create a LFTJ, load the datasets and ready to rumble
        TestRealTrees lftj = new TestRealTrees();
    }

}
