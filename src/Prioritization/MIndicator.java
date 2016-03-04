package Prioritization;

import java.util.ArrayList;

/**
 *  particular designed for quality evaluation of multi-objective
 *  solutions for prioritization problem
 */
public class MIndicator {

    public double[][] fitA ;    // size * 2
    public double[][] fitB ;    // size * 2

    public MIndicator( ArrayList<Sequence> a, ArrayList<Sequence> ref ) {
        initialize(a, ref);
    }

    public MIndicator( Sequence a, ArrayList<Sequence> ref ) {
        ArrayList<Sequence> tp = new ArrayList<>() ;
        tp.add(a);
        initialize(tp, ref);
    }

    /*
     *  initialize
     */
    private void initialize(  ArrayList<Sequence> a, ArrayList<Sequence> ref ) {
        fitA = new double[a.size()][2] ;
        fitB = new double[ref.size()][2] ;

        // normalize
        normalization(a, ref, fitA, fitB);
    }

    /*
     *  normalize cost and rfd value
     */
    public void normalization( ArrayList<Sequence> x, ArrayList<Sequence> y, double[][] d1, double[][] d2 ) {
        double max_cost = 0.0 ;
        double min_cost = Double.MAX_VALUE ;
        double max_value = 0 ;
        double min_value = Long.MAX_VALUE ;

        for( Sequence each : x ) {
            if( each.cost > max_cost )
                max_cost = each.cost ;
            if( each.cost < min_cost )
                min_cost = each.cost ;

            if( each.value > max_value )
                max_value = each.value ;
            if( each.value < min_value )
                min_value = each.value ;
        }
        for( Sequence each : y ) {
            if( each.cost > max_cost )
                max_cost = each.cost ;
            if( each.cost < min_cost )
                min_cost = each.cost ;

            if( each.value > max_value )
                max_value = each.value ;
            if( each.value < min_value )
                min_value = each.value ;
        }

        // normalized value
        for( int i = 0 ; i < x.size() ; i++ ) {
            Sequence each = x.get(i) ;
            d1[i][0] = (each.cost-min_cost) / (max_cost-min_cost) ;
            d1[i][1] = (each.value-min_value) / (max_value-min_value) ;
        }
        for( int i = 0 ; i < y.size() ; i++ ) {
            Sequence each = y.get(i) ;
            d2[i][0] = (each.cost-min_cost) / (max_cost-min_cost) ;
            d2[i][1] = (each.value-min_value) / (max_value-min_value) ;
        }
    }

    /*
     *  return EPSILON(A, REF) :
     *  The shortest distance that is required to transform every solution
     *  in A so that it dominates the reference front REF. It is a
     *  maximum-minimum-maximum measurement.
     */
    public double EPSILON() {

        int i, j ;
        double eps = 0.0, epsK, epsJ = 0.0 ;

        // for each point in reference front
        for( i = 0 ; i < fitB.length ; i++ ) {
            // for each point in front A
            for( j = 0 ; j < fitA.length ; j++ ) {
                // for each objective
                double diff0 = fitA[j][0] - fitB[i][0] ; // cost
                double diff1 = fitB[i][1] - fitA[j][1] ; // value
                epsK = diff0 > diff1 ? diff0 : diff1 ;

                // get the maximum-minimum
                if( j == 0 )
                    epsJ = epsK ;
                else if ( epsK < epsJ )
                    epsJ = epsK ;
            }

            // get the maximum-minimum-maximum
            if( i == 0 )
                eps = epsJ ;
            else if( epsJ > eps )
                eps = epsJ ;
        }

        return eps ;
    }


    /*
     *  return EPSILON(A, REF) :
     *  The average distance from solutions in REF to the closet
     *  solution in A
     */
    public double IGD() {
        double dist = 0.0 ;

        // for each point in reference front
        for( int i = 0 ; i < fitB.length ; i++ ) {
            double min = Double.MAX_VALUE ;

            // find the closet euclidean distance to point in A
            for( int j = 0 ; j < fitA.length ; j++ ) {
                double distTemp = Math.pow(fitB[i][0]-fitA[j][0], 2.0) + Math.pow(fitB[i][1]-fitA[j][1], 2.0);
                distTemp = Math.sqrt(distTemp) ;
                if( distTemp < min )
                    min = distTemp ;
            }
            dist += min ;
        }

        return dist / (double)fitB.length ;
    }

}
