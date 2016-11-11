package EA.GA.Common;

import java.util.Random;

public class GeneralMutation implements OperatorMutation<General> {

    private Random random = new Random();

    @Override
    public void mutation(General a, double PRO) {
        if( random.nextDouble() < PRO ) {
            onePoint(a);
        }
    }

    /*
     *  One point mutation
     */
    private void onePoint(General a) {
        int pos = random.nextInt(a.solution.length);
        int val = random.nextInt(a.range[pos]);
        while ( val == a.solution[pos] )
            val = random.nextInt(a.range[pos]);
        a.solution[pos] = val ;
    }

}
