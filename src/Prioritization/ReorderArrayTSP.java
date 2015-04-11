package Prioritization;

import Basic.TestSuite;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 *  Do switching cost based prioritization by TSP based algorithms
 */
public class ReorderArrayTSP {

    public ReorderArrayTSP() {
        tsp_file = "" ;
        par_file = "" ;
        tour_file = "" ;
        path = System.getProperty("user.dir");
    }

    public String path ;
    public String tsp_file ;
    public String par_file ;
    public String tour_file ;

    /*
     *  convert CA to graph representation with a dummy vertex v0
     *  if out = true, then prepare related files (name.tsp & name.par) for LKH solver
     */
    private int[][] toGraph( TestSuite test, boolean out, String name) {
        int size = test.tests.length ;
        int[][] graph = new int[size+1][size+1] ;

        // the first row and column
        for( int i=0 ; i<size+1 ; i++ ) {
            graph[0][i] = graph[i][0] = 0 ;
        }

        for( int i=1 ; i<size+1 ; i++ ) {
            graph[i][i] = 0 ;
            for( int j=i+1 ; j<size+1 ; j++ ) {
                graph[i][j] = graph[j][i] = (int)test.distance(i-1, j-1) ;
            }
        }

        // output the required input files for LKH solver
        if( out ) {
            /*.par
            FileWriter fileWriter1 = new FileWriter(name + ".par");
            String str1 = "PROBLEM_FILE = " + name + ".tsp \n" ;
            str1 += "MOVE_TYPE = 5 \n" ;
            str1 += "PATCHING_C = 3 \n" ;
            str1 += "PATCHING_A = 2 \n" ;
            str1 += "RUNS = 10" ;
            fileWriter1.write(str1);
            fileWriter1.close();
            */

            tsp_file = path + "\\resources\\lkh\\" + name + ".tsp";
            par_file = path + "\\resources\\lkh\\" + name + ".par";
            tour_file = path + "\\resources\\lkh\\" + name + ".tour";

            try {
                // *.tsp file
                String str2 = "NAME : " + name + " \n";
                str2 += "TYPE : TSP \n";
                str2 += "DIMENSION : " + String.valueOf(size + 1) + " \n";
                str2 += "EDGE_WEIGHT_TYPE : EXPLICIT \n";
                str2 += "EDGE_WEIGHT_FORMAT: FULL_MATRIX \n";
                str2 += "EDGE_WEIGHT_SECTION \n";

                BufferedWriter bw = new BufferedWriter(new FileWriter(tsp_file));
                bw.write(str2);
                bw.flush();

                for (int i = 0; i < size + 1; i++) {
                    str2 = "";
                    for (int j = 0; j < size + 1; j++) {
                        str2 += graph[i][j] + " ";
                    }
                    str2 += "\n";
                    bw.write(str2);
                    bw.flush();
                }
                bw.close();

                // .par file
                bw = new BufferedWriter(new FileWriter(par_file));
                bw.write("PROBLEM_FILE = " + name + ".tsp\n");
                bw.write("RUNS = 5\n");
                bw.write("TOUR_FILE = " + name + ".tour" );
                bw.close();

            } catch( IOException e ) {
                e.printStackTrace();
            }
        }
        return graph ;
    }

    /*
     *  prioritization CA by DP algorithm
     *  TSP solution:
     *  add a dummy vertex that has a distance of zero to all other vertexes.
     *  solve the TSP and get rid of the dummy point to get Hamiltonian Path
     */
    public int[] DPOrder( TestSuite test ) {
        int size = test.tests.length ;
        TSP tsp = new TSP(size+1, this.toGraph(test, false, null));
        tsp.DP();

        // remove v0 (dummy vertex) and then get shortest hamiltonian path
        int[] re = new int[size];
        for( int k=0 ; k<size ; k++ )
            re[k] = tsp.opt_sequence[k+1] - 1;
        return re ;
    }

    /*
     *  prioritization CA by GA algorithm
     */
    public int[] GAOrder( TestSuite test ) {
        int size = test.tests.length ;
        TSP tsp = new TSP(size+1, this.toGraph(test, false, null));
        tsp.GA(20, 1000, 0.7, 0.7);

        // remove v0 (dummy vertex) and then get shortest hamiltonian path
        int[] re = new int[size];

        int i = 0, j = 0 ;
        for( ; i<size+1 && tsp.opt_sequence[i] != 0 ; i++ );
        for( i=i+1 ; i<size+1 ; i++, j++ )
            re[j] = tsp.opt_sequence[i]-1 ;
        for( i = 0, j=j+1 ; j<size ; j++, i++ )
            re[j] = tsp.opt_sequence[i]-1 ;

        return re ;
    }

    /*
     *  read best test sequence from .tour file
     */
    public int[] GetFromTour() {
        String file = tour_file ;
        ArrayList<Integer> tp = new ArrayList<Integer>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            // skip the first 7 lines
            for( int i=0 ; i<7 ; i++ ) {
               reader.readLine();
            }
            // read sequence
            String str = reader.readLine();
            while( !str.equals("-1") ) {
                tp.add( Integer.valueOf(str) );
                //System.out.println("str = " + str + ", add " + Integer.valueOf(str));
                str = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // convert
        int[] re = new int[tp.size()];
        for( int i=0 ; i<tp.size() ; i++ ) {
            re[i] = tp.get(i)-2 ;
            //System.out.print(re[i] + " ");
        }
        //System.out.print(" size = " + re.length + "\n");

        return re ;
    }

    /*
     *  scripts to run LKH solver
     *  return the best sequence
     */
    public int[] LKHsolver( TestSuite test ) {

        // 1) prepare related files
        String name = "solver" ;
        this.toGraph( test, true, name);

        // 2) run LKH solver
        try {
            String cmd = "cmd /c cd " + path + "\\resources\\lkh & d: & lkh-1.exe " + par_file  ;
            Process proc = Runtime.getRuntime().exec(cmd);
            //System.out.println(cmd);
            //"cmd /c cd d:\\Workspace\\CombinatorialDesign\\tmp & d: & "+
            //        "lkh-1.exe d:\\Workspace\\CombinatorialDesign\\"+par_file
            //InputStreamReader ir = new InputStreamReader(proc.getInputStream());
            //LineNumberReader input = new LineNumberReader(ir);
            //String line;
            //while((line=input.readLine ()) != null)
            //    System.out.println("sto " + line);
            proc.waitFor();
        } catch ( IOException e ) {
            e.printStackTrace();
        } catch ( InterruptedException e ) {
            e.printStackTrace();
        }

        // 3) get best sequence
        int[] s = GetFromTour();

        // 4) delete files
        //System.out.println("delete files");
        new File(tour_file).delete();
        new File(par_file).delete();
        new File(tsp_file).delete();

        return s ;
    }


}
