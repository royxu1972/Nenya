package Prioritization;

import Model.SUT;
import Model.TestSuite;
import EA.oldGA.NSSolution2D;

import java.util.ArrayList;
import java.util.Random;

/**
 *  Test Suite Prioritization
 *  Using toSomeOrder() function to change TestSuite.solution[]
 */
public class ReorderArray {

    private Random rand ;
    public ReorderArray() {
        this.rand = new Random();
    }

    /*
     *  Default solution, from 0 to n-1
     */
    public void toDefaultOrder( TestSuite test ) {
        for( int i=0 ; i<test.order.length ; i++ )
            test.order[i] = i ;
    }

    /*
     *  Random solution
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
     *  t-way coverage based oder by greedy algorithm.
     *  Randomly select the first one.
     */
    public void toGreedyCoverageOrder( TestSuite test, int t ) {
        int size = test.order.length ;
        int[] already = new int[size] ;  // this test case has been added, default = 0

        // coverage measurement
        SUT s = new SUT(test.system.parameter, test.system.value, t) ;
        s.initialization();

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
     *  Switching cost based solution by greedy algorithm.
     *  Randomly select the first one.
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
     *  Hybrid based solution by greedy algorithm. When determining the next test case to run,
     *  metric = (t-way combination coverage) / (execution + switching cost)
     */
    public void toGreedyHybridOrder( TestSuite test, int t ) {
        // coverage measurement
        SUT s = new SUT(test.system.parameter, test.system.value, t) ;
        s.initialization();

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
        this.toGreedyHybridOrder(test, 2);
    }

    /*
     *  Switching cost based solution by GA
     */
    public void toGASwitchOrder( TestSuite test ) {
        /*
        SEvolution se = new SEvolution(test);
        se.run();

        // the recommended setting
        se.GA.setParameter(100, 360, 0.9, 0.7);

        System.arraycopy(se.solution, 0, test.order, 0, se.solution.length);
        */
    }

    /*
     *  Switching cost based solution by GA by DP for TSP
     */
    public void toDPSwitchOrder( TestSuite test ) {
        ReorderArrayTSP tsp = new ReorderArrayTSP();
        int[] best = tsp.DPOrder(test);
        System.arraycopy(best, 0, test.order, 0, best.length);
    }

    /*
     *  Switching cost based solution by GA LKH solver for TSP
     */
    public void toLKHSwitchOrder( TestSuite test ) {
        ReorderArrayTSP tsp = new ReorderArrayTSP();
        int[] best = tsp.LKHsolver(test);

        // save best sequence
        if( best.length == test.order.length )
            System.arraycopy(best, 0, test.order, 0, best.length);
        else
            System.err.println("solver does not run correctly");
    }

    /*
     *  Multi-objective optimization based solution by NSGA-II, whose aim is to balance
     *  combination coverage and testing cost. NSGA-II will return a set of near optimal
     *  non-dominated solutions, which will be saved in data.
     *
     *  Advanced Options
     *  INPUT:  test      - test suite
     *          other     - solutions that are produced by other approaches
     *          cost[]    - the minimum & maximum cost
     *          RFD[]     - the maximum & minimum RFD
     *  OUTPUT: data      - final front
     *          reference - the reference pareto front
     *          best      - the best solution, which will be saved in test.order
     */
    public void toMultiObjective( TestSuite test, ArrayList<NSSolution2D> front, ArrayList<NSSolution2D> other,
                                  ArrayList<NSSolution2D> reference, double[] cost, double[] RFD) {
        front.clear();
        reference.clear();

        MEvolution me = new MEvolution(test);

        // the recommended setting
        me.NSGA.setParameter(60, 600, 0.9, 0.7);

        me.run();

        // the final solution set: data = me.result
        me.result.stream().forEach( p -> front.add(p.clone()) );

        // the reference pareto front
        me.NSGA.assignReferenceFront(other, reference);

        if( cost != null && RFD != null ) {
            // the solutions that contribute to the reference front
            ArrayList<NSSolution2D> K = new ArrayList<>();
            me.NSGA.assignContributedSolutions(front, reference, K);

            // identify the best solution
            NSSolution2D best = me.NSGA.getBestSolution2D(cost, RFD, K);
            System.arraycopy(best.solution, 0, test.order, 0, best.solution.length);
        }
    }

}
