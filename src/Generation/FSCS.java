package Generation;

import Basic.TestSuite;

import java.util.Random;

/**
 *  ART generator FSCS
 */
public class FSCS {

    private Random rand;

    // size: the number of test cases to be generated
    // k:    the number of candidates, default = 10
    public void Generation( TestSuite ts, int size ) {
        this.Generation(ts, size, 10);
    }
    public void Generation( TestSuite ts, int size, int k ) {
        this.rand = new Random();
        int dim = ts.system.parameter ;
        ts.tests = new int[size][dim] ;

        // set first one randomly
        for( int j = 0 ; j < dim ; j++ )
            ts.tests[0][j] = rand.nextInt(ts.system.value[j]);

        // generate the others
        for( int i = 1 ; i < size ; i++ )
            this.NextTest(ts, i, k);
    }

    // index: the index of filled test case
    // k:     number of new random candidates
    private void NextTest( TestSuite ts, int index, int k ) {
        int dim = ts.system.parameter ;

        int[][] candidates = new int[k][dim];
        double[] fitness = new double[k];

        //generate candidate tests
        for ( int i = 0 ; i < k ; i++)  {
            for( int j = 0 ; j < dim ; j++ ){
                candidates[i][j]= rand.nextInt(ts.system.value[j]);
            }
            fitness[i] = Double.MAX_VALUE ;
        }

        // find maximum-minimum distance
        // A. minimum distance
        double dist;
        for ( int i = 0 ; i < k ; i++ ) {
            for ( int j = 0; j < index; j++ ) {
                dist = this.Distance( candidates[i], ts.tests[j] );
                if ( dist < fitness[i] )
                    fitness[i] = dist ;
            }
        }

        // B. maximum distance
        int maxIndex = 0 ;
        double maxFit = fitness[0] ;
        for ( int i = 1 ; i < k ; i++ ) {
            if( fitness[i] > maxFit ) {
                maxIndex = i ;
                maxFit = fitness[i] ;
            }
        }

        // add new test case into ts.test
        for( int m = 0 ; m < dim ; m++ ) {
            ts.tests[index][m] = candidates[maxIndex][m] ;
        }

    }

    // distance measure: Euler distance
    private double Distance( int[] x, int[] y) {
        int dim = x.length ;
        double dist = 0;
        for( int i=0 ; i<dim ; i++ ) {
            dist += (double)( (x[i]-y[i]) * (x[i]-y[i]) );
        }
        return ( Math.sqrt(dist) );
    }

}
