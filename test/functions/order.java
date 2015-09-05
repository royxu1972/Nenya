package functions;

import Basic.*;
import Generation.*;
import Prioritization.*;
import org.junit.Before;
import org.junit.Test;

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
                ", 2-RFD = " + ts.getRFD(null, 2) + " " + ts.getRFD(null, 2));
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
}
