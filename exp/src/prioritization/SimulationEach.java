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

/**
 *  examining the impact of each factor
 */
public class SimulationEach {

    // base SUT
    private static int p = 30;
    private static int v = 4;
    private static int t = 2;
    private static int type = 1;
    private static double r = 0.5;

    private int[] allP = {10, 20, 30, 40, 50, 60};
    private int[] allV = {2, 3, 4, 5, 6, 7, 8};
    private int[] allT = {2, 3};
    private int[] allType = {1, 2};
    private double[] allR = {
            0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0,
            1.1, 1.2, 1.3, 1.4, 1.5, 1.6, 1.7, 1.8, 1.9, 2.0
    };

    private Rand rand ;

    public SimulationEach() {
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
        // the first 10% parameters have weight 10, the others have weight 1
        int th = (int)((double)len * 0.1) ;
        for( int k=0 ; k<th ; k++ )
            w[k] = 10.0 ;
        for( int k=th ; k<len ; k++ )
            w[k] = 1.0 ;
        return w ;
    }

    public void exp( String filename ) {
        ORDER[] order = new ORDER[]{
                ORDER.RANDOM, ORDER.COVERAGE, ORDER.LKH, ORDER.HYBRID
        };

        //
        // each parameter
        //
        String sticks = "parameter: ";
        String[] data = new String[order.length];
        for( int i=0 ; i<order.length ; i++ )
            data[i] = order[i].toString() + ": " ;

        for( Integer pp : allP ) {
            Item it = new Item(order, pp, v, t, type, r, t);
            System.out.println( "evaluating CA(" + it.T + ", " + it.P + ", " + it.V + ")" );
            evaluate(it);

            sticks += pp + " " ;
            for( int i=0 ; i<order.length ; i++ )
                data[i] += String.valueOf(it.data[i]) + " " ;
        }

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("resources//" + filename + ".txt", true));
            bw.write(sticks + "\n");
            for( int i=0 ; i<order.length ; i++ )
                bw.write(data[i] + "\n");
            bw.write("\n");
            bw.close();
        } catch (IOException e) {
            System.err.println(e);
        }

        //
        // each value
        //
        sticks = "value: ";
        data = new String[order.length];
        for( int i=0 ; i<order.length ; i++ )
            data[i] = order[i].toString() + ": " ;

        for( Integer vv : allV ) {
            Item it = new Item(order, p, vv, t, type, r, t);
            System.out.println( "evaluating CA(" + it.T + ", " + it.P + ", " + it.V + ")" );
            evaluate(it);

            sticks += vv + " " ;
            for( int i=0 ; i<order.length ; i++ )
                data[i] += String.valueOf(it.data[i]) + " " ;
        }

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("resources//" + filename + ".txt", true));
            bw.write(sticks + "\n");
            for( int i=0 ; i<order.length ; i++ )
                bw.write(data[i] + "\n");
            bw.write("\n");
            bw.close();
        } catch (IOException e) {
            System.err.println(e);
        }

        //
        // each tway
        //
        sticks = "tway: ";
        data = new String[order.length];
        for( int i=0 ; i<order.length ; i++ )
            data[i] = order[i].toString() + ": " ;

        for( Integer tt : allT ) {
            Item it = new Item(order, p, v, tt, type, r, t);
            System.out.println( "evaluating CA(" + it.T + ", " + it.P + ", " + it.V + ")" );
            evaluate(it);

            sticks += tt + " " ;
            for( int i=0 ; i<order.length ; i++ )
                data[i] += String.valueOf(it.data[i]) + " " ;
        }

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("resources//" + filename + ".txt", true));
            bw.write(sticks + "\n");
            for( int i=0 ; i<order.length ; i++ )
                bw.write(data[i] + "\n");
            bw.write("\n");
            bw.close();
        } catch (IOException e) {
            System.err.println(e);
        }

        //
        // each type
        //
        sticks = "type: ";
        data = new String[order.length];
        for( int i=0 ; i<order.length ; i++ )
            data[i] = order[i].toString() + ": " ;

        for( Integer yy : allType ) {
            Item it = new Item(order, p, v, t, yy, r, t);
            System.out.println( "evaluating CA(" + it.T + ", " + it.P + ", " + it.V + ")" );
            evaluate(it);

            sticks += yy + " " ;
            for( int i=0 ; i<order.length ; i++ )
                data[i] += String.valueOf(it.data[i]) + " " ;
        }

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("resources//" + filename + ".txt", true));
            bw.write(sticks + "\n");
            for( int i=0 ; i<order.length ; i++ )
                bw.write(data[i] + "\n");
            bw.write("\n");
            bw.close();
        } catch (IOException e) {
            System.err.println(e);
        }

        //
        // each ratio
        //
        sticks = "ratio: ";
        data = new String[order.length];
        for( int i=0 ; i<order.length ; i++ )
            data[i] = order[i].toString() + ": " ;

        for( Double rr : allR ) {
            Item it = new Item(order, p, v, t, type, rr, t);
            System.out.println( "evaluating CA(" + it.T + ", " + it.P + ", " + it.V + ")" );
            evaluate(it);

            sticks += rr + " " ;
            for( int i=0 ; i<order.length ; i++ )
                data[i] += String.valueOf(it.data[i]) + " " ;
        }

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("resources//" + filename + ".txt", true));
            bw.write(sticks + "\n");
            for( int i=0 ; i<order.length ; i++ )
                bw.write(data[i] + "\n");
            bw.write("\n");
            bw.close();
        } catch (IOException e) {
            System.err.println(e);
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
        int tau = item.Tau ;

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
                int[] s = rand.Schema(tau, p, v);
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
                    tpValue[index] += ts.getFt(tau, s, null);
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
}
