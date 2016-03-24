package functions;

import Basic.ALG;
import org.junit.Test;

import java.util.Arrays;

public class basicALG {

    @Test
    public void ALG_allC() {
        int n = 3 , m = 1 ;
        int[][] data = ALG.cal_allC(n, m);
        for( int[] line : data )
            System.out.println(Arrays.toString(line));
    }

    @Test
    public void ALG_allV() {
        int[] v = {3, 4, 2, 3, 3};
        int[] pos = {1, 2};

        int[][] data = ALG.cal_allV(pos, 2, v);
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

        ALG.insertPSArray(p1, v1, p2, v2, pos, sch);
        System.out.println(Arrays.toString(pos));
        System.out.println(Arrays.toString(sch));
    }

}
