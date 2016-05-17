package Basic;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * 1) Read CA file (.txt) for updating parameter, value, CArray, and CSize
 * 2) Convert CA file (.txt) to TSP based file (.tsp) for TSP solvers
 */
public class IOFiles {

    /*
     *  Read a covering array from file, and use it to initialize ts
     *  line 1:    parameter 4
     *  line 2:    value 3 3 3 3
     *  line 3:    t-way 2
     *  line 3:    begin
     *  line 4-n:  covering array
     *  line n+1:  end
     */
    public static TestSuite readCoveringArrayFromFile(String fileName) {
        Random rd = new Random();
        File file = new File(fileName);
        TestSuite ts = null ;

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));

            // line 1 : parameter 4
            String line = reader.readLine();
            line = line.trim();
            int parameter = Integer.valueOf(line.substring(10));

            // line 2 : value 3 3 3 3
            line = reader.readLine();
            line = line.trim();
            line = line.substring(6);
            String[] tmp = line.split("\\s");
            int[] value = new int[parameter];
            for (int k = 0; k < tmp.length; k++)
                value[k] = Integer.valueOf(tmp[k]);

            // line 3 : t-way 2
            line = reader.readLine();
            line = line.trim();
            int t_way = Integer.valueOf(line.substring(6));

            // test suite
            List<int[]> ca = new ArrayList<>();
            String str = reader.readLine();
            str = str.trim();
            if (str.equals("begin")) {
                while ((str = reader.readLine()) != null) {
                    str = str.trim();
                    if (str.equals("end")) {
                        break;
                    }
                    tmp = str.split("\\s+");
                    int[] row = new int[parameter];
                    for (int k = 0; k < tmp.length; k++) {
                        int val = Integer.valueOf(tmp[k]);
                        if (val == -1)
                            val = rd.nextInt(value[k]);
                        row[k] = val;
                    }
                    ca.add(row);
                }
            }
            // close
            reader.close();

            // update ts
            ts = new TestSuite(parameter, value, t_way);
            transferTestSuite(ca, ts);

        } catch (FileNotFoundException e) {
            System.err.println(e);
        } catch (IOException e) {
            System.err.println(e);
        }

        return ts ;
    }

    /*
     *  Transfer an List<int[]> ca to test.int[][], ca[].length should
     *  be equal to test.system.parameter.
     *  Set default solution and set default execution cost = 0.0
     */
    public static void transferTestSuite( List<int[]> ca , TestSuite test ) {
        test.tests = new int[ca.size()][test.system.parameter];
        test.order = new int[ca.size()];
        test.executionCost = new double[ca.size()] ;
        int index = 0;
        for (int[] row : ca) {
            System.arraycopy(row, 0, test.tests[index], 0, row.length);
            test.order[index] = index ;
            test.executionCost[index] = 0.0 ;
            index += 1;
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
