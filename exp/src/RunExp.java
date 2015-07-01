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

        SimulationRandom sr = new SimulationRandom();
        sr.exp1( 1, "testing" );

        //SimulationSwitches ss = new SimulationSwitches();
        //ss.run();

        //SimulationEach se = new SimulationEach();
        //se.exp("each");

        //System.out.print("-----------\nSimulation\n-----------\n");
        //Collection results = new Collection();
        //Simulation sim = new Simulation();
        //sim.exp3(results);
        //results.printPlainData("data.txt");
    }
}
