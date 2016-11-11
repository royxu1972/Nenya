package function;

import Generation.*;
import Model.TestSuiteSequence;
import Model.TestSuite;
import org.junit.Test;

import java.util.Arrays;

public class TestGeneration {

    @Test
    public void testCoveredSchemaNumber() {
        /* auxiliary functions for AETG */
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
    public void testAETG() {
        /* basic version, no constraint */
        int p = 9 ;
        int[] v = new int[]{2, 2, 3, 3, 2, 2, 2, 2, 5};
        int t = 6 ;
        TestSuite ts = new TestSuite(p, v, t);

        new AETG().generation(ts);
        System.out.println("size = " + ts.testSuiteSize());
    }

    @Test
    public void testAETG_Constraint() {
        /* constraint version */
        /*
         *  p1  p2  p3  p4  p5
         *   1   4   7  10  13
         *   2   5   8  11  14
         *   3   6   9  12  15
         */
        int p = 5 ;
        int[] v = new int[p] ;
        for( int k=0 ; k<p ; k++ )
            v[k] = 3 ;
        int t = 2 ;
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
    public void testGA() {
        /* basic version, no constraint */
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
    public void testSCA() {
        /* basic version, no constraint */
        int e = 10 ;
        int t = 3 ;

        TestSuiteSequence ss = new TestSuiteSequence(e, t);
        SCA sca = new SCA();
        sca.generation(ss);

        System.out.print(ss.getTestSuite());
        System.out.print("size = " + ss.getSize() + "\n");
    }

    private void printCoverage(double[] cov ) {
        for( double v : cov )
            System.out.print(String.format("%.2f", v) + " ");
        System.out.print("\n");
    }

    @Test
    public void testCoverage() {
        //int p = 6 ;
        //int[] v = new int[]{3, 3, 3, 5, 5, 6};

        int p = 15 ;
        int[] v = new int[p];
        for( int k=0 ; k<p ; k++ )
            v[k] = 3 ;

        int t = 2 ;
        double[] pro = {0.5, 0.2, 0.1, 0.1, 0.05, 0.05};
        TestSuite ts = new TestSuite(p, v, t);

        // AETG (base)
        new AETG().generation(ts);
        int size = ts.testSuiteSize();
        System.out.println("size = " + size);

        System.out.print("AETG: ");
        printCoverage(ts.profileCoverage(null, pro));

        // RT
        System.out.print("RT:   ");
        new RT().generationFixedSize(ts, size);
        printCoverage(ts.profileCoverage(null, pro));

        // ART
        System.out.print("ART:  ");
        new ART().FSCS(ts, size);
        printCoverage(ts.profileCoverage(null, pro));
    }

    @Test
    public void testRCT() {
        int p = 15 ;
        int v[] = new int[p];
        for( int k=0 ; k<p ; k++ )
            v[k] = 5 ;
        int t = 2 ;
        double[] pro = {0.1, 0.1, 0.1, 0.2, 0.2, 0.3};
        TestSuite ts = new TestSuite(p, v, t);

        int size = 10 ;

        // RT
        System.out.print("RT:      ");
        new RT().generationFixedSize(ts, size);
        printCoverage(ts.profileCoverage(null, pro));

        // ART
        System.out.print("ART:     ");
        new ART().FSCS(ts, size);
        printCoverage(ts.profileCoverage(null, pro));

        // maximize 2-way
        new RCT().generationFixed(ts, size, 2);
        System.out.print("2-way:   ");
        printCoverage(ts.profileCoverage(null, pro));

        // maximize 3-way
        new RCT().generationFixed(ts, size, 3);
        System.out.print("3-way:   ");
        printCoverage(ts.profileCoverage(null, pro));

        // maximize 4-way
        new RCT().generationFixed(ts, size, 4);
        System.out.print("4-way:   ");
        printCoverage(ts.profileCoverage(null, pro));

        // maximize profile
        System.out.print("profile: ");
        new RCT().generationProfile(ts, size, pro);
        printCoverage(ts.profileCoverage(null, pro));
    }


}

