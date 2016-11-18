package Generation;

import Model.SUT;
import Model.TestSuite;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class RCT {

    private Random random;
    private SUT[] profileSUT ;

    public RCT() {
        random = new Random();
        profileSUT = new SUT[6];
    }

    private int[] sample( TestSuite ts ) {
        int[] test = new int[ts.system.parameter];
        for( int j = 0 ; j < ts.system.parameter ; j++ )
            test[j] = random.nextInt(ts.system.value[j]);
        return test ;
    }

    private boolean duplicate( final int[] t, ArrayList<int[]> arr) {
        for( int[] each : arr ) {
            if(Arrays.equals(t, each))
                return true;
        }
        return false;
    }

    // maximize the t-way coverage
    public void generationFixed(TestSuite ts, int size, int t) {
        ts.system.t_way = t;
        generation(ts, 30, size, 1, null);
    }
    // maximize profile coverage
    public void generationProfile(TestSuite ts, int size, double[] profile) {
        generation(ts, 30, size, 2, profile);
    }
    // generation
    public void generation(TestSuite ts, int N, int size, int mode, double[] profile ) {
        ArrayList<int[]> suite = new ArrayList<>();
        int dim = ts.system.parameter;

        // t-way coverage
        if( mode == 1 )
            ts.system.initialization();
        // profile coverage
        if( mode == 2 ) {
            for (int k = 0; k < 6; k++) {
                profileSUT[k] = new SUT(ts.system.parameter, ts.system.value, k+1);
                profileSUT[k].initialization();
            }
        }

        while ( suite.size() < size ) {
            // generate N random candidates, and select the best one
            int[] best = new int[dim];
            double best_fit = Double.MIN_VALUE;

            ArrayList<int[]> candidate = new ArrayList<>();
            for ( int i=0 ; i<N ; i++ ) {
                int[] tp = sample(ts);
                while ( duplicate(tp, candidate) || duplicate(tp, suite) )
                    tp = sample(ts);
                candidate.add(tp);

                double fit = 0 ;
                // compute fitness
                if( mode == 1 )
                    fit = (double)ts.system.FitnessValue(tp, 0);
                if( mode == 2 ) {
                    for( int k=0 ; k<6 ; k++ )
                        fit += profile[k] * (double)profileSUT[k].FitnessValue(tp, 0);
                }

                // compare
                if( fit > best_fit ) {
                    best = tp.clone();
                    best_fit = fit;
                }
            }

            // add the best into suite
            suite.add(best);

            // t-way
            if( mode == 1 )
                ts.system.FitnessValue(best, 1);
            // profile
            if( mode == 2 ) {
                for (int k = 0; k < 6; k++)
                    profileSUT[k].FitnessValue(best, 1);
            }

        } // end while

        // save to ts
        ts.tests = new int[suite.size()][] ;
        for( int i=0 ; i<suite.size() ; i++ ) {
            ts.tests[i] = suite.get(i).clone();
        }
    }
}
