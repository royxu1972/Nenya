package EA.oldGA.Common;

import java.util.List;
import java.util.Random;

public interface OPCrossover {
    String toString();
    List<int[]> crossover(int[] a, int[] b, Random r);
}
