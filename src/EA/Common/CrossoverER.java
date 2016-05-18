package EA.Common;

import java.util.List;
import java.util.Random;

/**
 *  Genetic Edge Recombination Crossover (ER)
 */
public class CrossoverER implements OperatorCrossover {
    @Override
    public String toString() {
        return "ER";
    }

    @Override
    public List<int[]> crossover(int[] a, int[] b, Random r) {
        /*
        int len = a.length ;
        int[] child = new int[len];
        int[] remain = new int[len];
        for( int k = 0; k < len; k++ )
            remain[k] = 0;
        int remain_count = len ;

        Map<Integer, Set<Integer>> edge_map = new HashMap<Integer, Set<Integer>>();
        // for each test case, find the indexes
        for (int i = 0; i < len; i++) {
            Set<Integer> temp = new HashSet<Integer>();

            for (int k = 0; k < len; k++) {
                if (p1[k] == i) {
                    temp.add(k == 0 ? p1[len - 1] : p1[k - 1]);
                    temp.add(k == len - 1 ? p1[0] : p1[k + 1]);
                    break ;
                }
            }
            for (int k = 0; k < len; k++) {
                if (p2[k] == i) {
                    temp.add(k == 0 ? p2[len - 1] : p2[k - 1]);
                    temp.add(k == len - 1 ? p2[0] : p2[k + 1]);
                    break ;
                }
            }
            edge_map.put(i, temp);
        }

        // procedure offspring
        int index = 0;

        // the first one
        int f1 = ((Set) edge_map.get(p1[index])).size();
        int f2 = ((Set) edge_map.get(p2[index])).size();
        if (f1 < f2)
            child[index] = p1[index];
        else if (f1 > f2)
            child[index] = p2[index];
        else {
            int k = random.nextInt(2);
            child[index] = k == 0 ? p1[index] : p2[index];
        }
        remain[child[index]] = 1;
        remain_count--;
        index++;

        while (true) {
            // remove all occurrences of the current vertex (child[index])
            Set<Integer> tp_set = edge_map.remove(child[index - 1]);
            for (Map.Entry<Integer, Set<Integer>> entry : edge_map.entrySet())
                ((Set)entry.getValue()).remove(child[index - 1]);

            // if the current vertex has entries
            if (!tp_set.isEmpty()) {
                // find the vertex which has fewest entries, ties are broken at random
                int min = Integer.MAX_VALUE;
                for (Integer t : tp_set) {
                    int esize = ((Set) edge_map.get(t)).size();
                    if (esize < min)
                        min = esize;
                }
                ArrayList<Integer> temp = new ArrayList<Integer>();
                for (Integer t : tp_set) {
                    if (((Set) edge_map.get(t)).size() == min)
                        temp.add(t);
                }
                int min_index = temp.get(random.nextInt(temp.size()));

                // add to child
                child[index] = min_index;
                remain[child[index]] = 1;
                remain_count--;
                index++;
            }
            // if there are no remaining unvisited vertexs, exit
            else if (remain_count == 0) {
                break;
            }
            // choose a random unvisited vertex
            else {
                int num = random.nextInt(remain_count);
                for (int k = 0; k < len; k++) {
                    if (remain[k] == 0 && num == 0) {
                        // add to child
                        child[index] = k;
                        remain[k] = 1;
                        remain_count--;
                        index++;
                        break;
                    } else if (remain[k] == 0)
                        num--;
                }
            }

        } // end while

        //for( int i = 0 ; i < len ; i++ )
        //    System.out.print(child[i] + " ");
        //System.out.print("\n");
*/
        return null;
    }
}
