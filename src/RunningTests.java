package src;

import java.io.IOException;

/**
 * Created by Administrator on 4/14/2017.
 */
public class RunningTests {
    public static void main(String[] args) throws IOException {
        String[] dataFiles = { // From smallest to largest
                "./data/self-created-test.txt",
                "./data/CA-GrQc",
                "./data/p2p-Gnutella04",
                "./data/facebook_combined",
                "./data/Wiki-Vote",
                "./data/twitter_combined"
        };
        LFTJ lftj;
        LFTJCacheCount lftjcc;

        System.out.println("----------- Starting -----------");
        for(int i=0; i<dataFiles.length; i++){
            System.out.println("----------- Data file : "+dataFiles[i]+" -----------");
            for(int j = 0 ; j < 2; j++){
                if(j==0){
                    LFTJ.CycleOrRoundsEnum cycleOrRounds = LFTJ.CycleOrRoundsEnum.PATH;
                } else {
                    LFTJ.CycleOrRoundsEnum cycleOrRounds = LFTJ.CycleOrRoundsEnum.CYCLE;
                }
                for( int k=4; k < 7; k++){
                    //lftj = new LFTJ(dataFiles[i], cycleOrRounds, k);
                }
            }
        }

    }
}
