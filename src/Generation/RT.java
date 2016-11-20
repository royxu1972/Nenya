package Generation;

import Basic.ALG;
import Model.TestSuite;

import java.util.ArrayList;
import java.util.Random;

public class RT {

    private Random random;

    public RT() {
        random = new Random();
    }

    /*
     *  Randomly sample a test case
     */
    private int[] sample( TestSuite ts ) {
        int[] test = new int[ts.system.parameter];
        for( int j = 0 ; j < ts.system.parameter ; j++ )
            test[j] = random.nextInt(ts.system.value[j]);
        return test ;
    }

    /*
     *  Generate a fixed number of test suite.
     *  If the newly generated test case has invalid combinations,
     *  simply discard it and generate a new one.
     */
    public void generationFixedSize(TestSuite ts, int size) {
        // begin to sample
        ArrayList<int[]> array = new ArrayList<>();
        while ( array.size() < size ) {
            int[] tp = sample(ts);
            while ( ALG.inList(array, tp) || !ts.system.isValid(tp) ) {
                tp = sample(ts);
            }
            array.add(tp);
        }
        // save to ts
        ts.tests = new int[array.size()][] ;
        for( int i=0 ; i<array.size() ; i++ ) {
            ts.tests[i] = array.get(i).clone();
        }
    }

    public void generationExhaustive(TestSuite ts) {
        int size = 1;
        int[] max = ts.system.value.clone();
        for( int k=0 ; k<max.length ; k++ )
            size = size * max[k];

        ArrayList<int[]> array = new ArrayList<>();

        int[] t = new int[ts.system.parameter];
        int end = t.length - 1;
        for( int i=0 ; i<size ; i++ ) {
            // if t is constraint satisfied, then add it into tests
            if( ts.system.isValid(t) )
                array.add(t.clone());

            // goto the next test case
            t[end] = t[end] + 1 ;
            int ptr = end ;
            while( ptr > 0 ) {
                if( t[ptr] == max[ptr] ) {
                    t[ptr] = 0;
                    t[--ptr]++;
                } else {
                    break;
                }
            }
        } // end for

        // save to ts
        ts.tests = new int[array.size()][] ;
        for( int i=0 ; i<array.size() ; i++ ) {
            ts.tests[i] = array.get(i).clone();
        }
    }

}
