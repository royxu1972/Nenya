package EA.Common;

import java.util.Random;

/**
 *  GA based evolutionary algorithms
 */
public abstract class Genetic {

    public Random random ;

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

    /*
     *  Assign control parameter.
     */
    public void setParameter( int n, int ite, double crossover, double mutation ) {
        N = n ;
        ITE = ite ;
        CROSSOVER_PRO = crossover ;
        MUTATION_PRO = mutation ;
    }

    /*
     *  Assign selection and variation operators.
     */
    public void setOperator( OperatorSelection selection,
                             OperatorCrossover crossover,
                             OperatorMutation mutation ) {
        op_selection = selection ;
        op_crossover = crossover ;
        op_mutation = mutation ;
    }

    /*
     *  Main algorithm procedure
     */
    public abstract void evolve();


}
