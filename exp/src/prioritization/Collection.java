package prioritization;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 *  data structure for exp prioritization
 */
public class Collection {

    /*
     *  the orders that can be examined
     */
    public enum ORDERS {
        RANDOM("random"),
        COVERAGE("coverage"),
        GREEDY("switch-greedy"),
        LKH("switch-lkh"),
        HYBRID("hybrid");

        private final String text;

        private ORDERS(String t) {
            this.text = t;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public String name ;

    // variables: p, v, ratio (execution / switching)
    public int[] pValue ;
    public int[] vValue ;
    public double[] rValue ;

    // obtained data
    public ORDERS[] dataLabel ;         // label for different orders
    public double[][][][] dataValue ;   // p * v * order * ratio

    public Collection() {}

    public void init( String nn, ORDERS[] l, int[] p, int[] v, double[] r) {
        this.name = nn ;

        this.pValue = new int[p.length];
        this.vValue = new int[v.length];
        this.rValue = new double[r.length];
        System.arraycopy(p, 0, this.pValue, 0, p.length);
        System.arraycopy(v, 0, this.vValue, 0, v.length);
        System.arraycopy(r, 0, this.rValue, 0, r.length);

        // |data| = |p| * |v| * |order| * |ratio|
        this.dataLabel = new ORDERS[l.length];
        System.arraycopy(l, 0, this.dataLabel, 0, l.length);

        this.dataValue = new double[p.length][v.length][l.length][r.length];
        for( int i=0 ; i<p.length ; i++ )
            for( int j=0 ; j<v.length ; j++ )
                for( int k=0 ; k<l.length ; k++ )
                    for( int m=0 ; m<r.length ; m++ )
                    this.dataValue[i][j][k][m] = 0.0 ;
    }

    /*
     *  convert double array to String
     */
    public String array2str( final double[] f, int precision ) {
        String str = "" ;
        for( int k=0 ; k<f.length ; k++ )
            str += String.format("%." + precision +"f", f[k]) + " " ;
        return str ;
    }

    /*
     *  print plain data to console and/or file
     */
    public void printPlainData( String filename ) {
        for( int i=0 ; i<pValue.length ; i++ ) {
            for( int j=0 ; j<vValue.length ; j++ ) {
                System.out.println("------------------------\n");
                System.out.println("p = " + pValue[i] + " , v = " + vValue[j] + "\n");
                System.out.println("------------------------\n");
                System.out.println("ratio: " + array2str(rValue, 1) );
                for (int k=0 ; k<dataLabel.length ; k++) {
                    System.out.println(dataLabel[k].toString() + ": " + array2str(dataValue[i][j][k], 3));
                }
                System.out.print("\n");
            }
        }

        if( filename != null ) {
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter("resources//" + filename )) ;
                bw.write(name + "\n\n");
                for( int i=0 ; i<pValue.length ; i++ ) {
                    for( int j=0 ; j<vValue.length ; j++ ) {
                        bw.write("------------------------\n");
                        bw.write("p = " + pValue[i] + " , v = " + vValue[j] + "\n");
                        bw.write("------------------------\n");
                        bw.write("ratio: " + array2str(rValue, 1) + "\n");
                        for (int k=0 ; k<dataLabel.length ; k++) {
                            bw.write(dataLabel[k].toString() + ": " + array2str(dataValue[i][j][k], 3) + "\n");
                        }
                        bw.write("\n");
                    }
                    bw.flush();
                }
                bw.close();
            }
            catch ( IOException e ) {
                System.err.println(e);
            }
        }
    }

}
