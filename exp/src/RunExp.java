import prioritization.Collection;
import prioritization.Simulation;

public class RunExp {

    public static void main(String[] args) {
        Collection results = new Collection();
        Simulation sim = new Simulation();
        sim.exp1(results);
        results.printPlainData("data.txt");
    }
}
