package prioritization;

import Basic.Rand;
import Basic.TestSuite;
import Generation.AETG;
import Prioritization.MIndicator;
import Prioritization.MPopulation;
import Prioritization.ReorderArray;
import Prioritization.Sequence;
import prioritization.DataItem.ORDER;

import java.io.*;
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
public class Simulation {

    private Rand rand ;
    private Random random ;
    private HashSet<DataItem> subjects ;

    public Simulation() {
        this.rand = new Rand();
        this.random = new Random();
        this.subjects = new HashSet<>();
    }

    public static ORDER[] order_1 = new ORDER[]{
            ORDER.RANDOM,
            ORDER.COVERAGE,
            ORDER.GREEDY,
            ORDER.GA,
            ORDER.LKH,
            ORDER.HYBRID,
            ORDER.MO};

    /*
     *  initialize subjects and save it to file
     */
    public void initSubjects( int num ) {
        int par_lower = 10, par_upper = 60 ;
        int val_lower = 2, val_upper = 8 ;
        double ratio_lower = 0.0, ratio_upper = 2.0 ;
        int strength_lower = 2, strength_upper = 4 ;
        ORDER[] oo = new ORDER[]{ORDER.RANDOM};

        do {
            int p = par_lower + random.nextInt(par_upper-par_lower+1);
            int v = val_lower + random.nextInt(val_upper-val_lower+1);
            int type = 1 + random.nextInt(2);
            double r = ratio_lower + (ratio_upper - ratio_lower) * random.nextDouble();
            int t = strength_lower + random.nextInt(strength_upper - strength_lower);
            int tau = strength_lower + random.nextInt(strength_upper-strength_lower+1);

            //p = 10 ;
            //v = 2 ;
            t = 2 ;
            tau = 2 ;
            //type = 1 ;

            DataItem di = new DataItem(oo, p, v, t, tau, type, r, 30);
            subjects.add(di);
        } while( subjects.size() < num );

        // write
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("resources//SUT.txt"));
            bw.write("NUM, P, V, T, Tau, Type, R\n");

            int i = 0 ;
            for( DataItem item : subjects ) {
                bw.write( i + ", " + item.getRow() + "\n" );
                i++ ;
            }

            bw.close();

        } catch (IOException e) {
            System.err.println(e);
        }

    }

    /*
     *  read the n-th DataItem from SUT file
     */
    public DataItem getDataItem( int n , ORDER[] o , int repeat ) {
        String str = "" ;
        try {
            BufferedReader rd = new BufferedReader(new FileReader("resources//SUT.txt"));
            // skip n+1 lines
            for( int i=0 ; i<n+1 ; i++ )
                rd.readLine();
            str = rd.readLine() ;
            rd.close();
        } catch (IOException e) {
            System.err.println(e);
        }

        str = str.substring( str.indexOf(",") + 1 ).trim();
        String[] strs = str.split(" ");

        // P, V, T, Tau, Type, R
        int p = Integer.valueOf(strs[0]);
        int v = Integer.valueOf(strs[1]);
        int t = Integer.valueOf(strs[2]);
        int tau = Integer.valueOf(strs[3]);
        int type = Integer.valueOf(strs[4]);
        double r = Double.valueOf(strs[5]);

        return new DataItem(o, p, v, t, tau, type, r, repeat);
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
     *  MAIN exp, the index of SUT is from n1 to n2
     */
    public void exp1( int n1 , int n2 ) {
        // repeat 30 times, testing 100 schemas for each
        int repeat = 30 ;
        int valid = 100 ;

        // evaluate SUT, from n1 to n2
        for( int i = n1 ; i <= n2 ; i++ ) {
            DataItem item = getDataItem(i, order_1, repeat);

            System.out.print("evaluating " + i + "-th CA(" + item.T + ", " + item.P + ", " + item.V +
                    ") with type " + item.Type + " and ratio " + item.R + " ");
            evaluate( item, repeat, valid );
            System.out.print("\n");

            item.writeFile(i + ".txt");
        }
    }


    /*
     *  evaluate each SUT, repeat N times * S tau-way failure schemas
     *  order_1 = random coverage switch-greedy switch-GA hybrid NSGA-II
     */
    private void evaluate( DataItem item, int N, int S ) {
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
            System.err.println("error type");
            return;
        }

        TestSuite ts = new TestSuite(p, v, t, w);
        AETG gen = new AETG();
        ReorderArray reorder = new ReorderArray();

        // repeat 30 times
        int repeat_num = N ;    // # of CA re-generations
        int valid_num = S ;     // # of failure schemas

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

            // 3. get different orders, and evaluate their basic indicators
            // the data structure to represent single and multi objective orders
            Sequence[] so_solutions = new Sequence[order_length-1];
            ArrayList<Sequence> mo_solution = new ArrayList<>();
            // the data structure to represent candidate reference front
            MPopulation ref_candidate = new MPopulation(0, length, ts);

            // for each single objective order
            for (int i = 0 ; i < order_length - 1 ; i++ ) {
                switch ( item.orders[i] ) {
                    case RANDOM:
                        reorder.toRandomOrder(ts);
                        break;
                    case COVERAGE:
                        reorder.toDefaultOrder(ts);
                        break;
                    case GREEDY:
                        reorder.toGreedySwitchOrder(ts);
                        break;
                    case GA:
                        reorder.toGASwitchOrder(ts);
                        break;
                    case LKH:
                        reorder.toLKHSwitchOrder(ts);
                        break;
                    case HYBRID:
                        reorder.toGreedyHybridOrder(ts, 2);
                        break;
                }

                if( !ts.isValidOrder(null))
                    System.err.println( item.orders[i].toString() + " error, Invalid Order!");

                // evaluate basic indicators, RFD and total switching cost
                double c = ts.getTotalSwitchingCost(null);
                double r = ts.getRFD(null, 2);
                so_solutions[i] = new Sequence(ts.order, c, r, 0, 0.0 );
                ref_candidate.append(so_solutions[i]);

                // save data to Item
                item.Cost[i][rep] = c ;
                item.RFD[i][rep] = r ;
            }

            // for multi objective order
            reorder.toMultiObjective(ts, mo_solution);
            ref_candidate.unionSet(mo_solution);

            // evaluate basic indicators, RFD and total switching cost
            double cc = 0.0 ;
            double rr = 0 ;
            for( Sequence seq : mo_solution ) {
                cc += ts.getTotalSwitchingCost(seq.order);
                rr += ts.getRFD(seq.order, 2);
            }
            item.Cost[order_length-1][rep] = cc / (double)mo_solution.size() ;
            item.RFD[order_length-1][rep] = rr / (double)mo_solution.size() ;


            // 4. evaluate multi objective optimization indicator
            // get reference pareto front
            ArrayList<Sequence> reference = new ArrayList<>();
            ref_candidate.NonDominatedSort();
            ref_candidate.getFirstLevelFront(reference);

            //System.out.println("-------------------REF-----------------");
            //for( Sequence each : reference )
            //    each.printSequence();
            //System.out.println("size: " + reference.size());

            // compute EPSILON and IGD for each order
            for( int a = 0 ; a < order_length-1 ; a++ ) {
                MIndicator mid = new MIndicator(so_solutions[a], reference);
                item.EPSILON[a][rep] = mid.EPSILON();
                item.IGD[a][rep] = mid.IGD();
            }

            MIndicator mid = new MIndicator(mo_solution, reference);
            item.EPSILON[order_length-1][rep] = mid.EPSILON();
            item.IGD[order_length-1][rep] = mid.IGD();


            // 5. evaluate the ability of early fault detection
            // compute Ft-measure for single objective orders
            for( int a = 0 ; a < order_length-1 ; a++ ) {
                double ft = 0.0 ;
                for (int[] s : ss) {
                    ft += ts.getFt(tau, s, so_solutions[a].order);
                }
                item.Ft_measure[a][rep] = ft / (double)valid_num ;
            }

            // compute ft-values for multi objective order
            // firstly get the solutions that contributes to the reference pareto front
            ArrayList<int[]> mo_solution_used = new ArrayList<>();
            for( Sequence mo : mo_solution ) {
                for( Sequence ref : reference ) {
                    if( mo.isEqualOrder(ref) ) {    // if contribute
                        mo_solution_used.add(mo.order.clone());
                    }
                }
            }

            double ft_ave = 0.0 ;
            for( int[] mo : mo_solution_used ) {
                double ft = 0.0 ;
                for (int[] s : ss) {
                    ft += ts.getFt(tau, s, mo);
                }
                ft_ave += ft / (double)valid_num ;
            }
            item.Ft_measure[order_length-1][rep] = ft_ave / (double)mo_solution_used.size() ;

            // compute RFDc value
            // firstly, get the maxTime of all obtained orders
            double maxTime = 0 ;
            for( Sequence seq : so_solutions) {
                if( seq.cost > maxTime )
                    maxTime = seq.cost ;
            }
            for( Sequence seq : mo_solution ) {
                if( seq.cost > maxTime )
                    maxTime = seq.cost ;
            }

            // compute values for single objective orders
            for( int a = 0 ; a < order_length-1 ; a++ ) {
                item.RFDc[a][rep] = ts.getRFDc(so_solutions[a].order, 2, maxTime);
            }
            // compute values for multi objective orders (mo_solution_used)
            double rfdc_ave = 0 ;
            for( int[] mo : mo_solution_used ) {
                rfdc_ave += ts.getRFDc(mo, 2, maxTime);
            }
            item.RFDc[order_length-1][rep] = rfdc_ave / (double)mo_solution_used.size() ;


        } // end for each iteration

    }


    /*
     *  Alg_Cost exp
     */
    public void exp2cost( String filename ) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("resources//" + filename, true));
            bw.write( "size random coverage switch-greedy switch-GA switch-lkh hybrid NSGA-II\n\n");
            bw.close();
        } catch (IOException e) {
            System.err.println(e);
        }

        int repeat = 30 ;
        int[] vNum = {2, 3, 4, 5, 6, 7, 8} ;
        int[] pNum = {10, 20, 30, 40, 50, 60} ;
        //int[] pNum = {60} ;

        int t = 2 ;
        int tau = 2 ;

        int i = 0 ;
        for( int p : pNum ) {
            for( int v : vNum ) {
                // initialize DataItem
                int type = 1 + random.nextInt(2);
                double r =  2.0 * random.nextDouble();
                DataItem di = new DataItem(order_1, p, v, t, tau, type, r, repeat);

                System.out.print("evaluating " + i + "-th CA(" + di.T + ", " + di.P + ", " + di.V +
                        ") with type " + di.Type + " and ratio " + di.R + " ");

                double size = evaluateCost( di, repeat );
                System.out.print("\n");

                di.writeFileCost(filename, size);
                i++ ;
            }
        }
    }

    private double evaluateCost( DataItem item, int N ) {
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
            System.err.println("error type");
            return 0;
        }

        TestSuite ts = new TestSuite(p, v, t, w);
        AETG gen = new AETG();
        ReorderArray reorder = new ReorderArray();

        double size_average = 0 ;

        for( int rep = 0 ; rep < N ; rep++ ) {
            // generate a covering array
            System.out.print(".");
            gen.Generation(ts);
            size_average += (double)ts.getTestSuiteSize() ;

            // set execution cost
            double exe = ts.getAverageSwitchingCost() * item.R;
            ts.setExecutionCost(exe, 0.5);

            // for each order
            // order_1 = [random coverage switch-greedy switch-GA hybrid NSGA-II]

            // for each single objective order
            for (int i = 0 ; i < order_length - 1 ; i++ ) {

                // start time
                long time_start = System.currentTimeMillis() ;

                switch ( item.orders[i] ) {
                    case RANDOM:
                        reorder.toRandomOrder(ts);
                        break;
                    case COVERAGE:
                        reorder.toDefaultOrder(ts);
                        break;
                    case GREEDY:
                        reorder.toGreedySwitchOrder(ts);
                        break;
                    case GA:
                        reorder.toGASwitchOrder(ts);
                        break;
                    case LKH:
                        reorder.toLKHSwitchOrder(ts);
                        break;
                    case HYBRID:
                        reorder.toGreedyHybridOrder(ts, 2);
                        break;
                }

                // end time
                long time_end = System.currentTimeMillis() ;

                if( !ts.isValidOrder(null))
                    System.err.println( item.orders[i].toString() + " error" ) ;

                // save
                item.Alg_Cost[i][rep] = time_end - time_start ;

            }

            // multi objective order
            long time_start_m = System.currentTimeMillis() ;

            ArrayList<Sequence> mo_solution = new ArrayList<>();
            reorder.toMultiObjective(ts, mo_solution);

            long time_end_m = System.currentTimeMillis() ;

            item.Alg_Cost[order_length-1][rep] = time_end_m - time_start_m ;

        } // end for each iteration

        return size_average / (double)N ;

    }


}
