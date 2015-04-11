package Prioritization;

import java.util.Comparator;

/**
 *  Multi-Objective Optimization
 */
public class Sequence {

    public int[] order ;

    // multi-objective optimality
    public int level ;
    public double crowd ;

    // fitness value
    public long value ;
    public int cost ;

    public Sequence( int[] seq, int cost, long value, int level, double crowd ) {
        order = new int[seq.length];
        System.arraycopy(seq, 0, this.order, 0, seq.length);
        this.level = level ;
        this.crowd = crowd ;
        this.cost = cost ;
        this.value = value ;
    }

    public void UpdateSequence( int[] seq ) {
        System.arraycopy(seq, 0, this.order, 0, seq.length);
    }

    public void UpdateFitness( int value, int cost ) {
        this.cost = cost ;
        this.value = value ;
    }

    public void UpdateLevel( int level ) {
        this.level = level ;
    }

    public void UpdateCrowd( double crowd ) {
        this.crowd = crowd ;
    }


    /*
     *  fitness comparison
     *  A > B : if A can make at least one attribute better than B
     *          without making any other worse off
     *  if A > B, namely A dominates B, return True, else, return False
     */
    public boolean isDominate( Sequence B ) {
        // low cost, large value is better
        if( this.cost < B.cost && this.value >= B.value )
            return true ;
        else if( this.cost <= B.cost && this.value > B.value )
            return true ;
        else
            return false ;
    }


    /*
     *  A is less than (negative integer), equal (zero) or
     *  greater than (positive integer) specified sequence B
     */
    static class allSort implements Comparator<Sequence> {
        public int compare( Sequence A, Sequence B ) {
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

    static class costSort implements Comparator<Sequence> {
        public int compare( Sequence A, Sequence B ) {
            if( A.cost < B.cost )
                return -1 ;      // A is better
            else if( A.cost == B.cost )
                return 0 ;
            else
                return 1 ;
        }
    }

    static class valueSort implements Comparator<Sequence> {
        public int compare( Sequence A, Sequence B ) {
            if( A.value > B.value )
                return -1 ;      // A is better
            else if( A.value == B.value )
                return 0 ;
            else
                return 1 ;
        }
    }


}



