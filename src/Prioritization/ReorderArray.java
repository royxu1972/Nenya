package Prioritization;

import Basic.SUT;
import Basic.TestSuite;

import java.util.ArrayList;
import java.util.Random;

/**
 *  Test Suite Prioritization
 *  Using toSomeOrder() function to change the TestSuite.order[]
 */
public class ReorderArray {

    private Random rand ;
    public ReorderArray() {
        this.rand = new Random();
    }

    /*
     *  default order, from 0 to n
     */
    public void toDefaultOrder( TestSuite test ) {
        for( int i=0 ; i<test.order.length ; i++ )
            test.order[i] = i ;
    }

    /*
     *  random order
     */
    public void toRandomOrder( TestSuite test ) {
        int size = test.order.length ;
        int[] flag = new int[size];
        for( int k=0 ; k<size; k++ )
            flag[k] = 0 ;
        
        int pos = rand.nextInt(size) ;
        test.order[0] = pos ;
        flag[pos] = 1 ;
        for( int index=1 ; index<size; index++ ) {
            while( flag[pos] == 1 )
                pos = rand.nextInt(size) ;
            test.order[index] = pos ;
            flag[pos] = 1 ;
        }
    }

    /*
     *  greedy prioritization by switching cost only
     *  randomly select the first one
     */
    public void toGreedySwitchOrder( TestSuite test ) {

        int size = test.order.length ;
        int[] already = new int[size];      // this test case has been added
        for (int k = 0; k < size; k++) {
            already[k] = 0;
        }
        
        // randomly select the first one
        int pc = rand.nextInt(size) ;
        test.order[0] = pc;
        already[pc] = 1;

        // select the i-th test case
        int index = -1;
        for (int i = 1; i < size; i++) {
            double min = Double.MAX_VALUE ;
            for (int k = 0; k < size; k++) {
                if (already[k] == 0) {
                    double tp = test.distance(test.order[i-1], k);
                    if (tp < min)
                        min = tp;
                }
            } // end for each unselected one
                
            // ties break
            ArrayList<Integer> Index = new ArrayList<Integer>();
            for (int k = 0; k < size; k++) {
                if (already[k] == 0) {
                    double tp = test.distance(test.order[i-1], k);
                    if (tp == min)
                        Index.add(k);
                }
            }
            index = rand.nextInt(Index.size());
            index = Index.get(index);

            test.order[i] = index;
            already[index] = 1;
        }
    }

    /*
     *  greedy prioritization by hybridising combination coverage and switching cost (coverage/cost)
     */
    public void toGreedyHybridOrder( TestSuite test ) {
        // coverage measurement
        SUT sut = test.system ;
        sut.GenerateS();

        int size = test.order.length ;
        int[] already = new int[size];      // this test case has been added
        for (int k = 0; k < size; k++)
            already[k] = 0;

        // random
        int pc = rand.nextInt(size) ;
        test.order[0] = pc;
        already[pc] = 1;

        // select the i-th test case
        int index = -1;
        for (int i = 1; i < size; i++) {
            double max = 0.0;
            for (int k = 0; k < size; k++) {
                if (already[k] == 0) {
                    double cost = test.distance(test.order[i-1], k);
                    int value = sut.FitnessValue(test.tests[k], 0);
                    double m = (double) value / cost;
                    if (m > max)
                        max = m;
                }
            } // end for each unselected one

            // ties break
            ArrayList<Integer> Index = new ArrayList<Integer>();
            for (int k = 0; k < size; k++) {
                if (already[k] == 0) {
                    double cost = test.distance(test.order[i - 1], k);
                    int value = sut.FitnessValue(test.tests[k], 0);
                    double m = (double)value / cost ;
                    if ( m == max)
                        Index.add(k);
                }
            }
            index = rand.nextInt(Index.size());
            index = Index.get(index);

            test.order[i] = index;
            already[index] = 1;
            sut.FitnessValue(test.tests[index],1);
        }
    }

    /*
     *  DP for TSP
     */
    public void toDPSwitchOrder( TestSuite test ) {
        ReorderArrayTSP tsp = new ReorderArrayTSP();
        int[] best = tsp.DPOrder(test);
        System.arraycopy(best, 0, test.order, 0, best.length);
    }

    /*
     *  GA for TSP
     */
    public void toGASwitchOrder( TestSuite test ) {
        ReorderArrayTSP tsp = new ReorderArrayTSP();
        int[] best = tsp.GAOrder(test);
        System.arraycopy(best, 0, test.order, 0, best.length);
    }

    /*
     *  LKH solver for TSP
     */
    public void toLKHSwitchOrder( TestSuite test ) {
        ReorderArrayTSP tsp = new ReorderArrayTSP();
        int[] best = tsp.LKHsolver(test);

        // save best sequence
        if( best.length == test.order.length )
            System.arraycopy(best, 0, test.order, 0, best.length);
        else
            System.err.println("solver is not run correctly");
    }
}
