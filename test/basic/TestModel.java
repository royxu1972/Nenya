package basic;

import Model.SUT;
import Model.SUTSequence;
import org.junit.Test;

public class TestModel {
    @Test
    public void SUT_Basic() {
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
                { 0, -1, 0, -1, -1},
                {-1, -1, 1,  0,  2}
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


    @Test
    public void SUTSequence_Basic() {
        int e = 5 ;
        int t = 3 ;

        SUTSequence sut = new SUTSequence(e, t);
        sut.initialization();
        sut.printInfo();

        System.out.println(sut.Covered(new int[]{1,3,4}, 1));
        System.out.println("fit(t1) = " + sut.FitnessValue(new Integer[]{1, 3, 4, 0, 2}, 1));
        System.out.println("fit(t1) = " + sut.FitnessValue(new Integer[]{1, 3, 4, 0, 2}, 1));
        sut.printInfo();
    }

}
