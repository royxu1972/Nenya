package Generation;

import Model.SUTSequence;
import Model.TestSuiteSequence;

import java.util.*;

/**
 *  A greedy algorithm to construct Sequence Covering Array
 *
 *  Reference
 *  [1] D.R. Kuhn, J.M. Higdon, J.F. Lawrence, R.N. Kacker, and Y. Lei,
 *      Combinatorial Methods for Event Sequence Testing, International
 *      Conference on Software Testing, Verification and Validation,
 *      pp.601–609. 2012
 */
public class SCA {

    private SUTSequence sut ;
    private ArrayList<Integer[]> coveringArray ;
    private Random random ;

    public SCA() {
        sut = null ;
        coveringArray = new ArrayList<>();
        random = new Random();
    }

    /*
     *  The main framework:
     *  Generate N random candidates at each iteration, then add the one
     *  that covers the most uncovered combinations and its inverted
     *  sequence into the coveringArray.
     */
    public void generation(TestSuiteSequence ss ) {
        generation(ss, 1000);
    }
    public void generation(TestSuiteSequence ss, int N ) {
        this.sut = ss.system;
        sut.initialization();

        coveringArray.clear();
        while (sut.combUncovered != 0) {
            // the best result
            Integer[] best = new Integer[sut.event];
            int covBest = Integer.MIN_VALUE ;

            int repeat = 0 ;
            do {
                Integer[] temp = randomPermutation();
                int covTemp = sut.FitnessValue(temp, 0);

                // if the best fitness is achieved
                if( covTemp == sut.testCaseCoverMax ) {
                    System.arraycopy(temp, 0, best, 0, sut.event);
                    break ;
                }
                // otherwise
                if( covTemp > covBest ) {
                    System.arraycopy(temp, 0, best, 0, sut.event);
                    covBest = covTemp ;
                }
                repeat++ ;
            } while (repeat < N);

            // add the best candidate into coveringArray,
            coveringArray.add(best);
            sut.FitnessValue(best, 1);

            // add the inverted sequence of the best candidate into coveringArray
            Integer[] bestR = reverse(best);
            coveringArray.add(bestR);
            sut.FitnessValue(bestR, 1);

        } // end while

        // save the final covering array in test.tests[][]
        ss.tests = new int[coveringArray.size()][sut.event];
        int x = 0;
        for (Integer[] t : coveringArray) {
            for( int y=0 ; y<t.length ; y++ )
                ss.tests[x][y] = t[y];
            x++;
        }
    }

    /*
     *  Generate a random permutation of events
     */
    private Integer[] randomPermutation() {
        List<Integer> permutation = new ArrayList<>();
        for( int k=0 ; k<sut.event ; k++ ) {
            permutation.add(k);
        }
        Collections.shuffle(permutation);
        return permutation.toArray(new Integer[0]);
    }

    /*
     *  reverse a sequence
     */
    private Integer[] reverse(Integer[] seq ) {
        Integer[] copy = seq.clone();
        Collections.reverse(Arrays.asList(copy));
        return copy;
    }


}
