package src;

import java.io.IOException;

/**
 * Created by Administrator on 4/14/2017.
 */
public class RunningTests {
    public static void main(String[] args) throws IOException {
        String[] dataFiles = { // From smallest to largest
                "./data/self-created-test.txt",
                "./data/CA-GrQc.txt",
                "./data/p2p-Gnutella04.txt",
                "./data/facebook_combined.txt",
                "./data/Wiki-Vote.txt",
                "./data/twitter_combined.txt"
        };
        LFTJ lftj;
        LFTJCacheCount lftjcc;
        LFTJ.CycleOrRoundsEnum cycleOrRounds;

        System.out.println("----------- Starting -----------");
        for(int i=0; i<dataFiles.length; i++){
            System.out.println("----------- Data file : "+dataFiles[i]+" -----------");
            System.out.println("Caching or no caching" + "\t" +
                    "Cycle or Path" + "\t" +
                    "Amount of relations" + "\t" +
                    "Initializing time" + "\t" +
                    "Computing time" + "\t" +
                    "Total running time" + "\t" +
                    "Amount of tuples found" + "\t" +
                    "Tuples found by cache" + "\t" +
                    "Tuples found by compution" + "\t" +
                    "Total cache hits" + "\t"
            );
            for(int j = 0 ; j < 2; j++){
                if(j==0){
                    cycleOrRounds = LFTJ.CycleOrRoundsEnum.PATH;
                } else {
                    cycleOrRounds = LFTJ.CycleOrRoundsEnum.CYCLE;
                }
                for( int k=4; k < 7; k++){
                    lftj = new LFTJ(dataFiles[i], cycleOrRounds, k);
                    lftj.multiJoin();
                    lftj = null;

                    lftjcc = new LFTJCacheCount(dataFiles[i], cycleOrRounds, k);
                    lftjcc.multiJoin();
                    lftjcc = null;

                }
            }
        }

    }
}
