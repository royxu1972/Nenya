package Generation;

import Basic.TestSuite;

import java.util.ArrayList;
import java.util.Random;

public class RT {

    private Random rand;

    /*
     *  randomly sample a test case
     */
    private int[] randomSample( TestSuite ts ) {
        int[] test = new int[ts.system.parameter];
        for( int j = 0 ; j < ts.system.parameter ; j++ ) {
            test[j] = rand.nextInt(ts.system.value[j]);
        }
        return test ;
    }

    /*
     *  determine whether a test case exists in a test suite
     */
    private boolean duplicateExist( int[] t , ArrayList<int[]> T ) {
        for( int i = 0 ; i < T.size() ; i++ ) {
            int equalNum = 0 ;
            for( int j = 0 ; j < t.length ; j++ ) {
                if( t[j] == T.get(i)[j] )
                    equalNum++ ;
            }
            if( equalNum == t.length )
                return true ;
        }
        return false ;
    }

    /*
     *  generate as more test cases as possible under a given time span
     */
    public void GenerationViaSpan( TestSuite ts, long ec, long span ) {
        this.rand = new Random();
        int dim = ts.system.parameter ;

        // begin to sample
        ArrayList<int[]> tempArray = new ArrayList<>();
        long start = System.currentTimeMillis() ;
        long EC = 0 ;
        while ( System.currentTimeMillis() - start <= span - EC ) {
            int[] tp = randomSample(ts);
            while ( duplicateExist(tp, tempArray) ) {
                tp = randomSample(ts);
            }
            tempArray.add(tp);
            EC += ec ;
        }

        // save to ts
        ts.tests = new int[tempArray.size()][dim] ;
        for( int i=0 ; i<tempArray.size() ; i++ ) {
            System.arraycopy(tempArray.get(i), 0, ts.tests[i], 0, dim);
        }
    }

}
