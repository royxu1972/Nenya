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
}
