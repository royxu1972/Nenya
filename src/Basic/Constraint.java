package Basic;

import java.util.Arrays;
import java.util.Vector;

public class Constraint {
    // constraint representation
    public int[] plain;        // test case based
    public int[] disjunction;  // disjunction of literals

    // set constraint according to test case representation
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

    // set constraint according to disjunction of literals
    public Constraint(final int[] c) {
        plain = null ;
        disjunction = new int[c.length];
        System.arraycopy(c, 0, disjunction, 0, c.length);
    }

    @Override
    public String toString() {
        return Arrays.toString(disjunction) ;
    }

}
