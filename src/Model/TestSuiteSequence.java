package Model;

import java.util.Arrays;

/**
 *  Basic data structure and evaluations for sequence test suite
 */
public class TestSuiteSequence {

    public SUTSequence system ;
    public int[][] tests;           // the test suite

    public TestSuiteSequence(int p, int t) {
        this.system = new SUTSequence(p, t);
        this.tests = null;
    }

    public int getSize() {
        return tests.length;
    }

    public String getTestSuite() {
        StringBuilder str = new StringBuilder();
        for( int[] row : tests )
            str.append(Arrays.toString(row) + "\n");
        return str.toString();
    }

    public double getCombinationCoverage() {
        return -1.0;
    }


}
