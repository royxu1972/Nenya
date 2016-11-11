package EA.GA.Common;

public interface OperatorMutation<T extends Chromosome> {
    /*
     *  Apply mutation operator on chromosomes a. Change
     *  a.solution based on mutation probability PRO.
     */
    void mutation(T a, double PRO );
}
