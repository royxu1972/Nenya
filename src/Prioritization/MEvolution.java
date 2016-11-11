package Prioritization;

import Model.TestSuite;
import EA.oldGA.Common.FitnessFunction2D;
import EA.oldGA.Common.InitializerND;
import EA.oldGA.NSGeneticAlgorithmII;
import EA.oldGA.NSSolution2D;

import java.util.ArrayList;


/**
 *  Multi-objective based prioritization (combination coverage
 *  and switching cost) by NSGA-II
 */
public class MEvolution {

    public class testingOrderInitializer2D implements InitializerND {
        @Override
        public void initialization(NSGeneticAlgorithmII NSGA, int size) {
            NSGA.population.clear();

            for( int i=0 ; i<size-1 ; i++ ) {
                // random solution
                int[] flag = new int[NSGA.LENGTH] ;
                for( int k=0 ; k<NSGA.LENGTH; k++ )
                    flag[k] = 0 ;

                int[] order = new int[NSGA.LENGTH] ;
                int pos = NSGA.random.nextInt(NSGA.LENGTH) ;
                order[0] = pos ;
                flag[pos] = 1 ;
                for( int index=1 ; index<NSGA.LENGTH; index++ ) {
                    while( flag[pos] == 1 )
                        pos = NSGA.random.nextInt(NSGA.LENGTH) ;
                    order[index] = pos ;
                    flag[pos] = 1 ;
                }

                // optimizing goals: total testing cost & RFD
                NSSolution2D seq = new NSSolution2D(order, ts.getTotalTestingCost(order), ts.getRFD(order, ts.system.t_way), 0, 0) ;
                NSGA.population.add(seq) ;
            }

            // add default solution
            int[] o = new int[NSGA.LENGTH] ;
            for( int k=0 ; k<NSGA.LENGTH ; k++ )
                o[k] = k ;
            NSSolution2D seq = new NSSolution2D(o, ts.getTotalTestingCost(o), ts.getRFD(o, ts.system.t_way), 0, 0) ;
            NSGA.population.add(seq) ;
        }
    }

    public class testingOrderFitness2D implements FitnessFunction2D {
        @Override
        public double[] value(int[] c) {
            double[] fit = new double[2];
            fit[0] = ts.getTotalTestingCost(c);
            fit[1] = ts.getRFD(c, ts.system.t_way);
            return fit ;
        }
    }

    private TestSuite ts ;
    public NSGeneticAlgorithmII NSGA ;

    // final front
    public ArrayList<NSSolution2D> result ;

    public MEvolution(TestSuite t) {
        ts = t ;
        NSGA = new NSGeneticAlgorithmII(ts.tests.length);
    }

    public void run() {
        NSGA.setInitializer(new testingOrderInitializer2D());
        NSGA.setFitnessFunction(new testingOrderFitness2D());
        NSGA.evolve();

        result = NSGA.finalFront;
    }

}
