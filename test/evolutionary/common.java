package evolutionary;

import EA.GA.CrossoverPMX;
import EA.GA.MutationExchange;
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
        Random r = new Random();

        // Exchange Mutation (EM)
        MutationExchange EM = new MutationExchange();
        EM.mutation(c, r);
        System.out.println(Arrays.toString(c));
    }
}
