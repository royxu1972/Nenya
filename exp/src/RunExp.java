import prioritization.Collection;
import prioritization.Comparison;
import prioritization.Simulation;

public class RunExp {

    public static void main(String[] args) {
        //Comparison cc = new Comparison();
        //cc.run();

        System.out.print("-----------\nSimulation\n-----------\n");
        Collection results = new Collection();
        Simulation sim = new Simulation();
        sim.exp3(results);
        results.printPlainData("data.txt");
    }
}
