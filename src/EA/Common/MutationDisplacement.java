package EA.Common;

import java.util.Random;

/**
 *  Displacement Mutation (DM)
 */
public class MutationDisplacement implements OperatorMutation {
    @Override
    public String toString() {
        return "DM";
    }

    /*
     *  Example
     *  0 1 2 3 4 5 6  =>  0 1 5 6  =>  0 1 5 2 3 4 6
     *      |   |              |
     *   cut=2,len=2        put=2
     */
    @Override
    public void mutation(int[] c, Random rand) {
        int cut = rand.nextInt(c.length);
        int len = cut == c.length - 1 ? 0 : rand.nextInt(c.length - cut - 1);
        int put = rand.nextInt(c.length - len - 1);

        // cut
        int[] cut_ary = new int[len + 1];
        for (int m = cut, n = 0; m <= cut + len && n <= len; m++, n++)
            cut_ary[n] = c[m];

        // remain
        int[] rem_ary = new int[c.length - len - 1];
        for (int m = 0, n = 0; m < c.length && n < c.length - len - 1; m++) {
            if (cut <= m && m <= cut + len)
                continue;
            else {
                rem_ary[n] = c[m];
                n++;
            }
        }

        // new chromosome
        int i = 0;  // c
        int k = 0;  // remain
        for (; k <= put; k++, i++)
            c[i] = rem_ary[k];
        for (int j = 0; j < cut_ary.length; j++, i++)
            c[i] = cut_ary[j];
        for (; k < c.length - len - 1; k++, i++)
            c[i] = rem_ary[k];
    }
}
