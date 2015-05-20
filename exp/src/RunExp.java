import prioritization.Collection;
import prioritization.Comparison;
import prioritization.Simulation;

public class RunExp {

    public static void main(String[] args) {
        //Comparison cc = new Comparison();
        //cc.run();

        Collection results = new Collection();
        Simulation sim = new Simulation();
        sim.exp2(results);
        results.printPlainData("data.txt");
    }
}
