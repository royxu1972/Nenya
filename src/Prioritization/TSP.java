package Prioritization;

import java.util.*;

/**
 * Travelling Salesman Problem
 * 1) Dynamic Programming
 * 2) LKH solver
 */
public class TSP {
    // graph data, start at vertex 0
    private int[][] Graph;
    private int node_num;

    // random
    Random random;

    // result
    public int opt_length;
    public int[] opt_sequence;  // opt_sequence
    public int[] opt_distance;  // opt_distance[i] = Graph[opt_sequence[i]][opt_sequence[i+1]]

    public TSP(int n, final int[][] gra) {
        node_num = n;
        Graph = new int[n][n];
        for (int x = 0; x < n; x++)
            for (int y = 0; y < n; y++)
                Graph[x][y] = gra[x][y];

        opt_sequence = new int[n];
        opt_distance = new int[n];
        opt_length = Integer.MAX_VALUE;
        random = new Random();
    }

    /*
     *  print opt_length in console
     */
    public void printResult() {
        System.out.println("opt length: " + opt_length);
        System.out.print("sequence: ");
        for (int k = 0; k < node_num; k++)
            System.out.print(opt_sequence[k] + " ");
        System.out.print("\n");
        System.out.print("distance: ");
        for (int k = 0; k < node_num; k++)
            System.out.print(opt_distance[k] + " ");
        System.out.print("\n");
    }

    /*
     *  dynamic programming, O(n^2*2^n)
     */
    public void DP() {
        int column = (int) Math.pow(2.0, (double) (node_num - 1));   // 列数
        int[][] state = new int[node_num][column];               // 状态矩阵，n * 2^(n-1)
        int[][] best = new int[node_num][column];                // 最优策略

        // 初始化
        for (int i = 0; i < node_num; i++) {
            for (int j = 0; j < column; j++) {
                state[i][j] = best[i][j] = -1;
            }
        }

        // 初始化state第一列
        for (int k = 1; k < node_num; k++)
            state[k][0] = Graph[k][0];

        // DP
        int min;
        for (int j = 1; j < column - 1; j++) {
            for (int i = 1; i < node_num; i++) {
                // 结点i不在列j中
                if (((int) (Math.pow(2.0, i - 1)) & j) == 0x00) {
                    min = Integer.MAX_VALUE;
                    // 对于j中所有结点
                    for (int k = 0; k < node_num; k++) {
                        if (((int) (Math.pow(2.0, k - 1)) & j) != 0x00) {
                            int tp = Graph[i][k] + state[k][j - (int) (Math.pow(2.0, k - 1))];
                            if (tp < min) {
                                min = tp;
                                state[i][j] = min;
                                best[i][j] = k;
                            }
                        }
                    } // for each k
                } // end if node i is not in column j
            }
        }

        // 最后一列
        min = Integer.MAX_VALUE;
        for (int i = 1; i < node_num; i++) {
            int tp = Graph[0][i] + state[i][column - 1 - (int) (Math.pow(2.0, i - 1))];
            if (tp < min) {
                min = tp;
                state[0][column - 1] = min;
                best[0][column - 1] = i;
            }
        }

        // save result
        opt_length = state[0][column - 1];
        opt_sequence[0] = 0;
        for (int k = column - 1, next = 0, index = 1; k > 0; index++) {
            next = best[next][k];
            k = k - (int) Math.pow(2.0, next - 1);

            opt_sequence[index] = next;
            opt_distance[index - 1] = Graph[opt_sequence[index - 1]][opt_sequence[index]];
        }
        opt_distance[node_num - 1] = Graph[opt_sequence[node_num - 1]][opt_sequence[0]];
    }

    /*
     * genetic algorithm (GA)
     * Reference:
     * [1] Larranaga, Pedro, et al. "Genetic algorithms for the travelling salesman problem: 
     * A review of representations and operators." Artificial Intelligence Review 13.2 (1999)
     */
    public void GA(int population, int iteration, double Pc, double Pm) {
        // initialize candidate randomly
        ArrayList<int[]> candidate = new ArrayList();
        for (int i = 0; i < population; i++) {
            int[] can = new int[this.node_num];
            int[] assigned = new int[this.node_num];
            for (int k = 0; k < this.node_num; k++)
                assigned[k] = 0;

            for (int k = 0; k < this.node_num; k++) {
                int p = random.nextInt(this.node_num);
                while (assigned[p] == 1)
                    p = random.nextInt(this.node_num);
                can[k] = p;
                assigned[p] = 1;
            }
            candidate.add(can);
        }

        int it = 1;
        int[] fitness_all = new int[population];
        while (it < iteration) {
            // compute fitness value
            // the best one is assigned to this.opt_sequence[]
            int k = 0;
            for (int[] a : candidate) {
                fitness_all[k] = this.GA_fitness(a);
                if (fitness_all[k] < this.opt_length) {
                    this.opt_length = fitness_all[k];
                    System.arraycopy(a, 0, this.opt_sequence, 0, this.node_num);
                }
                k++;
            }

            // regenerate candidate
            ArrayList<int[]> nextg = new ArrayList();
            while (nextg.size() < population) {
                // selection
                int par1 = this.GA_selection(fitness_all);
                int par2 = this.GA_selection(fitness_all);
                ArrayList<int[]> child = new ArrayList();

                // crossover
                double alpha = random.nextDouble();
                if (alpha < Pc) {
                    //child.add(GA_crossover_ER(candidate.get(par1), candidate.get(par2)));

                    int[][] c = GA_crossover_PMX(candidate.get(par1), candidate.get(par2));
                    child.add(c[0]);
                    child.add(c[1]);

                } else {
                    child.add(candidate.get(par1));
                    child.add(candidate.get(par2));
                }

                // mutation
                double beta = random.nextDouble();
                if (beta < Pm) {
                    for (int[] a : child) {
                        //GA_mutation_DM(a);
                        GA_mutation_EM(a);
                    }
                }

                // add
                nextg.addAll(child);
            } // end for

            // cory candidate
            candidate.clear();
            candidate.addAll(nextg);
            nextg.clear();

            // next iteration
            it++;

        } // end while
    }

    // fitness: return the length of sequence seq[]
    private int GA_fitness(final int[] seq) {
        int len = 0;
        for (int k = 0; k < seq.length - 1; k++)
            len += this.Graph[seq[k]][seq[k + 1]];
        len += this.Graph[seq[seq.length - 1]][seq[0]];
        return len;
    }

    // selection: return a chromosomes according to fitness array fit[]
    // method: Roulette Wheel Selection
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

    // crossover: return an offspring of two chromosomes
    // method: ER (genetic edge recombination crossover)
    private int[] GA_crossover_ER(final int[] par1, final int[] par2) {
        int[] child = new int[this.node_num];
        int[] remain = new int[this.node_num];
        for (int k = 0; k < this.node_num; k++)
            remain[k] = 0;
        int remain_count = this.node_num;

        Map<Integer, Set> edgemap = new HashMap();
        // for each vertex
        for (int i = 0; i < this.node_num; i++) {
            Set<Integer> temp = new HashSet();

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
        /*
        for( int i=0 ; i<this.node_num ; i++ ) {
            System.out.print(i+": ");
            Set<Integer>tp = (Set)edgemap.get(i) ;
            for( Integer t : tp ) {
                System.out.print(t + " ");
            }
            System.out.print("\n");
        }
        */
        // procedure offspring
        int index = 0;

        // the first one
        int f1 = ((Set) edgemap.get(par1[index])).size();
        int f2 = ((Set) edgemap.get(par2[index])).size();
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
            for (Map.Entry<Integer, Set> entry : edgemap.entrySet())
                ((Set) entry.getValue()).remove(child[index - 1]);

            // if the current vertex has entries
            if (!tp_set.isEmpty()) {
                // find the vertex which has fewest entries, ties are broken at random
                int min = 9999999;
                for (Integer t : tp_set) {
                    int esize = ((Set) edgemap.get(t)).size();
                    if (esize < min)
                        min = esize;
                }
                ArrayList<Integer> temp = new ArrayList();
                for (Integer t : tp_set) {
                    if (((Set) edgemap.get(t)).size() == min)
                        temp.add(t);
                }
                int min_index = (int) temp.get(random.nextInt(temp.size()));

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
                for (int k = 0; k < this.node_num; k++) {
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

        /*
        for( int i=0 ; i<this.node_num ; i++ ) {
            Set<Integer>tp = (Set)edgemap.get(i) ;
            if( tp == null )
                continue ;
            System.out.print(i+": "); 
            for( Integer t : tp ) {
                System.out.print(t + " ");
            }
            System.out.print("\n");
        }
        */

        //for( int i = 0 ; i < this.node_num ; i++ )
        //    System.out.print(child[i] + " ");
        //System.out.print("\n");

        return child;
    }

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

    // crossover: return two offsprings of two chromosomes
    // method: PMX (Partially mapped crossover) 
    private int[][] GA_crossover_PMX(final int[] par1, final int[] par2) {
        int[][] children = new int[2][];
        children[0] = new int[node_num];
        children[1] = new int[node_num];

        int cut1 = random.nextInt(node_num - 1);
        int cut2 = random.nextInt(node_num - 1);

        if (cut1 > cut2) {
            int tmp = cut2;
            cut2 = cut1;
            cut1 = tmp;
        }

        //System.out.println(cut1 + " " + cut2);
        // map
        Map<Integer, Integer> partially_1 = new HashMap();
        Map<Integer, Integer> partially_2 = new HashMap();
        for (int i = cut1 + 1; i <= cut2; i++) {
            // initial children[0] and [1]
            children[0][i] = par2[i];
            children[1][i] = par1[i];

            // add to map
            partially_2.put(par1[i], par2[i]);
            partially_1.put(par2[i], par1[i]);
        }

        // cross
        for (int i = 0; i < node_num; i++) {
            if (cut1 < i && i <= cut2)
                continue;

            // children[0]
            int temp = par1[i];
            while (partially_1.get(temp) != null)
                temp = partially_1.get(temp);
            children[0][i] = temp;

            // children[1]
            temp = par2[i];
            while (partially_2.get(temp) != null)
                temp = partially_2.get(temp);
            children[1][i] = temp;
        }

        return children;
    }

    // mutation: mutate chromosome chrom[]
    // method: EM (Exchange mutation)
    private void GA_mutation_EM(int[] chrom) {
        int i = random.nextInt(node_num);
        int j = random.nextInt(node_num);
        int tp = chrom[i];
        chrom[i] = chrom[j];
        chrom[j] = tp;
    }


}
