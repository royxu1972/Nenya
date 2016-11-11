package EA.GA.Common;

import java.util.ArrayList;

public interface Initializer <T extends Chromosome> {
    /*
     *  Add initial candidates into population
     */
    void init(ArrayList<T> population, int size);
}
