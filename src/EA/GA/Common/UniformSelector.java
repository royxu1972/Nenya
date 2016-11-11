package EA.GA.Common;

import java.util.ArrayList;
import java.util.Random;

public class UniformSelector<T extends Chromosome> implements OperatorSelection<T> {

    public enum operator {Binary, Roulette}
    private Random random ;
    private operator mode ;

    public UniformSelector() {
        random = new Random();
        mode = operator.Binary;
    }
    public UniformSelector(operator op) {
        random = new Random();
        mode = op;
    }

    @Override
    public int selection(ArrayList<T> population) {
        if ( mode == operator.Binary )
            return binary(population);
        if ( mode == operator.Roulette )
            return roulette(population);
        return 0 ;
    }

    /*
     *  Binary Tournament Selection
     *  Select two candidates randomly. The better one will
     *  be selected as a parent.
     */
    private int binary(ArrayList<T> population) {
        int N = population.size();

        int a = random.nextInt(N);
        int b = random.nextInt(N);
        while( a == b )
            b = random.nextInt(N);

        if( population.get(a).fitness < population.get(b).fitness )
            return a ;
        else
            return b ;
    }

    /*
     *  Roulette Selection
     */
    private int roulette(ArrayList<T> population) {
        // calculate the total weight
        double sum = 0;
        double max = -1;
        for( Chromosome each : population ) {
            sum += each.fitness;
            if( each.fitness > max )
                max = each.fitness;
        }
        max = max + 1.0 ;
        sum = (double)population.size() * max - sum ;

        // get a random value
        double ptr = random.nextDouble() * sum ;
        // locate the random value based on the weights
        int index = 0 ;
        for( Chromosome each : population) {
            ptr = ptr - (max - each.fitness) ;
            if( ptr <= 0 )
                return index;
            index++;
        }
        return index;
    }
}
