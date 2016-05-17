package function;

import Basic.TestSuite;
import Generation.AETG;
import org.junit.Test;

public class generation {

    @Test
    public void test_coveredSchemaNumber() {
        int p = 5 ;
        int[] v = new int[p] ;
        for( int k=0 ; k<p ; k++ )
            v[k] = 3 ;
        int t = 3 ;
        TestSuite ts = new TestSuite(p, v, t);
        ts.system.initialization();

        AETG gen = new AETG();

        int[] tc1 = {-1, -1, -1, -1, 0};
        int f1 = gen.coveredSchemaNumber(tc1, 1, 2);
        System.out.println("f1 = " + f1);

    }


    @Test
    public void test_AETG() {
        /*
            p1  p2  p3  p4  p5
            1   4   7  10  13
            2   5   8  11  14
            3   6   9  12  15
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

        AETG gen = new AETG();
        gen.Generation(ts);
        System.out.println(gen.getSize());
    }




}

