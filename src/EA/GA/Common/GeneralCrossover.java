package EA.GA.Common;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GeneralCrossover implements OperatorCrossover<General> {

    private Random random = new Random();

    @Override
    public List<General> crossover(General a, General b, double PRO) {
        if (random.nextDouble() < PRO)
            return twoPointX(a, b);
        else {
            List<General> children = new ArrayList<>();
            children.add(a.clone());
            children.add(b.clone());
            return children;
        }
    }

    /*
     *  Simple two point crossover
     */
    private List<General> twoPointX(General a, General b) {
        int LEN = a.solution.length ;
        int cut1 = random.nextInt(LEN);
        int cut2 = cut1 + random.nextInt(LEN - cut1);

        // exchange elements between cut1 and cut2
        for( int i=0 ; i<LEN ; i++ ) {
            if( i >= cut1 && i <= cut2 ) {
                int temp = a.solution[i];
                a.solution[i] = b.solution[i];
                b.solution[i] = temp;
            }
        }

        List<General> children = new ArrayList<>();
        children.add(new General(a.solution, a.range));
        children.add(new General(b.solution, b.range));
        return children;
    }
}