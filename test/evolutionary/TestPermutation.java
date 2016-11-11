package evolutionary;

import EA.GA.Common.PermutationCrossover;
import EA.oldGA.Common.Displacement;
import EA.oldGA.Common.Exchange;
import EA.oldGA.NSSolution2D;
import EA.GA.Common.Permutation;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

public class TestPermutation {

    private Permutation p1 ;
    private Permutation p2 ;

    @Before
    public void init() {
        p1 = new Permutation(5);
        p1.fitness = 10 ;
        p2 = new Permutation(5);
        p2.fitness = 20 ;
        System.out.println("p1 = " + Arrays.toString(p1.solution) + ", " + p1.fitness);
        System.out.println("p2 = " + Arrays.toString(p2.solution) + ", " + p2.fitness);
    }

    @Test
    public void basic() {
        Permutation c = p1.clone();
        p1.solution[0] = -1 ;
        System.out.println("new c = " + Arrays.toString(c.solution) + ", " + c.fitness);
        System.out.println("new a = " + Arrays.toString(p1.solution) + ", " + p1.fitness);
    }

    @Test
    public void crossover() {
        List<Permutation> children = new PermutationCrossover().crossover(p1, p2, 1.0);
        System.out.println("p1' = " + Arrays.toString(p1.solution));
        System.out.println("p2' = " + Arrays.toString(p2.solution));
    }

    @Test
    public void mutationOperators() {
        int[] c = {1, 2, 3, 4, 5, 6, 7, 8};
        int[] tp ;
        Random r = new Random();

        // Exchange Mutation (EM)
        tp = c.clone();
        Exchange EM = new Exchange();
        EM.mutation(tp, r);
        System.out.println(Arrays.toString(tp));

        // Displacement Mutation (DM)
        tp = c.clone();
        Displacement DM = new Displacement();
        DM.mutation(tp, r);
        System.out.println(Arrays.toString(tp));
    }

    @Test
    public void sorting() {

        ArrayList<NSSolution2D> c = new ArrayList<>();
        int[] s = {1,2,3,4,5,6} ;
        c.add(new NSSolution2D(s, 0, 0, 1, 5));
        c.add(new NSSolution2D(s, 0, 0, 2, 2));
        c.add(new NSSolution2D(s, 0, 0, 2, 3));
        c.add(new NSSolution2D(s, 0, 0, 1, 4));
        c.add(new NSSolution2D(s, 0, 0, 3, 0));
        c.add(new NSSolution2D(s, 1, 1, 1, 4));

        Collections.sort(c, new NSSolution2D.allSort());
        c.stream().forEach(p->System.out.println(p));
    }

}
