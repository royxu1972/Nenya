package functions;

import Basic.TestSuite;
import Generation.AETG;
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
        //Calculator calculator = new Calculator();
        //int sum = calculator.evaluate("1+2+3");
        //assertEquals(6, sum);
    }
}

