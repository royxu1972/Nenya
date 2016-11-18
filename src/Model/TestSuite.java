package Model;

import Basic.ALG;
import Basic.RandomTool;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *  Basic data structure and evaluations for classic test suite
 */
public class TestSuite {

    public SUT system;       // software under test
    public int[][] tests;    // test suite

    // For Test Suite Prioritization
    public int[] order;             // the test sequence
    public double[] executionCost;  // the execution cost of each test, default = 0
    public double[] weight;         // the switching weight of each parameter, default = 1.0

    public TestSuite(int p, int[] v, int t) {
        this.system = new SUT(p, v, t);
        this.tests = null;
        this.weight = new double[p];
        this.executionCost = null;
        for (int k = 0; k < p; k++)
            this.weight[k] = 1.0;
    }

    public TestSuite(SUT sut) {
        this.system = sut.clone() ;
        this.tests = null;
        this.weight = new double[sut.parameter];
        this.executionCost = null;
        for (int k = 0; k < sut.parameter; k++)
            this.weight[k] = 1.0;
    }

    public int testSuiteSize() {
        return this.tests.length;
    }

    public void showTestSuite() {
        int index = 0 ;
        for( int[] row : tests ) {
            System.out.println("#" + index + " : " + Arrays.toString(row));
            index++;
        }
        System.out.println("size = " + tests.length);
    }

    /*
     *  Set constraint
     */
    public void setConstraint( final int[][] constraint ) {
        system.setConstraint(constraint, 1);
    }

    /*
     *  BASIC EVALUATION
     *  Compute t-way combination coverage of tests
     */
    public double tCoverage( int t ) {
        // get the total number of t-way combinations
        // the invalid combinations are removed by init()
        SUT ss = system.clone();
        ss.setCoveringStrength(t);
        ss.initialization();
        int total = ss.getCombAll();

        // iterative each parameter combination
        // to compute the number of covered combinations
        int total_covered = 0 ;
        int[][] pComb = ALG.allC(ss.parameter, t);

        for( int[] pos : pComb ) {
            // all possible value combinations
            int len = ALG.combineValue(pos, ss.value);
            int[] cover = new int[len];
            for (int i = 0; i < len; i++)
                cover[i] = 0;

            int covered = 0 ;
            // for each row in tests
            int[] sch = new int[t];
            for (int[] row : tests) {
                for (int k = 0; k < t; k++)
                    sch[k] = row[pos[k]];
                int index = ALG.val2num(pos, sch, t, ss.value);

                if (cover[index] == 0) {
                    cover[index] = 1;
                    covered++;
                }
            }
            total_covered += covered;
        }

        return (double)(total_covered) / (double)total ;
    }


    /*
     *  BASIC EVALUATION
     *  Compute the fault profile coverage
     *  F = p(1) * cov(1) + p(2) * cov(2) + ... + p(6) * cov(6)
     *
     *  Return:
     *  t-way coverage where t = 1, 2, 3, 4, ..., 6 and profile coverage
     */
    public double[] profileCoverage( double[] profile ) {
        double[] cov = new double[profile.length+1];
        double f = 0.0 ;
        for( int t=0 ; t<profile.length ; t++ ) {
            cov[t] = tCoverage(t+1);
            f = f + profile[t] * cov[t];
        }
        cov[profile.length] = f ;

        return cov ;
    }

    /*
     *  BASIC EVALUATION
     *  Compute how many randomFaults in ArrayList<int[]> randomFaults can be
     *  detected by test suite tests[][]. Each fault is represented
     *  as a test case, where -1 indicates unfixed value.
     */
    public int getFaultDetection(final ArrayList<int[]> faults, final int[] defaults) {
        int num = 0 ;
        for( int[] f : faults ) {
            for( int[] test : tests ) {

                // f.length may be greater than test.length (f corresponds to full model)
                // in this case, the test must be extended by default values
                int[] ts = new int[f.length];
                if ( f.length == test.length )
                    System.arraycopy(test, 0, ts, 0, test.length);
                else {
                    System.arraycopy(test, 0, ts, 0, test.length);
                    System.arraycopy(defaults, test.length, ts, test.length, defaults.length-test.length);
                }

                // determine whether f can be detected or not
                int s1 = 0, s2 = 0 ;
                for( int l=0 ; l<ts.length ; l++ ) {
                    if( f[l] != -1 ) {
                        s1++ ;
                        if( f[l] == ts[l] )
                            s2++ ;
                    }
                }
                if( s1 == s2 ) {
                    // detected
                    num++;
                    break;
                }
            }   // end each test
        }
        return num ;
    }


    /*
     *  PRIORITIZATION
     *  Determine whether a testing order is valid or not.
     */
    public boolean isValidTestingOrder(int[] od) {
        if (od == null)
            od = this.order;

        int[] bit = new int[od.length];
        int assigned = 0;
        for (int val : od ) {
            if (bit[val] == 0) {
                bit[val] = 1;
                assigned++;
            }
        }
        return (assigned == od.length);
    }

    /*
     *  PRIORITIZATION
     *  Set parameter weight
     */
    public void setWeight(double[] w) {
        if (w.length != weight.length) {
            System.err.println("error length of weight[]");
            return;
        }
        this.weight = w.clone();
    }

    /*
     *  PRIORITIZATION
     *  Set execution cost based on normal distribution ~ N(m, s).
     *  But if m = 0, then set execution cost = 0 for all.
     */
    public void setExecutionCost(double m, double s) {
        if (executionCost == null)
            executionCost = new double[tests.length];

        if (m == 0.0) {
            for (int k = 0; k < executionCost.length; k++)
                executionCost[k] = 0.0;
        } else {
            RandomTool rd = new RandomTool();
            for (int k = 0; k < executionCost.length; k++) {
                double c = rd.Gaussian(m, s);
                if (c < 0.0)
                    c = 0.0;
                executionCost[k] = c;
            }
        }
    }

    /*
     *  PRIORITIZATION
     *  Set all execution cost to a fixed value.
     */
    public void setExecutionCost(double ex) {
        if (executionCost == null)
            executionCost = new double[tests.length];

        for( int i=0 ; i<executionCost.length ; i++ )
            executionCost[i] = ex ;
    }

    /*
     *  PRIORITIZATION
     *  Measure the distance between two test cases, where i, j
     *  are indexes of default order (i.e. the row in tests).
     */
    public double distance(int i, int j) {
        if (i > tests.length || j > tests.length) {
            return -1;
        }
        double dis = 0;
        for (int k = 0; k < system.parameter; k++) {
            if (tests[i][k] != tests[j][k])
                dis += weight[k];
        }
        return dis;
    }

    /*
     *  PRIORITIZATION
     *  Get the total switching cost of testing order od[].
     *  No setting up cost.
     */
    public double getTotalSwitchingCost(int[] od) {
        if (od == null)
            od = this.order;

        double sum = 0;
        for (int i = 0; i < od.length - 1; i++) {
            sum += distance(od[i], od[i + 1]);
        }
        return sum;
    }

    /*
     *  PRIORITIZATION
     *  Get the average switching cost between any two test cases.
     */
    public double getAverageSwitchingCost() {
        double sum = 0;
        for (int i = 0; i < order.length; i++) {
            for (int j = i + 1; j < order.length; j++) {
                sum += distance(i, j);
            }
        }
        int all = order.length * (order.length - 1) / 2;
        return sum / (double) all;
    }

    /*
     *  PRIORITIZATION
     *  Get the switching cost between adjacent test cases.
     *  No setting up cost.
     */
    public double[] getAdjacentSwitchingCost(int[] od) {
        if (od == null)
            od = this.order;

        double[] ad = new double[od.length - 1];
        for (int i = 0; i < od.length - 1; i++)
            ad[i] = distance(od[i], od[i + 1]);
        return ad;
    }

    /*
     *  PRIORITIZATION
     *  Get the total testing cost.
     *  f = setting-up cost + total switching cost + total execution cost
     */
    public double getTotalTestingCost(int[] od) {
        if (od == null)
            od = this.order;

        double sum = 0;
        for (double i : weight )
            sum += i;

        sum += executionCost[od[0]];
        for (int i = 0; i < od.length - 1; i++) {
            sum += distance(od[i], od[i + 1]);
            sum += executionCost[od[i + 1]];
        }
        return sum;
    }

    /*
     *  PRIORITIZATION
     *  Get the average number of switches of each parameter
     */
    public double getAverageNumberOfSwitches(int[] od) {
        if (od == null)
            od = this.order;

        int sum = 0;
        int[] number = new int[system.parameter];
        for (int k = 0; k < system.parameter; k++)
            number[k] = 0;

        for (int i = 0; i < od.length - 1; i++) {
            // add the # of switches between od[i] and od[i+1]
            for (int k = 0; k < system.parameter; k++) {
                if (tests[od[i]][k] != tests[od[i + 1]][k]) {
                    number[k]++;
                    sum++;
                }
            }
        }
        return (double) sum / (double) system.parameter;
    }

    /*
     *  PRIORITIZATION
     *  F(t)-measure = the required time unit to detect a specified
     *  t-way failure schema with testing order od[]
     */
    public double getFt(int tway, final int[] schema, int[] od) {
        if (od == null)
            od = this.order;

        double time = 0;
        for (int i = 0; i < tests.length; i++) {
            int index = od[i];
            time += executionCost[index];  // run the test

            int flag = 0;                  // check
            for (int k = 0; k < system.parameter; k++) {
                if (schema[k] != -1 && schema[k] == tests[index][k])
                    flag++;
            }
            if (flag == tway) {
                return time;
            }

            if (i + 1 < tests.length)       // switch to the next test
                time += distance(index, od[i + 1]);
        }

        return Double.MAX_VALUE;          // fail to detect failure
    }

    /*
     *  PRIORITIZATION
     *  t-RFD value = the RATE of t-way combination coverage over test case
     */
    public double getRFD(int[] od, int t) {
        if (od == null)
            od = this.order;
        int len = tests.length;

        SUT s = new SUT(system.parameter, system.value, t);
        s.initialization();

        long cur = 0, pre = 0;
        for (int k = 0; k < len - 1; k++) {
            int cov = s.FitnessValue(tests[od[k]], 1);
            pre += cov;
            cur += pre;
        }
        long totalCover = pre + s.FitnessValue(tests[od[len - 1]], 1);

        return (double) cur / ((double) len * (double) totalCover);
    }

    /*
     *  PRIORITIZATION
     *  Get the number of newly covered t-way combinations
     *  till each test case
     */
    public long[] getCoverEach(int[] od, int t) {
        if (od == null)
            od = this.order;
        long[] cover = new long[tests.length];

        SUT sut = system.clone();
        sut.setCoveringStrength(t);
        sut.initialization();

        long pre = 0;
        for (int k = 0; k < tests.length; k++) {
            int cov = system.FitnessValue(tests[od[k]], 1);
            pre += cov;
            cover[k] = pre;
        }
        return cover;
    }

    /*
     *  PRIORITIZATION
     *  Relative t-RFDc value = the RATE of t-way combination
     *  coverage over time cost
     */
    public double getRFDc(int[] od, int t, double maxTime) {
        if (od == null)
            od = this.order;
        int len = tests.length;

        // the switching cost between test cases, len = tests.length - 1
        double[] cost = getAdjacentSwitchingCost(od);
        double totalTime = getTotalTestingCost(od);

        // the number of t-way covered combinations, len = tests.length
        long[] cover = getCoverEach(od, t);

        // numerator
        double numerator = 0;
        for (int i = 0; i < len - 1; i++) {
            double tp = (cost[i] + executionCost[i + 1]) * (double) cover[i];
            numerator += tp;
        }
        numerator += (maxTime - totalTime) * (double) cover[len - 1];

        // denominator
        double denominator = maxTime * (double) cover[len - 1];

        return numerator / denominator;
    }

    /*
     *  PRIORITIZATION : REAL FAULT
     *  F(t)'-measure = the required time unit to detect the first fault
     */
    public double getFtTrue(int[] od, int[] testing_results) {
        if (od == null)
            od = this.order;

        double time = 0;
        for( int k = 0 ; k < weight.length ; k++ )
            time += weight[k] ;

        for (int i = 0; i < tests.length; i++) {
            int index = od[i];
            time += executionCost[index];  // run the test
            // check whether detect a fault
            if (testing_results[index] == 0) {
                return time;
            }
            if (i + 1 < tests.length)      // switch to the next test
                time += distance(index, od[i + 1]);
        }
        return Double.MAX_VALUE;          // fail to detect failure
    }

    /*
     *  PRIORITIZATION : REAL FAULT
     *  Relative APFDc
     */
    public double getAPFDc(int[] od, int[] testing_results, double maxTime) {
        if (od == null)
            od = this.order;

        double APFDc = 0 ;

        // the number of faulty test cases (a set of m randomFaults revealed by T)
        int m = 0 ;
        for( int k = 0 ; k < testing_results.length ; k++ )
            if( testing_results[k] == 0 )
                m += 1 ;

        //System.out.println(">> solution = " + Arrays.toString(od));
        //System.out.println(">> testing results = " + Arrays.toString(testing_results));
        //System.out.println(">> m = " + m);

        // the cost array
        // e.g. od   = [2,             4,             5,            ...]
        //      cost = [ec(2)+setting, ec(4)+es(2,4), ec(5)+es(4,5) ...]
        double[] costArray = new double[od.length];
        double settingCost = 0 ;
        for( int k = 0 ; k < weight.length ; k++ )
            settingCost += weight[k] ;

        costArray[0] = executionCost[od[0]] + settingCost ;
        for( int k = 1 ; k < od.length ; k++ )
            costArray[k] = executionCost[od[k]] + distance(od[k-1], od[k]);

        //System.out.println(">> costArray = " + Arrays.toString(costArray));

        // for loop
        double sum = 0 ;
        for( int i = 0 ; i < od.length ; i++ ) {
            // now execute tests[index]
            int index = od[i] ;

            // if tests[index] fails
            if( testing_results[index] == 0 ) {
                double tpc = 0 ;
                for( int j = i ; j < od.length ; j++ )
                    tpc += costArray[j] ;

                //System.out.println(">> f-measure = " + (getTotalTestingCost(od)-tpc+costArray[i]));
                tpc = tpc - 0.5 * costArray[i] ;
                sum += tpc ;
               //System.out.println(">> sum += " + tpc);
            }
        }

        // normalize maxTime
        double totalCost = getTotalTestingCost(od);
        if( totalCost < maxTime ) {
            sum += m * (maxTime - totalCost);
            //System.out.println(">> sum final -> " + maxTime + " - " + totalCost);
        }

        // final
        APFDc = sum / (maxTime * m) ;
        //System.out.println(">> " + sum + " / (" + maxTime + " * " + m + ")\n");
        return APFDc ;
    }

}
