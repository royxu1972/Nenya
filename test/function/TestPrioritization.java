package function;

import Model.TestSuite;
import EA.NSGA.NSSolution2D;
import Generation.AETG;
import Prioritization.ReorderArray;
import Prioritization.SEvolution;
import Prioritization.MEvolution;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

public class TestPrioritization {

    private TestSuite ts ;
    private ReorderArray re ;

    @Before
    public void init() {
        int p = 10 ;
        int[] v = new int[p] ;
        for( int k=0 ; k<p ; k++ )
            v[k] = 5 ;
        int t = 2 ;
        ts = new TestSuite(p, v, t);
        AETG gen = new AETG();
        gen.generation(ts);
        re = new ReorderArray();
    }


    @Test
    public void testSOBasedPrioritization() {
        re.toRandomOrder(ts);
        System.out.println("random,  cost = " + ts.getTotalSwitchingCost(null) + ", 2-RFD = " + ts.getRFD(null, 2));

        re.toDefaultOrder(ts);
        System.out.println("default, cost = " + ts.getTotalSwitchingCost(null) + ", 2-RFD = " + ts.getRFD(null, 2));

        re.toGreedySwitchOrder(ts);
        System.out.println("greedy,  cost = " + ts.getTotalSwitchingCost(null) + ", 2-RFD = " + ts.getRFD(null, 2));

        re.toGASwitchOrder(ts);
        System.out.println("GA,      cost = " + ts.getTotalSwitchingCost(null) + ", 2-RFD = " + ts.getRFD(null, 2));

        re.toLKHSwitchOrder(ts);
        System.out.println("LKH,     cost = " + ts.getTotalSwitchingCost(null) + ", 2-RFD = " + ts.getRFD(null, 2));

        re.toGreedyHybridOrder(ts);
        System.out.println("hybrid,  cost = " + ts.getTotalSwitchingCost(null) + ", 2-RFD = " + ts.getRFD(null, 2));
    }

    public enum ORDER {
        RANDOM("random"),
        COVERAGE("coverage"),
        GREEDY("switch-greedy"),
        GA("switch-GA"),
        LKH("switch-LKH"),
        HYBRID("hybrid"),
        MO("NSGA-II");

        private final String text;

        ORDER(String t) {
            this.text = t;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    @Test
    public void testEvaluation() {

        ORDER[] orders = new ORDER[]{
                ORDER.RANDOM,
                ORDER.COVERAGE,
                ORDER.GREEDY,
                ORDER.GA,
                ORDER.LKH,
                ORDER.HYBRID,
                ORDER.MO
        };

        // 3. get different orders, and evaluate basic indicators
        // the first length-1 orders are single-objective, the last one is the ideal order of multi-objective
        NSSolution2D[] solutions = new NSSolution2D[orders.length];

        // the solutions of single objective optimization
        // which will be used to determine the reference pareto front
        ArrayList<NSSolution2D> other = new ArrayList<>() ;

        // the best and worst value of cost and RFD, which is used to calculate reference (ideal) point
        double[] cost = {Double.MAX_VALUE, Double.MIN_VALUE};
        double[] RFD  = {Double.MIN_VALUE, Double.MAX_VALUE};

        // do prioritization for each single-objective order
        for (int i = 0 ; i < orders.length - 1 ; i++ ) {
            switch ( orders[i] ) {
                case RANDOM:
                    re.toRandomOrder(ts);
                    break;
                case COVERAGE:
                    re.toDefaultOrder(ts);
                    break;
                case GREEDY:
                    re.toGreedySwitchOrder(ts);
                    break;
                case GA:
                    re.toGASwitchOrder(ts);
                    break;
                case LKH:
                    re.toLKHSwitchOrder(ts);
                    break;
                case HYBRID:
                    re.toGreedyHybridOrder(ts, 2);
                    break;
            }

            if( !ts.isValidTestingOrder(null) )
                System.err.println( orders[i].toString() + " error, Invalid Order!");

            // evaluate basic indicators, RFD and total switching cost
            double tc = ts.getTotalTestingCost(null);
            double r = ts.getRFD(null, 2);

            // update ideal point
            if( tc < cost[0] )
                cost[0] = tc ;
            if( tc > cost[1] )
                cost[1] = tc ;

            if( r > RFD[0] )
                RFD[0] = r ;
            if( r < RFD[1] )
                RFD[1] = r ;

            // save single solution
            solutions[i] = new NSSolution2D(ts.order, tc, r, 0, 0.0 );

            // add single solution to reference front
            other.add(solutions[i]);
        }

        // 3.2 do prioritization for multi-objective order
        ArrayList<NSSolution2D> front = new ArrayList<>() ;     // the final front
        ArrayList<NSSolution2D> reference = new ArrayList<>() ; // the reference front

        re.toMultiObjective(ts, front, other, reference, cost, RFD);
        solutions[orders.length-1] = new NSSolution2D(
                ts.order, ts.getTotalTestingCost(null), ts.getRFD(null, 2), 0, 0.0);

        // print
        for( int i=0 ; i<orders.length ; i++ ) {
            System.out.println(orders[i] + " " + solutions[i]);
        }
    }


    @Test
    public void testSEvolution() {
        SEvolution se = new SEvolution(ts);
        se.run();
        System.out.println(Arrays.toString(se.solution));
        System.out.println("cost = " + ts.getTotalSwitchingCost(se.solution));
        System.out.println("RFD  = " + ts.getRFD(se.solution, 2));
    }


    @Test
    public void testMEvolution() {
        MEvolution me = new MEvolution(ts);
        me.run();
        System.out.println("final front");
        me.NSGA.printPopulation(me.NSGA.finalFront);
    }

}
