package prioritization;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class DataItem {

    /*
     *  the orders that can be examined
     */
    public enum ORDER {
        RANDOM("random"),
        // single objectives
        COVERAGE("coverage"),   // 2-cov, default
        GREEDY("switch-greedy"),
        GA("switch-GA"),
        LKH("switch-LKH"),
        // multi objectives
        HYBRID("hybrid"),     // 2-cov / cost
        MO("NSGA-II");

        private final String text;

        private ORDER(String t) {
            this.text = t;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    // independent variables: the SUT
    public int P ;
    public int V ;
    public int T ;      // covering strength
    public int Tau ;    // Tau-way failure causing schema
    public int Type ;
    public double R ;

    // orders to be examined
    public ORDER[] orders ;

    // dependent variable: the quality indicator of each order
    // # of orders * repeated times
    // basic indicator
    public double[][] Cost ;
    public long[][] RFD ;
    // optimization indicator
    public double[][] EPSILON ;
    public double[][] IGD ;
    // testing indicator
    public double[][] Ft_measure ;

    public DataItem() {}
    public DataItem( ORDER[] l, int p, int v, int t, int tau, int type, double r, int repeat) {
        this.P = p ;
        this.V = v ;
        this.T = t ;
        this.Tau = tau ;
        this.Type = type ;
        this.R = r ;
        this.orders = l.clone() ;

        this.Cost = new double[l.length][repeat];
        this.RFD = new long[l.length][repeat];
        this.EPSILON = new double[l.length][repeat];
        this.IGD = new double[l.length][repeat];
        this.Ft_measure = new double[l.length][repeat];
    }

    @Override
    public String toString() {
        String str = "" ;
        for( ORDER oo : orders )
            str += oo.toString() + " " ;
        return "p = " + P + ", v = " + V + ", t = " + T + ", tau = " + Tau +
                ", type = " + Type + ", ratio = " + R + ", orders = " + str ;
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    @Override
    public boolean equals( Object obj ) {
        if (this == obj)
            return true;
        else if (obj != null && obj instanceof DataItem ) {
            DataItem a = (DataItem)obj;
            if( this.P == a.P && this.V == a.V && this.T == a.T && this.Tau == a.Tau
                    && this.Type == a.Type && this.R == a.R )
                return true ;
            else
                return false ;
        }
        else
            return false ;
    }


    public String getRow() {
        // P, V, T, Tau, Type, R
        return P + " " + V + " " + T + " " + Tau + " " + Type + " " + R ;
    }


    // write all data to a single file
    public void writeFile( String filename ) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("resources//" + filename ));

            // write name as the first line
            bw.write( this.toString() + "\n\n");

            // write each indicators
            bw.write( "Cost\n");
            for( int i = 0 ; i < orders.length; i++ )
                bw.write( orders[i].toString() + ": " + Arrays.toString(Cost[i]) + "\n" );
            bw.write("\n");

            bw.write( "RFD\n");
            for( int i = 0 ; i < orders.length; i++ )
                bw.write( orders[i].toString() + ": " + Arrays.toString(RFD[i]) + "\n" );
            bw.write("\n");

            bw.write( "EPSILON\n");
            for( int i = 0 ; i < orders.length; i++ )
                bw.write( orders[i].toString() + ": " + Arrays.toString(EPSILON[i]) + "\n" );
            bw.write("\n");

            bw.write( "IGD\n");
            for( int i = 0 ; i < orders.length; i++ )
                bw.write( orders[i].toString() + ": " + Arrays.toString(IGD[i]) + "\n" );
            bw.write("\n");

            bw.write( "Ft-measure\n");
            for( int i = 0 ; i < orders.length; i++ )
                bw.write( orders[i].toString() + ": " + Arrays.toString(Ft_measure[i]) + "\n" );

            bw.close();

        } catch (IOException e) {
            System.err.println(e);
        }
    }


}
