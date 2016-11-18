package Basic;

import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

import java.util.Vector;

public class ConstraintSolver implements Cloneable {

    private int MAXVAR ;    // maximum number of variable
    private int NBCLAUSES ; // number of clauses

    private ISolver solver ;

    public ConstraintSolver( int var , int clause ) {
        MAXVAR = var ;
        NBCLAUSES = clause ;

        // set default solver
        solver = SolverFactory.newDefault();
        solver.newVar(MAXVAR);
        solver.setExpectedNumberOfClauses(NBCLAUSES);
    }

    /**
     * Feed the solver using Dimacs format, i.e. an integer arrays
     * @param constraints all constraints
     * @throws ContradictionException exception
     */
    public void addClauses( final Vector<Constraint> constraints ) throws ContradictionException {
        for( Constraint clause : constraints ) {
            solver.addClause(new VecInt(clause.disjunction));
        }
    }

    /**
     * Determine whether a clause is satisfiable or not
     * @param clause the candidate clause
     * @return satisfiable or not
     * @throws TimeoutException exception
     */
    public boolean isSatisfiable( final int[] clause ) throws TimeoutException {
        VecInt c = new VecInt(clause);
        IProblem problem = solver;
        return problem.isSatisfiable(c);
    }
}