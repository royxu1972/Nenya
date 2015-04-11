package prioritization;

import Basic.TestSuite;
import Generation.AETG;
import Prioritization.ReorderArray;

import java.util.ArrayList;

public class compare {

    public static void main(String[] args) {
        int p = 10 ;
        int[] v = new int[p] ;
        for( int k=0 ; k<p ; k++ )
            v[k] = 6 ;
        int t = 2 ;

        TestSuite ts = new TestSuite(p, v, t);
        AETG gen = new AETG();
        gen.Generation(ts);

        System.out.println(gen.getSize());
        ReorderArray re = new ReorderArray();

        ArrayList<int[]> data = new ArrayList<int[]>();
        re.toMultiObjective(ts, data);

    }
}
