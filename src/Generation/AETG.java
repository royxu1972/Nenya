package Generation;

import Basic.ALG;
import Basic.SUT;
import Basic.TestSuite;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 *  An AETG-like covering array generator
 *  with support for constraint solving (beta version)
 */
public class AETG {

    /**
     *  index-number pair
     *
     *  particular, index represents a candidate parameter or value, and
     *  number represents the number of uncovered combinations that involves
     *  this parameter or value
     */
     private class Pair implements Comparable<Pair> {
        public int index ;
        public int number ;

        public Pair( int i, int n ) {
            index = i ;
            number = n ;
        }

        /*
         *  compareTo should return < 0 if this is supposed to be
         *  less than other, > 0 if this is supposed to be greater than
         *  other and 0 if they are supposed to be equal
         *
         *  do a descending order via collection.sort (less to greater),
         *  and sorting is only based on the value of number
         */
        @Override
        public int compareTo(Pair B) {
            if( this.number < B.number )
                return 1 ;
            else if( this.number == B.number )
                return 0 ;
            else
                return -1 ;
        }

        @Override
        public String toString() {
            return String.valueOf(index);
        }
    }

    public SUT sut ;
    private ArrayList<int[]> coveringArray ;
    private Random random ;

    // the number of occurrence of each parameter value in all uncovered combinations
    private int[][] firstWeight ;

    public AETG() {
        sut = null ;
        coveringArray = new ArrayList<>();
        random = new Random();
    }

    /*
     * Get the size of current covering array.
     */
    public int getSize() {
        return coveringArray.size();
    }

    /*
     *  Write current covering aArray to file.
     */
    public void writeCoveringArray( String file ) throws IOException {
        FileWriter fileWriter = new FileWriter(file) ;
        String str = "parameter " + String.valueOf(sut.parameter) + "\n" ;
        str += "value " ;
        for( int k=0 ; k<sut.parameter ; k++ )
            str += (String.valueOf(sut.value[k]) + " ") ;
        str += "\n" ;

        str += "begin\n" ;
        for( int[] tp : coveringArray ) {
            for( int m=0 ; m<sut.parameter ; m++ )
                str += (tp[m] + " ") ;
            str += "\n" ;
        }
        str += "end\n" ;
        fileWriter.write(str);
        fileWriter.close();
    }

    /*
     *  The main AETG generation framework
     *  N = the number of candidates (default = 10)
     */
    public void Generation( TestSuite test ) {
        Generation( test, 10 );
    }
    public void Generation( TestSuite test, int N ) {
        this.sut = test.system ;
        sut.initialization();
        initializeFirstWeight();

        coveringArray.clear();
        while( sut.getSCount() != 0 ) {

            int[] best = generateTestCase();
            int covBest = sut.FitnessValue(best, 0) ;

            // generate another N-1 candidates
            for( int x=1 ; x<N ; x++ ) {
                int[] temp = generateTestCase();
                int covTemp = sut.FitnessValue(temp, 0);

                // the best fitness
                if( covTemp == sut.getTestCaseCoverMax() ) {
                    System.arraycopy(temp, 0, best, 0, sut.parameter);
                    break ;
                }
                // otherwise
                if( covTemp > covBest ) {
                    System.arraycopy(temp, 0, best, 0, sut.parameter);
                    covBest = covTemp ;
                }
            }

            // add the best candidate into CoverArray,
            // and then update uncovered combinations and firstWeight
            coveringArray.add(best);
            updateTestCaseAndFirstWeight(best);

            System.out.println(Arrays.toString(best) + ", uncovered = " + sut.getSCount() );

        } // end while

        // save the final covering array in test.tests[][]
        // and set default testing order and default execution cost
        test.tests = new int[coveringArray.size()][sut.parameter] ;
        test.order = new int[coveringArray.size()] ;
        test.executionCost = new double[coveringArray.size()] ;
        int x = 0 ;
        for( int[] t : coveringArray ) {
            System.arraycopy(t, 0, test.tests[x], 0, sut.parameter);
            test.order[x] = x ;             // set default order
            test.executionCost[x] = 0.0 ;   // set default execution cost = 0.0
            x++ ;
        }
    }

    /*
     *  Generate a new test case greedily.
     */
    private int[] generateTestCase() {
        int[] tc = new int[sut.parameter] ;
        for( int k=0 ; k<sut.parameter ; k++ )
            tc[k] = -1 ;

        // select the first parameter and corresponding value
        int par = selectFirstParameter();
        int val = selectFirstValue(par);

        // assign
        tc[par] = val ;
        //System.out.println("first par " + par + ", val " + val);

        // randomize a permutation of other parameters
        List<Integer> permutation = new ArrayList<>();
        for( int k=0 ; k<sut.parameter ; k++ ) {
            if (k != par)
                permutation.add(k);
        }
        java.util.Collections.shuffle(permutation);

        // for each of the remaining parameters
        List<Integer> conflicted = new ArrayList<>();
        for( int p : permutation ) {
            // find the best value, i.e. the value involved
            // in most uncovered t-way combinations
            int v = selectValue(tc, p);

            // a simple constraint solving is applied here, so we can make sure
            // that no conflict parameter-value will be selected and assigned
            if( isConstraintSatisfiable(tc, p, v) ) {
                tc[p] = v ;
            }
            else {
                // try basic selectValue() method 9 more times
                boolean success = false ;
                for( int k=0 ; k<9 ; k++ ) {
                    v = selectValue(tc, p);
                    if( isConstraintSatisfiable(tc, p, v) ) {
                        success = true ;
                        break ;
                    }
                }
                // if success, then assign value
                if( success )
                    tc[p] = v;
                // if not, consider this conflicted parameter later
                else
                    conflicted.add(p);
            }
        }

        // deal with the remaining conflicted parameters
        // by assigning random valid value to it
        while ( conflicted.size() != 0 ) {
            int p = conflicted.get(0) ;
            int v = random.nextInt(sut.value[p]);
            while( !isConstraintSatisfiable(tc, p, v) ) {
                v = random.nextInt(sut.value[p]);
            }
            tc[p] = v ;
            conflicted.remove(0);
        }

        return tc;
    }

    /*
     *  Determine a new parameter-value assignment is constraint satisfiable or not.
     */
    private boolean isConstraintSatisfiable( final int[] test, int p, int v ) {
        if( sut.hardConstraint == null )
            return true ;

        int[] test_temp = new int[test.length];
        System.arraycopy(test, 0, test_temp, 0, test.length);
        test_temp[p] = v ;
        return sut.isValid(test_temp);
    }

    /*
     *  Initialize firstWeight, which will be invoked before generation.
     *  Each row represents a single parameter, column 0 is the total number,
     *  and each of other columns represent a single value.
     */
    private void initializeFirstWeight() {
        firstWeight = new int[sut.parameter][] ;
        for( int k=0 ; k<sut.parameter ; k++ )
            firstWeight[k] = new int[sut.value[k]+1] ;

        // for each t-way combination
        // which is represented by par_row[] and val_row[]
        int[][] par = ALG.cal_allC(sut.parameter, sut.t_way);
        for( int[] par_row : par ) {
            int[][] val = ALG.cal_allV(par_row, sut.t_way, sut.value);
            for (int[] val_row : val) {
                // determine each combination is valid or not
                if( !sut.Covered(par_row, val_row, 0) ) {
                    for( int i=0 ; i<sut.t_way ; i++ ) {
                        firstWeight[par_row[i]][val_row[i]+1] += 1 ;
                        firstWeight[par_row[i]][0] += 1 ;
                    }
                }
            }
        }
    }

    public void printFirstWeight() {
        for( int[] row : firstWeight ) {
            System.out.println(Arrays.toString(row));
        }
    }

    /*
     *  Update uncovered combinations and firstWeight according to
     *  newly generated test case
     */
    public void updateTestCaseAndFirstWeight( final int[] test ) {
        // iterate all t-way parameter value combinations
        int[][] data = ALG.cal_allC(sut.parameter, sut.t_way);
        for( int i=0 ; i<data.length ; i++ ) {
            // get position and schema
            int[] position = data[i];
            int[] schema = new int[sut.t_way];
            for (int k = 0; k < sut.t_way; k++)
                schema[k] = test[position[k]];

            // if it is covered
            if (!sut.Covered(position, schema, 1)) {
                for(int k=0; k<sut.t_way; k++) {
                    int p = position[k];
                    int v = schema[k];
                    firstWeight[p][v+1] -= 1;
                    firstWeight[p][0] -= 1;
                }
            }
        }
    }

    /*
     *  Sorting pair list firstly and then apply random tie-breaking.
     *  It will return one of the maximum indexes of Pair List
     */
    private int sortAndBreaking( List<Pair> list ) {
        // sort
        Collections.sort(list);
        int max = list.get(0).number ;

        // filter for tie-breaking
        List<Pair> filtered = list
                .stream()
                .filter(p -> p.number == max)
                .collect(Collectors.toList());

        int r = random.nextInt(filtered.size());
        return filtered.get(r).index ;
    }

    /*
     *  Select the first parameter, namely arg_max( firstWeight[k][0] )
     */
    private int selectFirstParameter() {
        List<Pair> ps = new ArrayList<>();
        for( int i=0 ; i<sut.parameter ; i++ )
            ps.add(new Pair(i, firstWeight[i][0]));
        return sortAndBreaking(ps);
    }

    /*
     *  Select the best value of the first parameter
     */
    private int selectFirstValue( int par ) {
        List<Pair> vs = new ArrayList<>();
        for( int i=0 ; i<sut.value[par] ; i++ )
            vs.add(new Pair(i, firstWeight[par][i+1]));
        return sortAndBreaking(vs);
    }

    /*
     *  Select the best value of a given parameter
     */
    private int selectValue( final int[] test, int par  ) {
        List<Pair> vs = new ArrayList<>();
        for( int i=0 ; i<sut.value[par] ; i++ ) {
            int num = coveredSchemaNumber(test, par, i);
            vs.add(new Pair(i, num));
        }
        return sortAndBreaking(vs);
    }

    /*
     *  Given a parameter and its corresponding value, compute the
     *  number of uncovered combinations that can be covered by
     *  assigning this parameter value.
     *
     * INPUT: a parameter-tuple test[] with count fixed values
     * OUTPUT: the number of uncovered combinations if setting test[par] = val
     *
     * EXAMPLE:
     * CA(2,4,3), Test Suite = {(0,0,0,0), {1,1,1,1}}
     * given test = (-, -, 0, -)
     * if t = 2, then coveredSchemaNumber(test, 2, 2) = 1, which means that
     *           (-, 2, 0, -) can cover 1 new 2-way combinations
     * if t = 3, then coveredSchemaNumber(test, 2, 2) = 6, which means that
     *           (-, 2, 0, -) can cover 6 new 3-way combinations
     */
    public int coveredSchemaNumber(final int[] temp_test, int par , int val ) {
        int fit = 0 ;

        // copy the array
        int[] test = new int[sut.parameter];
        int count = 0 ;
        for( int i=0 ; i<sut.parameter ; i++ ) {
            test[i] = temp_test[i] ;
            if( temp_test[i] != -1 )
                count = count + 1 ;
        }

        // assign par-val to test, and update count
        test[par] = val;

        // condition 1: select r (= t - assigned - 1) parameters from unassigned ones
        // condition 2: select r (= t - 1) parameters from assigned ones
        //
        // 1 1 1 0   X        - - - - -
        // --------  -        ---------
        // assigned  par-val  unassigned

        // the number of parameters that are fixed or not fixed
        int assigned = count ;
        int unassigned = sut.parameter - count - 1;

        // the variable r
        int required ;

        // -----------------------------
        //         condition 1
        // -----------------------------
        // Currently count parameters are fixed, where assigned + 1 < t.
        // So we iterate all possible (t - count - 1)-way parameter value combinations
        // from unassigned ones to compute the total number of uncovered combinations
        // that can be covered by assigning par-val.
        // -----------------------------
        if( assigned + 1 < sut.t_way) {
            // required number of parameters to form a t-way combination
            required = sut.t_way - count - 1;

            // get the unassigned and candidate parameter vector
            int[] unassigned_vector = new int[unassigned];
            int[] candidate_par = new int[assigned+1];
            int[] candidate_val = new int[assigned+1];
            for (int i = 0, index = 0, un_index = 0; i < sut.parameter; i++) {
                if (test[i] == -1) {
                    unassigned_vector[un_index] = i;
                    un_index += 1;
                }
                else {
                    candidate_par[index] = i ;
                    candidate_val[index] = test[i] ;
                    index += 1 ;
                }
            }

            // for each possible r-way parameter combinations among unassigned_vector[]
            int[][] pComb = ALG.cal_allC(unassigned, required);
            for (int[] p : pComb) {
                int[] p_row = new int[required];
                for( int k=0 ; k<required ; k++ )
                    p_row[k] = unassigned_vector[p[k]];

                // for each possible r-way value combinations among p_row
                int[][] vComb = ALG.cal_allV(p_row, required, sut.value);
                for (int[] v_row : vComb) {

                    // construct a temp t-way combination
                    int[] position = new int[sut.t_way];
                    int[] schema = new int[sut.t_way];
                    ALG.insertPSArray(candidate_par, candidate_val, p_row, v_row, position, schema);
                    //System.out.println("check: " + Arrays.toString(position) + " - " + Arrays.toString(schema));

                    // determine whether this t-way combination is covered or not
                    if( !sut.Covered(position, schema, 0) )
                        fit++ ;
                }
            }
        }

        // -----------------------------
        //         condition 2
        // -----------------------------
        // Currently count parameters are fixed, where assigned + 1 >= t.
        // So we iterate all possible (t - 1) parameter value combinations
        // among all assigned ones to compute the total number of uncovered
        // combinations that can be covered by assigning par-val.
        // -----------------------------
        else {
            // required number of parameters to form a t-way combination
            required = sut.t_way - 1;

            // get the candidate (assigned) parameter vector
            int[] assigned_par = new int[assigned];
            int[] assigned_val = new int[assigned];
            for (int i = 0, index = 0; i < sut.parameter; i++) {
                if (test[i] != -1 && i != par ) {
                    assigned_par[index] = i ;
                    assigned_val[index] = test[i] ;
                    index += 1 ;
                }
            }

            int[] pp = new int[1];
            int[] vv = new int[1];
            pp[0] = par ;
            vv[0] = val ;

            // for each possible r-way parameter combinations among assigned_par[]
            int[][] pComb = ALG.cal_allC(assigned, required);
            for (int[] p : pComb) {
                int[] p_row = new int[required];
                int[] r_row = new int[required];
                for( int k=0 ; k<required ; k++ ) {
                    p_row[k] = assigned_par[p[k]];
                    r_row[k] = assigned_val[p[k]];
                }

                // construct a temp t-way combination
                int[] position = new int[sut.t_way];
                int[] schema = new int[sut.t_way];
                ALG.insertPSArray(p_row, r_row, pp, vv, position, schema);
                //System.out.println("check: " + Arrays.toString(position) + " - " + Arrays.toString(schema));

                // determine whether this t-way combination is covered or not
                if( !sut.Covered(position, schema, 0) )
                    fit++ ;
            }
        }

        return fit ;
    }
}
