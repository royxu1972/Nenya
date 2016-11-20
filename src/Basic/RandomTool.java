package Basic;

import Model.TestSuite;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

/**
 * generate random relations
 */
public class RandomTool {

    private Random random;
    public RandomTool() {
        random = new Random();
    }

    /**
     * Generate a random value ~ N(m, s).
     * @param m mean
     * @param s standard deviation
     * @return random value
     */
    public double Gaussian( double m, double s ) {
        return Math.sqrt(s)* random.nextGaussian() + m ;
    }

    /**
     * Generate a random t-way combination.
     * @param t strength
     * @param p number of parameters
     * @param val number of each parameter value
     * @return t-way combination
     */
    public int[] Schema( int t, int p, final int[] val ) {
        int[] re = new int[p] ;
        for( int k=0 ; k<p ; k++ )
            re[k] = -1 ;

        HashSet<Integer> pos_set = new HashSet<>() ;
        while ( pos_set.size() != t )
            pos_set.add( random.nextInt(p) );

        for( Integer x : pos_set ) {
            re[x] = random.nextInt( val[x] );
        }

        return re ;
    }

    /**
     * Generate a random t-way combination that exists in the given test suite.
     * @param t strength
     * @param p number of parameters
     * @param val number of each parameter value
     * @param TS test suite
     * @return t-way combination
     */
    public int[] SchemaExist( int t, int p, final int[] val, TestSuite TS ) {
        int[] re ;
        while( true ) {
            re = Schema(t, p, val) ;  // get a random schema
            // check
            for( int i = 0 ; i < TS.tests.length ; i ++ ) {
                int flag = 0 ;
                for( int k=0 ; k<TS.system.parameter ; k++ ) {
                    if( re[k] != -1 && re[k] == TS.tests[i][k] )
                        flag++ ;
                }
                if( flag == t )
                    return re ;
            }
        }
    }

    /**
     * Generate a set of random t-way combinations, which are
     * as evenly spread as possible.
     * @param schemas list for generated combinations
     * @param num the size of required set
     * @param t strength
     * @param p number of parameters
     * @param val number of each parameter value
     */
    public void SchemaSet( ArrayList<int[]> schemas, int num, int t, int p, int[] val ) {
        schemas.clear();
        for( int i=0 ; i<num ; i++ ) {
            int[] best = new int[p];
            int fit_best = 0 ;
            // generate 10 candidates (FSCS-like)
            for( int j=0 ; j<10 ; j++ ) {
                int[] s = Schema(t, p, val);
                // evaluate s, hamming distance
                int fit_s = 2 * t ;
                for( int[] each : schemas ) {
                    int fit = 0 ;
                    for( int k=0 ; k<p ; k++ ) {
                        if( each[k] != s[k] )
                            fit++ ;
                    }
                    if( fit < fit_s )
                        fit_s = fit ;
                }
                // whether select s
                if( fit_s > fit_best ) {
                    fit_best = fit_s ;
                    best = s.clone() ;
                }
            }
            // add the best one
            int[] added = best.clone();
            schemas.add(added);
        }
    }
}
