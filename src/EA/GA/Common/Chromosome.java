package EA.GA.Common;

import java.util.Random;

public abstract class Chromosome implements Cloneable {

    public Random random = new Random();

    // fitness value
    public double fitness ;

    @Override
    public Chromosome clone() {
        try {
            Chromosome c = (Chromosome) super.clone();
            c.fitness = this.fitness;
            return c;
        } catch (CloneNotSupportedException e){
            throw new InternalError();
        }
    }
}
