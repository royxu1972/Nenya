package evolutionary;

import EA.Common.CrossoverPMX;
import EA.Common.MutationDisplacement;
import EA.Common.MutationExchange;
import EA.NSGA.NSSolution2D;
import org.junit.Test;

import java.util.*;

public class common {

    @Test
    public void crossoverOperators() {
        int[] a = {1, 2, 3, 4, 5, 6, 7, 8};
        int[] b = {3, 7, 5, 1, 6, 8, 2, 4};
        Random r = new Random();

        // PMX
        CrossoverPMX PMX = new CrossoverPMX();
        List<int[]> c = PMX.crossover(a, b, r);

        for( int[] each : c )
            System.out.println(Arrays.toString(each));
    }

    @Test
    public void mutationOperators() {
        int[] c = {1, 2, 3, 4, 5, 6, 7, 8};
        int[] tp ;
        Random r = new Random();

        // Exchange Mutation (EM)
        tp = c.clone();
        MutationExchange EM = new MutationExchange();
        EM.mutation(tp, r);
        System.out.println(Arrays.toString(tp));

        // Displacement Mutation (DM)
        tp = c.clone();
        MutationDisplacement DM = new MutationDisplacement();
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
