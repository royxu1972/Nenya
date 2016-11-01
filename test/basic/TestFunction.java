package basic;

import Basic.Alg;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class TestFunction {

    @Test
    public void ALG_allC() {
        int n = 30 , m = 6 ;
        int[][] data = Alg.cal_allC(n, m);
        for( int[] line : data )
            System.out.println(Arrays.toString(line));
    }

    @Test
    public void ALG_allV() {
        int[] v = {3, 4, 2, 3, 3};
        int[] pos = {1, 2};

        int[][] data = Alg.cal_allV(pos, 2, v);
        for( int[] line : data )
            System.out.println(Arrays.toString(line));
    }

    @Test
    public void ALG_Insert() {
        int[] p1 = {0, 1, 3} ;
        int[] v1 = {0, 10, 30} ;

        int[] p2 = {2, 4} ;
        int[] v2 = {20, 40} ;

        int[] pos = new int[p1.length+p2.length];
        int[] sch = new int[v1.length+v2.length];

        Alg.combineSortedArray(p1, v1, p2, v2, pos, sch);
        System.out.println(Arrays.toString(pos));
        System.out.println(Arrays.toString(sch));
    }

    @Test
    public void ALG_Permutation() {
        int t = 3 ;
        int a[] = new int[t] ;
        for( int i = 0 ; i < t ; i++ )
            a[i] = i ;

        System.out.println("factorial(" + t + ") = " + Alg.cal_factorial(t));
        HashMap<ArrayList<Integer>, Integer> p = Alg.cal_permutation(t);
        p.forEach( (k,v) -> System.out.println(v + "\t = " + k.toString()));

        Integer[] kk = {2, 0, 1};
        System.out.println(p.get(new ArrayList<>(Arrays.asList(kk))));
    }

}
