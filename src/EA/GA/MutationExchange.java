package EA.GA;

import EA.Common.OperatorMutation;

import java.util.Random;

/**
 *  Exchange Mutation (EM)
 *  Select two positions randomly and then exchange them.
 */
public class MutationExchange implements OperatorMutation {

    @Override
    public String toString() {
        return "EM";
    }

    @Override
    public void mutation(int[] c, Random rand ) {
        int pos1 = rand.nextInt(c.length);
        int pos2 = rand.nextInt(c.length);

        if( pos1 != pos2 ) {
            int tp = c[pos1] ;
            c[pos1] = c[pos2] ;
            c[pos2] = tp ;
        }
    }
}
