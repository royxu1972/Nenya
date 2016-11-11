package EA.oldGA.Common;

import java.util.Random;

public interface OPMutation {
    String toString();
    void mutation(int[] c, Random rand);
}
