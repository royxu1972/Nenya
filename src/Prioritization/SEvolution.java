package Prioritization;

import EA.GA.Common.*;
import EA.GA.GeneticAlgorithm;
import Model.TestSuite;

import java.util.ArrayList;

/**
 *  Switching cost based prioritization by GA
 */
public class SEvolution {

    /**
     *  The fitness function of a testing solution
     */
    private class SwitchingFitness implements FitnessFunction<Permutation> {
        @Override
        public double value(Permutation p) {
            return ts.getTotalSwitchingCost(p.solution);
        }
    }

    /**
     *  Initialize a set of candidate testing orders
     */
    private class OrderInitializer implements Initializer<Permutation> {
        @Override
        public void init(ArrayList<Permutation> population, int size) {
            for( int i=0 ; i<size ; i++ ) {
                Permutation p = new Permutation(ts.testSuiteSize());
                population.add(p);
            }
        }
    }

    private TestSuite ts ;
    public GeneticAlgorithm<Permutation> GA ;

    public int[]  solution ;
    public double solution_fitness ;

    public SEvolution(TestSuite t) {
        ts = t ;
        GA = new GeneticAlgorithm<>() ;
    }

    public void run() {
        // configure GA
        GA.initializer = new OrderInitializer();
        GA.fitnessFunction = new SwitchingFitness();

        GA.OP_Selection = new UniformSelector<>();
        GA.OP_Crossover = new PermutationCrossover();
        GA.OP_Mutation = new PermutationMutation();

        GA.evolve();

        // save the best solution
        Permutation best = GA.bestCandidate;
        solution = best.solution.clone();
        solution_fitness = best.fitness;
    }

}
