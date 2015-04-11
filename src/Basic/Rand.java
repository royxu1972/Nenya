package Basic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

/**
 * generate random variables
 */
public class Rand {

    private Random rand ;
    public Rand() {
        rand = new Random() ;
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
    public int[] Schema( int t, int p, int[] val ) {
        int[] re = new int[p] ;
        for( int k=0 ; k<p ; k++ )
            re[k] = -1 ;

        HashSet<Integer> posset = new HashSet<Integer>() ;
        while ( posset.size() != t )
            posset.add( rand.nextInt(p) );

        for( Integer x : posset ) {
            re[x] = rand.nextInt( val[x] );
        }

        return re ;
    }

    /*
     *  generate a set of random t-way schemas, and make them as evenly spread as possible
     *  the result will be saved in ArrayList<int[]> Schemas
     */
    public void Schemas( ArrayList<int[]> Schemas, int num, int t, int p, int[] val ) {
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
