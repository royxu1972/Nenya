package functions;

import Basic.*;
import Prioritization.*;
import org.junit.Before;
import org.junit.Test;

public class orderMobile {

    private TestSuite ts ;
    private ReorderArray re = new ReorderArray() ;
/*
    @Before
    public void init() {
        ts = new TestSuite(
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
        ts.setExecutionCost(0.0, 0.0);
    }


    @Test
    public void testPaperCase() {
        // test getAverageNumberOfSwitches()
        System.out.println("--------------------------");
        System.out.println("GET average switches = " + ts.getAverageNumberOfSwitches(null));

        // test RFD and RFDc
        double maxTime = 0 ;
        int[] Pd = new int[]{0,1,2,3,4,5,6,7,8};
        int[] Po = new int[]{3,8,1,6,2,4,0,5,7};
        //re.toGreedyHybridOrder(ts);
        int[] Ph = new int[]{2,6,4,0,5,7,3,8,1};


        System.out.print("default total switching cost = " + ts.getTotalSwitchingCost(Pd) + ", switchCost = [");
        double[] a = ts.getAdjacentSwitchingCost(Pd);
        for ( int i=0 ; i<a.length ; i++ )
            System.out.print( a[i] + " ");
        System.out.print("]\n");

        System.out.print("DP total switching cost = " + ts.getTotalSwitchingCost(Po) + ", switchCost = [");
        a = ts.getAdjacentSwitchingCost(Po);
        for ( int i=0 ; i<a.length ; i++ )
            System.out.print( a[i] + " ");
        System.out.print("]\n");
        if( ts.getTotalTestingCost(Po) > maxTime )
            maxTime = ts.getTotalTestingCost(Po) ;

        System.out.print("hybrid total switching cost = " + ts.getTotalSwitchingCost(Ph) + ", switchCost = [");
        a = ts.getAdjacentSwitchingCost(Ph);
        for ( int i=0 ; i<a.length ; i++ )
            System.out.print( a[i] + " ");
        System.out.print("]\n");
        if( ts.getTotalTestingCost(Ph) > maxTime )
            maxTime = ts.getTotalTestingCost(Ph);


        // rfd
        System.out.println("\n---------2-RFD-----------");

        System.out.print("default rfd = " + ts.getRFD(Pd, 2) + ", seq = [");
        long[] od = ts.getCoverEach(Pd, 2);
        for ( int i=0 ; i<od.length ; i++ )
            System.out.print( od[i] + " ");
        System.out.print("]\n");

        System.out.print("optimized rfd = " + ts.getRFD(Po, 2) + ", seq = [");
        od = ts.getCoverEach(Po, 2);
        for ( int i=0 ; i<od.length ; i++ )
            System.out.print( od[i] + " ");
        System.out.print("]\n");

        System.out.print("hybrid rfd = " + ts.getRFD(Ph, 2) + ", seq = [");
        od = ts.getCoverEach(Ph, 2);
        for ( int i=0 ; i<od.length ; i++ )
            System.out.print( od[i] + " ");
        System.out.print("]\n");

        System.out.println("\n---------2-RFDc-----------");
        System.out.println("default S = " + ts.getRFDc(Pd, 2, maxTime));
        System.out.println("optimized S = " + ts.getRFDc(Po, 2, maxTime));
        System.out.println("hybrid S = " + ts.getRFDc(Ph, 2, maxTime));

    }*/
}
