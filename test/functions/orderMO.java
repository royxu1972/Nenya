package functions;

import Basic.TestSuite;
import Generation.AETG;
import Prioritization.ReorderArray;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

public class orderMO {

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
    public void testMO() {
        System.out.println("Multi-Objective Optimization");

        // switching greedy
        re.toGreedySwitchOrder(ts);
        System.out.println("greedy switch order, cost = " + ts.getTotalCost(null) +
                ", 2-RFD = " + ts.getRFD(null, 2));

        // hybrid greedy
        re.toGreedyHybridOrder(ts);
        double hy_cost = ts.getTotalCost(null);
        long hy_rfd = ts.getRFD(null);
        System.out.println("greedy hybrid order, cost = " + hy_cost +
                ", 2-RFD = " + hy_rfd);

        // MO
        ArrayList<int[]> d = new ArrayList<>();
        /*
        this.re.toMultiObjective(ts, d);

        double sum_cost = 0.0 ;
        long sum_RFD = 0 ;
        for( int[] seq : d ) {
            sum_cost += ts.getTotalCost(seq);
            sum_RFD += ts.getRFD(seq);
        }
        System.out.println("MO order, average, cost = " + sum_cost/(double)d.size() +
                ", 2-RFD = " + sum_RFD/(long)d.size());

        // find dominated solution
        for( int[] seq : d ) {
            double cc = ts.getTotalCost(seq) ;
            long rr = ts.getRFD(seq) ;

            if ( cc <= hy_cost && rr >= hy_rfd ) {
                System.out.println("better | cost = " + cc + ", rfd = " + rr );
            }
            if ( cc >= hy_cost && rr <= hy_rfd ) {
                System.out.println("worse | cost = " + cc + ", rfd = " + rr );
            }
        }
        */


    }
}
