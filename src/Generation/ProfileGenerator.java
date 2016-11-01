package Generation;

import Model.TestSuite;

import java.util.ArrayList;
import java.util.Random;

/**
 *  Demo Test
 */
public class ProfileGenerator {

    private Random rand;
    private ArrayList<int[]> suite;

    public ProfileGenerator() {
        rand = new Random();
        suite = new ArrayList<>();
    }

    private int[] randomSample( TestSuite ts ) {
        int[] test = new int[ts.system.parameter];
        for( int j = 0 ; j < ts.system.parameter ; j++ ) {
            test[j] = rand.nextInt(ts.system.value[j]);
        }
        return test ;
    }

    private boolean duplicateExist( int[] t, ArrayList<int[]> T) {
        for( int[] each : T ) {
            int equalNum = 0 ;
            for( int j = 0 ; j < t.length ; j++ ) {
                if( t[j] == each[j] )
                    equalNum++;
            }
            if( equalNum == t.length )
                return true;
        }
        return false;
    }

    private boolean duplicateExist( int[] t, ArrayList<int[]> T1, ArrayList<int[]> T2) {
        return duplicateExist(t, T1) || duplicateExist(t, T2);
    }

    // mode 1 -- highest t-way coverage
    // mode 2 -- highest profile coverage
    public void generation(TestSuite ts, int N, int size, int mode, double[] profile ) {
        int dim = ts.system.parameter ;
        suite.clear();

        ts.system.initialization();

        while ( suite.size() < size ) {
            // generate N candidates
            int[] best = new int[dim];
            double best_fit = Double.MIN_VALUE;

            int n = 0 ;
            ArrayList<int[]> candidate = new ArrayList<>();
            do {
                int[] tp = randomSample(ts);
                while ( duplicateExist(tp, suite, candidate) ) {
                    tp = randomSample(ts);
                }
                candidate.add(tp);

                double fit = -1 ;
                // compute fitness
                if( mode == 1 )
                    fit = (double)ts.system.FitnessValue(tp, 0);
                if( mode == 2 )
                    fit = ts.profileCoverage(transfer(suite, tp), profile);

                // compare
                if( fit > best_fit ) {
                    System.arraycopy(tp, 0, best, 0, dim);
                    best_fit = fit;
                }

                // next
                n++ ;

            } while( n < N );

            // add the best into suite
            suite.add(best);
            ts.system.FitnessValue(best, 1);

        } // end while

        // save to ts
        ts.tests = new int[suite.size()][dim] ;
        for( int i=0 ; i<suite.size() ; i++ ) {
            System.arraycopy(suite.get(i), 0, ts.tests[i], 0, dim);
        }
    }

    // return a + b as an array[][]
    private int[][] transfer( ArrayList<int[]> a, int[] b ) {
        int[][] array = new int[a.size()+1][];
        int index = 0 ;
        for( int[] each : a ) {
            array[index] = new int[each.length];
            System.arraycopy(each, 0, array[index], 0, each.length);
            index++;
        }
        array[index] = new int[b.length];
        System.arraycopy(b, 0, array[index], 0, b.length);
        return array ;
    }
}
