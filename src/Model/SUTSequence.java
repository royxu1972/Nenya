package Model;

import Basic.ALG;
import Basic.BitArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 *  Software Under Test for Sequence Covering Array
 */
public class SUTSequence {

    public int event ;     // number of events
    public int t_way ;     // covering strength

    // combinations to be covered
    private BitArray comb;        // all combinations, each of which is represented by a bit
    private int nRow;             // C(event, t_way)
    private int nColumn;          // factorial(event)
    public int combAll;           // number of combinations to be covered
    public int combUncovered;     // number of uncovered combinations
    public int testCaseCoverMax; // the maximum number of combinations that can be covered by a test case

    // the permutations of t relations
    private HashMap<ArrayList<Integer>, Integer> permutation ;

    public SUTSequence( int event, int t_way ) {
        this.event = event ;
        this.t_way = t_way ;

        // C(n, t) * t!
        this.nRow = ALG.combine(event, t_way);
        this.nColumn = ALG.cal_factorial(t_way);
        // all possible permutations of any t events
        // e.g. permutation [1, 2, 0] of events [a(0), b(1), c(2)]
        // represents the 3-way event sequence (b, c, a)
        this.permutation = ALG.cal_permutation(t_way);
    }

    /*
     *  Initialize all combinations to be covered (comb)
     *  This should be invoked before generation or evaluation process
     */
    public void initialization() {
        comb = null;
        comb = new BitArray(nRow, nColumn);
        combAll = nRow * nColumn ;
        combUncovered = combAll ;
        testCaseCoverMax = nRow ;
    }

    /*
     *  Get the number of uncovered t-way sequences that are covered
     *  by a given test case, i.e. fitness function of one-test-at-a-time
     *  test generation framework.
     *
     *  INPUT PARAMETER:
     *  If FLAG = 0, only a number is returned.
     *  If FLAG = 1, comb and combUncovered will be updated accordingly.
     */
    public int FitnessValue(final Integer[] test, int FLAG) {
        int fitness = 0;
        // get all combinations of C(event, t_way)
        int[][] data = ALG.allC(event, t_way);

        for( int i=0 ; i<data.length; i++ ) {
            // get each position
            int[] ref = data[i];
            int[] position = new int[t_way];
            for( int k=0 ; k<t_way; k++ )
                position[k] = test[ref[k]];
            // if it is covered
            if( !Covered(position, FLAG) )
                fitness++;
        }
        return fitness ;
    }

    /*
     *  Determine whether a particular k-way sequence is covered or not.
     *
     *  INPUT PARAMETER:
     *  If FLAG = 0, comb and combUncovered will not be updated.
     *  If FLAG = 1, comb and combUncovered will be updated accordingly.
     */
    public boolean Covered( int[] seq, int FLAG ) {
        // sort
        int[] seqOrdered = seq.clone();
        ALG.sortArray(seqOrdered);

        // determine the index of row and column
        int row = ALG.combine2num(seqOrdered, event, t_way);
        int column ;

        // seq:        (5, 0, 3)
        // seqOrdered: (0, 3, 5)
        // mapping:     |  |  |
        //             (0, 1, 2)
        // perIndex:   (2, 0, 1)
        HashMap<Integer, Integer> mapping = new HashMap<>();
        for( int k=0 ; k<seqOrdered.length ; k++ )
            mapping.put(seqOrdered[k], k);

        Integer[] perIndex = new Integer[seq.length];
        for( int k=0 ; k<seq.length ; k++ )
            perIndex[k] = mapping.get(seq[k]);
        column = permutation.get(new ArrayList<>(Arrays.asList(perIndex)));

        // determiner whether seq[] is covered or not
        boolean r = comb.getElement(row, column) != 0 ;
        if( !r & FLAG == 1 ) {
            comb.setElement(row, column, 1);
            combUncovered--;
        }
        return r ;
    }

    /*
     *  print
     */
    public void printInfo() {
        System.out.println("event = " + event + ", t_way = " + t_way);
        System.out.print("permutations: ");
        for( int i = 0 ; i < nColumn ; i++ ) {
            int index = i ;
            permutation.forEach((k,v) -> {
                if( v == index )
                    System.out.print(k.toString() + " ");
            });
        }
        System.out.print("\ncomb:\n");
        for (int i = 0; i < nRow; i++) {
            int[] p = ALG.num2combine(i, event, t_way);
            System.out.print(Arrays.toString(p) + " : ");
            System.out.print(comb.getRow(i, nColumn));
            System.out.print("\n");
        }
    }

}
