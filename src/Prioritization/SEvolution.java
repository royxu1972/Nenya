package Prioritization;

import Basic.TestSuite;

import java.util.*;

/**
 *  switching cost based prioritization by GA
 */
public class SEvolution {

    private ArrayList<int[]> pool ;     // candidate population
    private TestSuite ts ;
    private int LENGTH ;
    private Random random ;

    // parameter setting
    private int N ;
    private int ITE ;
    private double CROSS_PRO ;
    private double MUTATION_PRO ;

    // result
    public double opt_cost;
    public int[] opt_sequence;  // opt_sequence

    public SEvolution(  int size, int iteration, double cross, double mutation, TestSuite ts ) {
        this.N = size ;
        this.ITE = iteration ;
        this.CROSS_PRO = cross ;
        this.MUTATION_PRO = mutation ;
        this.ts = ts ;
        this.LENGTH = ts.getTestSuiteSize();

        pool = new ArrayList<>();
        this.random = new Random() ;

        this.opt_cost = Double.MAX_VALUE ;
        this.opt_sequence = new int[LENGTH] ;
    }


    /*
     * genetic algorithm (GA)
     * Reference:
     * [1] Larranaga, Pedro, et al. "Genetic algorithms for the travelling salesman problem:
     * A review of representations and operators." Artificial Intelligence Review 13.2 (1999)
     */
    public void GA() {
        pool.clear();

        // initialize candidate randomly
        for (int i = 0 ; i < N ; i++) {
            int[] can = new int[this.LENGTH];
            int[] assigned = new int[this.LENGTH];

            for (int k = 0; k < this.LENGTH; k++) {
                int p ;
                do {
                    p = random.nextInt(this.LENGTH);
                } while (assigned[p] == 1);
                can[k] = p;
                assigned[p] = 1;
            }
            pool.add(can);
        }

        // evolution
        int it = 1;
        double[] fitness_all = new double[N];
        while ( it < ITE ) {
            // compute fitness value
            // the best one is assigned to this.opt_sequence[]
            for( int k = 0 ; k < N ; k++ ) {
                fitness_all[k] = this.GA_fitness( pool.get(k) );
                if (fitness_all[k] < this.opt_cost) {
                    this.opt_cost = fitness_all[k];
                    System.arraycopy(pool.get(k), 0, this.opt_sequence, 0, this.LENGTH);
                }
            }

            // regenerate candidate
            ArrayList<int[]> next = new ArrayList<>();
            while ( next.size() < N ) {
                // selection
                int par1 = this.GA_selection_BT();
                int par2 = this.GA_selection_BT();
                int[] child ;

                // crossover
                double alpha = random.nextDouble() ;
                if( alpha < CROSS_PRO )
                    child = GA_crossover_PMX(pool.get(par1), pool.get(par2)) ;
                else {
                    child = pool.get(par1).clone() ;
                }

                // mutation
                double beta = random.nextDouble() ;
                if( beta < MUTATION_PRO )
                    GA_mutation_EX(child) ;

                // add
                next.add(child);
            } // end for

            // cory candidate
            pool.clear();
            pool.addAll(next);
            next.clear();

            // next iteration
            it++;

        } // end while
    }

    // fitness: return the length of sequence seq[]
    private double GA_fitness(final int[] seq) {
        return ts.getTotalSwitchingCost(seq);
    }

    // binary tournament selection
    private int GA_selection_BT() {
        int a = random.nextInt(N);
        int b = random.nextInt(N);
        while( a == b )
            b = random.nextInt(N);

        double fit_a = GA_fitness(pool.get(a)) ;
        double fit_b = GA_fitness(pool.get(b)) ;
        if( fit_a < fit_b )
            return a ;
        else
            return b ;
    }

    // partially matched crossover (PMX)
    public int[] GA_crossover_PMX( final int[] p1, final int[] p2 ) {
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

    // swap mutation
    private void GA_mutation_EX( int[] a ) {
        int pos1 = random.nextInt(a.length) ;
        int pos2 = random.nextInt(a.length) ;

        if( pos1 != pos2 ) {
            int tp = a[pos1] ;
            a[pos1] = a[pos2] ;
            a[pos2] = tp ;
        }
    }

    /*
    // selection: return a chromosomes according to fitness array fit[]
    // Roulette Wheel Selection
    private int GA_selection(final int[] fit) {
        int fit_sum = 0;
        for (int k = 0; k < fit.length; k++)
            fit_sum += fit[k];

        int ptr = random.nextInt(fit_sum);

        int temp = 0;
        int index;
        for (index = 0; index < fit.length; index++) {
            temp += fit[index];
            if (ptr < temp) {
                break;
            }
        }
        return index;
    }
    */

    /*
    // crossover: return an offspring of two chromosomes
    // method: ER (genetic edge recombination crossover)
    private int[] GA_crossover_ER(final int[] par1, final int[] par2) {
        int[] child = new int[this.LENGTH];
        int[] remain = new int[this.LENGTH];
        for (int k = 0; k < this.LENGTH; k++)
            remain[k] = 0;
        int remain_count = this.LENGTH;

        Map<Integer, Set<Integer>> edgemap = new HashMap<Integer, Set<Integer>>();
        // for each vertex
        for (int i = 0; i < this.LENGTH; i++) {
            Set<Integer> temp = new HashSet<Integer>();

            for (int k = 0; k < par1.length; k++) {
                if (par1[k] == i) {
                    temp.add(k == 0 ? par1[par1.length - 1] : par1[k - 1]);
                    temp.add(k == par1.length - 1 ? par1[0] : par1[k + 1]);
                }
            }
            for (int k = 0; k < par2.length; k++) {
                if (par2[k] == i) {
                    temp.add(k == 0 ? par2[par2.length - 1] : par2[k - 1]);
                    temp.add(k == par2.length - 1 ? par2[0] : par2[k + 1]);
                }
            }
            edgemap.put(i, temp);
        }

        // procedure offspring
        int index = 0;

        // the first one
        int f1 = edgemap.get(par1[index]).size();
        int f2 = edgemap.get(par2[index]).size();
        if (f1 < f2)
            child[index] = par1[index];
        else if (f1 > f2)
            child[index] = par2[index];
        else {
            int k = random.nextInt(2);
            child[index] = k == 0 ? par1[index] : par2[index];
        }
        remain[child[index]] = 1;
        remain_count--;
        index++;

        while (true) {
            // remove all occurrences of the current vertex (child[index])
            Set<Integer> tp_set = edgemap.remove(child[index - 1]);
            for (Map.Entry<Integer, Set<Integer>> entry : edgemap.entrySet())
                (entry.getValue()).remove(child[index - 1]);

            // if the current vertex has entries
            if (!tp_set.isEmpty()) {
                // find the vertex which has fewest entries, ties are broken at random
                int min = Integer.MAX_VALUE;
                for (Integer t : tp_set) {
                    int esize = ((Set) edgemap.get(t)).size();
                    if (esize < min)
                        min = esize;
                }
                ArrayList<Integer> temp = new ArrayList<Integer>();
                for (Integer t : tp_set) {
                    if (((Set) edgemap.get(t)).size() == min)
                        temp.add(t);
                }
                int min_index = temp.get(random.nextInt(temp.size()));

                // add to child
                child[index] = min_index;
                remain[child[index]] = 1;
                remain_count--;
                index++;
            }
            // if there are no remaining unvisited vertexes, exit
            else if (remain_count == 0) {
                break;
            }
            // choose a random unvisited vertex
            else {
                int num = random.nextInt(remain_count);
                for (int k = 0; k < this.LENGTH; k++) {
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

        //for( int i = 0 ; i < this.LENGTH ; i++ )
        //    System.out.print(child[i] + " ");
        //System.out.print("\n");

        return child;
    }
    */

    /*
    // mutation: mutate chromosome chrom[]
    // method: DM (Displacement mutation)
    // 0 1 2 3 4 5 6     0 1 5 6    => 0 1 5 2 3 4 6
    //     |   |             |
    //   cut=2,len=2       put=2
    private void GA_mutation_DM(int[] chrom) {
        int cut = random.nextInt(chrom.length);
        int len = cut == chrom.length - 1 ? 0 : random.nextInt(chrom.length - cut - 1);
        int put = random.nextInt(chrom.length - len - 1);

        //cut = 6 ;
        //len = 0 ;
        //put = 5 ;
        //System.out.println(cut + " " + len + " " + put);

        // cut
        int[] cut_ary = new int[len + 1];
        for (int m = cut, n = 0; m <= cut + len && n <= len; m++, n++)
            cut_ary[n] = chrom[m];

        // remain
        int[] rem_ary = new int[chrom.length - len - 1];
        for (int m = 0, n = 0; m < chrom.length && n < chrom.length - len - 1; m++) {
            if (cut <= m && m <= cut + len)
                continue;
            else {
                rem_ary[n] = chrom[m];
                n++;
            }
        }

        // new chromosome
        int i = 0;  // chrom
        int k = 0;  // remain
        for (; k <= put; k++, i++)
            chrom[i] = rem_ary[k];
        for (int j = 0; j < cut_ary.length; j++, i++)
            chrom[i] = cut_ary[j];
        for (; k < chrom.length - len - 1; k++, i++)
            chrom[i] = rem_ary[k];

        //for( i=0 ; i<chrom.length ; i++ )
        //    System.out.print(chrom[i] + " ");
        //System.out.print("\n");

    }

    // mutation: mutate chromosome chrom[]
    // method: EM (Exchange mutation)
    private void GA_mutation_EM(int[] chrom) {
        int i = random.nextInt(LENGTH);
        int j = random.nextInt(LENGTH);
        int tp = chrom[i];
        chrom[i] = chrom[j];
        chrom[j] = tp;
    }
    */
}
