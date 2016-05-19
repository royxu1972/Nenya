package Basic;

/**
 *  The basic data structure and evaluations for a test suite
 */
public class TestSuite {

    public SUT system;
    public int[][] tests;           // the test suite
    public int[] order;             // the test sequence
    public double[] executionCost;  // the execution cost of each test, default = null
    public double[] weight;         // the switching weight of each parameter, default = 1.0

    public TestSuite(int p, int[] v, int t) {
        this.system = new SUT(p, v, t);
        this.tests = null;
        this.weight = new double[p];
        this.executionCost = null;
        for (int k = 0; k < p; k++)
            this.weight[k] = 1.0;
    }

    public int[] getOrderInt() {
        return order.clone();
    }

    public int getTestSuiteSize() {
        return this.tests.length;
    }

    /*
     *  Compute k-way combination coverage of a given test suite.
     *  The input tests[][] must be a test suite of this.system.
     */
    public double getCombinationCoverage( int strength ) {
        return getCombinationCoverage(strength, this.tests);
    }
    public double getCombinationCoverage( int strength, final int[][] tests ) {

        if( tests[0].length != system.parameter )
            return -1 ;

        int total = 0 ;
        int total_uncovered = 0 ;

        // for each parameter combination
        int[][] pComb = ALG.cal_allC(system.parameter, strength);
        for( int[] pos : pComb ) {

            int uncovered = ALG.cal_combineValue(pos, system.value);
            total = total + uncovered ;

            // the number of covered value combinations
            int[] cover = new int[uncovered];
            for( int i=0 ; i<uncovered ; i++ )
                cover[i] = 0 ;

            // for each row in tests
            int[] sch = new int[strength];
            for( int[] row : tests ) {
                for( int k=0 ; k<strength ; k++ )
                    sch[k] = row[pos[k]];
                int index = ALG.cal_val2num(pos, sch, strength, system.value);

                if( cover[index] == 0 ) {
                    cover[index] = 1 ;
                    uncovered -= 1 ;
                }
            }
            total_uncovered = total_uncovered + uncovered ;
        }
        return (double)(total - total_uncovered) / (double)total ;
    }

    /*
     *  determine whether solution is valid
     */
    public boolean isValidTestingOrder(int[] od) {
        if (od == null)
            od = this.order;

        int[] bit = new int[od.length];
        int assigned = 0;
        for (int k = 0; k < od.length; k++) {
            if (bit[od[k]] == 0) {
                bit[od[k]] = 1;
                assigned++;
            }
        }
        return (assigned == od.length);
    }

    /*
     *  set hard constraint
     */
    public void setConstraint( final int[][] constraint ) {
        system.setConstraint(constraint);
    }

    /*
     *  set parameter weight
     */
    public void setWeight(double[] w) {
        if (w.length != weight.length) {
            System.err.println("error length of weight[]");
            return;
        }
        this.weight = w.clone();
    }

    /*
     *  set executionCost based on normal distribution N(m, s)
     *  but if m = 0, then set execution cost = 0 for all
     */
    public void setExecutionCost(double m, double s) {
        if (executionCost == null)
            executionCost = new double[tests.length];

        // assign values
        if (m == 0.0) {
            for (int k = 0; k < executionCost.length; k++)
                executionCost[k] = 0.0;
        } else {
            Rand rd = new Rand();
            for (int k = 0; k < executionCost.length; k++) {
                double c = rd.Gaussian(m, s);
                if (c < 0.0)
                    c = 0.0;
                executionCost[k] = c;
            }
        }
    }

    public void setExecutionCost(double ex) {
        if (executionCost == null)
            executionCost = new double[tests.length];

        for( int i=0 ; i<executionCost.length ; i++ )
            executionCost[i] = ex ;
    }

    /*
     *  measure the distance between two test cases,
     *  where i, j are indexes of default solution
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
     *  get the total switching cost of the test suite with the solution od[]
     *  no setting up cost
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
     *  get the average switching cost between any two test cases
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
     *  get the switching cost between adjacent test cases
     *  no setting up cost, so len(ad) = tests.length - 1
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
     *  get the total testing cost
     *  i.e. setting-up cost + total switching cost + total execution cost
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
     *  get the average number of switches of each parameter
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
     *  get F(t)-measure:
     *  the required time unit to detect specified t-way failure schema with solution od[]
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
     *  get t-RFD value:
     *  the rate of t-way combination coverage over test case
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
     *  get the number of covered t-way combinations till each test case
     */
    public long[] getCoverEach(int[] od, int t) {
        if (od == null)
            od = this.order;
        long[] cover = new long[tests.length];

        SUT s = new SUT(system.parameter, system.value, t);
        s.initialization();

        long pre = 0;
        for (int k = 0; k < tests.length; k++) {
            int cov = s.FitnessValue(tests[od[k]], 1);
            pre += cov;
            cover[k] = pre;
        }
        return cover;
    }

    /*
     *  compute the relative t-RFDc value:
     *  the rate of t-way combination coverage over time cost
     */
    public double getRFDc(int[] od, int t, double maxTime) {
        if (od == null)
            od = this.order;
        int len = tests.length;

        SUT s = new SUT(system.parameter, system.value, t);
        s.initialization();

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

    // ------------------------------------------------------
    // The followings are used when we have realistic faults
    // ------------------------------------------------------

    /*
     *  get F(t)'-measure:
     *  the required time unit to detect the first real faults
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

            if (i + 1 < tests.length)       // switch to the next test
                time += distance(index, od[i + 1]);
        }

        return Double.MAX_VALUE;          // fail to detect failure
    }

    /*
     *  get relative APFDc
     */
    public double getAPFDc(int[] od, int[] testing_results, double maxTime) {
        if (od == null)
            od = this.order;

        double APFDc = 0 ;

        // the number of faulty test cases (a set of m faults revealed by T)
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
