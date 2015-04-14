package prioritization;

import Basic.Rand;
import Basic.TestSuite;
import Generation.AETG;
import Prioritization.ReorderArray;

import java.util.HashSet;
import java.util.Set;

/**
 *  simulation experiment
 *
 *  The goal of this simulation is to compare the efficacy of different testing orders
 *  under different testing scenarios. F(t)-measure (i.e. the time unit required to detect
 *  the first failure) is used as the criterion, so a smaller F(t)-value indicates a better
 *  test sequence.
 *
 *  To cover different testing scenarios, we control the following variables:
 *  orders: random, coverage based, switching cost based
 *  weight: the distribution of parameter weights
 *      type 1: w[i] = 1 for all i
 *      type 2: w[i] = 10 for 10% parameters, w[i] = 1 for the remaining
 *  t: covering strength, from 2 to 3
 *  p: number of parameters, 10, 20, 30, 40, 60, 80, 100
 *  v: number of values, 2, 3, 4, 6, 8
 *
 *  For each SUT, we firstly generate a covering array, and then get its different testing
 *  orders. Then, 100 random t-way failure causing schemas are generated, and so we can get
 *  100 F(t)-values for each testing order. As the randomness should be addressed,
 *  the above process will be repeated 30 times. Finally, the average number of F(t)-value
 *  from 100 * 30 samples is used as the final result.
 *
 */
public class Simulation {

    // ratio = execution cost / switching cost
    private static double[] ratio = {0.0, 0.2, 0.4, 0.6, 0.8, 1.0, 1.2, 1.4, 1.6, 1.8, 2.0} ;

    private static int[] par = {4,6,10};//{10, 20, 30, 40, 60, 80, 100} ;
    private static int[] val = {2,3,4};//{2, 3, 4, 6, 8} ;
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
        re.init("exp1, t=2, w[i]=1", new String[]{"coverage", "random", "cost"}, par, val, ratio) ;

        for( int i=0 ; i<par.length ; i++ ) {
            for( int j=0 ; j<val.length ; j++ ) {
                exp1Each(i, j, re);
            }
        }
    }

    // each value of exp 1, repeat 30 times
    // par[p_index]: the number of parameter
    // val[v_index]: the number of each parameter value
    private void exp1Each( int p_index, int v_index, Collection re ) {
        int p = par[p_index] ;
        int va = val[v_index] ;
        int t = 2 ;
        System.out.println( "processing CA(" + t + ", " + p + ", " + va + ")" );

        int[] v = new int[p] ;
        for( int k=0 ; k<p ; k++ )
            v[k] = va ;
        double[] w = weightTypeOne(p);

        TestSuite ts = new TestSuite(p, v, t, w);
        AETG gen = new AETG();
        ReorderArray order = new ReorderArray();

        double[] aveCoverage = new double[ratio.length] ;
        double[] aveRand = new double[ratio.length] ;
        double[] aveCost = new double[ratio.length] ;
        for( int k=0 ; k<ratio.length ; k++ )
            aveCoverage[k] = aveRand[k] = aveCost[k] = 0.0 ;

        // repeat 30 times
        for( int rep = 0 ; rep < 30 ; rep++ ) {
            // 1. generate a covering array
            gen.Generation(ts);

            // 2. generate 100 random t-way failure schemas
            Set<int[]> ss = new HashSet<int[]>() ;
            do {
                int[] s = rand.Schema(t, p, v);
                ss.add(s);
            } while( ss.size() < 100 );

            // 3. evaluate different orders under different ratios
            for( int rt = 0 ; rt < ratio.length ; rt++ ) {
                double tpCoverage = 0.0 ;
                double tpRand = 0.0 ;
                double tpCost = 0.0 ;

                // set execution cost
                double exe = ts.getAverageSwitchingCost() * ratio[rt] ;
                ts.setExecutionCost( exe, 0.5 );

                // coverage order, default
                order.toDefaultOrder(ts);
                for( int[] s : ss ) {
                    tpCoverage += ts.getFt(2, s, null);
                }

                // random order
                order.toRandomOrder(ts);
                for( int[] s : ss ) {
                    tpRand += ts.getFt(2, s, null);
                }

                // switching cost order
                order.toLKHSwitchOrder(ts);
                for( int[] s : ss ) {
                    tpCost += ts.getFt(2, s, null);
                }

                aveCoverage[rt] += tpCoverage / 100.0 ;
                aveRand[rt] += tpRand / 100.0 ;
                aveCost[rt] += tpCost / 100.0 ;
            }
        }

        // save mean results
        for( int k=0 ; k<ratio.length ; k++ ) {
            re.dataValue[p_index][v_index][0][k] = aveCoverage[k] / 30.0 ;
            re.dataValue[p_index][v_index][1][k] = aveRand[k] / 30.0 ;
            re.dataValue[p_index][v_index][2][k] = aveCost[k] / 30.0 ;
        }

    }

}
