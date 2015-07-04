package Basic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

/**
 * generate random variables
 */
public class Rand {

    private Random rand ;
    public Rand( long seed ) {
        rand = new Random(seed);
    }
    public Rand() {
        rand = new Random();
    }

    /*
     *  generate a random vale ~ N(m, s)
     */
    public double Gaussian( double m, double s ) {
        return Math.sqrt(s)*rand.nextGaussian() + m ;
    }

    /*
     *  generate a random t-way schema
     *  example: Schema(2, 4, [3, 3, 3, 3]) -> [-1, -1, 2, 0]
     */
    public int[] Schema( int t, int p, final int[] val ) {
        int[] re = new int[p] ;
        for( int k=0 ; k<p ; k++ )
            re[k] = -1 ;

        HashSet<Integer> pos_set = new HashSet<>() ;
        while ( pos_set.size() != t )
            pos_set.add( rand.nextInt(p) );

        for( Integer x : pos_set ) {
            re[x] = rand.nextInt( val[x] );
        }

        return re ;
    }

    /*
     *  generate a random t-way schema that exists in the given test suite TS
     */
    public int[] SchemaExist( int t, int p, final int[] val, TestSuite TS ) {
        int[] re ;
        while( true ) {
            // get a random schema
            re = Schema(t, p, val) ;

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

    /*
     *  generate a set of random t-way schemas, and make them as evenly spread as possible
     *  the result will be saved in ArrayList<int[]> Schemas
     */
    public void SchemaSet( ArrayList<int[]> Schemas, int num, int t, int p, int[] val ) {
        Schemas.clear();

        for( int i=0 ; i<num ; i++ ) {
            int[] best = new int[p];
            int fit_best = 0 ;

            // generate 10 candidates (FSCS)
            for( int j=0 ; j<10 ; j++ ) {
                int[] s = Schema(t, p, val);

                // evaluate s, hamming distance
                int fit_s = 2 * t ;
                for( int[] each : Schemas ) {
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
            Schemas.add(added);
        }
    }
}
