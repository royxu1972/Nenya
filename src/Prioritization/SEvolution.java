package Prioritization;

import Basic.TestSuite;
import EA.Common.FitnessFunction;
import EA.Common.Initializer;
import EA.GA.GeneticAlgorithm;


/**
 *  Switching cost based prioritization by GA
 */
public class SEvolution {

    /**
     *  Initialize a set of testing orders
     */
    public class testingOrderInitializer implements Initializer {
        @Override
        public void initialization(GeneticAlgorithm GA, int size) {
            for (int i = 0 ; i < size ; i++) {
                int[] can = new int[GA.LENGTH];
                int[] assigned = new int[GA.LENGTH];

                for (int k = 0; k < GA.LENGTH; k++) {
                    int p ;
                    do {
                        p = GA.random.nextInt(GA.LENGTH);
                    } while (assigned[p] == 1);
                    can[k] = p;
                    assigned[p] = 1;
                }
                GA.pool.add(can);
            }
        }
    }

    /**
     *  The fitness function of a testing solution
     */
    public class testingOrderFitness implements FitnessFunction {
        @Override
        public double value(final int[] c) {
            return ts.getTotalSwitchingCost(c);
        }
    }

    private TestSuite ts ;
    public GeneticAlgorithm GA ;

    public int[]  solution ;
    public double solution_fitness ;

    public SEvolution(TestSuite t) {
        ts = t ;
        GA = new GeneticAlgorithm(ts.tests.length) ;
    }

    public void run() {
        GA.setInitializer(new testingOrderInitializer());
        GA.setFitnessFunction(new testingOrderFitness());
        GA.evolve();

        solution = GA.best_candidate.clone() ;
        solution_fitness = GA.best_fitness ;
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
