package EA.Common;

import EA.GA.GeneticAlgorithm;
import EA.NSGA.NSGeneticAlgorithm;

public interface OperatorSelection {
    String toString();
    int selection(GeneticAlgorithm GA);
    int selection(NSGeneticAlgorithm NSGA);
}
