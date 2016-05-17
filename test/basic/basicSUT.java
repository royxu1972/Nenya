package basic;

import Basic.SUT;
import org.junit.Test;

public class basicSUT {
    @Test
    public void SUT_BasicMethod() {
        /*
            p1  p2  p3  p4  p5
            1   4   7  10  13
            2   5   8  11  14
            3   6   9  12  15
        */
        int p = 5 ;
        int[] v = new int[p] ;
        for( int k=0 ; k<p ; k++ )
            v[k] = 3 ;
        int t = 2 ;
        int[][] c = {
                {0, -1, 0, -1, -1},
                {-1, -1, 1, 0, 2}
        };

        SUT sut = new SUT(p, v, t);
        sut.setConstraint(c);

        // initialization
        sut.initialization();
        // compute fitness value
        //int[] test = {0, 1, 1, 0, 2};
        //sut.FitnessValue(test, 1);
        // print
        sut.printInfo();
    }

}
