package function;

import Generation.*;
import Model.SequenceSuite;
import Model.TestSuite;
import org.junit.Test;

public class TestGeneration {

    @Test
    public void test_CoveredSchemaNumber() {
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
    public void test_AETG() {
        /* basic version, no constraint */
        int p = 10 ;
        int v[] = new int[p];
        for( int k=0 ; k<p ; k++ )
            v[k] = 6 ;
        int t = 2 ;
        double[] pro = {0.7, 0.1, 0.1, 0.05, 0.05};
        TestSuite ts = new TestSuite(p, v, t);

        AETG gen = new AETG();
        for( int k=0 ; k<30 ; k++ ) {
            gen.generation(ts);
            System.out.println("size = " + ts.testSuiteSize() +
                            " , profile-cov = " + ts.profileCoverage(null, pro));
        }
    }

    @Test
    public void test_AETG2() {
        /*
         *  constraint version
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
    public void test_SCA() {
        /* basic version, no constraint */
        int e = 10 ;
        int t = 3 ;

        SequenceSuite ss = new SequenceSuite(e, t);
        SCA sca = new SCA();
        sca.generation(ss);

        System.out.print(ss.getTestSuite());
        System.out.print("size = " + ss.getSize() + "\n");
    }

    @Test
    public void test_ProfileCoverage() {

        int p = 26 ;
        int v[] = new int[p];
        for( int k=0 ; k<p ; k++ )
            v[k] = 3 ;
        int t = 2 ;
        double[] pro = {0.5, 0.3, 0.1, 0.1, 0.0, 0.0};
        TestSuite ts = new TestSuite(p, v, t);

        // AETG
        System.out.println("AETG");
        new AETG().generation(ts);
        int size = ts.testSuiteSize();
        for( int i=1 ; i<=6 ; i++ )
            System.out.println( i + "-cov = " + ts.tWayCoverage(null, i));
        System.out.println("profile-cov = " + ts.profileCoverage(null, pro)+"\n");

        // RT
        System.out.println("RT");
        new RT().generationFixedSize(ts, size);
        for( int i=1 ; i<=6 ; i++ )
            System.out.println( i + "-cov = " + ts.tWayCoverage(null, i));
        System.out.println("profile-cov = " + ts.profileCoverage(null, pro)+"\n");

        // ART
        System.out.println("ART");
        new FSCS().Generation(ts, size);
        for( int i=1 ; i<=6 ; i++ )
            System.out.println( i + "-cov = " + ts.tWayCoverage(null, i));
        System.out.println("profile-cov = " + ts.profileCoverage(null, pro)+"\n");
    }


}

