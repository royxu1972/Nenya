import prioritization.*;

public class RunExp {

    public static void main(String[] args) {
        System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
        //Comparison cc = new Comparison();
        //cc.run();

        //int[] p1 = {0, 1, 2, 3, 4, 5, 6, 7};
        //int[] p2 = {2, 6, 4, 0, 3, 1, 7, 5};
        //for( int i=0 ; i<10 ; i++ )
        //    MEvolution.crossover_PMX(p1,p2);

        Simulation s = new Simulation();
        //s.initSubjects(1000);
        //System.out.println(s.getDataItem(6, s.order_1, 30).toString());
        //s.exp1(59,100);

        s.exp2cost("alg.txt");
    }
}
