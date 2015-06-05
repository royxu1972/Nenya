import prioritization.*;

public class RunExp {

    public static void main(String[] args) {
        //Comparison cc = new Comparison();
        //cc.run();

        SimulationRandom sr = new SimulationRandom();
        sr.exp1( 100, "samples.0623" );

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
