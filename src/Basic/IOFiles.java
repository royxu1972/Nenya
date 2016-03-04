package Basic;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/**
 * 1) Read CA file (.txt) for updating parameter, value, CArray, and CSize
 * 2) Convert CA file (.txt) to TSP based file (.tsp) for TSP solvers
 */
public class IOFiles {

    public int parameter ;
    public int[] value ;
    public ArrayList<int[]> CA ;

    /*
     *  read CA from file
     *  line 1:    parameter 4
     *  line 2:    value 3 3 3 3
     *  line 3:    begin
     *  line 4-n:  covering array
     *  line n+1:  end
     */
    public void readFromFile(String fileName) throws IOException {
        Random rd = new Random();
        File file = new File(fileName);
        try {
            BufferedReader reader = null ;
            reader = new BufferedReader(new FileReader(file));

            // line 1 : parameter 4
            String line1 = reader.readLine();
            line1 = line1.trim();
            parameter = Integer.valueOf(line1.substring(10));

            // line 2 : value 3 3 3 3
            String line2 = reader.readLine();
            line2 = line2.trim() ;
            line2 = line2.substring(6);
            String[] tmp = line2.split("\\s");
            value = new int[parameter];
            for (int k = 0; k < tmp.length; k++)
                value[k] = Integer.valueOf(tmp[k]);

            // CA
            CA = new ArrayList<int[]>();
            String str = reader.readLine() ;
            str = str.trim();
            if( str.equals("begin")) {
                while ((str = reader.readLine()) != null) {
                    str = str.trim();
                    if (str.equals("end")) {
                        break;
                    }
                    tmp = str.split("\\s+");
                    int[] row = new int[parameter];
                    for (int k = 0; k < tmp.length; k++) {
                        int val = Integer.valueOf(tmp[k]);
                        if( val == -1 )
                            val = rd.nextInt(value[k]) ;
                        row[k] = val ;
                    }
                    CA.add(row);
                }
            }
            // close
            reader.close();
        } catch (FileNotFoundException ex) {
            System.err.println(ex);
        }
    }


    /*
     *  write a CA to .tsp file
     */
    public void WriteToTSP( String filename ) throws Exception {
        Scanner sc = new Scanner(new File("resources/" + filename));
        int DIMENSION = 0;
        boolean begin = false;
        while (sc.hasNext()) {
            String line = sc.nextLine();
            if (line.equals("end")) {
                break;
            }
            if (begin) {
                DIMENSION++;
            }
            if (line.equals("begin")) {
                begin = true;
            }
        }
        sc.close();
        System.out.println("DIMENSION: " + DIMENSION);

        sc = new Scanner(new File("data/" + filename));
        PrintWriter pw = new PrintWriter(new File("resources/" + filename.replace(".txt", ".tsp")));
        pw.write("DIMENSION : " + DIMENSION + "\n");
        pw.write("EDGE_WEIGHT_TYPE : COST\n");
        pw.write("NODE_COORD_SECTION\n");

        while (sc.hasNext()) {
            String line = sc.nextLine();
            if (line.equals("begin")) {
                break;
            }
        }

        while (sc.hasNext()) {
            String line = sc.nextLine();
            if (line.equals("end")) {
                break;
            }
            else {
                pw.write(line + "\n");
            }
        }
        pw.write("EOF\n");
        sc.close();
        pw.close();
    }

}
