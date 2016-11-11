package evolutionary;

import EA.GA.Common.General;
import EA.GA.Common.GeneralCrossover;
import EA.GA.Common.GeneralMutation;
import EA.GA.Common.UniformSelector;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class TestGeneral {

    private General g1 ;
    private General g2 ;

    @Before
    public void init() {
        int[] range = {5, 5, 5, 5, 5, 5};
        g1 = new General(range.length, range);
        g2 = new General(range.length, range);
        System.out.println("a = " + Arrays.toString(g1.solution));
        System.out.println("b = " + Arrays.toString(g2.solution));
    }

    @Test
    public void selectionOperator() {
        ArrayList<General> pop = new ArrayList<>();
        for( int k=0 ; k<10 ; k++ ) {
            General tp = new General(5, new int[]{5,5,5,5,5});
            tp.fitness = new Random().nextInt(5);
            pop.add(tp);
            System.out.println("# " + k + ": fit = " + tp.fitness);
        }

        int pos = new UniformSelector<General>(UniformSelector.operator.Roulette).selection(pop);
        System.out.println("select " + pos);

    }

    @Test
    public void crossoverOperator() {
        List<General> children = new GeneralCrossover().crossover(g1, g2, 1.0);
        System.out.println("g1' = " + Arrays.toString(children.get(0).solution));
        System.out.println("g2' = " + Arrays.toString(children.get(1).solution));
    }

    @Test
    public void mutationOperator() {
        new GeneralMutation().mutation(g1, 1.0);
        System.out.println("mutate(g1) = " + Arrays.toString(g1.solution));
    }
}
