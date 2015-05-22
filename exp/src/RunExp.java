import prioritization.Collection;
import prioritization.Comparison;
import prioritization.Simulation;
import prioritization.SimulationRandom;

public class RunExp {

    public static void main(String[] args) {
        //Comparison cc = new Comparison();
        //cc.run();

        SimulationRandom sr = new SimulationRandom();
        sr.exp1( 1000 );
        sr.printItems("data.txt");

        //System.out.print("-----------\nSimulation\n-----------\n");
        //Collection results = new Collection();
        //Simulation sim = new Simulation();
        //sim.exp3(results);
        //results.printPlainData("data.txt");
    }
}
