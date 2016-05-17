package EA.Common;

import java.util.List;
import java.util.Random;

public interface OperatorCrossover {
    String toString();
    List<int[]> crossover(int[] a, int[] b, Random r);
}
