package EA.oldGA;

import EA.oldGA.Common.*;

import java.util.*;

/**
 *  NSGA-II for test suite prioritization.
 *  Two goals: testing cost (execution + switching) and RFD.
 */
public class NSGeneticAlgorithmII extends Genetic {

    public ArrayList<NSSolution2D> population;

    public InitializerND     init ;
    public FitnessFunction2D fitness ;

    // the best result
    public ArrayList<NSSolution2D> finalFront;

    public NSGeneticAlgorithmII( int len ) {
        population = new ArrayList<>();
        LENGTH = len ;

        finalFront = new ArrayList<>();

        // default algorithm settings
        N             = 60 ;
        ITE           = 600 ;
        CROSSOVER_PRO = 0.9 ;
        MUTATION_PRO  = 0.7 ;

        op_selection = new BinaryTournament();
        op_crossover = new PMX();
        op_mutation = new Exchange();
    }

    /*
     *  Assign initializer and fitness function
     */
    public void setInitializer( InitializerND i ) {
        init = i ;
    }
    public void setFitnessFunction( FitnessFunction2D f ) {
        fitness = f ;
    }

    /*
     *  Fast-non-dominated-sort
     *  Assign domination level and crowd to each solution of pool
     */
    public void nonDominatedSort( ArrayList<NSSolution2D> pool ) {
        int SIZE = pool.size() ;

        // for each solution, initialize dominateCount and dominateSet
        int[] dominateCount = new int[SIZE] ;
        ArrayList<HashSet<Integer>> dominateSet = new ArrayList<>();
        ArrayList<Integer> F = new ArrayList<>() ;

        int level = 1 ;
        int p_index = 0 ;
        for( NSSolution2D p : pool) {
            dominateCount[p_index] = 0 ;
            HashSet<Integer> Sp = new HashSet<>() ;

            // if p dominates q, add q to p's dominateSet
            // if q dominates p, increase p's dominateCount
            int q_index = 0 ;
            for( NSSolution2D q : pool) {
                if( p.isDominate(q) ) {
                    Sp.add(q_index);
                    //System.out.println(p_index + " > " + q_index) ;
                }
                else if ( q.isDominate(p) ) {
                    dominateCount[p_index]++;
                    //System.out.println(p_index + " < " + q_index);
                }
                q_index += 1 ;
            }
            dominateSet.add(p_index, Sp);

            // if p belongs to the first front
            if( dominateCount[p_index] == 0 ) {
                p.updateLevel(1) ;
                F.add(p_index) ;
            }
            p_index += 1 ;
        }
        crowdAssignment(pool, F);

        while( !F.isEmpty() ) {
            ArrayList<Integer> Q = new ArrayList<>() ;

            // for each x (the index of original list) in F
            for( Integer x : F ) {
                // for each y (the index of original list) in x's dominateSet
                for( Integer y : dominateSet.get(x) ) {
                    dominateCount[y]-- ;
                    // if q belongs to the next front
                    if( dominateCount[y] == 0 ) {
                        pool.get(y).updateLevel(level + 1);
                        Q.add(y);
                    }
                }
            }

            level += 1 ;

            // F = Q
            F.clear();
            for( Integer k : Q )
                F.add(k) ;
            if( !F.isEmpty() )
                crowdAssignment(pool, F);
        }
    }

    /*
     *  crowding-distance-assignment
     *  input:  an non-dominated set I
     *  output: assign crowd distance to each member of I
     */
    public void crowdAssignment( ArrayList<NSSolution2D> pool, ArrayList<Integer> I ) {
        ArrayList<NSSolution2D> II = new ArrayList<>() ;
        for( Integer i : I )
            II.add(pool.get(i)) ;
        int l = II.size() ;
        for( int i=0 ; i<l ; i++ )
            II.get(i).updateCrowd(0);

        //
        // cost distance, <
        //
        Collections.sort(II, new NSSolution2D.costSort());
        double f_min = II.get(0).cost ;
        double f_max = II.get(l-1).cost ;

        II.get(0).updateCrowd(Integer.MAX_VALUE);   // boundary points
        II.get(l-1).updateCrowd(Integer.MAX_VALUE);
        for( int i=1 ; i<l-1 ; i++ ) {
            NSSolution2D si = II.get(i) ;
            double tp = (II.get(i+1).cost-II.get(i-1).cost) / (f_max-f_min) ;
            si.updateCrowd(si.crowd + tp) ;
        }

        //
        // value distance, >
        //
        Collections.sort(II, new NSSolution2D.valueSort());
        double l_min = II.get(0).value ;
        double l_max = II.get(l-1).value ;

        II.get(0).updateCrowd(Integer.MAX_VALUE);   // boundary points
        II.get(l-1).updateCrowd(Integer.MAX_VALUE);
        for( int i=1 ; i<l-1 ; i++ ) {
            NSSolution2D si = II.get(i) ;
            double tp = (II.get(i+1).value-II.get(i-1).value) / (l_max-l_min) ;
            si.updateCrowd(si.crowd + tp) ;
        }

        II.clear();
    }

    /*
     *  level and crowded based selection:
     *  sort the population based on Crowded-Comparison, and then select
     *  the first N sequences and remove the others
     */
    public void candidateSort( ArrayList<NSSolution2D> pool, int N ) {
        // sort, ascending solution
        Collections.sort(pool, new NSSolution2D.allSort());

        // keep the best N candidates
        if( pool.size() > N ) {
            int step = pool.size() - N ;
            for( int k=0 ; k<step ; k++ )
                pool.remove(N);
        }
    }

    /*
     *  The union of two set:
     *  append solutions in B to current population
     */
    public void unionSet( ArrayList<NSSolution2D> B ) {
        for( NSSolution2D seq : B ) {
            int[] nt = seq.solution.clone() ;
            // keep old cost and value
            // but let level = 0 and crowed = 0
            NSSolution2D nq = new NSSolution2D(nt, seq.cost, seq.value, 0, 0.0);
            population.add(nq);
        }
    }

    /*
     *  Get the first level front of pool (level = 1)
     */
    public void assignFirstLevelFront( ArrayList<NSSolution2D> pool, ArrayList<NSSolution2D> data ) {
        data.clear();
        pool.stream().forEach( each -> {
            if( each.level == 1 ) {
                NSSolution2D nq = each.clone();
                data.add(nq);
            }
        });
    }

    /*
     *  Get the reference pareto front
     *  i.e. the first level front of (finalFront + other)
     */
    public void assignReferenceFront(ArrayList<NSSolution2D> other, ArrayList<NSSolution2D> data ) {
        if( other.size() == 0 ) {
            finalFront.stream().forEach(each -> data.add(each.clone()));
        }
        else {
            other.stream().forEach(each -> finalFront.add(each.clone()));
            nonDominatedSort(finalFront);
            assignFirstLevelFront(finalFront, data);
        }
    }

    /*
     *  Get the solutions in front that contribute to the reference pareto front.
     */
    public void assignContributedSolutions(ArrayList<NSSolution2D> front, ArrayList<NSSolution2D> reference,
                                           ArrayList<NSSolution2D> data) {
        data.clear();
        for( NSSolution2D f : front ) {
            for( NSSolution2D ref : reference ) {
                if( f.isEqual(ref) ) {    // if contribute
                    data.add(f.clone());
                    break;
                }
            }
        }
    }

    /*
     *  Get the solution in front K that is the closest to the ideal point,
     *  where normalized best cost = 0 and normalized best value = 1
     *  The variable crowd of returned object is set to the shortest distance.
     *
     *  INPUT:
     *  cost[]  - [0]: best (min), [1] worst (max)
     *  value[] - [0]: best (max), [1] worst (min)
     */
    public NSSolution2D getBestSolution2D( double[] cost,  double[] value, ArrayList<NSSolution2D> K) {
        double cost_best = cost[0], cost_worst = cost[1] ;
        double value_best = value[0], value_worst = value[1];

        // check the best and worst value of cost and value
        for( NSSolution2D each : K ) {
            if( each.cost < cost_best )
                cost_best = each.cost ;
            else if( each.cost > cost_worst )
                cost_worst = each.cost ;

            if( each.value > value_best )
                value_best = each.value ;
            else if( each.value < value_worst )
                value_worst = each.value ;
        }

        NSSolution2D best = null ;
        double shortest = Double.MAX_VALUE ;
        for( NSSolution2D each : K ) {
            // normalization
            double c = (each.cost-cost_best) / (cost_worst-cost_best);
            double v = (each.value-value_worst) / (value_best-value_worst);
            double d = Math.sqrt((c*c)+(1.0-v)*(1.0-v) );
            if ( d < shortest ) {
                shortest = d ;
                best = each.clone();
                best.updateCrowd(d);
                //System.out.println("find a better one with d = " + d + ", " + each);
            }
        }
        return best ;
    }

    @Override
    public void evolve() {
        // initialize
        init.initialization(this, N);

        // main loop
        for( int it=0 ; it<ITE ; it++ ) {

            // fast-non-dominated-sort (identify level and crowd)
            nonDominatedSort(population);

            // select the first N candidates as the new population
            candidateSort(population, N);

            // make new population
            ArrayList<NSSolution2D> Q = new ArrayList<>();
            while ( Q.size() < N ) {
                // selection
                int x1 = op_selection.selection(this);
                int x2 = op_selection.selection(this);

                // crossover
                List<int[]> children ;
                NSSolution2D p1 = population.get(x1);
                NSSolution2D p2 = population.get(x2);

                double alpha = random.nextDouble() ;
                if( alpha < CROSSOVER_PRO )
                    children = op_crossover.crossover(p1.solution, p2.solution, random);
                else {
                    children = new ArrayList<>();
                    children.add(p1.solution.clone());
                    children.add(p2.solution.clone());
                }

                // mutation
                for( int[] each : children ) {
                    double beta = random.nextDouble() ;
                    if( beta < MUTATION_PRO )
                        op_mutation.mutation(each, random);
                }

                // add children to Q
                for( int[] each : children ) {
                    double[] fit = fitness.value(each);     // cost, value
                    NSSolution2D q = new NSSolution2D(each, fit[0], fit[1], 0, 0 );
                    Q.add(q) ;
                }
            }

            // union two population set
            unionSet(Q);

        } // end main loop

        // the result front, only save the first N candidates
        nonDominatedSort(population);
        candidateSort(population, N);

        // the final front
        assignFirstLevelFront(population, finalFront);
        //printPopulation(finalFront);
    }

    public void printPopulation( ArrayList<NSSolution2D> pool) {
        System.out.println("-------------------------");
        int i = 0 ;
        for( NSSolution2D seq : pool ) {
            System.out.print(i + ") ");
            System.out.print( "cost = " + seq.cost + ", value = " + seq.value +
                    ", level = " + seq.level + ", crowd = " + seq.crowd + "\n");
            i++ ;
        }
        System.out.println("-------------------------");
    }

}
