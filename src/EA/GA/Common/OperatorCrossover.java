package EA.GA.Common;

import java.util.List;

public interface OperatorCrossover<T extends Chromosome> {
    /*
     *  Apply crossover operator on chromosomes a and b
     *  based on crossover probability PRO. Return a list
     *  consists of their children.
     */
    List<T> crossover( T a, T b, double PRO );
}
