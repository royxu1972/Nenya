package EA.oldGA;

import EA.oldGA.Common.OPCrossover;
import EA.oldGA.Common.OPMutation;
import EA.oldGA.Common.OPSelection;

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
    public OPMutation op_mutation ;
    public OPCrossover op_crossover ;
    public OPSelection op_selection ;

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
    public void setOperator(OPSelection selection, OPCrossover crossover,
                            OPMutation mutation ) {
        op_selection = selection ;
        op_crossover = crossover ;
        op_mutation = mutation ;
    }

    // the algorithm
    public abstract void evolve();

}
