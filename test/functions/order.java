package functions;

import Basic.*;
import Generation.*;
import Prioritization.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

public class order {

    private TestSuite ts ;
    private AETG gen ;
    private ReorderArray re ;

    @Before
    public void init() {
        int p = 6 ;
        int[] v = new int[p] ;
        for( int k=0 ; k<p ; k++ )
            v[k] = 5 ;
        int t = 2 ;
        this.ts = new TestSuite(p, v, t);
        this.gen = new AETG();
        this.gen.Generation(ts);
        this.re = new ReorderArray();
    }

    @Test
    public void testPrioritization() {
        this.re.toDefaultOrder(ts);
        System.out.println("average number of switches = " + ts.getAverageSwitchingCost());
        System.out.println("default (" + ts.getOrderString() + "), cost = " + ts.getTotalSwitchingCost(null));

        this.re.toGreedySwitchOrder(ts);
        System.out.println("greedy (" + ts.getOrderString() + "), cost = " + ts.getTotalSwitchingCost(null));

        this.re.toGASwitchOrder(ts);
        System.out.println("GA (" + ts.getOrderString() + "), cost = " + ts.getTotalSwitchingCost(null));

        this.re.toLKHSwitchOrder(ts);
        System.out.println("LKH (" + ts.getOrderString() + "), cost = " + ts.getTotalSwitchingCost(null));
    }

    @Test
    public void testMO() {
        System.out.println("Multi-Objective Optimization");
        ArrayList<int[]> d = new ArrayList<int[]>();
        this.re.toMultiObjective(ts, d);
    }

    @Test
    public void testSmallInstance() {
        // SUT setting
        int p = 10 ;
        int[] v = new int[p] ;
        for( int k=0 ; k<p ; k++ )
            v[k] = 3 ;
        int t = 2 ;
        TestSuite ts = new TestSuite(p, v, t);

        AETG gen = new AETG();
        gen.Generation(ts);

        ReorderArray re = new ReorderArray();
        re.toDefaultOrder(ts);
        System.out.println("average number of switches = " + ts.getAverageSwitchingCost());
        System.out.println("default (" + ts.getOrderString() + "), cost = " + ts.getTotalSwitchingCost(null));

        re.toGreedySwitchOrder(ts);
        System.out.println("greedy (" + ts.getOrderString() + "), cost = " + ts.getTotalSwitchingCost(null));

        re.toGASwitchOrder(ts);
        System.out.println("GA (" + ts.getOrderString() + "), cost = " + ts.getTotalSwitchingCost(null));

        re.toDPSwitchOrder(ts);
        System.out.println("DP (" + ts.getOrderString() + "), cost = " + ts.getTotalSwitchingCost(null));
    }

    @Test
    public void testPaperCase() {
        TestSuite ts = new TestSuite(
                5,
                new int[]{3,3,2,3,2},
                2,
                new double[]{2.0, 4.0, 1.0, 2.0, 1.0}
        );
        ts.tests = new int[][]{
                {2,2,0,0,0},
                {1,0,1,1,0},
                {0,1,1,2,1},
                {0,0,0,0,1},
                {2,1,0,1,1},
                {1,2,0,2,1},
                {1,1,1,0,0},
                {0,2,1,1,0},
                {2,0,1,2,0}
        };
        ts.order = new int[]{0,1,2,3,4,5,6,7,8};

        re.toDefaultOrder(ts);
        System.out.println("default (" + ts.getOrderString() + "), cost = " + ts.getTotalSwitchingCost(null));
        double[] a = ts.getAdjacentSwitchingCost(null);
        for ( int i=0 ; i<a.length ; i++ )
            System.out.print( a[i] + " ");
        System.out.print("\n");

        re.toDPSwitchOrder(ts);
        System.out.println("switching (" + ts.getOrderString() + "), cost = " + ts.getTotalSwitchingCost(null));
        a = ts.getAdjacentSwitchingCost(null);
        for ( int i=0 ; i<a.length ; i++ )
            System.out.print( a[i] + " ");
        System.out.print("\n");

        // rfd
        System.out.println("----------------------");
        int[] Pd = new int[]{0,1,2,3,4,5,6,7,8};
        int[] Po = new int[]{3,8,1,6,2,4,0,5,7};

        System.out.println("default cost = " + ts.getTotalSwitchingCost(Pd) + ", rfd = " + ts.getRFD(Pd));
        long[] od = ts.getRFDeach(Pd);
        for ( int i=0 ; i<od.length ; i++ )
            System.out.print( od[i] + " ");
        System.out.print("\n");

        System.out.println("optimized cost = " + ts.getTotalSwitchingCost(Po) + ", rfd = " + ts.getRFD(Po));
        od = ts.getRFDeach(Po);
        for ( int i=0 ; i<od.length ; i++ )
            System.out.print( od[i] + " ");
        System.out.print("\n");
    }
}
