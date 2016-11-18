package exp;

import Generation.AETG;
import Generation.ART;
import Generation.RCT;
import Generation.RT;
import Model.TestSuite;
import org.junit.Test;

public class Profile {

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
        printCoverage(ts.profileCoverage(pro));

        // RT
        System.out.print("RT:   ");
        new RT().generationFixedSize(ts, size);
        printCoverage(ts.profileCoverage(pro));

        // ART
        System.out.print("ART:  ");
        new ART().FSCS(ts, size);
        printCoverage(ts.profileCoverage(pro));
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
        printCoverage(ts.profileCoverage(pro));

        // ART
        System.out.print("ART:     ");
        new ART().FSCS(ts, size);
        printCoverage(ts.profileCoverage(pro));

        // maximize 2-way
        new RCT().generationFixed(ts, size, 2);
        System.out.print("2-way:   ");
        printCoverage(ts.profileCoverage(pro));

        // maximize 3-way
        new RCT().generationFixed(ts, size, 3);
        System.out.print("3-way:   ");
        printCoverage(ts.profileCoverage(pro));

        // maximize 4-way
        new RCT().generationFixed(ts, size, 4);
        System.out.print("4-way:   ");
        printCoverage(ts.profileCoverage(pro));

        // maximize profile
        System.out.print("profile: ");
        new RCT().generationProfile(ts, size, pro);
        printCoverage(ts.profileCoverage(pro));
    }

}
