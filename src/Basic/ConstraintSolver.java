package Basic;

import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

import java.util.Vector;

public class ConstraintSolver {

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

    /*
     *  feed the solver using Dimacs format, using arrays of int
     */
    public void addClauses( final Vector<Constraint> constraints ) throws ContradictionException {
        for( Constraint clause : constraints ) {
            solver.addClause(new VecInt(clause.disjunction));
        }
    }

    /*
     *  determine whether a clause is satisfiable or not
     */
    public boolean isSatisfiable( final int[] clause ) throws TimeoutException {
        VecInt c = new VecInt(clause);
        IProblem problem = solver;
        if (problem.isSatisfiable(c)) {
            //System.out.println("satisfiable");
            return true;
        }
        else {
            //System.out.println("no");
            return false;
        }
    }





}