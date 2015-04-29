import prioritization.Collection;
import prioritization.Simulation;

public class RunExp {

    public static void main(String[] args) {
        Collection results = new Collection();

        Simulation sim = new Simulation();
        sim.exp1("exp-1 test hybrid order", new String[]{"coverage", "random", "cost-greedy", "cost-lkh"}, results);

        results.printPlainData("data.txt");
    }
}
