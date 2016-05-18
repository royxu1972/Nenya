package EA.GA;

import EA.Common.*;

import java.util.*;

/**
 *  A basic genetic algorithm for combinatorial testing.
 *      Representation: string (an integer array)
 *      Fitness Function: f(x) -> double
 *  Initializer, Fitness Function and Operators should be implemented
 *  according to particular problem.
 *
 *  Reference:
 *      Larranaga, Pedro, et al. "Genetic algorithms for the travelling salesman problem:
 *      A review of representations and operators." Artificial Intelligence Review 13.2 (1999)
 */
public class GeneticAlgorithm extends Genetic {

    public ArrayList<int[]> population;

    public Initializer init ;
    public FitnessFunction fitness ;

    // the best result
    public double best_fitness;
    public int[]  best_candidate;

    // the fitness of each candidate
    public double[] fit;

    public GeneticAlgorithm( int len ) {
        population = new ArrayList<>();
        LENGTH = len ;

        // a smaller fitness value indicates a better solution
        best_candidate = new int[len];
        best_fitness = Double.MAX_VALUE ;
        fit = new double[N];

        // default algorithm settings
        N = 30 ;
        ITE = 1000 ;
        CROSSOVER_PRO = 0.7 ;
        MUTATION_PRO = 0.3 ;

        op_selection = new SelectionBinaryTournament();
        op_crossover = new CrossoverPMX();
        op_mutation = new MutationExchange();
    }

    /*
     *  Assign initializer and fitness function
     */
    public void setInitializer( Initializer i ) {
        init = i ;
    }
    public void setFitnessFunction( FitnessFunction f ) {
        fitness = f ;
    }

    @Override
    public void evolve() {
        population.clear();

        // initialize candidates
        init.initialization(this, N);

        // evolution
        int it = 1;
        while ( it < ITE ) {
            // evaluate each candidate solution
            // the best one is stored in best_candidate
            for( int k = 0 ; k < N ; k++ ) {
                fit[k] = fitness.value(population.get(k));
                if (fit[k] < best_fitness) {
                    best_fitness = fit[k];
                    System.arraycopy(population.get(k), 0, best_candidate, 0, LENGTH);
                }
            }

            // produce the next generation
            ArrayList<int[]> next = new ArrayList<>();
            while ( next.size() < N ) {
                // selection
                int par1 = op_selection.selection(this);
                int par2 = op_selection.selection(this);

                // crossover
                List<int[]> children ;
                int[] p1 = population.get(par1);
                int[] p2 = population.get(par2);

                double alpha = random.nextDouble() ;
                if( alpha < CROSSOVER_PRO )
                    children = op_crossover.crossover(p1, p2, random);
                else {
                    children = new ArrayList<>();
                    children.add(p1.clone());
                    children.add(p2.clone());
                }

                // mutation
                for( int[] each : children ) {
                    double beta = random.nextDouble() ;
                    if( beta < MUTATION_PRO )
                        op_mutation.mutation(each, random);
                }

                // add to the next generation
                next.addAll(children);
            }

            // population = next
            population.clear();
            population.addAll(next);
            next.clear();

            // next iteration
            it++;

        } // end while

    }

    public void printCurrentPopulation() {
        for( int[] each : population) {
            System.out.println(Arrays.toString(each));
        }
    }
}
