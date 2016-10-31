package Prioritization;

import Model.TestSuite;
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
                GA.population.add(can);
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

}
