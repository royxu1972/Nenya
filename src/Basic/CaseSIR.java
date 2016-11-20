package Basic;

import Model.SUT;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.regex.Pattern;

public class CaseSIR {

    /**
     * Each test case in .frame file
     */
    private class SIRTestCase implements Cloneable {
        int id = -1 ;              // the index corresponds to .fault.matrix file
        String Key;                // Key representation
        String UniverseLine;       // universeLine representation

        // the fault that can be triggered by this test case, where each integer
        // represents the index of seeded faulty variants
        HashSet<Integer> faulty ;

        @Override
        public SIRTestCase clone() {
            SIRTestCase s = new SIRTestCase();
            s.id = id ;
            s.Key = Key ;
            s.UniverseLine = UniverseLine ;
            s.faulty = new HashSet<>();
            s.faulty.addAll(faulty);
            return s;
        }
    }

    /**
     * Each version of SIR software
     */
    private class SIRVersion {
        public String identifier ;     // version name

        // .fault.matrix file
        public int listVersions ;     // number of faulty variants
        public int listTests ;        // number of test cases
        public String[] listSuite;    // each test case
        public int[][] faultMatrix ;  // which test case detect which faulty variants (size * version)

        // .frame file
        public int keyTests ;                      // number of key tests
        public ArrayList<SIRTestCase> wholeSuite;  // all valid key tests (the test case must exist in fault matrix file)
        public ArrayList<SIRTestCase> faultySuite; // test cases that can detected faults
        public HashSet<Integer> totalFaultSet ;    // the faults detected by wholeSuite

        public void showFaultMatrix() {
            for( int i=0 ; i<faultMatrix.length ; i++ )
                System.out.println("#" + i + ": " + Arrays.toString(faultMatrix[i]));
        }

        public void showStat() {
            System.out.println("--------------  " + identifier + "  -----------------------");
            System.out.println("# faulty versions = " + listVersions);
            System.out.println("# tests with results = " + listTests );
            System.out.println("# key tests = " + keyTests);
            System.out.println("# valid key tests (whileSuite) = " + wholeSuite.size());
            System.out.println("# key tests that can detect fault (faultySuite) = " + faultySuite.size());
            System.out.println("# faults that can be detected = " + totalFaultSet.size() + " " + totalFaultSet);
            // for each fault, calculate the number of test cases that can trigger it
            for( int f : totalFaultSet ) {
                int num = 0 ;
                for( SIRTestCase test : wholeSuite )
                    if( test.faulty.contains(f) )
                        num++;
                System.out.println( identifier + "." + f + " \t# " + num);
            }
            System.out.println("--------------------------------------------------");
        }
    }

    public ArrayList<SIRVersion> allVersions ;

    /**
     *  data from .model and .mapping file
     */
    public int parameter ;
    public int[] value ;

    // mapping between parameter values and relations in CNF.
    // give a parameter p_i and its value v_j, the index of
    // this parameter-value = relations[p_i][v_j] (start from 1)
    public int[][] relations ;

    // constraint (disjunction representation)
    public int[][] constraint ;

    // mapping
    public int[] baseKey;     // [-1,-1,0,-1,-1,-1,-1,-1,-1,-1,2,1,1,1]
    public int[] defaultKey;  // indicates default values of baseKey
    public int[][] mapping ;  // 3 columns: ca-number, parameter, ca-param-value



    /**
     *  main function
     */
    public void readSIR(String name, int[] versions) {
        String path = "resources/data/" + name + "/" + name;

        // model and mapping
        readModel( path + ".model.csv" );
        readMapping( path + ".mapping" );

        // read tests and faults in each version
        allVersions = new ArrayList<>();
        for( int v : versions ) {
            SIRVersion sv = new SIRVersion();
            sv.identifier = "version." + v ;

            readFaultMatrix(path + ".v" + v + ".fault.matrix", sv);
            readTestCase(path + ".v" + v + ".frame", sv);

            //sv.showFaultMatrix();
            sv.showStat();

            // check duplicate
            for( int i=0 ; i<sv.wholeSuite.size() ; i++ ) {
                for( int j=i+1 ; j<sv.wholeSuite.size() ; j++ ) {
                    if( sv.wholeSuite.get(i).equals(sv.wholeSuite.get(j)))
                        System.out.println("duplicate");
                }
            }


            allVersions.add(sv);
        }
    }

    /**
     *  full model (.csv)
     */
    public void readModel(String filename) {
        File file = new File(filename);
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));

            // line 1 : <Parameter List>
            // line 2 : p1 - pn
            int n = 2 ;
            while ( n-- > 0 )
                reader.readLine();

            // parameter and value
            ArrayList<String[]> tp = new ArrayList<>();
            String line ;
            while( !(line = reader.readLine()).equals("") ) {
                tp.add(line.split(","));
            }

            parameter = tp.get(0).length ;
            value = new int[parameter];

            for( String[] row : tp ) {
                for( int j=0 ; j<parameter && j<row.length ; j++ ) {
                    if( !row[j].equals(" ") )
                        value[j]++;
                }
            }

            // constraint (disjunction representation)
            reader.readLine();
            ArrayList<Integer[]> con = new ArrayList<>();
            while( (line = reader.readLine()) != null ) {
                String[] k = line.split(",");
                ArrayList<Integer> m = new ArrayList<>();
                for( String s : k )
                    if( !s.equals("*") )
                        m.add( - Integer.valueOf(s) - 1);
                con.add(m.toArray(new Integer[0]));
            }

            constraint = new int[con.size()][];
            int index = 0 ;
            for( Integer[] row : con ){
                constraint[index] = new int[row.length];
                for( int j=0 ; j<row.length ; j++ )
                    constraint[index][j] = row[j] ;
                index++;
            }

            // set relations
            relations = new int[parameter][];
            int start = 1 ;
            for( int i=0 ; i<parameter ; i++ ) {
                relations[i] = new int[value[i]];
                for( int j=0 ; j<value[i] ; j++ , start++ )
                    relations[i][j] = start ;
            }

            reader.close();

        } catch ( IOException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Read .mapping file
     * @param filename the path of file
     */
    private void readMapping(String filename) {
        try {
            File file = new File(filename);
            BufferedReader reader = new BufferedReader(new FileReader(file));

            // first line, base Key
            String line = reader.readLine();
            line = line.substring(line.indexOf("[")+1, line.indexOf("]"));
            String[] ls = line.split(",");
            baseKey = new int[ls.length];
            for( int k=0 ; k<ls.length ; k++ )
                baseKey[k] = Integer.valueOf(ls[k]);

            // second line, default Key
            line = reader.readLine();
            line = line.substring(line.indexOf("[")+1, line.indexOf("]"));
            ls = line.split(",");
            defaultKey = new int[ls.length];
            for( int k=0 ; k<ls.length ; k++ )
                defaultKey[k] = Integer.valueOf(ls[k]);

            // skip third line
            reader.readLine();

            // mapping
            int row = relations[parameter-1][value[parameter-1]-1];
            mapping = new int[row][3];
            int index = 0 ;
            while ( index < row ) {
                ls = reader.readLine().split(",");
                mapping[index][0] = Integer.valueOf(ls[0]);
                mapping[index][1] = Integer.valueOf(ls[1]);
                mapping[index][2] = Integer.valueOf(ls[2]);
                index++;
            }

            reader.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }


    /**
     *  fault matrix (i.e. .universe)
     */
    private void readFaultMatrix(String filename, SIRVersion sv) {
        try {
            File file = new File(filename);
            BufferedReader reader = new BufferedReader(new FileReader(file));

            // number of version
            String line = reader.readLine().trim();
            sv.listVersions = Integer.valueOf(line.split(" ")[0]);

            // number of tests
            line = reader.readLine().trim();
            sv.listTests = Integer.valueOf(line.split(" ")[0]);

            // read test cases
            sv.listSuite = new String[sv.listTests];
            for( int i=0 ; i<sv.listTests ; i++ )
                sv.listSuite[i] = reader.readLine().trim();

            // read fault matrix
            sv.faultMatrix = new int[sv.listTests][sv.listVersions];
            for( int i=0 ; i<sv.listTests ; i++ ) {
                reader.readLine();                      // unitest0:
                for( int j=0 ; j<sv.listVersions ; j++ ) {
                    reader.readLine();                  // v0, v1, v2, ...
                    line = reader.readLine().trim();    // 0, 1
                    sv.faultMatrix[i][j] = Integer.valueOf(line);
                }
            }

            //for( int i=0 ; i<sv.listTests ; i++ )
            //    System.out.println("#" + i + ": " + Arrays.toString(sv.faultMatrix[i]));
            reader.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     *  read key test cases (.frame)
     */
    private void readTestCase(String filename, SIRVersion sv) {
        try {
            File file = new File(filename);
            BufferedReader reader = new BufferedReader(new FileReader(file));

            sv.wholeSuite = new ArrayList<>();

            String line;
            int count = 0 ;
            while ( (line = reader.readLine()) != null ) {
                // only consider test case with Key
                // no [single] or [error] test case
                if( line.trim().startsWith("Test") && line.contains("Key") ) {
                    count++ ;
                    int i = line.indexOf("=")+1;
                    int j = line.indexOf(")");

                    // key
                    String ls = line.substring(i, j).trim();

                    // universeLine
                    while ( (line = reader.readLine()) != null ) {
                        if( line.trim().startsWith("Universe") ) {
                            line = line.substring(line.indexOf(":")+1).trim();

                            SIRTestCase t = new SIRTestCase();
                            t.Key = ls ;
                            t.UniverseLine = line ;
                            for( int k=0 ; k<sv.listSuite.length ; k++ ) {
                                if( line.equals(sv.listSuite[k]) ) {
                                    t.id = k ;    // the index in fault matrix file
                                    HashSet<Integer> f = new HashSet<>();
                                    for( int m=0 ; m<sv.listVersions ; m++ ) {
                                        if( sv.faultMatrix[k][m] != 0 )
                                            f.add(m);
                                    }
                                    t.faulty = f;
                                    break;
                                }
                            }

                            if( t.id != -1 )
                                sv.wholeSuite.add(t);
                            else
                                System.err.println("FRAME NOT IN FAULT MATRIX " + ls + " | " + line);

                            break;
                        }
                    }
                }   // end if
            }   // end while

            // the number of all key tests
            sv.keyTests = count ;

            // find the faults that can be detected by this wholeSuite
            sv.faultySuite = new ArrayList<>();
            sv.totalFaultSet = new HashSet<>();
            for( SIRTestCase t : sv.wholeSuite) {
                //System.out.println(t.id + " -- " + t.Key + " : " + t.faulty);
                if( t.faulty.size() != 0 ) {
                    sv.faultySuite.add(t.clone());
                    sv.totalFaultSet.addAll(t.faulty);
                }
            }

            reader.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Transfer a CT test case to key representation
     * [2, 0, 3, 4, 3, 2, 2, 0, 0] -> "3.1.0.21.9.6.3.4.1.1.2.1.1.1"
     * @param t conventional test case
     * @return Key in .tsl file
     */
    public String getKey( int[] t ) {
        // transfer to parameter-value representation
        int[] pv = new int[parameter];
        for( int p=0 ; p<parameter ; p++ ) {
            // parameter-value in mapping starts from 0
            // but in relations starts from 1
            pv[p] = relations[p][t[p]] - 1;
        }

        int[] tt = baseKey.clone();
        for( int v : pv ) {
            // parameter in mapping starts from 1
            int par = mapping[v][1] - 1;
            int val = mapping[v][2];
            tt[par] = val;
        }

        String str = "";
        for( int val : tt )
            str += String.valueOf(val) + "." ;

        return str;
    }


    /**
     * Transfer key to conventional CT test case representation
     * "3.1.0.21.9.6.3.4.1.1.2.1.1.1" -> [2, 0, 3, 4, 3, 2, 2, 0, 0]
     * @param k Key in .tsl file
     * @return conventional test case
     */
    public int[] getTestCase( String k ) {
        int[] test = new int[parameter];
        String[] key = k.trim().split(Pattern.quote("."));
        for(int i = 0; i< baseKey.length ; i++ ) {
            // for each -1 position
            if( baseKey[i] == -1 ) {
                // find the index of parameter-value
                int keyValue = Integer.valueOf(key[i]);
                int rValue = -1;
                for( int[] row : mapping ) {
                    // parameter in mapping starts from 1
                    if( row[1] == i+1 && row[2] == keyValue ) {
                        // parameter-value in mapping starts from 0
                        // but in relations starts from 1
                        rValue = row[0] + 1;
                        break;
                    }
                }
                // find the parameter and value, respectively
                int par = -1, val = -1 ;
                for( int p=0 ; p<relations.length ; p++ ) {
                    for( int v=0 ; v<relations[p].length ; v++ ) {
                        if( relations[p][v] == rValue ) {
                            par = p ;
                            val = v ;
                            break;
                        }
                    }
                }
                // assign parameter and its value
                test[par] = val ;
            }
        }
        return test;
    }

    /**
     * Calculate the number of unique faults that can be detected
     * by the given test suite.
     * @param tests test suite
     * @param dev whether to output information
     * @return the number of detected faults
     */
    public int evaluateFaultNumber( final int[][] tests, boolean dev ) {
        // all faults that can be detected
        // version.[n].[k] indicates the k-th faulty variant of version n can be detected
        HashSet<String> detected = new HashSet<>();
        for( int[] t : tests ) {
            String k = getKey(t);
            if( dev ) System.out.print(Arrays.toString(t) + " Key = " + k);
            // iterative each version
            for( SIRVersion sv : allVersions ) {
                // iterative each faultySuite
                for( SIRTestCase each : sv.faultySuite ) {
                    if( k.equals(each.Key) ) {
                        if( dev ) System.out.print(" -> " + sv.identifier + " : " + each.faulty);
                        // each faults can be detected
                        for( Integer f : each.faulty ) {
                            String str = sv.identifier + "." + f ;
                            detected.add(str);
                        }
                    }
                }
            }
            if( dev ) System.out.print("\n");
        }
        if( dev ) System.out.println("detected faults = " + detected);
        return detected.size();
    }

    /**
     * Checking a given test suite with all wholeSuite.
     * Determine whether each test case exists in any wholeSuite
     * and whether each SIRTestCase is constraint satisfied.
     * @param tests conventional test suite (e.g. covering array)
     */
    public void checking( final int[][] tests ) {
        // part 1
        int num = 0;
        for (int[] t : tests) {
            String k = getKey(t);
            boolean ex = false;
            for (SIRVersion sv : allVersions) {
                for (SIRTestCase each : sv.wholeSuite) {
                    if (k.equals(each.Key)) {
                        ex = true;
                        break;
                    }
                }
            }
            if (!ex) {
                num++;
                System.out.println("does not exist: " + Arrays.toString(t) + " key = " + k);
            }
        }
        System.out.println(num + " test cases are not in wholeSuite");
        // part 2
        num = 0 ;
        SUT sut = getFullModel(2);
        for (SIRVersion sv : allVersions) {
            for (SIRTestCase each : sv.wholeSuite ) {
                int[] t = getTestCase(each.Key);
                if (!sut.isValid(t)) {
                    num++;
                    System.out.println("invalid " + Arrays.toString(t) + " Key = " + each.Key);
                }
            }
        }
        System.out.println(num + " SIRTestCase are invalid");
    }



    // get full model
    public SUT getFullModel( int tway ) {
        SUT sut = new SUT(parameter, value, tway);
        sut.setConstraint(constraint, 2);
        return sut;
    }

    // get sub model
    // p_ratio : ratio of parameter
    // c_ratio : ratio of constraint
    public SUT getSubModel( double p_ratio , double c_ratio, int tway ) {

        // use the first (n * p_ratio) parameters
        int par = (int)Math.round(p_ratio * (double)parameter);
        int[] val = new int[par];
        System.arraycopy(value, 0, val, 0, val.length);
        SUT sut = new SUT(par, val, tway);

        // find the first (k * c_ratio) constraints
        // correspond to the selected parameters
        ArrayList<int[]> con = new ArrayList<>();
        for( int[] c : constraint ) {
            int maxConstraint = -c[c.length-1];
            int maxParameter = relations[par-1][value[par-1]-1];
            if( maxConstraint <= maxParameter )
                con.add(c.clone());
        }

        int con_size = (int)Math.round(c_ratio * (double)con.size());
        for( int k=con.size() ; k>con_size ; k--)
            con.remove(k-1);

        sut.setConstraint(con, 2);

        return sut ;
    }

    /**
     * Print basic data of SIRCase
     */
    public void showCase() {
        System.out.print("parameter = " + parameter);
        System.out.print(", value = " + Arrays.toString(value) + "\n");
        System.out.println("Default Key = " + Arrays.toString(defaultKey));
        System.out.println("# forbidden constraint = " + constraint.length);

        /* the number of constraints for each proportion of parameters
        double[] RATE = {0.2, 0.4, 0.6, 0.8, 1.0};
        int[] par = new int[RATE.length];
        int[] con = new int[RATE.length];
        for(int k = 0; k< RATE.length ; k++ ) {
            par[k] = (int)Math.round(RATE[k] * (double)parameter);
            for( int[] c : constraint ) {
                int maxConstraint = -c[c.length-1];
                int maxParameter = relations[par[k]-1][value[par[k]-1]-1];
                if( maxConstraint <= maxParameter )
                    con[k]++;
            }
        }
        System.out.println("par = " + Arrays.toString(par));
        System.out.println("con = " + Arrays.toString(con));
        */

    }




    // generate N random randomFaults based on fault profile
    public ArrayList<int[]> randomFaults;   // test case representation
    private static double[] PROFILE = new double[]{0.4, 0.3, 0.1, 0.1, 0.05, 0.05};
    public void initRandomFaults(int N ) {
        randomFaults = new ArrayList<>();

        SUT s = getFullModel(2);
        RandomTool rand = new RandomTool();

        // from 1-way to 6-way
        for( int t=1 ; t<=6 ; t++ ) {
            int num = (int)(N * PROFILE[t-1]);
            for( int l=0 ; l<num ; l++ ) {
                int[] sch = rand.Schema(t, s.parameter, s.value);
                while ( !s.isValid(sch) )
                    sch = rand.Schema(t, s.parameter, s.value);
                randomFaults.add(sch);
            }
        }
    }

    // determine how many randomFaults can not be detected
    // by a sub model with the first n parameters
    private int faultUndetected( int n ) {
        int num = 0;
        for (int[] f : randomFaults) {
            // check each position of fault f after n,
            // if there exists a value that is not either 0 or -1,
            // then this fault cannot be detected
            for (int l = n; l < f.length; l++) {
                if (f[l] != -1 && f[l] != 0) {
                    num++;
                    break;
                }
            }
        }
        return num;
    }


}
