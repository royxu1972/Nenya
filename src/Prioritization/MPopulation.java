package Prioritization;

import Basic.TestSuite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;

/**
 *  Population for Multi-Objective Optimization
 */
public class MPopulation {

    public ArrayList<Sequence> population ;
    public int LENGTH ;  // the length of sequence
    private int SIZE ;   // the size of population
    private TestSuite TS ;

    public MPopulation( int size, int length, TestSuite ts ) {
        this.SIZE = size ;
        this.LENGTH = length ;
        this.TS = ts ;
        population = new ArrayList<>() ;
    }

    /*
     *  print current population
     */
    public void printPopulation() {
        int i = 0 ;
        for( Sequence seq : population ) {
            System.out.print(i + ") ");
            for( int k=0; k<LENGTH; k++ ) {
                System.out.print(seq.order[k] + " ");
            }
            System.out.print(", cost=" + seq.cost + ", value=" + seq.value +
                    ", level=" + seq.level + ", crowd=" + seq.crowd + "\n");
            i++ ;
        }
    }

    /*
     *  initialize population
     */
    public void initialization() {
        Random rd = new Random(1);
        population.clear();

        for( int i=0 ; i<SIZE-1 ; i++ ) {
            // random order
            int[] flag = new int[LENGTH] ;
            for( int k=0 ; k<LENGTH; k++ )
                flag[k] = 0 ;

            int[] order = new int[LENGTH] ;
            int pos = rd.nextInt(LENGTH) ;
            order[0] = pos ;
            flag[pos] = 1 ;
            for( int index=1 ; index<LENGTH; index++ ) {
                while( flag[pos] == 1 )
                    pos = rd.nextInt(LENGTH) ;
                order[index] = pos ;
                flag[pos] = 1 ;
            }

            // optimizing goals: total testing cost & RFD
            Sequence seq = new Sequence(order, TS.getTotalTestingCost(order), TS.getRFD(order, TS.system.t_way), 0, 0) ;
            population.add(seq) ;
        }

        // add default order
        int[] o = new int[LENGTH] ;
        for( int k=0 ; k<LENGTH ; k++ )
            o[k] = k ;
        Sequence seq = new Sequence(o, TS.getTotalTestingCost(o), TS.getRFD(o, TS.system.t_way), 0, 0) ;
        population.add(seq) ;

    }

    /*
     *  fast-non-dominated-sort
     *  output: assign domination level and crowd to each sequence of the population
     */
    public void NonDominatedSort() {
        // for each sequence, initialize dominateCount and dominateSet
        int[] dominateCount = new int[SIZE] ;
        ArrayList<HashSet<Integer>> dominateSet = new ArrayList<>();
        ArrayList<Integer> F = new ArrayList<>() ;

        int level = 1 ;
        int p_index = 0 ;
        for( Sequence p : population ) {
            dominateCount[p_index] = 0 ;
            HashSet<Integer> Sp = new HashSet<>() ;

            // if p dominates q, add q to p's dominateSet
            // if q dominates p, increase p's dominateCount
            int q_index = 0 ;
            for( Sequence q : population ) {
                if( p.isDominate(q) ) {
                    Sp.add(q_index);
                    //System.out.println(p_index + " > " + q_index) ;
                }
                else if ( q.isDominate(p) ) {
                    dominateCount[p_index]++;
                    //System.out.println(p_index + " < " + q_index);
                }
                q_index += 1 ;
            }
            dominateSet.add(p_index, Sp);

            // if p belongs to the first front
            if( dominateCount[p_index] == 0 ) {
                p.UpdateLevel(1) ;
                F.add(p_index) ;
            }
            p_index += 1 ;
        }
        this.CrowdAssignment(F);

        while( !F.isEmpty() ) {
            ArrayList<Integer> Q = new ArrayList<Integer>() ;

            // for each x (the index of original list) in F
            for( Integer x : F ) {
                // for each y (the index of original list) in x's dominateSet
                for( Integer y : dominateSet.get(x) ) {
                    dominateCount[y]-- ;
                    // if q belongs to the next front
                    if( dominateCount[y] == 0 ) {
                        population.get(y).UpdateLevel( level + 1 );
                        Q.add(y);
                    }
                }
            }

            level += 1 ;

            // F = Q
            F.clear();
            for( Integer k : Q )
                F.add(k) ;
            if( !F.isEmpty() )
                this.CrowdAssignment(F);
        }
    }

    /*
     *  crowding-distance-assignment
     *  input:  an non-dominated set I
     *  output: assign crowd distance to each member of I
     */
    public void CrowdAssignment( ArrayList<Integer> I ) {
        ArrayList<Sequence> II = new ArrayList<>() ;
        for( Integer i : I )
            II.add(population.get(i)) ;
        int l = II.size() ;
        for( int i=0 ; i<l ; i++ )
            II.get(i).UpdateCrowd(0);

        //
        // cost distance, <
        //
        Collections.sort(II, new Sequence.costSort());
        double f_min = II.get(0).cost ;
        double f_max = II.get(l-1).cost ;

        II.get(0).UpdateCrowd(Integer.MAX_VALUE);   // boundary points
        II.get(l-1).UpdateCrowd(Integer.MAX_VALUE);
        for( int i=1 ; i<l-1 ; i++ ) {
            Sequence si = II.get(i) ;
            double tp = (II.get(i+1).cost-II.get(i-1).cost) / (f_max-f_min) ;
            si.UpdateCrowd( si.crowd + tp ) ;
        }

        //
        // value distance, >
        //
        Collections.sort(II, new Sequence.valueSort());
        double l_min = II.get(0).value ;
        double l_max = II.get(l-1).value ;

        II.get(0).UpdateCrowd(Integer.MAX_VALUE);   // boundary points
        II.get(l-1).UpdateCrowd(Integer.MAX_VALUE);
        for( int i=1 ; i<l-1 ; i++ ) {
            Sequence si = II.get(i) ;
            double tp = (II.get(i+1).value-II.get(i-1).value) / (l_max-l_min) ;
            si.UpdateCrowd( si.crowd + tp ) ;
        }

        II.clear();
    }

    /*
     *  level and crowded based selection:
     *  sorting the population according to Crowded-Comparison, and then select
     *  the first N sequences and remove the others
     */
    public void CandidateSort( int N ) {
        // sort, ascending order
       Collections.sort(this.population, new Sequence.allSort());

        // select
        if( this.population.size() == N )
            return ;
        else {
            for( int k=0 ; k<N ; k++ )
                this.population.remove(N);
        }
    }

    /*
     *  the union of two set
     */
    public void unionSet( ArrayList<Sequence> B ) {
        for( Sequence seq : B ) {
            int[] nt = seq.order.clone() ;
            // remain cost and value, but level = 0 and crowed = 0
            Sequence nq = new Sequence(nt, seq.cost, seq.value, 0, 0.0);
            this.population.add(nq);
        }
        this.SIZE = this.population.size() ;
    }

    /*
     *  add one solution to the current population
     *  (for constructing reference front)
     */
    public void append( Sequence a ) {
        int[] nt = a.order.clone() ;
        Sequence nq = new Sequence(nt, a.cost, a.value, 0, 0.0);
        this.population.add(nq);
        this.SIZE = this.population.size() ;
    }

    /*
     *  get the first level front (level = 1)
     */
    public void getFirstLevelFront( ArrayList<Sequence> data ) {
        data.clear();
        if( population.size() == 0 )
            return ;
        for( Sequence each : population ) {
            if( each.level == 1 ) {
                int[] a = each.order.clone() ;
                Sequence nq = new Sequence(a, each.cost, each.value, each.level, each.crowd);
                data.add(nq);
            }
        }
    }

}
