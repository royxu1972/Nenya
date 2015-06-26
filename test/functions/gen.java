package functions;

import Basic.SUT;
import Basic.TestSuite;
import Generation.AETG;
import Generation.FSCS;
import org.junit.Test;

public class gen {
    @Test
    public void testAETG() {
        // SUT setting
        int p = 10 ;
        int[] v = new int[p] ;
        for( int k=0 ; k<p ; k++ )
            v[k] = 6 ;
        int t = 2 ;

        TestSuite ts = new TestSuite(p, v, t);
        AETG gen = new AETG();
        gen.Generation(ts);

        System.out.println(gen.getSize());
    }


    @Test
    public void T1() {
        int p = 10 ;
        int[] v = new int[p] ;
        for( int k=0 ; k<p ; k++ )
            v[k] = 3 ;
        int t = 3 ;

        TestSuite ts1 = new TestSuite(p, v, t);
        TestSuite ts2 = new TestSuite(p, v, t);

        // AETG
        AETG gen = new AETG();
        gen.Generation(ts1);
        int size =ts1.getTestSuiteSize();

        // ART
        FSCS f = new FSCS();
        f.Generation(ts2, size);

        // evaluate
        int[][] data = new int[size][2];
        SUT s1 = new SUT(p, v, t);
        s1.GenerateS();

        SUT s2 = new SUT(p, v, t);
        s2.GenerateS();

        for( int k=0 ; k<size ; k++ ) {
            data[k][0] = s1.FitnessValue( ts1.tests[k], 1 );
        }
        for( int k=0 ; k<size ; k++ ) {
            data[k][1] = s2.FitnessValue( ts2.tests[k], 1 );
        }

        //
        for( int k=0 ; k<size ; k++ ) {
            System.out.println( k + " : " + data[k][0] + " | " + data[k][1]);
        }
        System.out.println( "uncovered : " + s1.getSCount() + " | " + s2.getSCount());
    }


}

