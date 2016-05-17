package EA.Common;

import EA.GA.GeneticAlgorithm;
import EA.NSGA.NSGeneticAlgorithmII;

public interface OperatorSelection {
    String toString();
    int selection(GeneticAlgorithm GA);
    int selection(NSGeneticAlgorithmII NSGA);
}
