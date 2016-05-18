package EA.Common;

import EA.GA.GeneticAlgorithm;
import EA.NSGA.NSGeneticAlgorithmII;

/**
 *  Binary Tournament Selection
 *  Select two candidates randomly. The better one will
 *  be selected as a parent.
 */
public class SelectionBinaryTournament implements OperatorSelection {
    @Override
    public String toString() {
        return "Binary Tournament";
    }

    // single-objective
    @Override
    public int selection(GeneticAlgorithm GA) {
        int a = GA.random.nextInt(GA.N);
        int b = GA.random.nextInt(GA.N);
        while( a == b )
            b = GA.random.nextInt(GA.N);

        double fit_a = GA.fitness.value(GA.population.get(a));
        double fit_b = GA.fitness.value(GA.population.get(b));
        if( fit_a < fit_b )
            return a ;
        else
            return b ;
    }

    // multi-objective
    @Override
    public int selection(NSGeneticAlgorithmII NSGA) {
        int a = NSGA.random.nextInt(NSGA.N);
        int b = NSGA.random.nextInt(NSGA.N);
        while( a == b )
            b = NSGA.random.nextInt(NSGA.N);

        int fit_a = NSGA.population.get(a).level ;
        int fit_b = NSGA.population.get(b).level ;
        if( fit_a < fit_b )
            return a ;
        else if ( fit_a == fit_b ) {
            double alpha = NSGA.random.nextDouble();
            if( alpha < 0.5 )
                return a ;
            else
                return b ;
        }
        else
            return b ;
    }
}
