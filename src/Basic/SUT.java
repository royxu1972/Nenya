package Basic;

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
    public ConstraintSolver constraintSolver ;

    // combinations to be covered
    private int[][] AllS;           // all the combinations, each of which is represented by a bit
    private int SCountAll;          // the total number of combinations to be covered
    private int SCount;             // the number of uncovered combinations
    private int uniformRow;         // C(parameter, t_way), the number of uniform covering strength rows in AllS
    private int testCaseCoverMax;   // the maximum number of combinations that can be covered by a test case


    public SUT(int p, int[] v, int t) {
        parameter = p;
        value = new int[p];
        System.arraycopy(v, 0, value, 0, p);
        t_way = t;
        variables = null;
        hardConstraint = null;
        basicConstraint = null;
        constraintSolver = null ;

        uniformRow = ALG.cal_combine(p, t_way);
        testCaseCoverMax = uniformRow;
    }


    // get SCountAll
    public int getSCountAll() { return SCountAll; }

    // get SCount
    public int getSCount() { return SCount; }

    // get CoverMain
    public int getUniformRow() { return uniformRow; }

    // get testCaseCoverMax
    public int getTestCaseCoverMax() { return testCaseCoverMax; }

    // get current coverage
    public double getCoverage() { return (double) (SCountAll - SCount) / (double) (SCountAll); }

    /*
     *  Determine whether a k-tuple is invalid or not.
     *  INPUT-1: k-tuple representation, which indicate parameter and values, respectively
     *  INPUT-2: test case representation, which use -1 to indicate unfixed values
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
            int[][] data = ALG.cal_allC(value[i], 2);
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
        constraintSolver = new ConstraintSolver(max_var,basicConstraint.size()+hardConstraint.size());
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
     *  will be removed from AllS.
     */
    private void preProcessConstraint() {
        // for each t-way combination
        // which is represented by par_row[] and val_row[]
        int[][] par = ALG.cal_allC(parameter, t_way);
        for( int[] par_row : par ) {
            int[][] val = ALG.cal_allV(par_row, t_way, value);
            for( int[] val_row : val ) {
                if( !isValid(par_row, val_row) )
                    Covered(par_row, val_row, 1);
            }
        }
    }


    /*
     *  Initialize AllS (all combinations to be covered).
     *  This should be the first step before invoking any generation or evaluation methods.
     */
    public void initialization() {
        AllS = null;
        SCount = 0;

        // assign uniformRow rows to AllS
        AllS = new int[uniformRow][];

        // get all combinations of C(parameter, t_way)
        int[][] data = ALG.cal_allC(parameter, t_way);

        // for each combination
        for( int i=0 ; i< uniformRow; i++ ) {
            // compute the number of t-way value combinations that are related to current parameters
            int comb = 1;
            for (int t = 0; t < t_way; t++)
                comb = comb * value[data[i][t]];

            // set and initialize AllS
            int column = (int) Math.ceil((double) comb / (double) 32);
            AllS[i] = new int[column];

            for (int k = 0; k < column; k++)
                AllS[i][k] = 0x00000000;

            // update SCount (the number of combinations to be covered)
            SCount += comb;
        }

        // update SCountAll
        SCountAll = SCount;

        // remove combinations that appear in hardConstraint
        if( hardConstraint != null )
            preProcessConstraint();
    }


    /*
     *  Get the number of uncovered combinations that are covered by a given test case,
     *  i.e. the fitness function of one-test-at-a-time based CA generation.
     *
     *  INPUT PARAMETER:
     *  If FLAG = 0, only a number is returned. AllS and SCount will not be updated.
     *  If FLAG = 1, AllS and SCount will be updated accordingly.
     */
    public int FitnessValue(final int[] test, int FLAG) {
        int num = 0;

        // get all combinations of C(parameter, t_way)
        int[][] data = ALG.cal_allC(parameter, t_way);

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
     *  Determine whether a particular k-way combination is covered or not, where
     *  position[] indicates the indexes of parameters, and schema[] indicates the
     *  corresponding parameter values.
     *
     *  INPUT PARAMETER:
     *  If FLAG = 0, AllS and SCount will not be updated.
     *  If FLAG = 1, AllS and SCount will be updated accordingly.
     */
    public boolean Covered(int[] position, int[] schema, int FLAG) {
        boolean ret = true;

        // check the value of AllS[row][column] to determine cover or not
        // the row and column is computed based on position and schema, respectively
        int row = ALG.cal_combine2num(position, parameter, t_way);
        int column ;       // which BYTE
        int column_bit ;   // which bit

        // compute column
        int index = 0;
        int it ;
        for (int i = 0; i < t_way; i++) {
            it = schema[i];
            for (int j = i + 1; j < t_way; j++)
                it = value[position[j]] * it;
            index += it;
        }

        column = index / 32;
        column_bit = index % 32;

        // determine column_bit is 0 (uncovered) or 1 (covered)
        // index : 0 1 2 3 4 5 6 7 ...
        // BYTE  : 0 0 0 0 0 0 0 0 ...
        //                 |
        //             column_bit
        if ( (AllS[row][column] >>> (31-column_bit) & 0x00000001) != 0x00000001 ) {
            ret = false;
            if (FLAG == 1) {
                AllS[row][column] = AllS[row][column] | 0x00000001 << (31 - column_bit);
                SCount--;
            }
        }
        return ret;
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

        // AllS
        System.out.println("AllS: ");
        int[] p ;
        for (int i = 0; i < uniformRow; i++) {
            p = ALG.cal_num2combine(i, parameter, t_way);
            System.out.print("{ ");
            for (int m = 0; m < t_way; m++)
                System.out.print(p[m] + " ");
            System.out.print("} : ");

            int comb = 1;
            for (int q = 0; q < t_way; q++)
                comb = comb * value[p[q]];
            int column = (int) Math.ceil((double) comb / (double) 32);

            int out = 0;
            for (int column_index = 0; column_index < column; column_index++) {
                int ac = AllS[i][column_index];
                for (int c = 0; c < 32 && out < comb; c++) {
                    // 循环左移一位
                    int b = ac >>> 31;
                    ac = ac << 1;
                    ac = ac | b;

                    if ((ac & 0x00000001) == 0x00000001)
                        System.out.print(1 + " ");
                    else
                        System.out.print(0 + " ");
                    out++;
                }
            }
            System.out.print("\n");
        }

        // Coverage
        System.out.println("uniformRow = " + uniformRow);
        System.out.println("SCountAll = " + SCountAll + ", SCount = " + SCount);
    }
}