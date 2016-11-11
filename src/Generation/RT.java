package Generation;

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
     *  Generate a fixed number of test suite
     */
    public void generationFixedSize(TestSuite ts, int size ) {
        // begin to sample
        ArrayList<int[]> array = new ArrayList<>();
        while ( array.size() < size ) {
            int[] tp = sample(ts);
            while ( array.contains(tp) ) {
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

}
