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
        int p = 20 ;
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
        System.out.println("----------------------------");
        System.out.println("average number of switches = " + ts.getAverageSwitchingCost());

        this.re.toDefaultOrder(ts);
        System.out.println("default order, cost = " + ts.getTotalSwitchingCost(null) +
                ", 2-RFD = " + ts.getRFD(null, 2) + " " + ts.getRFD(null));
        //System.out.println(ts.getOrderString());

        this.re.toGreedySwitchOrder(ts);
        System.out.println("greedy switch order, cost = " + ts.getTotalSwitchingCost(null) +
                ", 2-RFD = " + ts.getRFD(null, 2) +
                ", 3-RFD = " + ts.getRFD(null, 3) +
                ", 4-RFD = " + ts.getRFD(null, 4) );
        //System.out.println(ts.getOrderString());

        this.re.toLKHSwitchOrder(ts);
        System.out.println("LKH order, cost = " + ts.getTotalSwitchingCost(null) +
                ", 2-RFD = " + ts.getRFD(null, 2) +
                ", 3-RFD = " + ts.getRFD(null, 3) +
                ", 4-RFD = " + ts.getRFD(null, 4) );
        //System.out.println(ts.getOrderString());

        this.re.toGreedyCoverageOrder(ts, 2);
        System.out.println("2-cov order, cost = " + ts.getTotalSwitchingCost(null) +
                ", 2-RFD = " + ts.getRFD(null, 2) +
                ", 3-RFD = " + ts.getRFD(null, 3) +
                ", 4-RFD = " + ts.getRFD(null, 4) );
        //System.out.println(ts.getOrderString());

        this.re.toGreedyCoverageOrder(ts, 3);
        System.out.println("3-cov order, cost = " + ts.getTotalSwitchingCost(null) +
                ", 2-RFD = " + ts.getRFD(null, 2) +
                ", 3-RFD = " + ts.getRFD(null, 3) +
                ", 4-RFD = " + ts.getRFD(null, 4) );
        //System.out.println(ts.getOrderString());

        this.re.toGreedyCoverageOrder(ts, 4);
        System.out.println("4-cov order, cost = " + ts.getTotalSwitchingCost(null) +
                ", 2-RFD = " + ts.getRFD(null, 2) +
                ", 3-RFD = " + ts.getRFD(null, 3) +
                ", 4-RFD = " + ts.getRFD(null, 4) );
        //System.out.println(ts.getOrderString());

        this.re.toGreedyHybridOrder(ts, 2);
        System.out.println("2-hybrid order, cost = " + ts.getTotalSwitchingCost(null) +
                ", 2-RFD = " + ts.getRFD(null, 2) +
                ", 3-RFD = " + ts.getRFD(null, 3) +
                ", 4-RFD = " + ts.getRFD(null, 4) );
        //System.out.println(ts.getOrderString());

        this.re.toGreedyHybridOrder(ts, 3);
        System.out.println("3-hybrid order, cost = " + ts.getTotalSwitchingCost(null) +
                ", 2-RFD = " + ts.getRFD(null, 2) +
                ", 3-RFD = " + ts.getRFD(null, 3) +
                ", 4-RFD = " + ts.getRFD(null, 4) );
        //System.out.println(ts.getOrderString());

        this.re.toGreedyHybridOrder(ts, 4);
        System.out.println("4-hybrid order, cost = " + ts.getTotalSwitchingCost(null) +
                ", 2-RFD = " + ts.getRFD(null, 2) +
                ", 3-RFD = " + ts.getRFD(null, 3) +
                ", 4-RFD = " + ts.getRFD(null, 4) );
        //System.out.println(ts.getOrderString());

        System.out.println("----------------------------");
    }

    @Test
    public void testMO() {
        System.out.println("Multi-Objective Optimization");
        ArrayList<int[]> d = new ArrayList<>();
        this.re.toMultiObjective(ts, d);
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

        // test getAverageSwitches()
        System.out.println("average switches = " + ts.getAverageSwitches(null));

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
