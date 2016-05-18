package EA.Common;

import EA.GA.GeneticAlgorithm;
import EA.NSGA.NSGeneticAlgorithmII;

/**
 *  Roulette Wheel Selection
 */
public class SelectionRoulette implements OperatorSelection {
    @Override
    public int selection(GeneticAlgorithm GA) {
        int fit_sum = 0;
        for (int k = 0; k < GA.fit.length; k++)
            fit_sum += GA.fit[k];

        int ptr = GA.random.nextInt(fit_sum);

        int temp = 0;
        int index ;
        for (index = 0; index < GA.fit.length; index++) {
            temp += GA.fit[index];
            if (ptr < temp) {
                break;
            }
        }
        return index;
    }

    @Override
    public int selection(NSGeneticAlgorithmII NSGA) {
        return 0;
    }
}
