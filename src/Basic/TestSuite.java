package Basic;

import java.util.ArrayList;

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
     *  get the total switching cost of the test suite with order[]
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
     *  get the total testing cost, i.e. switching cost + execution cost
     */
    public double getTotalCost( int[] od ) {
        if( od == null )
            od = this.order ;

        double sum = executionCost[od[0]] ;
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
     *  get the t-RFD value: the rate of t-way combination coverage based on test cases
     */
    public long getRFD( int[] od, int t ) {
        if( od == null )
            od = this.order ;

        long rfd = 0;
        long pre = 0;

        SUT s = new SUT(system.parameter, system.value, t);
        s.GenerateS();

        for( int k=0 ; k<tests.length ; k++ ) {
            int cov = s.FitnessValue(tests[od[k]], 1);
            pre += cov;
            rfd += pre;
        }
        return rfd;
    }

    public long getRFD( int[] od ) {
        return getRFD(od, 2) ;
    }

    /*
     *  get the number of covered 2-way combinations till each test case
     */
    public long[] getRFDeach( int[] od ) {
        if( od == null )
            od =this.order ;
        long[] rfds = new long[tests.length];
        long pre = 0;
        system.GenerateS();
        for( int k=0 ; k<tests.length ; k++ ) {
            int cov = system.FitnessValue(tests[od[k]], 1);
            pre += cov;
            rfds[k] = pre;
        }
        return rfds ;
    }

    /*
     *  get F(t)-measure: the required time unit to detect specified t-way failure schema with order od[]
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
     *  testing ...
     *
     *  data[0]: get F(t)-measures: the required time unit to detect the first failure schema
     *  data[1]: get NAPFD(c): the rate of t-way failure detection over testing time
     *                  CF1 + CF2 + ... + CFm    p
     *  NAPFD(c) = p - ---------------------- + --
     *                         m * n            2n
     *  where m = number of faults
     *        n = number of time units
     *        CF(i) = the time required to detect fault i
     *        p = number of detected faults / number of all faults
     */
    public void getNAPFDC(double[] data, int tway, final ArrayList<int[]> schemas, int[] od) {
        double[] CF = new double[schemas.size()];
        double CF_sum = 0.0 ;
        int index = 0 ;
        for( int[] each : schemas ) {
            CF[index] = this.getFt(tway, each, od);
            CF_sum += CF[index];
            index++;
        }

        System.out.print("CF: ");
        for( int k=0 ; k<CF.length ; k++ )
            System.out.print(CF[k] + " ");
        System.out.print("\n");
        System.out.println("total cost: " + this.getTotalCost(od));

        double min = Double.MAX_VALUE ;
        for( int k=0 ; k<CF.length ; k++ )
            if( CF[k] < min )
                min = CF[k] ;

        double max = 0 ;
        for( int k=0 ; k<CF.length ; k++ )
            if( CF[k] > max )
                max = CF[k] ;

        data[0] = min ;
        data[1] = 1 - CF_sum / (schemas.size() * this.getTotalCost(od)) + 1 / (2 * this.getTotalCost(od)) ;
        data[2] = max ;
    }


}
