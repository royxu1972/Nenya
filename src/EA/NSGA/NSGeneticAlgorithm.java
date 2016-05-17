package EA.NSGA;

import Basic.TestSuite;
import EA.Common.Genetic;
import EA.GA.CrossoverPMX;
import EA.GA.MutationExchange;
import EA.GA.SelectionBinaryTournament;

import java.util.ArrayList;
import java.util.List;

/**
 *  Multi-Objective Optimization, NSGA-II
 *  designed specifically for test suite prioritization according to
 *  two goals: testing cost (execution + switching) and 2-way combination coverage
 */
public class NSGeneticAlgorithm extends Genetic {

    public NSPopulation2D pool;
    public TestSuite ts ;

    public NSGeneticAlgorithm(TestSuite t) {
        ts = t ;

        // default algorithm settings
        N = 30 ;
        ITE = 1000 ;
        CROSSOVER_PRO = 0.7 ;
        MUTATION_PRO = 0.3 ;

        op_selection = new SelectionBinaryTournament();
        op_crossover = new CrossoverPMX();
        op_mutation = new MutationExchange();

        pool = new NSPopulation2D(N, t.tests.length, t);
    }

    /*
     *  Get the final population.
     */
    public ArrayList<NSSolution2D> getFinalFront() {
        ArrayList<NSSolution2D> data = new ArrayList<>();
        pool.getFirstLevelFront(data);
        return data;
    }

    /*
     *  Get the reference pareto front.
     *  i.e. the first level front of final population + other populations
     */
    public ArrayList<NSSolution2D> getReferenceFront(ArrayList<NSSolution2D> other) {
        ArrayList<NSSolution2D> data = new ArrayList<>();

        for( NSSolution2D each : other )
            pool.append(each);

        pool.NonDominatedSort();
        pool.getFirstLevelFront(data);

        return data ;
    }

    /*
     *  Get the front solutions that contribute to the reference pareto front.
     */
    public ArrayList<NSSolution2D> getContributedSolutions(ArrayList<NSSolution2D> front, ArrayList<NSSolution2D> reference) {
        ArrayList<NSSolution2D> data = new ArrayList<>();
        for( NSSolution2D f : front ) {
            for( NSSolution2D ref : reference ) {
                if( f.isEqual(ref) ) {    // if contribute
                    data.add(f.clone());
                    break;
                }
            }
        }
        return data ;
    }

    /*
     *  Get the solution in front K that is the closest to the ideal point
     */
    public NSSolution2D getBestSolution2D( double value, double cost, ArrayList<NSSolution2D> K) {
        NSSolution2D best = null ;
        double shortest = Double.MAX_VALUE ;
        for( NSSolution2D each : K ) {
            double c = each.cost ;
            double v = each.value ;
            double d = Math.sqrt((c-cost)*(c-cost)+(v-value)*(v-value));
            if ( d < shortest )
                best = each.clone();
        }
        return best ;
    }

    @Override
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
            ArrayList<NSSolution2D> Q = new ArrayList<>();
            while ( Q.size() < N ) {
                // selection
                int x1 = op_selection.selection(this);
                int x2 = op_selection.selection(this);

                // crossover
                List<int[]> children ;
                NSSolution2D p1 = pool.population.get(x1);
                NSSolution2D p2 = pool.population.get(x2);

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
                    NSSolution2D q = new NSSolution2D(each, ts.getTotalTestingCost(each), ts.getRFD(each, ts.system.t_way), 0, 0 );
                    Q.add(q) ;
                }
            }

            // union two population set
            pool.unionSet(Q);

        } // end main loop

        // the result front, only save the first N candidates
        pool.NonDominatedSort();
        pool.CandidateSort(N);

        //System.out.println("final:");
        //pool.printPopulation();
    }

    public void printCurrentPopulation() {
        System.out.println("the current population: ");
        pool.printPopulation();
    }
}
