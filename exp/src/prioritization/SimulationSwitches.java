package prioritization;

import Basic.TestSuite;
import Generation.AETG;
import Prioritization.ReorderArray;

/**
 *  evaluate the average number of switches under different combinations of parameter and value
 */
public class SimulationSwitches {

    public double[][] data ;

    public static int[] par = {10, 20, 30, 40, 50, 60};
    public static int[] val = {2, 3, 4, 5, 6, 7, 8};

    public SimulationSwitches(){
        // init
        data = new double[par.length][val.length] ;
        for( int i = 0 ; i < par.length ; i++ )
            for( int j = 0 ; j < val.length ; j++ )
                data[i][j] = 0.0 ;
    }

    public void run() {
        // for each
        for( int i = 0 ; i < par.length ; i++ ) {
            for( int j = 0 ; j < val.length ; j++ ) {
                double ave = 0.0 ;

                int p = par[i];
                int[] v = new int[p] ;
                for( int k=0 ; k<p ; k++ )
                    v[k] = val[j] ;
                int t = 2 ;
                TestSuite ts = new TestSuite(p, v, t);
                AETG gen = new AETG();
                ReorderArray re = new ReorderArray();

                System.out.println( "run p = " + p + ", v = " + v[0] );

                // repeat 30 times
                for( int time = 0 ; time < 30 ; time++ ) {
                    gen.Generation(ts);
                    int size = ts.getTestSuiteSize();
                    re.toRandomOrder(ts);
                    //ave += ts.getAverageSwitches(null) ;
                    //ave += (double)size / (double)v[0] ;
                    ave += ts.getAverageSwitchingCost() / (double)ts.system.parameter ;
                }

                // save data
                data[i][j] = ave / 30.0 ;
            }
        }

        // print
        for( int i = 0 ; i < par.length ; i++ ) {
            System.out.print("par = " + par[i] + ": ");
            for( int j = 0 ; j < val.length ; j++ ) {
                System.out.print( String.format("%.4f", data[i][j]) + " " );
            }
            System.out.print("\n");
        }
    }

}
