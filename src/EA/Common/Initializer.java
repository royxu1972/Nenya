package EA.Common;

import EA.GA.GeneticAlgorithm;

public interface Initializer {
    void initialization(GeneticAlgorithm GA, int size);
}