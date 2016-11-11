package EA.GA.Common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *  Permutation representation of solution,
 *  which is used for test suite prioritization.
 */
public class Permutation extends Chromosome implements Cloneable {

    // the representation of solution
    public int[] solution;

    // initialize a random permutation
    public Permutation(int length) {
        List<Integer> permutation = new ArrayList<>();
        for (int k = 0; k < length; k++)
            permutation.add(k);
        Collections.shuffle(permutation);
        Integer[] a = permutation.toArray(new Integer[0]);

        solution = new int[length];
        for (int k = 0; k < length; k++)
            solution[k] = a[k] ;
    }

    // create a permutation from an existing order
    public Permutation(final int[] solution) {
        this.solution = solution.clone();
    }

    @Override
    public Permutation clone() {
        Permutation c = (Permutation)super.clone();
        c.solution  = this.solution.clone();
        return c;
    }
}