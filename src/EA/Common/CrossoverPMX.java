package EA.Common;

import java.util.*;

/**
 *  Partially Matched Crossover (PMX)
 *  A portion of one parent's string is mapped onto a portion of the
 *  other parent's string, and the remaining information is exchanged.
 */
public class CrossoverPMX implements OperatorCrossover {

    @Override
    public String toString() {
        return "PMX";
    }

    /*
     *  Example
     *  p1 : 1 5 | 2 8 7 | 4 3 6
     *  p2 : 4 2 | 5 8 1 | 3 6 7
     *  --------------------------
     *  mapping: 2-5, 8-8, 7-1
     *  --------------------------
     *  o1 : 4 2' | 2 8 7 | 3 6 7'
     *  ==>  4 5  | 2 8 7 | 3 6 1
     *
     *  o2 : 1' 5' | 5 8 1 | 4 3 6
     *  ==>  7  2  | 5 8 1 | 4 3 6
     */
    @Override
    public List<int[]> crossover(int[] a, int[] b, Random r) {
        int LEN = a.length ;

        int[] child1 = new int[LEN] ;
        int[] child2 = new int[LEN] ;

        // generate two cut points
        int cut1 = r.nextInt(LEN);
        int cut2 = cut1 + r.nextInt(LEN - cut1);

        // the mapping relationship [cut1, cut2]
        Map<Integer, Integer> mapping1 = new HashMap<>();
        Map<Integer, Integer> mapping2 = new HashMap<>();

        for( int k = cut1 ; k <= cut2 ; k++ ) {
            child1[k] = a[k] ;
            child2[k] = b[k] ;
            mapping1.put(a[k], b[k]);
            mapping2.put(b[k], a[k]);
        }

        // the remaining part
        for( int k = 0 ; k < LEN ; k++ ) {
            if( k < cut1 || k > cut2 ) {
                int tp = b[k] ;
                while( mapping1.containsKey(tp) )
                    tp = mapping1.get(tp);
                child1[k] = tp ;

                tp = a[k] ;
                while( mapping2.containsKey(tp) )
                    tp = mapping2.get(tp);
                child2[k] = tp ;
            }
        }

        List<int[]> children = new ArrayList<>();
        children.add(child1);
        children.add(child2);

        return children ;
    }
}
