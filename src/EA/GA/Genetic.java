package EA.GA;

import EA.Common.OperatorCrossover;
import EA.Common.OperatorMutation;
import EA.Common.OperatorSelection;

import java.util.Random;

/**
 *  GA based evolutionary algorithms
 */
public abstract class Genetic {

    public Random random ;

    // the length of each candidate solution
    public int    LENGTH ;

    // parameter
    public int    N ;
    public int    ITE ;
    public double CROSSOVER_PRO ;
    public double MUTATION_PRO ;

    // operator
    public OperatorMutation op_mutation ;
    public OperatorCrossover op_crossover ;
    public OperatorSelection op_selection ;

    public Genetic() {
        random = new Random();
    }

    // assign control parameter
    public void setParameter( int n, int ite, double crossover, double mutation ) {
        N = n ;
        ITE = ite ;
        CROSSOVER_PRO = crossover ;
        MUTATION_PRO = mutation ;
    }

    // assign selection and variation operators
    public void setOperator( OperatorSelection selection, OperatorCrossover crossover,
                             OperatorMutation mutation ) {
        op_selection = selection ;
        op_crossover = crossover ;
        op_mutation = mutation ;
    }

    // the algorithm
    public abstract void evolve();

}
