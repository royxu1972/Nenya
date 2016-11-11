package Generation;

import EA.GA.Common.*;
import EA.GA.GeneticAlgorithm;
import Model.SUT;
import Model.TestSuite;

import java.util.ArrayList;
import java.util.Arrays;

public class GA {

    /**
     *  The fitness function of a test case
     */
    private class TestFitness implements FitnessFunction<General> {
        @Override
        public double value(General candidate) {
            // the smaller the better
            return sut.getTestCaseCoverMax()-sut.FitnessValue(candidate.solution, 0);
        }
    }

    /**
     *  Initialize a set of candidate test cases
     */
    private class TestInitializer implements Initializer<General> {
        @Override
        public void init(ArrayList<General> population, int size) {
            for( int i=0 ; i<size ; i++ ) {
                General a = new General(sut.parameter, sut.value);
                population.add(a);
            }
        }
    }

    private SUT sut ;
    private ArrayList<int[]> coveringArray ;

    public GeneticAlgorithm<General> GA ;

    public GA() {
        sut = null ;
        coveringArray = new ArrayList<>();
        GA = new GeneticAlgorithm<>();
    }

    /*
     *  Basic one-test-at-a-time framework
     */
    public void oneTestGeneration(TestSuite ts) {
        // initialize
        sut = ts.system ;
        sut.initialization();

        // configure GA
        GA.initializer     = new TestInitializer();
        GA.fitnessFunction = new TestFitness();
        GA.assignParameter(300, 600, 0.9, 0.7);

        GA.OP_Selection = new UniformSelector<>(UniformSelector.operator.Roulette);
        GA.OP_Crossover = new GeneralCrossover();
        GA.OP_Mutation  = new GeneralMutation();

        coveringArray.clear();
        while( sut.getCombUncovered() != 0 ) {
            // run GA to generate a test case
            // that covers the most uncovered combinations
            GA.evolve();

            // add the best test case into covering array
            General best = GA.bestCandidate;
            coveringArray.add(best.solution.clone());
            System.out.println(Arrays.toString(best.solution) + ", fit = " + best.fitness);

            // update uncovered combinations
            sut.FitnessValue(best.solution, 1);

        } // end while

        // save the final covering array in test.tests[][]
        // and set default testing solution and default execution cost
        ts.tests = new int[coveringArray.size()][sut.parameter] ;
        ts.order = new int[coveringArray.size()] ;
        ts.executionCost = new double[coveringArray.size()] ;
        int x = 0 ;
        for( int[] t : coveringArray ) {
            ts.tests[x] = t.clone();
            ts.order[x] = x ;             // set default order
            ts.executionCost[x] = 0.0 ;   // set default execution cost = 0.0
            x++ ;
        }

    }
}
