package prioritization;

import Basic.Rand;
import Basic.TestSuite;
import Generation.AETG;
import Prioritization.ReorderArray;

import java.util.HashSet;

/**
 *  simulation experiment
 *
 *  The goal of this simulation is to compare the efficacy of different testing orders
 *  under different testing scenarios. F(t)-measure (i.e. the time unit required to detect
 *  the first failure) is used as the criterion, so a smaller F(t)-value indicates a better
 *  test sequence.
 *
 *  To cover different testing scenarios, we control the following variables:
 *  orders: random, coverage based, switching cost based (greedy and lkh)
 *  weight: the distribution of parameter weights
 *      type 1: w[i] = 1 for all i
 *      type 2: w[i] = 10 for 10% parameters, w[i] = 1 for the remaining
 *  t: covering strength, [2, 3]
 *  p: number of parameters [10, 20, 30, 40, 60, 80, 100]
 *  v: number of values [2, 3, 4, 6, 8]
 *
 *  For each SUT, 1) we generate a covering array, and then get its different testing
 *  orders. 2) 100 random t-way failure causing schemas are generated, and so we can get
 *  100 F(t)-values for each testing order. 3) As the randomness should be addressed,
 *  the above process will be repeated 30 times. 4) Finally, the average value of F(t)-value
 *  from 100 * 30 samples is used as the final result.
 *
 */
public class Simulation {

    // ratio = execution cost / switching cost
    private static double[] ratio = {0.0, 0.2, 0.4, 0.6, 0.8, 1.0, 1.2, 1.4, 1.6, 1.8, 2.0} ;

    private static int[] par = {10, 20, 30, 40, 60};//, 80, 100} ;
    private static int[] val = {2,3,4,6};//{2, 3, 4, 6, 8} ;
    private static int[] tway = {2,3} ;

    private Rand rand ;

    public Simulation() {
        this.rand = new Rand();
    }

    /*
     *  return weight array, the number of parameter is len
     */
    private double[] weightTypeOne( int len ) {
        double[] w = new double[len];
        for( int k=0 ; k<len ; k++ )
            w[k] = 1.0 ;
        return w ;
    }
    private double[] weightTypeTwo( int len ) {
        double[] w = new double[len];

        // the first 10% parameters has weight 10
        int th = (int)((double)len * 0.1) ;
        for( int k=0 ; k<th ; k++ )
            w[k] = 10.0 ;
        for( int k=th ; k<len ; k++ )
            w[k] = 1.0 ;
        return w ;
    }

    /*
     *  exp - 1
     *  t = 2, weight = normal (type-1)
     */
    public void exp1( Collection re ) {
        String name = "exp-1 test hybrid order";
        Collection.ORDERS[] orders = new Collection.ORDERS[]{
                Collection.ORDERS.RANDOM,
                Collection.ORDERS.COVERAGE,
                Collection.ORDERS.GREEDY,
                Collection.ORDERS.LKH,
                Collection.ORDERS.HYBRID
        };
        re.init(name, orders, par, val, ratio) ;

        // run each SUT
        for( int i=0 ; i<par.length ; i++ ) {
            for( int j=0 ; j<val.length ; j++ ) {
                expEach(2, 1, i, j, re);
            }
        }
    }

    /*
     *  each SUT for exp, repeat 30 times
     *  five orders: random, coverage, greedy, lkh, hybrid
     *
     *  INPUT REQUIRED
     *  t: coverage strength
     *  type: distribution of parameter weight
     *  par[p_index]: the number of parameter
     *  val[v_index]: the number of each parameter value
     *  re: data storage
     */
    private void expEach( int t, int type, int p_index, int v_index, Collection re ) {
        int p = par[p_index] ;
        int va = val[v_index] ;
        System.out.println( "processing CA(" + t + ", " + p + ", " + va + ")" );

        int[] v = new int[p] ;
        for( int k=0 ; k<p ; k++ )
            v[k] = va ;

        double[] w = weightTypeOne(p); // type - 1
        //if( type == 2 )
        //    w = weightTypeTwo(p);   // type - 2

        TestSuite ts = new TestSuite(p, v, t, w);
        AETG gen = new AETG();
        ReorderArray order = new ReorderArray();

        // final results to be saved |labels| * |ratios|
        double[][] aveFinal = new double[re.dataLabel.length][ratio.length] ;
        for (int x=0 ; x<re.dataLabel.length ; x++)
            for (int y=0 ; y<ratio.length ; y++)
                aveFinal[x][y] = 0.0 ;

        // repeat 30 times
        for( int rep = 0 ; rep < 30 ; rep++ ) {
            // 1. generate a covering array
            gen.Generation(ts);

            // 2. generate 100 random t-way failure schemas
            HashSet<int[]> ss = new HashSet<>() ;
            do {
                int[] s = rand.Schema(t, p, v);
                ss.add(s);
            } while( ss.size() < 100 );

            // 3. evaluate different orders under different ratios
            for( int rt = 0 ; rt < ratio.length ; rt++ ) {
                double[] tpValue = new double[re.dataLabel.length];

                // set execution cost
                double exe = ts.getAverageSwitchingCost() * ratio[rt] ;
                ts.setExecutionCost(exe, 0.5);

                // orders
                int index = 0 ;
                for ( Collection.ORDERS or : re.dataLabel ) {
                    // change order
                    switch ( or ) {
                        case RANDOM :
                            order.toRandomOrder(ts);
                            break;
                        case COVERAGE:
                            order.toDefaultOrder(ts);
                            break;
                        case GREEDY:
                            order.toGreedySwitchOrder(ts);
                            break;
                        case LKH:
                            order.toLKHSwitchOrder(ts);
                            break;
                        case HYBRID:
                            order.toGreedyHybridOrder(ts);
                            break;
                    }
                    // calculate ft-value
                    for( int[] s : ss ) {
                        tpValue[index] += ts.getFt(t, s, null);
                    }
                    index++ ;
                }

                // get average
                for (int k=0; k<re.dataLabel.length; k++)
                    aveFinal[k][rt] += tpValue[k] / 100.0 ;
            }
        }

        // save final average results
        for (int x=0 ; x<re.dataLabel.length ; x++)
            for (int y=0 ; y<ratio.length ; y++)
                re.dataValue[p_index][v_index][x][y] = aveFinal[x][y] / 30.0 ;

    }

}
