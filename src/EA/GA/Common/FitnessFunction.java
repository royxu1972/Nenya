package EA.GA.Common;

public interface FitnessFunction <T extends Chromosome> {
    /*
     *  Compute the fitness value of the given candidate solution.
     *  The smaller the fitness value, the better.
     */
    double value(T candidate);
}
