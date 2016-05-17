package EA.NSGA;

import java.util.ArrayList;

/**
 *  Indicator is used to evaluate the final solution set
 *  of multi-objective optimization algorithms.
 */
public class Indicator2D {

    public double[][] front;              // size * 2
    public double[][] reference_front;    // size * 2

    public Indicator2D(ArrayList<NSSolution2D> a, ArrayList<NSSolution2D> ref ) {
        initialize(a, ref);
    }

    public Indicator2D(NSSolution2D a, ArrayList<NSSolution2D> ref ) {
        ArrayList<NSSolution2D> tp = new ArrayList<>() ;
        tp.add(a);
        initialize(tp, ref);
    }

    /*
     *  initialize
     */
    private void initialize(ArrayList<NSSolution2D> a, ArrayList<NSSolution2D> ref ) {
        front = new double[a.size()][2] ;
        reference_front = new double[ref.size()][2] ;
        normalization(a, ref, front, reference_front);
    }

    /*
     *  normalize cost and rfd value
     */
    public void normalization(ArrayList<NSSolution2D> x, ArrayList<NSSolution2D> y, double[][] d1, double[][] d2 ) {
        double max_cost = 0.0 ;
        double min_cost = Double.MAX_VALUE ;
        double max_value = 0 ;
        double min_value = Long.MAX_VALUE ;

        ArrayList<NSSolution2D> tp = new ArrayList<>();
        tp.addAll(x);
        tp.addAll(y);

        for( NSSolution2D each : tp ) {
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
            NSSolution2D each = x.get(i) ;
            d1[i][0] = (each.cost-min_cost) / (max_cost-min_cost) ;
            d1[i][1] = (each.value-min_value) / (max_value-min_value) ;
        }
        for( int i = 0 ; i < y.size() ; i++ ) {
            NSSolution2D each = y.get(i) ;
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
        for(i = 0 ; i < reference_front.length ; i++ ) {
            // for each point in front A
            for(j = 0 ; j < front.length ; j++ ) {
                // for each objective
                double diff0 = front[j][0] - reference_front[i][0] ; // cost
                double diff1 = reference_front[i][1] - front[j][1] ; // value
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
        for(int i = 0; i < reference_front.length ; i++ ) {
            double min = Double.MAX_VALUE ;

            // find the closet euclidean distance to point in A
            for(int j = 0; j < front.length ; j++ ) {
                double distTemp = Math.pow(reference_front[i][0]- front[j][0], 2.0) + Math.pow(reference_front[i][1]- front[j][1], 2.0);
                distTemp = Math.sqrt(distTemp) ;
                if( distTemp < min )
                    min = distTemp ;
            }
            dist += min ;
        }

        return dist / (double) reference_front.length ;
    }

}
