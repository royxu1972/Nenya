package prioritization;

import Basic.TestSuite;
import Generation.AETG;
import Prioritization.ReorderArray;

import java.util.ArrayList;

/**
 * Compare the ability of minimizing switching cost among
 * greedy approach, GA, LKH solver and multi objective approach.
 */
public class Comparison {

    // 6 subjects (final)
    double[] default_cost ;
    long[] default_rfd ;
    // 6 subjects * 4 approaches (final)
    double[][] best_cost ;
    long[][] best_rfd ;
    long[][] best_time ;
    double[][] mean_cost ;
    long[][] mean_rfd ;
    long[][] mean_time ;
    // 6 subjects * 4 approaches * 30 repeat
    double[][][] cost ;
    long[][][] rfd ;
    long[][][] time ;

    // all subjects
    ArrayList<TestSuite> Subject ;

    public Comparison(){
        this.Subject = new ArrayList<>();
        this.default_cost = new double[6] ;
        this.default_rfd = new long[6] ;
        this.best_cost = new double[6][4];
        this.best_rfd = new long[6][4];
        this.best_time = new long[6][4];
        this.mean_cost = new double[6][4];
        this.mean_rfd = new long[6][4];
        this.mean_time = new long[6][4];
        this.cost = new double[6][4][30];
        this.rfd = new long[6][4][30];
        this.time = new long[6][4][30];
        init();
    }

    // run experiment
    public void run() {
        AETG gen = new AETG();
        ReorderArray re = new ReorderArray();

        // for each subject
        for ( int index=0 ; index<6 ; index++ ) {
            System.out.println("do subject " + index);
            TestSuite ts = Subject.get(index) ;

            // generate CA
            gen.Generation(ts);

            // reorder, for each approach
            // 0. default
            re.toDefaultOrder(ts);
            default_cost[index] = ts.getTotalSwitchingCost(null) ;
            default_rfd[index] = ts.getRFD(null) ;

            // 1. greedy
            for ( int i=0 ; i<30 ; i++ ) {
                long t1 = System.currentTimeMillis();
                re.toGreedySwitchOrder(ts);
                long t2 = System.currentTimeMillis();

                cost[index][0][i] = ts.getTotalSwitchingCost(null) ;
                rfd[index][0][i] = ts.getRFD(null) ;
                time[index][0][i] = t2 - t1 ;
            }

            // 2. GA
            for ( int i=0 ; i<30 ; i++ ) {
                long t1 = System.currentTimeMillis();
                re.toGASwitchOrder(ts);
                long t2 = System.currentTimeMillis();

                cost[index][1][i] = ts.getTotalSwitchingCost(null) ;
                rfd[index][1][i] = ts.getRFD(null) ;
                time[index][1][i] = t2 - t1 ;
            }

            // 3. LKH
            for ( int i=0 ; i<30 ; i++ ) {
                long t1 = System.currentTimeMillis();
                re.toLKHSwitchOrder(ts);
                long t2 = System.currentTimeMillis();

                cost[index][2][i] = ts.getTotalSwitchingCost(null) ;
                rfd[index][2][i] = ts.getRFD(null) ;
                time[index][2][i] = t2 - t1 ;
            }

            // 4. multi
            for ( int i=0 ; i<30 ; i++ ) {
                long t1 = System.currentTimeMillis();
                re.toGreedyHybridOrder(ts);
                long t2 = System.currentTimeMillis();

                cost[index][3][i] = ts.getTotalSwitchingCost(null) ;
                rfd[index][3][i] = ts.getRFD(null) ;
                time[index][3][i] = t2 - t1 ;
            }
        }

        // sort
        for (int i = 0 ; i < 6 ; i++) {
            for (int j = 0; j < 4; j++) {
                int index = 0;
                double min = Double.MAX_VALUE;
                double sum1 = 0.0;
                long sum2 = 0, sum3 = 0;
                for (int k = 0; k < 30; k++) {
                    if (cost[i][j][k] < min) {
                        index = k;
                        min = cost[i][j][k];
                    }
                    sum1 += cost[i][j][k];
                    sum2 += rfd[i][j][k];
                    sum3 += time[i][j][k];
                }
                best_cost[i][j] = min;
                best_rfd[i][j] = rfd[i][j][index];
                best_time[i][j] = time[i][j][index];

                mean_cost[i][j] = sum1 / 30.0;
                mean_rfd[i][j] = sum2 / 30 ;
                mean_time[i][j] = sum3 / 30 ;
            }
        }

        print();
    }

    // print result
    public void print() {
        for( int index = 0 ; index < 6 ; index++ ) {
            System.out.println("---------case " + index + "---------");
            System.out.println("size: " + Subject.get(index).getTestSuiteSize() );
            System.out.println("default: cost = " + default_cost[index] + ", rfd = " + default_rfd[index]);
            System.out.print("greedy: " +
                    "cost = " + best_cost[index][0] + ", " +
                    "rfd = " + best_rfd[index][0] + ", " +
                    "time = " + best_time[index][0] + "\n");
            System.out.print("GA: " +
                    "cost = " + best_cost[index][1] + ", " +
                    "rfd = " + best_rfd[index][1] + ", " +
                    "time = " + best_time[index][1] + "\n" );
            System.out.print("LKH: " +
                    "cost = " + best_cost[index][2] + ", " +
                    "rfd = " + best_rfd[index][2] + ", " +
                    "time = " + best_time[index][2] + "\n" );
            System.out.print("MO: " +
                    "cost = " + best_cost[index][3] + ", " +
                    "rfd = " + best_rfd[index][3] + ", " +
                    "time = " + best_time[index][3] + "\n" );
            System.out.println("");
        }
    }

    // initialize subjects
    public void init(){
        // ts1
        int[] v1 = new int[34] ;
        for( int k=0 ; k<v1.length ; k++ )
            v1[k] = 2 ;
        Subject.add(new TestSuite(34, v1, 2));

        // ts2, bugzilla
        int[] v2 = new int[52] ;
        for( int k=0 ; k<49 ; k++ )
            v2[k] = 2 ;
        v2[49] = 3 ;
        v2[50] = 4 ;
        v2[51] = 4 ;
        Subject.add(new TestSuite(52, v2, 2));

        // ts3, apache
        int[] v3 = new int[172] ;
        for( int k=0 ; k<158 ; k++ )
            v3[k] = 2 ;
        for( int k=158 ; k<166 ; k++ )
            v3[k] = 3 ;
        for( int k=166 ; k<170 ; k++ )
            v3[k] = 4 ;
        v3[170] = 5 ;
        v3[171] = 6 ;
        Subject.add(new TestSuite(172, v3, 2));

        // ts4, flex
        int[] v4 = new int[7];
        v4[0] = 2 ;
        v4[1] = 2 ;
        v4[2] = 2 ;
        v4[3] = 2 ;
        v4[4] = 3 ;
        v4[5] = 6 ;
        v4[6] = 16 ;
        Subject.add(new TestSuite(7, v4, 3));

        // ts5, make, 3-way
        int[] v5 = new int[8] ;
        v5[0] = 2 ;
        v5[1] = 2 ;
        v5[2] = 2 ;
        v5[3] = 3 ;
        v5[4] = 3 ;
        v5[5] = 3 ;
        v5[6] = 4 ;
        v5[7] = 5 ;
        Subject.add(new TestSuite(8, v5, 4));

        // ts5, grep, 4-way
        int[] v6 = new int[7];
        v6[0] = 2 ;
        v6[1] = 2 ;
        v6[2] = 3 ;
        v6[3] = 3 ;
        v6[4] = 4 ;
        v6[5] = 4 ;
        v6[6] = 12 ;
        Subject.add(new TestSuite(7, v6, 4));

    }





}
