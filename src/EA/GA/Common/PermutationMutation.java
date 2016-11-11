package EA.GA.Common;

import java.util.Random;

public class PermutationMutation implements OperatorMutation<Permutation> {

    private enum operator {Exchange, Displacement}
    private Random random ;
    private operator mode ;

    public PermutationMutation() {
        random = new Random();
        mode = operator.Exchange;
    }
    public PermutationMutation( operator op ) {
        random = new Random();
        mode = op;
    }

    @Override
    public void mutation(Permutation a, double PRO) {
        if( random.nextDouble() < PRO ) {
            if (mode == operator.Exchange)
                exchange(a);
            if (mode == operator.Displacement)
                displacement(a);
        }
    }

    /*
     *  Exchange Mutation (EM) / Swap Mutation
     *  Select two positions randomly and then exchange them.
     */
    private void exchange( Permutation a ) {
        int pos1 = random.nextInt(a.solution.length);
        int pos2 = random.nextInt(a.solution.length);

        if( pos1 != pos2 ) {
            int tp = a.solution[pos1] ;
            a.solution[pos1] = a.solution[pos2] ;
            a.solution[pos2] = tp ;
        }
    }

    /*
     *  Displacement Mutation (DM)
     *  Example
     *  0 1 2 3 4 5 6  =>  0 1 5 6  =>  0 1 5 2 3 4 6
     *      |   |              |
     *   cut=2,len=2        put=2
     */
    private void displacement( Permutation a ) {
        int length = a.solution.length ;

        int cut = random.nextInt(length);
        int len = cut == length - 1 ? 0 : random.nextInt(length - cut - 1);
        int put = random.nextInt(length - len - 1);

        // cut
        int[] cut_ary = new int[len + 1];
        for (int m = cut, n = 0; m <= cut + len && n <= len; m++, n++)
            cut_ary[n] = a.solution[m];

        // remain
        int[] rem_ary = new int[length - len - 1];
        for (int m = 0, n = 0; m < length && n < length - len - 1; m++) {
            if( !(cut <= m && m <= cut + len) ) {
                rem_ary[n] = a.solution[m];
                n++;
            }
        }

        // new chromosome
        int i = 0;  // c
        int k = 0;  // remain
        for (; k <= put; k++, i++)
            a.solution[i] = rem_ary[k];
        for (int j = 0; j < cut_ary.length; j++, i++)
            a.solution[i] = cut_ary[j];
        for (; k < length - len - 1; k++, i++)
            a.solution[i] = rem_ary[k];
    }


}
