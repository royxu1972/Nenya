package EA.GA.Common;

import java.util.ArrayList;

public interface OperatorSelection<T extends Chromosome> {
    /*
     *  Return the index of selected candidate solution
     */
    int selection(ArrayList<T> population);
}
