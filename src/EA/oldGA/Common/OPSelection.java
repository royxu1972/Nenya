package EA.oldGA.Common;

import EA.oldGA.NSGeneticAlgorithmII;

public interface OPSelection {
    String toString();
    int selection(NSGeneticAlgorithmII NSGA);
}
