package evolutionary;

import EA.Common.CrossoverPMX;
import EA.Common.MutationDisplacement;
import EA.Common.MutationExchange;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

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
}
