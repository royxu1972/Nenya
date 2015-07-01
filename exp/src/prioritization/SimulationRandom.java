package prioritization;

import Basic.Rand;
import Basic.TestSuite;
import Generation.AETG;
import Prioritization.MPopulation;
import Prioritization.ReorderArray;
import Prioritization.Sequence;
import prioritization.Item.ORDER;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

/**
 *  Main Simulation Experiment
 *
 *  The goal of this simulation is to compare the efficacy of different testing orders
 *  under different testing scenarios. F(t)-measure (i.e. the time unit required to detect
 *  the first failure) is used as the criterion, so a smaller F(t)-value indicates a better
 *  test sequence.
 *
 *  To cover different testing scenarios, we control the following independent variables:
 *  p:    number of parameters, [10, 60]
 *  v:    number of values, [2, 8]
 *  t:    covering strength, 2
 *  tau:  strength of failure schemas, [2, 4]
 *  type: the distribution of parameter weights
 *        type = 1: w[i] = 1 for all i
 *        type = 2: w[i] = 10 for 10% parameters, w[i] = 1 for the remains
 *  r:    ratio, execution cost / switching cost, [0.0, 2,0]
 *
 *  We firstly construct 1000 different SUTs (based on independent variables) randomly,
 *  and then evaluate which order is the best one for this SUT. Class Item is used to
 *  save the data.
 */
public class SimulationRandom {

    private static int par_lower = 10, par_upper = 30 ;
    private static int val_lower = 2, val_upper = 4 ;
    private static double ratio_lower = 0.0, ratio_upper = 2.0 ;
    private static int strength_lower = 2, strength_upper = 4 ;

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
            int p = par_lower + random.nextInt(par_upper-par_lower+1);
            int v = val_lower + random.nextInt(val_upper-val_lower+1);
            int type = 1 + random.nextInt(2);
            double r = ratio_lower + (ratio_upper - ratio_lower) * random.nextDouble();
            int t = strength_lower + random.nextInt(strength_upper - strength_lower);
            int tau = strength_lower + random.nextInt(strength_upper-strength_lower+1);

            p = 10 ;
            v = 2 ;
            t = 2 ;
            tau = 2 ;
            type = 1 ;

            Item item = new Item(o, p, v, t, tau, type, r);
            subjects.add(item);
        } while( subjects.size() < num );

    }

    /*
     *  exp - 1
     */
    public void exp1( int num, String filename ) {
        // initialization
        ORDER[] order = new ORDER[]{
                ORDER.RANDOM,
                ORDER.COVERAGE2,
                ORDER.LKH,
                ORDER.HYBRID2,
                ORDER.MO
        };
        initSubjects(num, order);

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("resources//" + filename + ".txt"));
            bw.write( Item.getColumnName() + "\n" );
            bw.close();
        } catch (IOException e) {
            System.err.println(e);
        }

        // evaluate each SUT
        int n = 0 ;
        for( Item item : subjects ) {
            // evaluate
            System.out.print("evaluating the " + n + "-th, CA(" + item.T + ", " + item.P + ", " + item.V + ")" +
                    ", Tau = " + item.Tau + " ");
            evaluateNew(item);
            n++;
            System.out.print("\n");

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
     *  evaluate each SUT, repeat 30 times * 100 tau-way failure schemas
     *  evaluate based on reference pareto front
     */
    private void evaluateNew( Item item ) {
        int p = item.P ;
        int va = item.V ;
        int[] v = new int[p] ;
        for( int k=0 ; k<p ; k++ )
            v[k] = va ;
        int t = item.T ;
        int tau = item.Tau ;
        int order_length = item.orders.length ;

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
        double[] aveFinal = new double[order_length] ;
        double[] aveIGD = new double[order_length] ;

        // repeat 30 times
        int repeat_num = 10 ;    // # of CA re-generations
        int valid_num = 30 ;    // # of failure schemas
        for( int rep = 0 ; rep < repeat_num ; rep++ ) {
            // 1. generate a covering array
            System.out.print(".");
            gen.Generation(ts);
            int length = ts.getTestSuiteSize();

            // set execution cost
            double exe = ts.getAverageSwitchingCost() * item.R;
            ts.setExecutionCost(exe, 0.5);

            // 2. generate valid_num (100) random Tau-way failure schemas
            HashSet<int[]> ss = new HashSet<>();
            do {
                int[] s = rand.SchemaExist(tau, p, v, ts);
                ss.add(s);
            } while (ss.size() < valid_num);

            // 3. get different orders
            MPopulation ref_candidate = new MPopulation(0, length, ts);

            // single and multi objective orders
            Sequence[] so_solutions = new Sequence[order_length-1];
            ArrayList<Sequence> mo_solution = new ArrayList<>();

            // for each order
            for (int i = 0 ; i < order_length ; i++ ) {
                switch ( item.orders[i] ) {
                    case RANDOM:
                        order.toRandomOrder(ts);
                        break;
                    case GREEDY:
                        order.toGreedySwitchOrder(ts);
                        break;
                    case LKH:
                        order.toLKHSwitchOrder(ts);
                        break;
                    case COVERAGE2:
                        order.toDefaultOrder(ts);
                        break;
                    case COVERAGE3:
                        order.toGreedyCoverageOrder(ts, 3);
                        break;
                    case COVERAGE4:
                        order.toGreedyCoverageOrder(ts, 4);
                        break;
                    case HYBRID2:
                        order.toGreedyHybridOrder(ts, 2);
                        break;
                    case HYBRID3:
                        order.toGreedyHybridOrder(ts, 3);
                        break;
                    case HYBRID4:
                        order.toGreedyHybridOrder(ts, 4);
                        break;
                    case MO:
                        order.toMultiObjective(ts, mo_solution);
                        break;
                }
                if( item.orders[i] != ORDER.MO ) {  // single objective
                    int[] oo = ts.getOrderInt();
                    so_solutions[i] = new Sequence(ts.getOrderInt(), (int)ts.getTotalCost(oo),
                            ts.getRFD(oo), 0, 0.0 );
                    ref_candidate.append(so_solutions[i]);
                }
                else {  // multi objective
                    ref_candidate.unionSet(mo_solution);
                    //System.out.println("-----------MO-------------");
                    //ref_candidate.printPopulation();
                }
            } // end for each order

            // 4. evaluation
            // get reference pareto front
            ArrayList<Sequence> reference = new ArrayList<>();
            ref_candidate.NonDominatedSort();
            ref_candidate.getFirstLevelFront(reference);

            //System.out.println("-------------------REF-----------------");
            //for( Sequence each : reference )
            //    each.printSequence();
            //System.out.println("size: " + reference.size());

            // compute ft-values for single objective order
            for( int a = 0 ; a < order_length-1 ; a++ ) {
                //if( !ts.isValidOrder(so_solutions[a]) )
                //    System.err.println("invalid order in single objective");
                double ft = 0.0 ;
                for (int[] s : ss) {
                    ft += ts.getFt(tau, s, so_solutions[a].order);
                }
                aveFinal[a] += ft / (double)valid_num ;
            }

            // compute ft-values for multi objective order
            // firstly get the solutions that contributes to the reference pareto front
            ArrayList<int[]> mo_solution_used = new ArrayList<>();
            for( Sequence mo : mo_solution ) {
                for( Sequence ref : reference ) {
                    if( mo.isEqualOrder(ref) ) {    // if contribute
                        int[] tp = mo.order.clone() ;
                        mo_solution_used.add(tp);
                    }
                }
            }

            double ft_ave = 0.0 ;
            for( int[] mo : mo_solution_used ) {
                //if( !ts.isValidOrder(mo) )
                //    System.err.println("invalid order in multi objective");
                double ft = 0.0 ;
                for (int[] s : ss) {
                    ft += ts.getFt(tau, s, mo);
                }
                ft_ave += ft / (double)valid_num ;
            }
            aveFinal[order_length-1] += ft_ave / (double)mo_solution_used.size() ;


            //System.out.print( rep + " - ");
            //for( int k = 0 ; k < aveFinal.length ; k++ )
            //    System.out.print( aveFinal[k] + " " );
            //System.out.print("\n");

            // evaluate IGD
            for( int a = 0 ; a < order_length-1 ; a++ )
                aveIGD[a] += MPopulation.getIGD(reference, so_solutions[a]);
            aveIGD[order_length-1] += MPopulation.getIGD(reference, mo_solution);

        } // end for each iteration

        // save final results to item
        for (int x = 0 ; x < item.orders.length ; x++)
            item.data[x] = aveFinal[x] / (double)repeat_num ;
        item.updateBestOrder();

        for (int x = 0 ; x < item.orders.length ; x++)
            aveIGD[x] = aveIGD[x] / (double)repeat_num ;

        System.out.print("\nIGD: ");
        for( int k = 0 ; k < aveIGD.length ; k++ )
            System.out.print( aveIGD[k] + " " );
        System.out.print("\n");

        System.out.print("aveFinal: ");
        for( int k = 0 ; k < aveFinal.length ; k++ )
            System.out.print( aveFinal[k] + " " );
        System.out.print("\n");
    }


    /*
     *  evaluate each SUT, repeat 30 times * 100 tau-way failure schemas
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
        int repeat_num = 30 ;    // # of CA re-generations
        int valid_num = 100 ;    // # of failure schemas
        for( int rep = 0 ; rep < repeat_num ; rep++ ) {
            // 1. generate a covering array
            System.out.print(".");
            gen.Generation(ts);

            // set execution cost
            double exe = ts.getAverageSwitchingCost() * item.R;
            ts.setExecutionCost(exe, 0.5);

            // 2. generate 100 (valid_num) random Tau-way failure schemas
            HashSet<int[]> ss = new HashSet<>();
            do {
                int[] s = rand.SchemaExist(tau, p, v, ts);
                ss.add(s);
            } while (ss.size() < valid_num);

            // 3. evaluate different orders
            double[] tpValue = new double[item.orders.length] ;
            for( int l = 0 ; l < item.orders.length ; l++ )
                tpValue[l] = 0.0 ;

            ArrayList<int[]> MO_Solutions = null ;
            for (int i = 0 ; i < item.orders.length ; i++ ) {
                ORDER od = item.orders[i];
                switch (od) {
                    case RANDOM:
                        order.toRandomOrder(ts);
                        break;
                    case GREEDY:
                        order.toGreedySwitchOrder(ts);
                        break;
                    case LKH:
                        order.toLKHSwitchOrder(ts);
                        break;
                    case COVERAGE2:
                        order.toDefaultOrder(ts);
                        break;
                    case COVERAGE3:
                        order.toGreedyCoverageOrder(ts, 3);
                        break;
                    case COVERAGE4:
                        order.toGreedyCoverageOrder(ts, 4);
                        break;
                    case HYBRID2:
                        order.toGreedyHybridOrder(ts, 2);
                        break;
                    case HYBRID3:
                        order.toGreedyHybridOrder(ts, 3);
                        break;
                    case HYBRID4:
                        order.toGreedyHybridOrder(ts, 4);
                        break;
                }

                // calculate average ft-value
                double ft = 0.0 ;
                for (int[] s : ss) {
                    ft += ts.getFt(tau, s, null);
                }
                aveFinal[i] += ft / (double)valid_num ;

            } // end for each order

        } // end for each iteration

        // save final results to item
        for (int x = 0 ; x < item.orders.length ; x++)
            item.data[x] = aveFinal[x] / (double)repeat_num ;
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
