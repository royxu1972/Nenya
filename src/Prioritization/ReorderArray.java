package Prioritization;

import Basic.SUT;
import Basic.TestSuite;

import java.util.ArrayList;
import java.util.Random;

/**
 *  Test Suite Prioritization
 *  Using toSomeOrder() function to change TestSuite.order[]
 */
public class ReorderArray {

    private Random rand ;
    public ReorderArray() {
        this.rand = new Random();
    }

    /*
     *  default order, from 0 to n-1
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
        int[] flag = new int[size]; // default = 0
        
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
     *  greedy prioritization by t-way coverage only
     *  randomly select the first one
     */
    public void toGreedyCoverageOrder( TestSuite test, int t ) {
        int size = test.order.length ;
        int[] already = new int[size] ;  // this test case has been added, default = 0

        // coverage measurement
        SUT s = new SUT(test.system.parameter, test.system.value, t) ;
        s.GenerateS();

        // randomly select the first one
        int pc = rand.nextInt(size) ;
        test.order[0] = pc ;
        already[pc] = 1 ;
        s.FitnessValue(test.tests[pc], 1);

        // select the i-th test case
        int index ;
        for (int i = 1; i < size; i++) {
            // compute the # of covered combinations for remaining test cases
            int[] covered = new int[size] ;
            int max = Integer.MIN_VALUE ;
            for (int k = 0; k < size; k++) {
                if (already[k] == 0) {
                    covered[k] = s.FitnessValue(test.tests[k], 0);
                    if( covered[k] > max )
                        max = covered[k] ;
                }
                else
                    covered[k] = -1 ;
            }

            // ties break
            ArrayList<Integer> II = new ArrayList<>();
            for (int k = 0; k < size; k++) {
                if (already[k] == 0 && covered[k] == max )
                    II.add(k);
            }
            index = rand.nextInt(II.size());
            index = II.get(index);

            test.order[i] = index;
            already[index] = 1;
            s.FitnessValue(test.tests[index], 1);
        }
    }

    /*
     *  greedy prioritization by switching cost only
     *  randomly select the first one
     */
    public void toGreedySwitchOrder( TestSuite test ) {
        int size = test.order.length ;
        int[] already = new int[size];      // this test case has been added, default = 0
        
        // randomly select the first one
        int pc = rand.nextInt(size) ;
        test.order[0] = pc;
        already[pc] = 1;

        // select the i-th test case
        int index ;
        for (int i = 1; i < size; i++) {
            // compute the cost for remaining test cases
            double[] costs = new double[size];
            double min = Double.MAX_VALUE ;
            for (int k = 0; k < size; k++) {
                if (already[k] == 0) {
                    costs[k] = test.distance(test.order[i-1], k);
                    if (costs[k] < min)
                        min = costs[k];
                }
                else
                    costs[k] = -1.0 ;
            } // end for each unselected one
                
            // ties break
            ArrayList<Integer> II = new ArrayList<>();
            for (int k = 0; k < size; k++) {
                if (already[k] == 0 && costs[k] == min)
                    II.add(k);
            }
            index = rand.nextInt(II.size());
            index = II.get(index);

            test.order[i] = index;
            already[index] = 1;
        }
    }

    /*
     *  greedy prioritization by hybridising combination coverage and switching cost
     *            coverage
     *  metric = ---------- when selecting each test case
     *              cost
     */
    public void toGreedyHybridOrder( TestSuite test, int t ) {
        // coverage measurement
        SUT s = new SUT(test.system.parameter, test.system.value, t) ;
        s.GenerateS();

        int size = test.order.length ;
        int[] already = new int[size];  // this test case has been added, default = 0

        // randomly select the first one
        int pc = rand.nextInt(size) ;
        test.order[0] = pc;
        already[pc] = 1;
        s.FitnessValue(test.tests[pc], 1);

        // select the i-th test case
        int index ;
        for (int i = 1; i < size; i++) {
            // compute the metric value for remaining test cases
            double[] metrics = new double[size] ;
            double max = 0.0;
            for (int k = 0; k < size; k++) {
                if (already[k] == 0) {
                    double cost = test.distance(test.order[i-1], k) + test.executionCost[k] ;
                    int value = s.FitnessValue(test.tests[k], 0);
                    metrics[k] = (double) value / cost;
                    if (metrics[k] > max)
                        max = metrics[k] ;
                }
                else
                    metrics[k] = -1.0 ;
            }

            // ties break
            ArrayList<Integer> II = new ArrayList<>();
            for (int k = 0; k < size; k++) {
                if (already[k] == 0 && metrics[k] == max )
                    II.add(k);
            }
            index = rand.nextInt(II.size());
            index = II.get(index);

            test.order[i] = index;
            already[index] = 1;
            s.FitnessValue(test.tests[index], 1);
        }
    }

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
        int index ;
        for (int i = 1; i < size; i++) {
            double max = 0.0;
            for (int k = 0; k < size; k++) {
                if (already[k] == 0) {
                    double cost = test.distance(test.order[i-1], k) + test.executionCost[k] ;
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
                    double cost = test.distance(test.order[i - 1], k) + test.executionCost[k] ;
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

    /*
     *  Multi-Objective Optimization to balance combination coverage and switching cost
     *  NSGA-II will return a set of near optimal non-dominated solutions, which will be
     *  saved in ArrayList<int[]> data
     */
    public void toMultiObjective( TestSuite test, ArrayList<int[]> data ) {
        MEvolution me = new MEvolution(40, 1000, 0.9, 0.7, test);
        me.evolve();
        //me.printPool();
        me.assignBestFront(data);
    }

}
