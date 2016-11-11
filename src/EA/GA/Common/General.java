package EA.GA.Common;

import java.util.Random;

/**
 *  General representation of solution,
 *  which is used for test suite generation.
 */
public class General extends Chromosome implements Cloneable {

    private Random random ;

    public int[] solution;  // the representation of solution
    public int[] range;     // the maximum value of each element

    // initialize a random solution
    public General(int length, final int[] range) {
        this.random = new Random();
        this.solution = new int[length];
        this.range = range.clone();

        for (int k = 0; k < length; k++)
            solution[k] = random.nextInt(range[k]);
    }

    public General(final int[] solution, final int[] range) {
        this.random = new Random();
        // create a solution from an existing one
        this.solution = solution.clone();
        this.range = range.clone();
    }

    @Override
    public General clone() {
        General c = (General)super.clone();
        c.solution = this.solution.clone();
        c.range = this.range.clone();
        return c;
    }
}
