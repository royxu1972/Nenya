package prioritization;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 *  data structure for exp prioritization
 */
public class Collection {

    public String name ;

    // variables: p, v and ratio (execution / switching)
    public int[] pValue ;
    public int[] vValue ;
    public double[] rValue ;

    // obtained data
    public String[] dataLabel ;  // label for different orders
    public double[][][][] dataValue ;  // p * v * order * ratio

    public Collection() {}

    public void init( String nn, String[] l, int[] p, int[] v, double[] r) {
        this.name = nn ;

        this.pValue = new int[p.length];
        this.vValue = new int[v.length];
        this.rValue = new double[r.length];
        System.arraycopy(p, 0, this.pValue, 0, p.length);
        System.arraycopy(v, 0, this.vValue, 0, v.length);
        System.arraycopy(r, 0, this.rValue, 0, r.length);

        // |data| = |p| * |v| * |order| * |ratio|
        this.dataLabel = new String[l.length];
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
            System.out.println("------------------------" );
            System.out.println("p = " + pValue[i] );
            System.out.println("------------------------" );

            for( int j=0 ; j<vValue.length ; j++ ) {
                System.out.println("v = " + vValue[j] );
                System.out.println("ratio: " + array2str(rValue, 1) );
                for (int k=0 ; k<dataLabel.length ; k++) {
                    System.out.println(dataLabel[k] + ": " + array2str(dataValue[i][j][k], 3));
                }
                System.out.print("\n");
            }
        }

        if( filename != null ) {
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter("resources//" + filename )) ;
                bw.write(name + "\n");
                for( int i=0 ; i<pValue.length ; i++ ) {
                    bw.write("------------------------\n");
                    bw.write("p = " + pValue[i] + "\n");
                    bw.write("------------------------\n");

                    for( int j=0 ; j<vValue.length ; j++ ) {
                        bw.write("v = " + vValue[j] + "\n");
                        bw.write("ratio: " + array2str(rValue, 1) + "\n");
                        for (int k=0 ; k<dataLabel.length ; k++) {
                            bw.write(dataLabel[k] + ": " + array2str(dataValue[i][j][k], 3) + "\n");
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

    /*
     *  print data for plot to file [beta]
     */
    public void printPlotData( String filename ) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter("resources//" + filename ));

        // for each parameter
        for( int p=0 ; p<pValue.length ; p++ ) {
            bw.write("TITLE p=" + pValue[p] + "\n");
            bw.write("X-AXIS ratios\n");
            bw.write("Y-AXIS f(t)-switching/f(t)-coverage\n");
            bw.write("LABEL ");
            for( int v=0 ; v<vValue.length ; v++ )
                bw.write("v="+vValue[v]+ " ");
            bw.write("\n");
            bw.write("X-STICKS " + array2str(rValue, 1) + "\n");
            bw.write("DATA\n");
            for( int v=0 ; v<vValue.length ; v++ ) {
                for( int m=0 ; m<rValue.length ; m++ ) {
                    // cost / coverage
                    double d = dataValue[p][v][2][m] / dataValue[p][v][0][m] ;
                    bw.write( String.format("%.3f", d) + " ");
                }
                bw.write("\n");
                bw.flush();
            }
            bw.write("\n");
        }
        bw.close();
    }


}
