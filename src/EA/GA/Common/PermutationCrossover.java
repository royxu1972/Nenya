package EA.GA.Common;

import java.util.*;

public class PermutationCrossover implements OperatorCrossover<Permutation> {

    private Random random = new Random();

    @Override
    public List<Permutation> crossover(Permutation a, Permutation b, double PRO) {
        if( random.nextDouble() < PRO )
            return PMX(a, b);
        else {
            List<Permutation> children = new ArrayList<>();
            children.add(a.clone());
            children.add(b.clone());
            return children;
        }
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
    private List<Permutation> PMX(Permutation a, Permutation b) {
        int LEN = a.solution.length ;

        int[] child1 = new int[LEN] ;
        int[] child2 = new int[LEN] ;

        // generate two cut points
        int cut1 = random.nextInt(LEN);
        int cut2 = cut1 + random.nextInt(LEN - cut1);

        // the mapping relationship [cut1, cut2]
        Map<Integer, Integer> mapping1 = new HashMap<>();
        Map<Integer, Integer> mapping2 = new HashMap<>();

        for( int k = cut1 ; k <= cut2 ; k++ ) {
            child1[k] = a.solution[k] ;
            child2[k] = b.solution[k] ;
            mapping1.put(a.solution[k], b.solution[k]);
            mapping2.put(b.solution[k], a.solution[k]);
        }

        // the remaining part
        for( int k = 0 ; k < LEN ; k++ ) {
            if( k < cut1 || k > cut2 ) {
                int tp = b.solution[k] ;
                while( mapping1.containsKey(tp) )
                    tp = mapping1.get(tp);
                child1[k] = tp ;

                tp = a.solution[k] ;
                while( mapping2.containsKey(tp) )
                    tp = mapping2.get(tp);
                child2[k] = tp ;
            }
        }

        List<Permutation> children = new ArrayList<>();
        children.add(new Permutation(child1));
        children.add(new Permutation(child2));
        return children ;
    }
}
