package function;

import Generation.*;
import Model.TestSuiteSequence;
import Model.TestSuite;
import org.junit.Test;


public class TestGeneration {

    @Test
    /* auxiliary functions for AETG */
    public void testCoveredSchemaNumber() {
        int p = 5 ;
        int[] v = new int[p] ;
        for( int k=0 ; k<p ; k++ )
            v[k] = 3 ;
        int t = 3 ;
        TestSuite ts = new TestSuite(p, v, t);
        ts.system.initialization();

        AETG gen = new AETG();
        int[] tc1 = {-1, -1, -1, -1, 0};
        System.out.println("f1 = " + gen.coveredSchemaNumber(tc1, 1, 2));
    }

    @Test
    /* exhaustive generation */
    public void testExhaustive() {
        int p = 5 ;
        int[] v = new int[]{3, 4, 3, 6, 4};
        TestSuite ts = new TestSuite(p, v, 2);
        int[][] c = new int[][]{
                {0, 0, -1, -1, -1},
                {-1, 0, -1, 1, 1}
        };
        ts.setConstraint(c);
        new RT().generationExhaustive(ts);
        ts.showTestSuite();
    }

    @Test
    /* basic version, no constraint */
    public void testAETG() {
        int p = 9 ;
        int[] v = new int[]{2, 2, 3, 3, 2, 2, 2, 2, 5};
        int t = 1 ;
        TestSuite ts = new TestSuite(p, v, t);

        new AETG().generation(ts);
        System.out.println("size = " + ts.testSuiteSize());
    }

    @Test
    /* constraint version */
    public void testAETG_Constraint() {
        int p = 5 ;
        int[] v = new int[p] ;
        for( int k=0 ; k<p ; k++ )
            v[k] = 3 ;
        int t = 2 ;
        /*
         *  p1  p2  p3  p4  p5
         *   1   4   7  10  13
         *   2   5   8  11  14
         *   3   6   9  12  15
         */
        int[][] c = {
                {0, -1, 0, -1, -1},
                {-1, -1, 2, 0, 1}
        };

        TestSuite ts = new TestSuite(p, v, t);
        ts.setConstraint(c);

        new AETG().generation(ts);
        ts.showTestSuite();
    }

    @Test
    /* basic version, no constraint */
    public void testGA() {
        int p = 10 ;
        int[] v = new int[p];
        for( int k=0 ; k<p ; k++ )
            v[k] = 6 ;
        int t = 2 ;
        TestSuite ts = new TestSuite(p, v, t);

        new GA().oneTestGeneration(ts);
        System.out.println("size = " + ts.testSuiteSize());
    }

    @Test
    /* basic version, no constraint */
    public void testSCA() {
        int e = 10 ;
        int t = 3 ;

        TestSuiteSequence ss = new TestSuiteSequence(e, t);
        SCA sca = new SCA();
        sca.generation(ss);

        System.out.print(ss.getTestSuite());
        System.out.print("size = " + ss.getSize() + "\n");
    }

}

