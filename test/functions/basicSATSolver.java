package functions;

import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.*;
import org.junit.Test;

public class basicSATSolver {
    @Test
    public void solver() throws ContradictionException, TimeoutException {
        final int MAXVAR = 100;
        final int NBCLAUSES = 50;

        ISolver solver = SolverFactory.newDefault();

        // prepare the solver to accept MAXVAR variables. MANDATORY for MAXSAT solving
        solver.newVar(MAXVAR);
        solver.setExpectedNumberOfClauses(NBCLAUSES);

        // Feed the solver using Dimacs format, using arrays of int
        // (best option to avoid dependencies on SAT4J IVecInt)
        for (int i=0 ; i<NBCLAUSES ; i++ ) {
            // int [] clause = // get the clause from somewhere
            int[] clause1 = {-1, -4};
            // the clause should not contain a 0, only integer (positive or negative)
            // with absolute values less or equal to MAXVAR
            // e.g. int [] clause = {1, -3, 7}; is fine
            // while int [] clause = {1, -3, 7, 0}; is not fine

            // adapt Array to IVecInt
            solver.addClause(new VecInt(clause1));
            //solver.addClause(new VecInt(clause2));
            //solver.addClause(new VecInt(clause3));
        }

        int[] ass = {4,5,1};
        VecInt kkk = new VecInt(ass);

        // we are done. Working now on the IProblem interface
        IProblem problem = solver;

        if (problem.isSatisfiable(kkk)) {
            System.out.println("satisfiable");
        }
        else {
            System.out.println("no");
        }
    }



}
