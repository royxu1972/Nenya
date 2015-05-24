package prioritization;

import Basic.Rand;
import Basic.TestSuite;
import Generation.AETG;
import Prioritization.ReorderArray;
import prioritization.Item.ORDER;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Random;

/**
 *  simulation (Random version) experiment
 *
 *  The goal of this simulation is to compare the efficacy of different testing orders
 *  under different testing scenarios. F(t)-measure (i.e. the time unit required to detect
 *  the first failure) is used as the criterion, so a smaller F(t)-value indicates a better
 *  test sequence.
 *
 *  To cover different testing scenarios, we control the following independent variables:
 *  p: number of parameters, [10, 60]
 *  v: number of values, [2, 8]
 *  t: covering strength, 2 or 3
 *  type: the distribution of parameter weights
 *      type = 1: w[i] = 1 for all i
 *      type = 2: w[i] = 10 for 10% parameters, w[i] = 1 for the remains
 *  r: ratio, execution cost / switching cost, [0.0, 2,0]
 *
 *  We firstly construct 2000 different SUTs (independent variables) randomly,
 *  and then evaluate which order is the best one for this SUT.
 *
 */
public class SimulationRandom {

    private static int par_lower = 10, par_upper = 60 ;
    private static int val_lower = 2, val_upper = 8 ;
    private static double ratio_lower = 0.0, ratio_upper = 2.0 ;

    private Rand rand ;
    private Random random ;
    private HashSet<Item> subjects ;

    public SimulationRandom() {
        this.rand = new Rand();
        this.random = new Random();
        this.subjects = new HashSet<>();
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
        // the first 10% parameters have weight 10, the others have weight 1
        int th = (int)((double)len * 0.1) ;
        for( int k=0 ; k<th ; k++ )
            w[k] = 10.0 ;
        for( int k=th ; k<len ; k++ )
            w[k] = 1.0 ;
        return w ;
    }

    /*
     *  initialize subjects
     */
    public void initSubjects( int num, ORDER o[] ) {
        do {
           // randomly
            int p = par_lower + random.nextInt(par_upper-par_lower+1);
            int v = val_lower + random.nextInt(val_upper-val_lower+1);
            int t = 3 ;
            int type = 1 + random.nextInt(2);
            double r = ratio_lower + (ratio_upper - ratio_lower) * random.nextDouble();
            Item item = new Item(o, p, v, t, type, r);
            subjects.add(item);
        } while( subjects.size() < num );
    }

    /*
     *  exp - 1
     */
    public void exp1( int num , String filename ) {
        // initialization
        ORDER[] order = new ORDER[]{
                ORDER.RANDOM, ORDER.COVERAGE, ORDER.LKH, ORDER.HYBRID
        };
        initSubjects(num, order);

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("resources//" + filename + ".txt"));
            bw.write(new Item().getColumnName() + "\n");
            bw.close();
        } catch (IOException e) {
            System.err.println(e);
        }

        // evaluate each SUT
        int n = 0 ;
        for( Item item : subjects ) {
            // evaluate
            System.out.println( "evaluating the " + n + "-th, CA(" + item.T + ", " + item.P + ", " + item.V + ")" );
            evaluate( item );
            n++;

            // write data to file
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter("resources//" + filename + ".txt", true));
                BufferedWriter log = new BufferedWriter(new FileWriter("resources//" + filename + ".log", true));
                bw.write(item.getRowData() + "\n");
                bw.close();
                log.write(item.getData() + "\n");
                log.close();
            } catch (IOException e) {
                System.err.println(e);
            }
        }
    }

    /*
     *  evaluate each SUT, repeat 30 times * 100 failure schemas
     */
    private void evaluate( Item item ) {
        int p = item.P ;
        int va = item.V ;
        int[] v = new int[p] ;
        for( int k=0 ; k<p ; k++ )
            v[k] = va ;
        int t = item.T ;

        double[] w ;
        if( item.Type == 1 )
            w = weightTypeOne(p);   // type - 1
        else if ( item.Type == 2 )
            w = weightTypeTwo(p);   // type - 2
        else {
            System.out.println("error type");
            return;
        }

        TestSuite ts = new TestSuite(p, v, t, w);
        AETG gen = new AETG();
        ReorderArray order = new ReorderArray();

        // final results to be saved
        double[] aveFinal = new double[item.orders.length] ;
        for (int x=0 ; x<item.orders.length ; x++)
            aveFinal[x] = 0.0 ;

        // repeat 30 times
        for( int rep = 0 ; rep < 30 ; rep++ ) {
            // 1. generate a covering array
            gen.Generation(ts);

            // set execution cost
            double exe = ts.getAverageSwitchingCost() * item.R;
            ts.setExecutionCost(exe, 0.5);

            // 2. generate 100 random t-way failure schemas
            HashSet<int[]> ss = new HashSet<>();
            do {
                int[] s = rand.Schema(t, p, v);
                ss.add(s);
            } while (ss.size() < 100);

            // 3. evaluate different orders
            double[] tpValue = new double[item.orders.length] ;
            int index = 0;
            for (ORDER od : item.orders) {
                // change order
                switch (od) {
                    case RANDOM:
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
                for (int[] s : ss) {
                    tpValue[index] += ts.getFt(t, s, null);
                }
                index++;
            }

            // 4. get average
            for (int k = 0 ; k < item.orders.length ; k++)
                aveFinal[k] += tpValue[k] / 100.0;
        }

        // save final results to item
        for (int x = 0 ; x < item.orders.length ; x++)
            item.data[x] = aveFinal[x] / 30.0 ;
        item.updateBestOrder();
    }


    /*
     *  print subjects to console
     */
    public void printSubjects() {
        for( Item a : subjects )
            System.out.println(a.name());
    }
}