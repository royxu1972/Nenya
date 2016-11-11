package EA.GA;

import EA.GA.Common.*;

import java.util.*;

/**
 *  Basic framework of genetic algorithm (GA).
 */
public class GeneticAlgorithm <T extends Chromosome> {

    // the population
    public ArrayList<T> population;

    // the best result and its fitness value
    private double bestFitness;
    public T bestCandidate;

    // method to initialize population
    public Initializer<T> initializer ;
    // method to compute fitness value, the smaller the better
    public FitnessFunction<T> fitnessFunction;

    // operators
    public OperatorSelection<T> OP_Selection;
    public OperatorCrossover<T> OP_Crossover;
    public OperatorMutation<T> OP_Mutation ;

    // configuration parameters
    private int    N ;
    private int    ITE ;
    private double CROSSOVER_PRO ;
    private double MUTATION_PRO ;

    public GeneticAlgorithm() {
        population = new ArrayList<>();
        bestFitness = Double.MAX_VALUE;

        // default parameter settings
        N             = 400 ;
        ITE           = 600 ;
        CROSSOVER_PRO = 0.9 ;
        MUTATION_PRO  = 0.7 ;
    }

    public void assignParameter( int n, int ite, double crossover, double mutation ) {
        N = n ;
        ITE = ite ;
        CROSSOVER_PRO = crossover ;
        MUTATION_PRO = mutation ;
    }

    /*
     *  Comparator for chromosome
     */
    private class ChromosomeComparator implements Comparator<T> {
        @Override
        public int compare(T o1, T o2) {
            // the smaller the better
            return Double.compare(o1.fitness, o2.fitness);
        }
    }

    @SuppressWarnings("unchecked")
    public void evolve() {
        // reset
        population.clear();
        bestFitness = Double.MAX_VALUE;

        // initialize population
        initializer.init(population, N);

        // evolution starts
        int it = 1;
        while ( true ) {
            // evaluate each candidate solution
            for( T each : population ) {
                each.fitness = fitnessFunction.value(each);
                if ( each.fitness < bestFitness ) {
                    bestFitness = each.fitness ;
                    bestCandidate = (T)each.clone();
                }
                else if( each.fitness == bestFitness ) {
                    if( new Random().nextDouble() < 0.5 ) {
                        bestFitness = each.fitness ;
                        bestCandidate = (T)each.clone();
                    }
                }
            }

            if( it == ITE )
                break;

            // produce the next generation
            while ( population.size() < 2 * N ) {
                // selection
                int par1 = OP_Selection.selection(population);
                int par2 = OP_Selection.selection(population);

                // crossover
                T p1 = population.get(par1);
                T p2 = population.get(par2);
                List<T> children = OP_Crossover.crossover(p1, p2, CROSSOVER_PRO);

                // mutation
                children.forEach( each -> OP_Mutation.mutation(each, MUTATION_PRO));

                // add to the next generation
                population.addAll(children);
            }

            // keep the best N candidates to the next generation
            Collections.sort(population, new ChromosomeComparator());
            while (population.size() > N)
                population.remove(population.size()-1);

            // next iteration
            it++;

        } // end while
    }
}
