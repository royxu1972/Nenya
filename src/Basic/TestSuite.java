package Basic;

/**
 * The basic data structure and evaluations for a test suite
 */
public class TestSuite {

    public SUT system ;
    public int[][] tests ;           // the test suite
    public int[] order ;             // the test sequence
    public double[] executionCost ;  // the execution cost of each test, default = 0
    private double[] weight ;        // the switching weight of each parameter. default = 1

    public TestSuite(int p, int[] v, int t, double[] w) {
        this.system = new SUT(p, v, t);
        this.tests = null ;
        this.order = null ;
        this.executionCost = null ;
        this.weight = w.clone() ;
    }
    public TestSuite(int p, int[] v, int t) {
        this.system = new SUT(p, v, t);
        this.tests = null ;
        this.weight = new double[p] ;
        this.executionCost = null ;
        for( int k=0 ; k<p ; k++ )
            this.weight[k] = 1.0;  // default weight = 1
    }

    public String getSystemName() {
        return "CA(" + system.tway + "," + system.parameter + "," + system.value[0] + ")" ;
    }

    public int getTestSuiteSize() {
        return this.tests.length ;
    }


    /*--------------------------------------------------------------------
     * Switching Cost Based and Combination Coverage Based Prioritization
     ---------------------------------------------------------------------*/

    /*
     *  set parameter weight
     */
    public void setWeight( double[] w ) {
        if( w.length != weight.length ) {
            System.err.println("error length of weight[]");
            return ;
        }
        this.weight = w.clone() ;
    }

    /*
     *  set executionCost based on normal distribution N(m, s)
     *  but if m = 0, then set execution cost = 0 for all
     */
    public void setExecutionCost( double m, double s ) {
        if( executionCost == null )
            executionCost = new double[tests.length];

        // assign values
        if( m == 0.0 ) {
            for( int k = 0; k < executionCost.length; k++ )
                executionCost[k] = 0.0 ;
        }
        else {
            Rand rd = new Rand();
            for( int k = 0; k < executionCost.length; k++ ) {
                double c = rd.Gaussian(m, s);
                if (c < 0.0)
                    c = 0.0;
                executionCost[k] = c;
            }
        }
    }

    /*
     *  measure the distance between two test cases, where i, j are indexes of default order
     */
    public double distance( int i, int j ) {
        if( i > tests.length || j > tests.length ) {
            return -1 ;
        }
        double dis = 0 ;
        for( int k=0 ; k<system.parameter ; k++ ) {
            if( tests[i][k] != tests[j][k] )
                dis += weight[k] ;
        }
        return dis ;
    }

    /*
     *  get current testing order
     */
    public int[] getOrderInt() {
        return order.clone() ;
    }
    public String getOrderString() {
        String str = "" ;
        for( int i=0 ; i<order.length ; i++ )
            str += String.valueOf(order[i]) + " " ;
        return str ;
    }

    /*
     *  testing whether order is valid
     */
    public boolean isValidOrder( int[] od ) {
        if( od == null )
            od = this.order ;

        int[] bit = new int[od.length];
        int assigned = 0 ;
        for( int k = 0 ; k < od.length ; k++ ) {
            if( bit[od[k]] == 0 ) {
                bit[od[k]] = 1 ;
                assigned++ ;
            }
        }
        return (assigned == od.length);
    }

    /*
     *  get the total switching cost of the test suite with the order od[]
     *  no setting up cost
     */
    public double getTotalSwitchingCost( int[] od ) {
        if( od == null )
            od = this.order ;

        double sum = 0 ;
        for( int i=0 ; i<od.length-1 ; i++ ) {
            sum += distance(od[i], od[i+1]) ;
        }
        return sum ;
    }

    /*
     *  get the switching cost between adjacent test cases
     *  no setting up cost, so len(ad) = tests.length - 1
     */
    public double[] getAdjacentSwitchingCost( int[] od ) {
        if( od == null )
            od = this.order ;

        double[] ad = new double[od.length-1];
        for (int i=0 ; i<od.length-1 ; i++)
            ad[i] = distance(od[i], od[i+1]);
        return ad ;
    }

    /*
     *  get the total testing cost
     *  i.e. setting-up cost + switching cost + execution cost
     */
    public double getTotalCost( int[] od ) {
        if( od == null )
            od = this.order ;

        double sum = 0 ;
        for( int i=0 ; i<weight.length ; i++ )
            sum += weight[i] ;

        sum += executionCost[od[0]] ;
        for( int i=0 ; i<od.length-1 ; i++ ) {
            sum += distance(od[i], od[i+1]) ;
            sum += executionCost[od[i+1]] ;
        }
        return sum ;
    }

    /*
     *  get the average switching cost between any two test cases
     */
    public double getAverageSwitchingCost() {
        double sum = 0 ;
        for( int i=0 ; i<order.length ; i++ ) {
            for( int j=i+1 ; j<order.length ; j++ ) {
                sum += distance(i,j);
            }
        }
        int all = order.length * (order.length - 1) / 2 ;
        return sum/(double)all ;
    }

    /*
     *  get the average number of switches of each parameter
     */
    public double getAverageSwitches( int[] od ) {
        if( od == null )
            od = this.order ;

        int sum = 0 ;
        int[] number = new int[system.parameter] ;
        for( int k = 0 ; k < system.parameter ; k++ )
            number[k] = 0 ;

        for( int i = 0 ; i < od.length-1 ; i++ ) {
            // add the # of switches between od[i] and od[i+1]
            for( int k = 0 ; k < system.parameter ; k++ ) {
                if( tests[od[i]][k] != tests[od[i+1]][k] ) {
                    number[k]++;
                    sum++;
                }
            }
        }
        return (double)sum / (double)system.parameter ;
    }

    /*
     *  get F(t)-measure:
     *  the required time unit to detect specified t-way failure schema with order od[]
     */
    public double getFt(int tway, final int[] schema, int[] od) {
        if( od == null )
            od = this.order ;

        double time = 0 ;
        for( int i=0 ; i<tests.length ; i++ ) {
            int index = od[i] ;
            time += executionCost[index] ;  // run the test

            int flag = 0 ;                  // check
            for( int k=0 ; k<system.parameter ; k++ ) {
                if( schema[k] != -1 && schema[k] == tests[index][k] )
                    flag++ ;
            }
            if( flag == tway ) {
                return time;
            }

            if( i+1 < tests.length )       // switch to the next test
                time += distance(index, od[i+1]) ;
        }

        return Double.MAX_VALUE ;          // fail to detect failure
    }

    /*
     *  get the t-RFD value:
     *  the rate of t-way combination coverage based on test cases
     */
    public double getRFD( int[] od, int t ) {
        if( od == null )
            od = this.order ;
        int len = tests.length ;

        SUT s = new SUT(system.parameter, system.value, t);
        s.GenerateS();

        long cur = 0, pre = 0 ;
        for( int k=0 ; k<len-1 ; k++ ) {
            int cov = s.FitnessValue(tests[od[k]], 1);
            pre += cov ;
            cur += pre ;
        }
        long totalCover = pre + s.FitnessValue(tests[od[len-1]], 1);

        return (double)cur / ((double)len * (double)totalCover);
    }

    /*
     *  get the number of covered t-way combinations till each test case
     */
    public long[] getCoverEach( int[] od, int t ) {
        if( od == null )
            od =this.order ;
        long[] cover = new long[tests.length];

        SUT s = new SUT(system.parameter, system.value, t);
        s.GenerateS();

        long pre = 0;
        for( int k=0 ; k<tests.length ; k++ ) {
            int cov = s.FitnessValue(tests[od[k]], 1);
            pre += cov;
            cover[k] = pre;
        }
        return cover ;
    }

    /*
     *  compute the relative t-RFDc value:
     *  the rate of t-way combination coverage based on time cost
     */
    public double getRFDc(int[] od, int t, double maxTime) {
        if( od == null )
            od = this.order ;
        int len = tests.length ;

        SUT s = new SUT(system.parameter, system.value, t);
        s.GenerateS();

        // the switching cost between test cases, len = tests.length - 1
        double[] cost = getAdjacentSwitchingCost(od);
        double totalTime = getTotalCost(od);

        // the number of t-way covered combinations, len = tests.length
        long[] cover = getCoverEach(od, t);

        // numerator
        double numerator = 0 ;
        for( int i=0 ; i<len-1 ; i++ ) {
            double tp = ( cost[i] + executionCost[i+1] ) * (double)cover[i] ;
            numerator += tp ;
        }
        numerator += ( maxTime - totalTime ) * (double)cover[len-1] ;

        // denominator
        double denominator = maxTime * (double)cover[len-1] ;

        return numerator / denominator ;
    }


}
