package EA.NSGA;

import java.util.Comparator;

/**
 *  NSGA-II for test suite prioritization.
 */
public class NSSolution2D {

    public int[] solution;

    // multi-objective optimality
    public int level ;
    public double crowd ;

    // fitness
    public double value ;   // to be maximized
    public double cost ;    // to be minimized

    public NSSolution2D( int[] s, double cost, double value, int level, double crowd ) {
        this.solution = s.clone() ;
        this.level = level ;
        this.crowd = crowd ;
        this.cost = cost ;
        this.value = value ;
    }

    @Override
    public NSSolution2D clone() {
        return new NSSolution2D(solution, cost, value, level, crowd);
    }

    @Override
    public String toString() {
        return "cost=" + cost + ", value=" + value +
                ", level=" + level + ", crowd=" + crowd ;
    }

    public void updateFitness( double value, double cost ) {
        this.cost = cost ;
        this.value = value ;
    }

    public void updateLevel(int level) {
        this.level = level ;
    }
    public void updateCrowd(double crowd) {
        this.crowd = crowd ;
    }

    /*
     *  Determine whether a solution A dominates another solution B.
     *  A > B : if A can make at least one attribute better than B
     *          without making any other worse off
     *  if A > B (A dominates B), return True, else, return False
     */
    public boolean isDominate( NSSolution2D B ) {
        // low cost is better
        // high value is better
        if( cost < B.cost && value >= B.value )
            return true ;
        else if( cost <= B.cost && value > B.value )
            return true ;
        else
            return false ;
    }

    /*
     *  A is less than (negative integer), equal (zero) or
     *  greater than (positive integer) specified sequence B
     */
    static class allSort implements Comparator<NSSolution2D> {
        public int compare(NSSolution2D A, NSSolution2D B ) {
            if( A.level < B.level )
                return -1 ;      // A is better
            else if( A.level == B.level ) {
                if( A.crowd > B.crowd )
                    return -1 ;   // A is better
                else if( A.crowd == B.crowd )
                    return 0 ;
                else
                    return 1 ;
            }
            else
                return 1 ;
        }
    }

    static class costSort implements Comparator<NSSolution2D> {
        public int compare(NSSolution2D A, NSSolution2D B ) {
            if( A.cost < B.cost )
                return -1 ;      // A is better
            else if( A.cost == B.cost )
                return 0 ;
            else
                return 1 ;
        }
    }

    static class valueSort implements Comparator<NSSolution2D> {
        public int compare(NSSolution2D A, NSSolution2D B ) {
            if( A.value > B.value )
                return -1 ;      // A is better
            else if( A.value == B.value )
                return 0 ;
            else
                return 1 ;
        }
    }

    /*
     *  Determine weather two solutions are equal.
     */
    public boolean isEqual( NSSolution2D B ) {
        for(int i = 0; i < solution.length ; i++ ) {
            if( this.solution[i] != B.solution[i] ) {
                return false ;
            }
        }
        return true ;
    }

}



