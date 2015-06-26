package Generation;

import Basic.SUT;
import Basic.TestSuite;
import Basic.ALG;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 *  Basic AETG generator
 */
public class AETG {

    private SUT sut ;
    private ArrayList<int[]> CoverArray ;
    private int[][] firstWeight;  // 记录各参数各取值在未覆盖对中出现次数
    private Random random ;

    public AETG() {
        sut = null ;
        CoverArray = new ArrayList<>();
        random = new Random();
    }

    /*
     *  get the size of current covering array
     */
    public int getSize() {
        return this.CoverArray.size();
    }

    /*
     *  save CoverArray to file
     */
    public void writeCA( String file ) throws IOException {
        FileWriter fileWriter = new FileWriter(file) ;
        String str = "parameter " + String.valueOf(sut.parameter) + "\n" ;
        str += "value " ;
        for( int k=0 ; k<sut.parameter ; k++ )
            str += (String.valueOf(sut.value[k]) + " ") ;
        str += "\n" ;

        str += "begin\n" ;
        for( int k=0 ; k<CoverArray.size() ; k++ ) {
            int[] tp = CoverArray.get(k);
            for( int m=0 ; m<sut.parameter ; m++ )
                str += (tp[m] + " ") ;
            str += "\n" ;
        }

        str += "end\n" ;
        fileWriter.write(str);
        fileWriter.close();

    }

    /*
     *  AETG
     *  N = the number of candidates, default value = 10
     */
    public void Generation( TestSuite test ) {
        Generation( test, 10 );
    }
    public void Generation( TestSuite test, int N ) {
        this.sut = test.system ;
        sut.GenerateS();
        InitFirstWeight();
        CoverArray.clear();

        while( sut.getSCount() != 0 ) {
            // the first candidate
            int[] best = GenerateTestCaseEnhanced() ;
            int bestcov = sut.FitnessValue(best, 0) ;

            // generate another N-1 candidates
            for( int x=1 ; x<N ; x++ ) {
                int[] temp = GenerateTestCaseEnhanced();
                int tempcov = sut.FitnessValue(temp, 0);

                // the best fitness
                if( tempcov == sut.getTestcaseCoverMax() ) {
                    System.arraycopy(temp, 0, best, 0, sut.parameter);
                    break ;
                }
                // otherwise
                if( tempcov > bestcov ) {
                    System.arraycopy(temp, 0, best, 0, sut.parameter);
                    bestcov = tempcov ;
                }
            }  // end for

            // add the best candidate into CoverArray, and then update uncovered combinations
            CoverArray.add(best);
            UpdateFirstWeight(best);

        } // end while

        // the CoverArray should be stored in test.tests[][]
        test.tests = new int[CoverArray.size()][sut.parameter] ;
        test.order = new int[CoverArray.size()] ;
        test.executionCost = new double[CoverArray.size()] ;
        int x = 0 ;
        for( int[] t : CoverArray ) {
            for( int y=0 ; y<sut.parameter ; y++ )
                test.tests[x][y] = t[y] ;
            test.order[x] = x ;             // set default order
            test.executionCost[x] = 0.0 ;   // set default execution cost = 0.0
            x++ ;
        }
    }

    /*
     *  intialize firstWeight
     */
    private void InitFirstWeight() {
        firstWeight = new int[sut.parameter][] ;
        for( int k=0 ; k<sut.parameter ; k++ )
            firstWeight[k] = new int[sut.value[k]+1] ;

        // 按序生成所有参数间的组合情况
        int[] temp = new int[sut.tway];      // 迭代记录
        int[] temp_max = new int[sut.tway];   // 各自最大值
        for (int k = 0; k < sut.tway; k++) { // 初始化
            temp[k] = k;
            temp_max[k] = sut.parameter - sut.tway + k;
        }
        int end = sut.tway - 1;
        int ptr = end;

        int already = 0;
        while( already < sut.gerCoverMain() ) {
            // 处理temp，此时temp[]标记了参数序号
            // 计算涉及参数的组合个数
            int allcomb = 1;
            for (int p = 0; p < sut.tway; p++) {
                allcomb = allcomb * sut.value[temp[p]];
            }

            // firstWeight
            for( int k=0 ; k<sut.tway ; k++ ) {
                int p = temp[k] ;                // 参数
                int v = allcomb / sut.value[p] ;
                for( int l=0 ; l<sut.value[p] ; l++ ) {
                    firstWeight[p][l+1] += v ;
                    firstWeight[p][0] += v ;
                }
            }

            // 求下一个组合
            temp[end] = temp[end] + 1;  // 末位加1
            ptr = end;
            while (ptr > 0) {
                if (temp[ptr] > temp_max[ptr]) { // 超过该位允许最大值
                    temp[ptr - 1] = temp[ptr - 1] + 1;   // 前一位加1
                    ptr--;
                } else {
                    break;
                }
            }
            if (temp[ptr] <= temp_max[ptr]) { // 若该位值不是最大，后面每位在前一位基础上加1
                for (int i = ptr + 1; i < sut.tway; i++) {
                    temp[i] = temp[i - 1] + 1;
                }
            }
            already++;
        }
    }

    /*
     *  update firstWeight, 并将新组合置为已覆盖
     */
    private void UpdateFirstWeight( final int[] test) {
        // 按序生成所有参数间的组合情况
        int[] temp = new int[sut.tway];      // 迭代记录
        int[] temp_max = new int[sut.tway];   // 各自最大值
        for (int k = 0; k < sut.tway; k++) { // 初始化
            temp[k] = k;
            temp_max[k] = sut.parameter - sut.tway + k;
        }
        int end = sut.tway - 1;
        int ptr = end;

        int[] sch = new int[sut.tway] ;
        int already = 0;
        while( already < sut.gerCoverMain() ) {
            // 处理temp，此时temp[]标记了参数序号 
            for( int k=0 ; k<sut.tway ; k++ )
                sch[k] = test[temp[k]] ;

            if( !sut.Covered(temp, sch, 1)) {
                for( int i=0 ; i<sut.tway ; i++ ) {
                    int p = temp[i];
                    int v = sch[i];
                    firstWeight[p][v+1]--;
                    firstWeight[p][0]--;
                }
            }

            // 求下一个组合
            temp[end] = temp[end] + 1;  // 末位加1
            ptr = end;
            while (ptr > 0) {
                if (temp[ptr] > temp_max[ptr]) { // 超过该位允许最大值
                    temp[ptr - 1] = temp[ptr - 1] + 1;   // 前一位加1
                    ptr--;
                } else {
                    break;
                }
            }
            if (temp[ptr] <= temp_max[ptr]) { // 若该位值不是最大，后面每位在前一位基础上加1
                for (int i = ptr + 1; i < sut.tway; i++) {
                    temp[i] = temp[i - 1] + 1;
                }
            }
            already++;
        }
    }


    /*
     *  generate a new test case
     */
    private int[] GenerateTestCase() {
        int[] tc = new int[sut.parameter] ;
        for( int k=0 ; k<sut.parameter ; k++ )
            tc[k] = -1 ;

        int assigned_count = 0 ;                          // 已确定参数个数
        int[] parameter_order = new int[sut.parameter];   // 参数序列

        // temp
        int p_number ;
        int v_number ;
        ArrayList<Integer> order = new ArrayList<Integer>();  // ties break

        // 最终选择结果
        int maxp = 0 ;
        int maxv = 0 ;
        /*
        System.out.println("firstWeight");
        for( int i=0 ; i<parameter ; i++) {
            for( int j=0 ; j<value[i]+1 ; j++ ) {
                System.out.print(firstWeight[i][j] + " ");
            }
            System.out.print("\n"); 
        }
        */
        //
        // 选第一个参数，从 firstweight 中计算
        //
        // 找到一个最大的参数
        p_number = firstWeight[0][0];
        for( int x=1 ; x<sut.parameter ; x++ ){
            if( firstWeight[x][0] > p_number )
                p_number = firstWeight[x][0] ;
        }
        order.clear();
        for( int x=0 ; x<sut.parameter ; x++ ){
            if( firstWeight[x][0] == p_number )
                order.add(x);
        }
        int p_index = random.nextInt(order.size());
        maxp = order.get(p_index);

        // 找到一个最大取值
        int col = sut.value[maxp] ;
        v_number = firstWeight[maxp][1] ;
        for( int x=1 ; x<col ; x++ ) {
            if( firstWeight[maxp][x+1] > v_number )
                v_number = firstWeight[maxp][x+1] ;
        }
        order.clear();
        for( int x=0 ; x<col ; x++ ) {
            if( firstWeight[maxp][x+1] == v_number )
                order.add(x);
        }
        int v_index = random.nextInt(order.size());
        maxv = order.get(v_index);

        // 加入test case
        tc[maxp] = maxv ;
        //System.out.println("first p: " + maxp + ", v: " + maxv);

        // 更新
        parameter_order[0] = maxp ;
        assigned_count++;

        //
        // 第一个参数选择完毕
        //

        // 构建随机序列 parameter_order
        order.clear();
        for( int k=0 ; k<sut.parameter ; k++ ) {
            if( k != maxp )
                order.add(k);
        }
        for( int k=1 ; k<sut.parameter ; k++ ) {
            int pk = random.nextInt(order.size());
            parameter_order[k] = order.get(pk);
            order.remove(pk);
        }

        // 
        // 为每个参数选取值
        //
        while( assigned_count < sut.parameter ) {
            // 当前考虑参数为par
            int par = parameter_order[assigned_count] ;
            int[] weight = new int[sut.value[par]];
            
            for( int i=0 ; i<sut.value[par]; i++ )
                weight[i] = FitSchema(tc, par, i, assigned_count+1) ;
            
            // 找到一个最大取值
            v_number = weight[0];
            for( int x = 1 ; x < sut.value[par]; x++ ) {
                if( weight[x] > v_number )
                    v_number = weight[x];
            }
            order.clear();
            for( int x = 0; x < col; x++ ) {
                if( weight[x] == v_number )
                    order.add(x);
            }
            int vk = random.nextInt(order.size());
            int val = order.get(vk);

            // 加入test case
            tc[par] = val ;
            assigned_count++;

        } // end while

        //for int k=0 ; k<tc.length ; k++ )
        //    System.out.print(tc[k]+" ");
        //System.out.print("\n");
        return tc ;
    }

    /*
     *  generate a new test case, enhanced version
     */
    private int[] GenerateTestCaseEnhanced() {
        int[] tc = new int[sut.parameter] ;
        for( int k=0 ; k<sut.parameter ; k++ )
            tc[k] = -1 ;

        int assigned_count = 0 ;     // 已确定参数个数
        int[] parameter_order ;      // 参数序列

        // temp
        int p_number ;
        int v_number ;
        ArrayList<Integer> order = new ArrayList<Integer>();  // ties break

        // 最终选择结果
        int maxp = 0 ;
        int maxv = 0 ;

        //
        // 选第一个参数，从firstweight中计算
        //
        // 找到一个最大的参数
        p_number = firstWeight[0][0];
        for( int x=1 ; x<sut.parameter ; x++ ){
            if( firstWeight[x][0] > p_number )
                p_number = firstWeight[x][0] ;
        }
        order.clear();
        for( int x=0 ; x<sut.parameter ; x++ ){
            if( firstWeight[x][0] == p_number )
                order.add(x);
        }
        int p_index = random.nextInt(order.size());
        maxp = order.get(p_index);

        // 找到一个最大取值
        int col = sut.value[maxp] ;
        v_number = firstWeight[maxp][1] ;
        for( int x=1 ; x<col ; x++ ) {
            if( firstWeight[maxp][x+1] > v_number )
                v_number = firstWeight[maxp][x+1] ;
        }
        order.clear();
        for( int x=0 ; x<col ; x++ ) {
            if( firstWeight[maxp][x+1] == v_number )
                order.add(x);
        }
        int v_index = random.nextInt(order.size());
        maxv = order.get(v_index);

        // 加入test case
        tc[maxp] = maxv ;
        //System.out.println("first p: " + maxp + ", v: " + maxv);

        // 更新
        assigned_count++;

        //
        // 第一个参数选择完毕
        //

        //
        // 为每个参数选取值
        //
        while( assigned_count < sut.parameter ) {
            // 找到未确定的参数序号
            int un_count = sut.parameter - assigned_count ;
            parameter_order = new int[un_count];
            for (int x = 0, y = 0; x < sut.parameter && y < un_count ;x++ )   {
                if (tc[x] == -1) {
                    parameter_order[y] = x;
                    y++;
                }
            }

            // 生成新的二维数组，并同时计算weight
            int[][] weight = new int[un_count][];
            for( int i=0 ; i<un_count ; i++ ) {
                int jmax = sut.value[parameter_order[i]] + 2 ;
                weight[i] = new int[jmax] ;
                weight[i][0] = parameter_order[i] ;
                weight[i][1] = 0 ;
                for( int j=2 ; j<jmax ; j++ ) {
                    int pp = weight[i][0];
                    int vv = j - 2;
                    weight[i][j] = FitSchema(tc, pp, vv, assigned_count+1);
                    weight[i][1] += weight[i][j];
                }
            }

            // 找到一个最大的参数
            p_number = 0 ;
            int[] p_order = new int[un_count] ;
            for( int x = 0 ; x < un_count ; x++ ){
                p_order[x] = weight[x][0] ;
                if( weight[x][1] > p_number )
                    p_number = weight[x][1] ;
            }
            order.clear();
            for (int x = 0; x < un_count; x++) {
                if ( weight[x][1] == p_number)
                    order.add(p_order[x]);
            }
            p_index = random.nextInt(order.size());
            maxp = order.get(p_index);

            // 找到一个最大取值
            int row = 0 ;
            for( int x=0 ; x<un_count; x++ ) {
                if( weight[x][0] == maxp )
                    row = x ;
            }

            col = sut.value[maxp];
            v_number = weight[row][2] ;
            for( int m=1 ; m<col ; m++ ) {
                if( weight[row][m+2] > v_number )
                    v_number = weight[row][m+2] ;
            }
            order.clear();
            for (int m=0; m<col; m++) {
                if( weight[row][m+2] == v_number ) {
                    order.add(m);
                }
            }
            //System.out.println("vorder: " + order.size());
            v_index = random.nextInt(order.size());
            maxv = order.get(v_index);

            // 加入test case
            tc[maxp] = maxv ;

            assigned_count++;

        } // end while

        return tc ;
    }

    /*
     * 输入：test[parameter]，count-1位前已确定为fixed value，目前考虑第count个参数，参数序号n，参数取值v
     * 输出：该test在S中的覆盖数
     * 
     * example, CA(2,4,3):
     * Test Suite = {(0,0,0,0),{1,1,1,1}} 已覆盖
     * temp_test = (-, -, 0, -)
     * 如果 t = 3, 则 FitSchema(temp_test, 1, 2, 2) = 6, 即(-, 2, 0, -)可达到的3-way组合共6个
     * 如果 t = 2, 则 FitSchema(temp_test, 1, 2, 2) = 1, 即(-, 2, 0, -)包含的2-way组合共1个
     */
    private int FitSchema(final int[] temp_test , int n , int v , int count) {
        int fit = 0 ;  // 返回值
        int[] test = new int[sut.parameter];
        System.arraycopy(temp_test, 0, test, 0, sut.parameter);

        //
        // 此时参数n的值v被加入，已确定的参数个数count小于tway，从未确定的参数中选择所有的tway-count个参数的组合情况，
        // 对于每一种情况，计算该情况下各涉及参数的所有取值，然后生成一个position_1和schema_1，使用Covered函数计算该情况是否覆盖
        //
        if( count < sut.tway ) {
            test[n] = v ; // 将v加入test[]

            int[] position = new int[sut.tway];
            int[] schema = new int[sut.tway];

            // 已确定count个参数
            // 未确定parameter - count个参数
            int un_count = sut.parameter - count;
            int[] un_pos = new int[un_count];

            // 赋值
            for( int k=0 , p=0 , q=0 ; k<sut.parameter ; k++ ) {
                if( test[k] != -1 ) {
                    position[q] = k;
                    schema[q] = test[k];
                    q++;
                } else {
                    un_pos[p] = k;
                    p++;
                }
            }

            // 从un_pos中选择tway-count个，与position组成一个position和schema，然后计算是否可覆盖
            int need_choose = sut.tway - count;
            int allrow = ALG.cal_combine(un_count, need_choose);

            // 按序生成所有组合情况
            int[] temp = new int[need_choose];      // 迭代记录，存储位置，对应un_pos
            int[] un_value = new int[need_choose];   // 迭代记录，各取值
            int[] temp_max = new int[need_choose];   // 各自最大值
            for (int k = 0; k < need_choose; k++) { // 初始化
                temp[k] = k;
                temp_max[k] = un_count - need_choose + k;
            }
            int end = need_choose - 1;
            int ptr = end;

            int already = 0;
            while (already < allrow) {
                // begin every value //
                // 对该位置的各取值 
                int allcomb = 1;   // 组合数
                for (int p = 0; p < need_choose; p++) {
                    allcomb = allcomb * sut.value[un_pos[temp[p]]];
                }

                // 递增记录为un_value
                for (int k = 0; k < need_choose; k++) {
                    un_value[k] = 0;
                }
                int cend = need_choose - 1;    // 标记每次加1的位置，即最末尾
                int cptr = cend;               // 标记依次进位          

                // 对所有组合
                for( int k = 0 ; k < allcomb ; k++ ) {
                    // 加入position
                    for( int x=count , y=0 ; x<sut.tway && y<need_choose ; x++ , y++ ) {
                        position[x] = un_pos[temp[y]] ;
                        schema[x] = un_value[y] ;
                    }

                    int[] temp_position = new int[sut.tway];
                    int[] temp_schema = new int[sut.tway];
                    System.arraycopy(position, 0, temp_position, 0, sut.tway);
                    System.arraycopy(schema, 0, temp_schema, 0, sut.tway);

                    // position_1和schema_1排序
                    ALG.cal_sortArray(temp_position, temp_schema, 0, sut.tway - 1, 0);

                    /*
                    System.out.print("position: ");
                    for( int kk=0 ; kk<tway ; kk++ )
                        System.out.print(temp_position[kk] + " ");
                    System.out.print("\n");
                    System.out.print("schema: ");
                    for( int kk=0 ; kk<tway ; kk++ )
                        System.out.print(temp_schema[kk] + " ");
                    System.out.print("\n");
                    */

                    // 判断是否覆盖
                    if( !sut.Covered( temp_position , temp_schema , 0 ) )
                        fit++ ;

                    // add
                    un_value[cend] = un_value[cend] + 1;
                    cptr = cend;
                    while (cptr > 0) {
                        if (un_value[cptr] > sut.value[un_pos[temp[cptr]]] - 1) {
                            un_value[cptr - 1] = un_value[cptr - 1] + 1;
                            un_value[cptr] = 0;
                            cptr--;
                        } else {
                            break;
                        }
                    }
                }
                // end every value //

                // 求下一个组合
                temp[end] = temp[end] + 1;  // 末位加1
                ptr = end;
                while (ptr > 0) {
                    if (temp[ptr] > temp_max[ptr]) { // 超过该位允许最大值
                        temp[ptr - 1] = temp[ptr - 1] + 1;   // 前一位加1
                        ptr--;
                    } else
                        break;
                }
                if (temp[ptr] <= temp_max[ptr]) { // 若该位值不是最大，后面每位在前一位基础上加1
                    for (int i = ptr + 1; i < need_choose; i++)
                        temp[i] = temp[i - 1] + 1;
                }
                already++;
            }  // end while

        } // end if

        //
        // 此时已确定参数个数count>=tway，因此，从已确定的参数中选择所有的tway-1组合情况，
        // 对每一种情况，与参数n的值v构成position与schema，使用Covered函数计算覆盖数
        //
        else {
            int[] position = new int[sut.tway];
            int[] schema = new int[sut.tway];

            // 已确定参数个数为count-1
            int al_count = count - 1;
            int[] al_pos = new int[al_count];
            int[] al_value = new int[al_count];

            // 赋值
            for (int k = 0, p = 0; k < sut.parameter; k++) {
                if (test[k] != -1) {
                    al_pos[p] = k;
                    al_value[p] = test[k];
                    p++;
                }
            }

            // 从已确定的al_count个参数中选need_choose个参数
            int need_choose = sut.tway - 1 ;
            int allrow = ALG.cal_combine(al_count, need_choose);

            // 按字典序生成所有组合情况
            int[] temp = new int[need_choose];      // 迭代记录，位置，对应un_pos
            int[] temp_max = new int[need_choose];  // 各自最大值
            for (int k = 0; k < need_choose; k++) { // 初始化
                temp[k] = k;
                temp_max[k] = al_count - need_choose + k;
            }
            int end = need_choose - 1;
            int ptr = end;

            int already = 0;
            while (already < allrow) {
                // 此时temp[]中所存内容未al_pos[]的下标，将其取出加入position和schema中
                for (int w = 0; w < sut.tway - 1; w++) {
                    position[w] = al_pos[temp[w]];
                    schema[w] = al_value[temp[w]];
                }
                // 加入参数n
                position[sut.tway-1] = n ;
                schema[sut.tway-1] = v ;

                // 排序
                ALG.cal_sortArray(position, schema, 0, sut.tway - 1, 0);

                // 判断是否覆盖
                if( !sut.Covered( position , schema , 0 ) )
                    fit++ ;

                // 求下一个组合
                temp[end] = temp[end] + 1;  // 末位加1
                ptr = end;
                while (ptr > 0) {
                    if (temp[ptr] > temp_max[ptr]){ // 超过该位允许最大值
                        temp[ptr - 1] = temp[ptr - 1] + 1;   // 前一位加1
                        ptr--;
                    } else
                        break;
                }
                if (temp[ptr] <= temp_max[ptr]) { // 若该位值不是最大，后面每位在前一位基础上加1
                    for (int i = ptr + 1; i < need_choose; i++)
                        temp[i] = temp[i - 1] + 1;
                }
                already++;
            }  // end while

        } // end else

        return fit ;
    }
}
