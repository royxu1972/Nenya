package basic;

import Generation.AETG;
import Model.SUT;
import Model.SUTSequence;
import Model.TestSuite;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

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
        sut.setConstraint(c, 1);

        // init
        sut.initialization();
        // compute fitness value
        //int[] test = {0, 1, 1, 0, 2};
        //sut.FitnessValue(test, 1);
        // print
        sut.show();
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


    @Test
    public void Coverage_Basic() {
        int p = 6 ;
        int[] v = new int[p] ;
        for( int k=0 ; k<p ; k++ )
            v[k] = 3 ;
        int t = 2 ;
        int[][] c = {
                {0, -1, 0, -1, -1, -1},
                {-1, -1, 2, 0, 1, -1}
        };

        TestSuite ts = new TestSuite(p, v, t);
        ts.setConstraint(c);

        AETG gen = new AETG();
        gen.generation(ts);

        ts.showTestSuite();
        System.out.println("2-cov = " + ts.tCoverage(2));
        System.out.println("3-cov = " + ts.tCoverage(3));
        System.out.println("4-cov = " + ts.tCoverage(4));

        double[] pro = {0.7, 0.1, 0.1, 0.05, 0.05};
        System.out.println("profile-cov = " + ts.profileCoverage(pro));
    }


    @Test
    public void faultDetection() {
        TestSuite ts = new TestSuite(4, new int[]{3,3,3,3}, 2);
        new AETG().generation(ts);

        int[] defaults = new int[6];
        ArrayList<int[]> ff = new ArrayList<>();
        ff.add(new int[]{ 0, -1, -1, -1, -1, -1});  // yes
        ff.add(new int[]{-1, -1, -1, -1,  0, -1});  // yes
        ff.add(new int[]{-1, -1, -1, -1,  1, -1});  // no
        ff.add(new int[]{ 0, -1,  2, -1, -1, -1});  // yes
        ff.add(new int[]{-1,  1, -1, -1, -1,  1});  // no
        ff.add(new int[]{-1,  1, -1, -1, -1,  0});  // yes
        ff.add(new int[]{-1,  1, -1,  1,  1, -1});  // no
        ff.add(new int[]{-1,  2, -1, -1,  0,  0});  // yes

        int num = ts.getFaultDetection(ff, defaults);
        System.out.println("detected num = " + num);
        Assert.assertEquals( 5, num );
    }

}
