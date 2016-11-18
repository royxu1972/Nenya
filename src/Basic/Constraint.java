package Basic;

import java.util.Arrays;
import java.util.Vector;

public class Constraint implements Cloneable {

    /*
     *  To use constraint solver, each parameter value must be mapped
     *  into an unique integer value, which starts from 1.
     *
     *  The mapping for CA(N;t,5,3) is as follows:
     *  p1  p2  p3  p4  p5
     *   1   4   7  10  13
     *   2   5   8  11  14
     *   3   6   9  12  15
     *
     *  Each forbidden constraint can be represented as
     *  test case (unfixed value -1), or disjunction of literals.
     *  {0,  -1, 0, -1, -1}           &  [-1, -7]
     *  {-1, -1, 2,  0,  1}           &  [-9, -10, -14]
     */

    // constraint representation
    private int[] plain;        // test case based
    private int[] disjunction;  // disjunction of literals

    public Constraint() {}

    /**
     * Set constraint according to test case representation.
     * @param c test case
     * @param variables the mapping
     */
    public Constraint(final int[] c, final int[][] variables) {
        plain = new int[c.length];
        Vector<Integer> tp = new Vector<>();
        for( int k=0 ; k<c.length ; k++ ) {
            plain[k] = c[k];
            if( c[k] != -1 )
                tp.add(k);
        }
        disjunction = new int[tp.size()];
        for( int k=0 ; k<tp.size(); k++ ) {
            int par = tp.get(k);
            disjunction[k] = 0 - variables[par][c[par]];
        }
    }

    /**
     * Set constraint according to disjunction of literals
     * @param c disjunction of literals
     */
    public Constraint(final int[] c) {
        plain = null ;
        disjunction = new int[c.length];
        System.arraycopy(c, 0, disjunction, 0, c.length);
    }

    @Override
    public Constraint clone() {
        Constraint c = new Constraint();
        if( c.plain != null )
            c.plain = plain.clone();
        c.disjunction = disjunction.clone();
        return c;
    }

    @Override
    public String toString() {
        return Arrays.toString(disjunction) ;
    }

}
