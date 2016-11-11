package Model;

import Basic.Alg;
import Basic.BitArray;
import Basic.Constraint;
import Basic.ConstraintSolver;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.TimeoutException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

/**
 *  Software Under Test
 */
public class SUT {

    // testing model
    public int parameter;
    public int[] value;
    public int t_way;

    // constraint solving
    public int[][] variables;                   // the mapping between parameter value and variable in CNF
    public Vector<Constraint> hardConstraint;   // hard constraint
    public Vector<Constraint> basicConstraint;  // at-least and at-most constraint
    public ConstraintSolver constraintSolver;

    // combinations to be covered
    private BitArray comb;        // all the combinations, each of which is represented by a bit
    private int combAll;          // the total number of combinations to be covered
    private int combUncovered;    // the number of uncovered combinations
    private int uniformRow;       // C(parameter, t_way), the number of uniform covering strength rows in comb
    private int testCaseCoverMax; // the maximum number of combinations that can be covered by a test case

    public SUT() {}
    public SUT(int p, int[] v, int t) {
        parameter = p;
        value = new int[p];
        System.arraycopy(v, 0, value, 0, p);
        t_way = t ;

        uniformRow = Alg.cal_combine(parameter, t_way);
        testCaseCoverMax = uniformRow;

        variables = null;
        hardConstraint = null;
        basicConstraint = null;
        constraintSolver = null;
    }

    // get combAll
    public int getCombAll() { return combAll; }

    // get combUncovered
    public int getCombUncovered() { return combUncovered; }

    // get CoverMain
    public int getUniformRow() { return uniformRow; }

    // get testCaseCoverMax
    public int getTestCaseCoverMax() { return testCaseCoverMax; }

    // get current coverage
    public double getCoverage() { return (double) (combAll - combUncovered) / (double) (combAll); }

    /*
     *  Change covering strength to a new level
     */
    public void setCoveringStrength( int t ) {
        t_way = t ;
        uniformRow = Alg.cal_combine(parameter, t_way);
        testCaseCoverMax = uniformRow;
    }

    /*
     *  Determine whether a k-tuple is invalid or not.
     *  INPUT: k-tuple representation,
     *         which indicate parameters and values, respectively.
     */
    public boolean isValid(final int[] position, final int[] schema) {
        if( hardConstraint == null )
            return true ;

        // transfer to disjunction of literals
        int[] clause = new int[position.length];
        for( int i=0 ; i<position.length ; i++ )
            clause[i] = variables[position[i]][schema[i]];

        // determine satisfiable or not
        boolean satisfiable = false ;
        try {
            satisfiable = constraintSolver.isSatisfiable(clause);
        } catch (TimeoutException e) {
            System.err.println("isValid ERROR: " + e);
        }
        return satisfiable ;
    }

    /*
     *  Determine whether a k-tuple is invalid or not.
     *  INPUT: test case representation,
     *         which use -1 to indicate unfixed values.
     */
    public boolean isValid(final int[] test) {
        if( hardConstraint == null )
            return true ;

        // transfer test to clause representation
        List<Integer> list = new ArrayList<>();
        for( int i=0 ; i<test.length ; i++ ) {
            if( test[i] != -1 )
                list.add(variables[i][test[i]]);
        }
        int[] clause = new int[list.size()];
        for( int i=0 ; i<list.size() ; i++ )
            clause[i] = list.get(i) ;

        // determine satisfiable
        boolean satisfiable = false ;
        try {
            satisfiable = constraintSolver.isSatisfiable(clause);
        } catch (TimeoutException e) {
            System.err.println("isValid ERROR: " + e);
        }
        return satisfiable ;
    }


    /*
     *  Set (hard) constraint. variables[][] is the mapping from parameter value to
     *  variable of constraint solver. At-least and at-most constraints will be
     *  generated automatically based on testing model.
     */
    public void setConstraint( int[][] c ) {
        basicConstraint = new Vector<>();
        hardConstraint = new Vector<>();

        // set mapping relationship
        variables = new int[parameter][];
        int start = 1 ;
        for( int i=0 ; i<parameter ; i++ ) {
            variables[i] = new int[value[i]];
            for( int j=0 ; j<value[i] ; j++ , start++ )
                variables[i][j] = start ;
        }
        int max_var = start ;

        // set at-least constraint
        for( int i=0 ; i<parameter ; i++ ) {
            basicConstraint.add(new Constraint(variables[i]));
        }

        // set at-most constraint
        for( int i=0 ; i<parameter ; i++ ) {
            int[][] data = Alg.cal_allC(value[i], 2);
            for( int[] row : data ) {
                int[] tp = new int[2];
                tp[0] = 0 - variables[i][row[0]];
                tp[1] = 0 - variables[i][row[1]];
                basicConstraint.add(new Constraint(tp));
            }
        }

        // set hard constraints
        for( int[] k : c )
            hardConstraint.add(new Constraint(k, variables));

        // initialize solver
        int SS = basicConstraint.size() + hardConstraint.size();
        constraintSolver = new ConstraintSolver(max_var, SS);
        try {
            constraintSolver.addClauses(basicConstraint);
            constraintSolver.addClauses(hardConstraint);
        }
        catch (ContradictionException e) {
            System.err.println(e);
        }
    }

    /*
     *  Pre-processing when solving constraint. Check every k-way combination
     *  to determine whether it is valid or not. Each invalid combination is
     *  either explicit or implicit constraint. All these invalid combinations
     *  will be removed from comb.
     */
    private void preProcessConstraint() {
        // for each t-way combination
        // which is represented by par_row[] and val_row[]
        int[][] par = Alg.cal_allC(parameter, t_way);
        for( int[] par_row : par ) {
            int[][] val = Alg.cal_allV(par_row, t_way, value);
            for( int[] val_row : val ) {
                if( !isValid(par_row, val_row) ) {
                    Covered(par_row, val_row, 1);
                    combAll-- ;
                }
            }
        }
    }

    /*
     *  Initialize all combinations to be covered (i.e. comb) and
     *  variables combAll and combUncovered. Generally, this should
     *  be the first step before invoking any generation methods.
     */
    public void initialization() {
        comb = null;
        combUncovered = 0;

        // assign uniformRow rows
        comb = new BitArray(uniformRow);

        // get all combinations of C(parameter, t_way)
        int[][] data = Alg.cal_allC(parameter, t_way);

        // for each combination
        for( int i=0 ; i<uniformRow; i++ ) {
            // compute the number of t-way value combinations that are related to current parameters
            int cc = 1;
            for (int t = 0; t < t_way; t++)
                cc = cc * value[data[i][t]];

            // assign columns
            comb.initializeRow(i, cc);

            // update the number of uncovered combinations
            combUncovered += cc;
        }

        // update combAll
        combAll = combUncovered;

        // remove combinations that appear in hardConstraint
        if( hardConstraint != null )
            preProcessConstraint();
    }

    /*
     *  Get the number of uncovered combinations that are covered
     *  by a given test case, i.e. the fitness function of
     *  one-test-at-a-time based CA generation.
     *
     *  INPUT PARAMETER:
     *  If FLAG = 0, only a number is returned be.
     *  If FLAG = 1, comb and combUncovered will be updated accordingly.
     */
    public int FitnessValue(final int[] test, int FLAG) {
        int num = 0;

        // get all combinations of C(parameter, t_way)
        int[][] data = Alg.cal_allC(parameter, t_way);

        for( int i=0 ; i<uniformRow; i++ ) {
            // get position and schema
            int[] position = data[i];
            int[] schema = new int[t_way];
            for( int k=0 ; k<t_way; k++ )
                schema[k] = test[position[k]];

            // if it is covered
            if (!Covered(position, schema, FLAG))
                num++;
        }
        return num;
    }

    /*
     *  Determine whether a particular k-way combination is
     *  covered or not, where position[] indicates the indexes
     *  of parameters, and schema[] indicates the corresponding
     *  parameter values.
     *
     *  INPUT PARAMETER:
     *  If FLAG = 0, comb and combUncovered will not be updated.
     *  If FLAG = 1, comb and combUncovered will be updated accordingly.
     */
    public boolean Covered(int[] position, int[] schema, int FLAG) {
        // check the value of comb[row][column] to determine cover or not
        // the row and column is computed based on position and schema, respectively
        int row = Alg.cal_combine2num(position, parameter, t_way);
        int column = 0;
        int it ;
        for (int i = 0; i < t_way; i++) {
            it = schema[i];
            for (int j = i + 1; j < t_way; j++)
                it = value[position[j]] * it;
            column += it;
        }

        // determiner whether combination is covered or not
        boolean r = comb.getElement(row, column) != 0 ;
        if( !r & FLAG == 1 ) {
            comb.setElement(row, column, 1);
            combUncovered--;
        }
        return r;
    }

    /*
     *  Print testing information
     */
    public void printInfo() {
        // Basic
        System.out.println("parameter = " + parameter + ", value = " + Arrays.toString(value) + ", t_way = " + t_way);

        // Constraint
        System.out.println("Hard Constraint: ");
        for( Constraint c : hardConstraint )
            System.out.println("    " + c.toString());

        // Constraint
        System.out.println("Basic Constraint: ");
        for( Constraint c : basicConstraint )
            System.out.println("    " + c.toString());

        // comb
        System.out.println("comb: ");
        int[] p ;
        for (int i = 0; i < uniformRow; i++) {
            p = Alg.cal_num2combine(i, parameter, t_way);
            System.out.print("{ ");
            for (int m = 0; m < t_way; m++)
                System.out.print(p[m] + " ");
            System.out.print("} : ");
            int cc = 1;
            for (int q = 0; q < t_way; q++)
                cc = cc * value[p[q]];
            System.out.print(comb.getRow(i, cc));
        }

        // Coverage
        System.out.println("uniformRow = " + uniformRow);
        System.out.println("combAll = " + combAll + ", combUncovered = " + combUncovered);
    }
}