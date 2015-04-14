import prioritization.Collection;
import prioritization.Simulation;

import java.io.IOException;

public class RunExp {

    public static void main(String[] args) {
        Collection results = new Collection();

        Simulation sim = new Simulation();
        sim.exp1(results);

        try {
            results.printPlainData("data.txt");
        } catch ( IOException e ) {
            System.err.println(e);
        }

    }
}
