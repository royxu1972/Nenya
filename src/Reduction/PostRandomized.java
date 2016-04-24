package Reduction;

import Basic.ALG;
import Basic.SUT;
import Basic.TestSuite;

import java.util.*;

/**
 *  Post-randomized Covering Array Reduction
 *
 *  Xiaohua Li, Zhao Dong, Huayao Wu, Changhai Nie, Kai-Yuan Cai,
 *  Refining a Randomized Post-optimization Method for Covering Arrays,
 *  3rd International Workshop on Combinatorial Testing (IWCT), pp.143-152, 2014
 */
public class PostRandomized {

    private TestSuite test ;
    private SUT sut ;
    private Random random ;
    private int[][] coveringArray ;

    public PostRandomized( TestSuite ts ) {
        random = new Random();

        // copy ts.tests to this.coveringArray
        test = ts ;
        sut = ts.system ;
        coveringArray = new int[ts.tests.length][sut.parameter];
        for( int i=0 ; i<ts.tests.length ; i++ )
            System.arraycopy(ts.tests[i], 0, coveringArray[i], 0, ts.tests[i].length);
    }

    /*
     *	Main algorithm framework
     *
     *  INPUT:
     *  mode = 1, safe mode, check t-way coverage at each iteration
     *  mode = 0, fast mode, do not run checking method
	 */
    public void execution( int max_iteration, int mode ) {

        sut.initialization();
        int iteration = 0 ;

        while (iteration < max_iteration) {
            randomReplacement(identifyRelativeFreePosition());
            eliminateReplacement(identifyFreePosition());

            if( mode == 1 ) {
                double cov = test.getCombinationCoverage(sut.t_way, coveringArray);
                if( cov != 1.0 ) {
                    System.err.println("ERROR in iteration " + iteration + " with coverage " + cov);
                    return ;
                }
            }
            iteration += 1 ;
        }

        // update test.tests
        test.tests = coveringArray.clone();

    }


    /*
     *  Find the set of relatively free positions
     *
     *  RETURN:
     *  An array where relatively free position is represented as -1
     *
     *  ALGORITHM:
     *  Initialize an array with all *, then iterate each of C(p,t) parameter combinations.
     *  For each t-way value combination, if it appears only once, then assign
     *  its original values to the array. Finally, the remaining * represent the set
     *  of relatively free positions.
     */
    public int[][] identifyRelativeFreePosition() {
        int row = coveringArray.length ;
        int column = sut.parameter ;

        // initialize a covering array with all * (i.e. -1)
        int[][] tempCA = new int[row][column];
        for( int i=0 ; i<row ; i++ )
            for( int j=0 ; j<column ; j++ )
                tempCA[i][j] = -1 ;

        // for each t-way parameter combination
        int[][] p_Comb = ALG.cal_allC(column, sut.t_way);
        for( int[] pos : p_Comb ) {

            // the number of all possible value combinations
            int num = ALG.cal_combineValue(pos, sut.value);
            // the number of appearances of each value combinations
            int[] cover = new int[num] ;
            // the index of the first appearances
            int[] cover_pos = new int[num] ;
            for( int k=0 ; k<num ; k++ ) {
                cover[k] = 0 ;
                cover_pos[k] = -1 ;
            }

            // for each row of tempCA
            int[] sch = new int[sut.t_way];
            for( int j=0 ; j<row ; j++ ) {
                for( int k=0 ; k<sut.t_way ; k++ )
                    sch[k] = coveringArray[j][pos[k]];

                // compute the index value of pos-sch
                int index = ALG.cal_val2num(pos, sch, sut.t_way, sut.value);

                cover[index]++ ;
                if( cover[index] == 1 )
                    cover_pos[index] = j ;  // the row where this combination appears firstly
            }

            // find the combination whose cover[] = 1
            for( int k=0 ; k<num ; k++ ) {
                // this combination totally appears once
                // then assign it by original values
                if( cover[k] == 1 ) {
                    for( int n=0 ; n<sut.t_way ; n++ ) {
                        if( tempCA[cover_pos[k]][pos[n]] == -1 ) {
                            tempCA[cover_pos[k]][pos[n]] = coveringArray[cover_pos[k]][pos[n]] ;
                        }
                    }
                }
            }
        }
        return tempCA ;
    }

    /*
     *  Reorder this.coveringArray based on the number of * in relative
     *  and then random replace some free values
     */
    public void randomReplacement(final int[][] relative) {
        int row = relative.length ;
        int column = sut.parameter ;

        // the number of * in each row of relative
        int[] count = numFreePosition(relative);
        int[] count1 = count.clone();

        // reorder
        ALG.sortArray2D(count, relative);
        ALG.sortArray2D(count1, coveringArray);

        // find the first row which has *
        int begin_star = 0 ;
        for( ; begin_star < count.length && count[begin_star] == 0 ; begin_star++ ) ;

        // as one row can be modified only once,
        // so we need to remember the indexes of rows which have been modified
        Set<Integer> modifiedRow = new HashSet<>();

        // copy relative, the modification will be made on tempRelative
        int[][] tempRelative = relative.clone();

        //print("ordered free set", relative);
        //print("ordered covering array", coveringArray);


        // if the last row has some non-free (fixed) value combinations
        // then find a previous free position and replace its value
        if( count[row-1] != column && count[row-1] != 0 ) {

            // for each t-way parameter combination of the last row
            int[][] pComb = ALG.cal_allC(sut.parameter, sut.t_way);
            for( int[] p_row : pComb ) {

                // determine whether it is a fixed combination
                boolean fixed = true ;
                int[] v_row = new int[sut.t_way];
                for( int k=0 ; k<p_row.length ; k++ ) {
                    v_row[k] = tempRelative[row-1][p_row[k]];
                    if( v_row[k] == -1 ) {
                        fixed = false ;
                        break ;
                    }
                }

                if( fixed ) {
                    for( int j=begin_star ; j<row ; j++ ) {
                        // determine whether a previous value combination can be replaced
                        fixed = true ;
                        for( int k=0 ; k<p_row.length ; k++ ) {
                            if( tempRelative[j][p_row[k]] != -1 && tempRelative[j][p_row[k]] != v_row[k] )
                                fixed = false ;
                        }

                        // replace *
                        if( fixed ) {
                            // the position can be replaced if and only if
                            // its value is different with those in modifiedRow
                            boolean replace = true ;
                            for( int st : modifiedRow ) {
                                for( int k : p_row ) {
                                    // two exceptions:
                                    // 1) current and modified rows have the same original value
                                    // 2) current row is the modified row
                                    if( tempRelative[j][k] != -1 && tempRelative[st][k] != -1
                                        && coveringArray[j][k] == coveringArray[st][k] || j == st )
                                        continue ;

                                    //System.out.println("(" + j + " " + k + ") (" + st + " " + k + ")");
                                    //System.out.println(coveringArray[j][k] + " " + coveringArray[st][k]);
                                    if( coveringArray[j][k] == coveringArray[st][k] ) {
                                        replace = false;
                                        break;
                                    }
                                }
                            }

                            if( replace ) {
                                for( int k=0 ; k<p_row.length ; k++ )
                                    tempRelative[j][p_row[k]] = v_row[k];
                                modifiedRow.add(j);
                                break ;
                            }
                        }
                    }
                } // end if fixed
            }
        }  // end if

        // update this.coveringArray accordingly
        for( int i=0 ; i<row ; i++ ) {
            for( int j=0 ; j<column ; j++ ) {
                if( tempRelative[i][j] != -1 )
                    coveringArray[i][j] = tempRelative[i][j] ;
            }
        }

        //print("replaced covering array", coveringArray);

    }

    /*
     *  Find the set of free positions (can be modified simultaneously)
     *
     *  RETURN:
     *  An array where free position is represented as -1
     *
     *  ALGORITHM:
     *  Initialize an array with all *, then iterate each of C(p,t) parameter combinations.
     *  For each t-way value combination, assign original values at its first appearance.
     *  Finally, the remaining * represent the set of free positions.
     */
    public int[][] identifyFreePosition() {
        int row = coveringArray.length ;
        int column = sut.parameter ;

        // initialize a covering array with all * (i.e. -1)
        int[][] tempCA = new int[row][column];
        for( int i=0 ; i<row ; i++ )
            for( int j=0 ; j<column ; j++ )
                tempCA[i][j] = -1 ;

        // for each t-way parameter combination
        int[][] p_Comb = ALG.cal_allC(column, sut.t_way);
        for( int[] pos : p_Comb ) {

            // the number of all possible value combinations
            int num = ALG.cal_combineValue(pos, sut.value);
            // the appearances of each value combinations
            int[] cover = new int[num] ;
            for( int k=0 ; k<num ; k++ )
                cover[k] = 0 ;

            // for each row of tempCA
            int[] sch = new int[sut.t_way];
            for( int j=0 ; j<row ; j++ ) {
                for( int k=0 ; k<sut.t_way ; k++ )
                    sch[k] = coveringArray[j][pos[k]];

                // compute the index value of pos-sch
                int index = ALG.cal_val2num(pos, sch, sut.t_way, sut.value);

                // the first appearance will be set to original values
                if( cover[index] == 0 ) {
                    cover[index] = 1 ;
                    for( int c=0 ; c<sut.t_way ; c++ ) {
                        if( tempCA[j][pos[c]] == -1 )
                            tempCA[j][pos[c]] = coveringArray[j][pos[c]];
                    }
                }
            }
        }
        return tempCA ;
    }


    /*
     *  Eliminate all free rows (i.e. full of free positions)
     */
    public void eliminateReplacement(final int[][] free) {
        int row = coveringArray.length ;
        int column = sut.parameter ;

        // compute the number of * in free
        int[] count = numFreePosition(free);
        // the indexes of rows that will be deleted
        Set<Integer> deleted = new HashSet<>();

        for( int k=0 ; k<row ; k++ ) {
            if( count[k] == sut.parameter ) {
                deleted.add(k);
            }
        }

        // modify this.coveringArray
        int[][] tempCA = new int[row-deleted.size()][column];
        for( int i=0, pi=0 ; i<row ; i++ ) {
            if( !deleted.contains(i) )
            {
                for( int j=0 ; j<column ; j++ ) {
                    if( free[i][j] != -1 ) {
                        tempCA[pi][j] = coveringArray[i][j];
                    }
                    else {
                        // replace the remaining * positions randomly
                        tempCA[pi][j] = random.nextInt(sut.value[j]);
                    }
                }
                pi = pi + 1 ;
            }
        }
        coveringArray = tempCA ;
    }


    /*
     *  Compute the number of * in each row of an array
     */
    private int[] numFreePosition( final int[][] A ) {
        int[] count = new int[A.length] ;
        for( int i=0 ; i<A.length ; i++ ) {
            int n = 0 ;
            for( int j=0 ; j<A[i].length ; j++ )
                if( A[i][j] == -1 )
                    n++ ;
            count[i] = n ;
        }
        return count ;
    }

    /*
     *  Print a 2D array to console with its name
     */
    public void print( String name, int[][] CA ) {
        System.out.println("------ " + name + " -------");
        for( int[] row : CA ) {
            for( int k=0 ; k<row.length ; k++ ) {
                if( row[k] == -1 )
                    System.out.print("* ");
                else
                    System.out.print(row[k] + " ");
            }
            System.out.print("\n");
        }
    }

}

