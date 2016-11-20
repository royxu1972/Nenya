package exp;

import Basic.CaseSIR;
import Generation.AETG;
import Generation.RT;
import Model.SUT;
import Model.TestSuite;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

public class Comparison {

    @Test
    public void transfer() {
        CaseSIR grep = new CaseSIR();
        grep.readSIR("grep", new int[]{1});

        int[] t = new int[]{2,0,3,4,3,2,2,0,0};
        String s = "3.1.0.21.9.6.3.4.1.1.2.1.1.1.";

        Assert.assertEquals(grep.getKey(t), s);
        Assert.assertArrayEquals(grep.getTestCase(s), t);
    }

    @Test
    public void exhaustive() {
        CaseSIR grep = new CaseSIR();
        grep.readSIR("grep", new int[]{1,2,3,4});

        TestSuite ts = new TestSuite(grep.getFullModel(2));
        new RT().generationExhaustive(ts);
        grep.checking(ts.tests);
    }

    @Test
    public void test() {
        CaseSIR grep = new CaseSIR();
        grep.readSIR("grep", new int[]{3});
        grep.showCase();

        // t = 2-way
        SUT s = grep.getFullModel(2);
        TestSuite ts = new TestSuite(s);

        new AETG().generation(ts);
        System.out.println("AETG size = " + ts.testSuiteSize());
        System.out.println(grep.evaluateFaultNumber(ts.tests, true));
        System.out.println("###################");
        new RT().generationFixedSize(ts, ts.testSuiteSize());
        System.out.println(grep.evaluateFaultNumber(ts.tests, true));

    }


    @Test
    public void compareBasic() {
        CaseSIR grep = new CaseSIR();
        grep.readSIR("grep", new int[]{3});

        // iterative each sub-model
        double[] PRATE = new double[]{1.0};
        double[] CRATE = new double[]{1.0};

        for( double prate : PRATE ) {
            for (double crate : CRATE) {
                // t-way coverage
                SUT s = grep.getSubModel(prate, crate, 3);
                TestSuite ts = new TestSuite(s);
                s.showModel();

                // repeat 30 times for each model
                int[] ct_result = new int[30];
                int[] rt_result = new int[30];
                for( int i=0 ; i<30 ; i++ ) {
                    // CT
                    new AETG().generation(ts);
                    int size = ts.testSuiteSize();
                    ct_result[i] = grep.evaluateFaultNumber(ts.tests, false);

                    // RT
                    new RT().generationFixedSize(ts, size);
                    rt_result[i] = grep.evaluateFaultNumber(ts.tests, false);
                }
                System.out.println(Arrays.toString(ct_result));
                System.out.println(Arrays.toString(rt_result));

                System.out.println("CT: " + stats(ct_result));
                System.out.println("RT: " + stats(rt_result));
                System.out.println("");
            }
        }
    }

    private String stats( int[] array ) {
        double mean = 0 ;
        int max = Integer.MIN_VALUE;
        for( int a : array ) {
            if( a > max )
                max = a ;
            mean += (double)a ;
        }
        mean = mean / (double)array.length;
        return "max = " + String.valueOf(max) + ", mean = " + String.valueOf(mean);
    }

    private void printCoverage(double[] cov ) {
        for( double v : cov )
            System.out.print(String.format("%.2f", v) + " ");
        System.out.print("\n");
    }
}
