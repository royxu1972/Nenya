package Prioritization;

import Basic.TestSuite;

import java.util.*;

/**
 *  Multi-Objective Optimization, NSGA-II
 *  designed specifically for test suite prioritization according to
 *  two goals: testing cost (execution + switching) and 2-way combination coverage
 */
public class MEvolution {

    private MPopulation pool;
    private TestSuite ts ;
    private Random random ;

    // parameter setting
    private int N ;
    private int ITE ;
    private double CROSS_PRO ;
    private double MUTATION_PRO ;

    public MEvolution( int size, int iteration, double cross, double mutation, TestSuite ts ) {
        this.N = size ;
        this.ITE = iteration ;
        this.CROSS_PRO = cross ;
        this.MUTATION_PRO = mutation ;
        this.ts = ts ;

        pool = new MPopulation(N, ts.tests.length, ts);
        this.random = new Random() ;
    }

    /*
     *  print current pool
     */
    public void printPool() {
        System.out.println("the population pool: ");
        pool.printPopulation();
    }

    /*
     *  get current population pool
     */
    public ArrayList<Sequence> getPopulation() {
        return pool.population ;
    }

    /*
     *  copy best_front (i.e. final population)
     */
    public void assignBestFront( ArrayList<Sequence> data ) {
        data.clear();
        if( pool.population.size() == 0 )
            return ;
        pool.getFirstLevelFront(data);
    }

    /*
     *  run NSGA-II
     */
    public void evolve() {

        // initialize
        pool.initialization();

        // main loop
        for( int it=0 ; it<ITE ; it++ ) {

            // fast-non-dominated-sort (identify level and crowd)
            pool.NonDominatedSort();

            // select the first N candidates as the new pool
            pool.CandidateSort(N);

            // make new population
            ArrayList<Sequence> Q = new ArrayList<>();
            for( int i=0 ; i<N ; i++ ) {
                // selection
                int x1 = selection_BT() ;
                int x2 = selection_BT() ;
                Sequence p1 = pool.population.get(x1) ;
                Sequence p2 = pool.population.get(x2) ;
                int[] child ;

                // crossover
                double alpha = random.nextDouble() ;
                if( alpha < CROSS_PRO )
                    child = crossover_PMX(p1.order, p2.order) ;
                else {
                    child = p1.order.clone() ;
                }

                // mutation
                double beta = random.nextDouble() ;
                if( beta < MUTATION_PRO )
                    mutation_EX( child ) ;

                // add to Q
                Sequence q = new Sequence(child, ts.getTotalTestingCost(child), ts.getRFD(child, ts.system.tway), 0, 0 );
                Q.add(q) ;
            }

            // union two population set
            pool.unionSet(Q);

        } // end main loop

        // the result front, only save the first N candidates
        pool.NonDominatedSort();
        pool.CandidateSort(N);

        //System.out.println("final:");
        //pool.print();
    }


    /*
     *  binary tournament selection
     *  output: an integer representing the indexes selected from two parents
     */
    private int selection_BT() {

        int a = random.nextInt(N);
        int b = random.nextInt(N);
        while( a == b )
            b = random.nextInt(N);

        int fit_a = pool.population.get(a).level ;
        int fit_b = pool.population.get(b).level ;
        if( fit_a < fit_b )
            return a ;
        else
            return b ;
    }

    /*
     *  partially matched crossover (PMX)
     *  build an offspring by choosing a sub-sequence of a tour from one parent
     *  preserving the order and position of as many positions as possible from
     *  the other parent
     *
     *  p1 : 1 5 | 2 8 7 | 4 3 6
     *  p2 : 4 2 | 5 8 1 | 3 6 7
     *  mapping: 2-5, 8-8, 7-1
     *  o  : 4 [2] | 2 8 7 | 3 6 [7]
     *  ==>  4  5  | 2 8 7 | 3 6  1
     */
    public int[] crossover_PMX( final int[] p1, final int[] p2 ) {
        int LEN = p1.length ;
        int[] child = new int[LEN] ;

        // generate two cut points
        Random random = new Random();
        int cut1 = random.nextInt(LEN);
        int cut2 = cut1 + random.nextInt(LEN - cut1);

        // the mapping part
        Map<Integer, Integer> mapping = new HashMap<>(); // (p1, p2)
        for( int k = cut1 ; k <= cut2 ; k++ ) {
            child[k] = p1[k] ;
            mapping.put(p1[k], p2[k]);
        }

        // the remain part
        for( int k = 0 ; k < cut1 ; k++ ) {
            int tp = p2[k] ;
            while( mapping.containsKey(tp) ) {
                tp = mapping.get(tp);
            }
            child[k] = tp ;

        }
        for( int k = cut2 + 1 ; k < LEN ; k++ ) {
            int tp = p2[k] ;
            while( mapping.containsKey(tp) ) {
                tp = mapping.get(tp);
            }
            child[k] = tp ;
        }

        return child ;
    }


    /*
     *  genetic edge recombination crossover
     *  output: a new sequence by combining p1 and p2
     */
    private int[] crossover_ER( final int[] p1, final int[] p2 ) {
        int len = p1.length ;
        int[] child = new int[len];
        int[] remain = new int[len];
        for( int k = 0; k < len; k++ )
            remain[k] = 0;
        int remain_count = len ;

        Map<Integer, Set<Integer>> edge_map = new HashMap<Integer, Set<Integer>>();
        // for each test case, find the indexes
        for (int i = 0; i < len; i++) {
            Set<Integer> temp = new HashSet<Integer>();

            for (int k = 0; k < len; k++) {
                if (p1[k] == i) {
                    temp.add(k == 0 ? p1[len - 1] : p1[k - 1]);
                    temp.add(k == len - 1 ? p1[0] : p1[k + 1]);
                    break ;
                }
            }
            for (int k = 0; k < len; k++) {
                if (p2[k] == i) {
                    temp.add(k == 0 ? p2[len - 1] : p2[k - 1]);
                    temp.add(k == len - 1 ? p2[0] : p2[k + 1]);
                    break ;
                }
            }
            edge_map.put(i, temp);
        }

        // procedure offspring
        int index = 0;

        // the first one
        int f1 = ((Set) edge_map.get(p1[index])).size();
        int f2 = ((Set) edge_map.get(p2[index])).size();
        if (f1 < f2)
            child[index] = p1[index];
        else if (f1 > f2)
            child[index] = p2[index];
        else {
            int k = random.nextInt(2);
            child[index] = k == 0 ? p1[index] : p2[index];
        }
        remain[child[index]] = 1;
        remain_count--;
        index++;

        while (true) {
            // remove all occurrences of the current vertex (child[index])
            Set<Integer> tp_set = edge_map.remove(child[index - 1]);
            for (Map.Entry<Integer, Set<Integer>> entry : edge_map.entrySet())
                ((Set)entry.getValue()).remove(child[index - 1]);

            // if the current vertex has entries
            if (!tp_set.isEmpty()) {
                // find the vertex which has fewest entries, ties are broken at random
                int min = Integer.MAX_VALUE;
                for (Integer t : tp_set) {
                    int esize = ((Set) edge_map.get(t)).size();
                    if (esize < min)
                        min = esize;
                }
                ArrayList<Integer> temp = new ArrayList<Integer>();
                for (Integer t : tp_set) {
                    if (((Set) edge_map.get(t)).size() == min)
                        temp.add(t);
                }
                int min_index = temp.get(random.nextInt(temp.size()));

                // add to child
                child[index] = min_index;
                remain[child[index]] = 1;
                remain_count--;
                index++;
            }
            // if there are no remaining unvisited vertexs, exit
            else if (remain_count == 0) {
                break;
            }
            // choose a random unvisited vertex
            else {
                int num = random.nextInt(remain_count);
                for (int k = 0; k < len; k++) {
                    if (remain[k] == 0 && num == 0) {
                        // add to child
                        child[index] = k;
                        remain[k] = 1;
                        remain_count--;
                        index++;
                        break;
                    } else if (remain[k] == 0)
                        num--;
                }
            }

        } // end while

        //for( int i = 0 ; i < len ; i++ )
        //    System.out.print(child[i] + " ");
        //System.out.print("\n");

        return child;
    }

    /*
     *  swap mutation
     *  randomly pick two positions and swap their values
     */
    private void mutation_EX( int[] a ) {
        int pos1 = random.nextInt(a.length) ;
        int pos2 = random.nextInt(a.length) ;

        if( pos1 != pos2 ) {
            int tp = a[pos1] ;
            a[pos1] = a[pos2] ;
            a[pos2] = tp ;
        }
    }

}
