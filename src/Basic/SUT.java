package Basic;

/**
 *  the Software Under Test
 */
public class SUT {
    // testing model
    public int parameter;
    public int[] value;
    public int tway;

    // BETA: constraint as forbidden tuple, one constraint each row (m * parameter)
    public int[][] constraints;

    // the combinations to be covered
    private int SCountAll;  // 总的待覆盖组合数
    private int SCount;     // 未覆盖组合数
    private int coverMain;          // 记录Main行数，C(parameter,tway)
    private int testcaseCoverMax;   // 一个测试用例最多能覆盖组合数

    // 所有需要覆盖的组合，按bit计算
    private int[][] AllS;

    public SUT() {}
    public SUT(int p, int[] v, int t) {
        parameter = p;
        value = new int[p];
        for (int k = 0; k < p; k++)
            value[k] = v[k];
        tway = t;
        coverMain = ALG.cal_combine(p, tway);
        testcaseCoverMax = coverMain;
        constraints = null ;
    }

    // set constraint
    public void setConstraints( int[][] c ) {
        constraints = new int[c.length][parameter];
        for( int i = 0 ; i < c.length ; i++ ) {
            System.arraycopy(c[i], 0, constraints[i], 0, parameter);
        }
    }

    // SCountAll
    public int getSCountAll() {
        return SCountAll;
    }

    // SCount
    public int getSCount() {
        return SCount;
    }

    // CoverMain
    public int gerCoverMain() {
        return coverMain;
    }

    // testcaseCoverMax
    public int getTestcaseCoverMax() {
        return testcaseCoverMax;
    }

    // coverage
    public double getCoverage() {
        return (double) (SCountAll - SCount) / (double) (SCountAll);
    }

    // ----------------------------------------------------------------------------
    // AllS初始化
    // ----------------------------------------------------------------------------
    public void GenerateS() {
        AllS = null;
        SCount = 0;
        // 分配coverMax行
        AllS = new int[coverMain][];
        // 按序生成所有参数间的组合情况
        int[] temp = new int[tway];      // 迭代记录
        int[] temp_max = new int[tway];   // 各自最大值
        for (int k = 0; k < tway; k++) { // 初始化
            temp[k] = k;
            temp_max[k] = parameter - tway + k;
        }
        int end = tway - 1;
        int ptr = end;

        int already = 0;
        while (already < coverMain) {
            // 处理temp，此时temp[]标记了参数序号
            // 计算涉及参数的组合个数
            int allcomb = 1;
            for (int p = 0; p < tway; p++) {
                allcomb = allcomb * value[temp[p]];
            }
            // 生成新的1行
            int column = (int) Math.ceil((double) allcomb / (double) 32);
            AllS[already] = new int[column];
            // 初始化，全为0
            for (int k = 0; k < column; k++) {
                AllS[already][k] = 0x00000000;
            }
            // 计算总的组合个数
            SCount += allcomb;

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
                for (int i = ptr + 1; i < tway; i++) {
                    temp[i] = temp[i - 1] + 1;
                }
            }
            already++;
        }
        // 记录总的待覆盖组合数
        SCountAll = SCount;

        // BETA: 约束处理，仅支持约束参数个数 = t-way 的情况
        if( constraints != null ) {
            //System.out.println("before SCount = " + SCount);
            // make constraint be covered
            for( int[] c : constraints ) {
                int[] pos = new int[tway];
                int[] sch = new int[tway];
                int index = 0 ;
                for( int k = 0 ; k < c.length ; k++ ) {
                    if( c[k] != -1 ) {
                        pos[index] = k ;
                        sch[index] = c[k] ;
                        index++ ;
                    }
                }
                Covered(pos, sch, 1);
            }
            //System.out.println("after SCount = " + SCount);
            //printAllS();
        }
    }

    // ----------------------------------------------------------------------------
    // 计算测试用例test在未覆盖组合对集S中能覆盖的组合对数
    // 标记FLAG=0只计算不修改，FLAG=1则将覆盖的组合情况设置为已覆盖
    // 输入：一个测试用例test，标记FLAG
    // 输出：覆盖组合数
    // ----------------------------------------------------------------------------
    public int FitnessValue(final int[] test, int FLAG) {
        int num = 0;   // 返回值

        // 依次按序生成test的各种组合情况，然后从AllS表中判断是否覆盖，O(C(par,tway))
        int[] pos = new int[tway];        // 储存参数情况
        int[] pos_max = new int[tway];    // 各自最大值
        for (int k = 0; k < tway; k++) {  // 初始化
            pos[k] = k;
            pos_max[k] = parameter - tway + k;
        }
        int end = tway - 1;
        int ptr = end;

        int[] sch = new int[tway];  // 存储取值的情况

        for (int row = 0; row < coverMain; row++) {
            // 得到组合情况
            for (int k = 0; k < tway; k++) {
                sch[k] = test[pos[k]];
            }
            // 判断是否覆盖
            if (!Covered(pos, sch, FLAG)) {
                num++;
            }

            // 生成一下个
            pos[end] = pos[end] + 1;  // 末位加1
            ptr = end;
            while (ptr > 0) {
                if (pos[ptr] > pos_max[ptr]) { // 超过该位允许最大值
                    pos[ptr - 1] = pos[ptr - 1] + 1;   // 前一位加1
                    ptr--;
                } else {
                    break;
                }
            }
            if (pos[ptr] <= pos_max[ptr]) { // 若该位值不是最大，后面每位在前一位基础上加1
                for (int i = ptr + 1; i < tway; i++) {
                    pos[i] = pos[i - 1] + 1;
                }
            }

        }
        return num;
    }

    // ----------------------------------------------------------------------------
    // 计算一个取值情况是否覆盖，其中pos存储参数字典序号，sch存储对应取值
    // 输入：pos[tway]，sch[tway]，FLAG=1则表示未覆盖时需要将该位置1
    // 输出：true为已覆盖，false为未覆盖
    // ----------------------------------------------------------------------------
    public boolean Covered(int[] pos, int[] sch, int FLAG) {
        // 通过position计算得到行号，schema计算得到列号，查AllS得出结果
        boolean ret = true;  // 返回值

        // 行列信息
        int row = ALG.cal_combine2num(pos, parameter, tway);
        int column = 0;       // 哪一个BYTE
        int column_bit = 0;   // 哪一个bit

        // 计算column，下标从0开始计算
        int index = 0;
        int it = 0;
        for (int i = 0; i < tway; i++) {
            it = sch[i];
            for (int j = i + 1; j < tway; j++)
                it = value[pos[j]] * it;
            index += it;
        }

        column = index / 32;
        column_bit = index % 32;

        // 判断column_bit位是否为0
        // index : 0 1 2 3 4 5 6 7 ...
        // BYTE  : 0 0 0 0 0 0 0 0 ...
        //                 |
        //             column_bit
        if ((AllS[row][column] >>> (31 - column_bit) & 0x00000001) != 0x00000001) {
            ret = false;
            if (FLAG == 1) {
                AllS[row][column] = AllS[row][column] | 0x00000001 << (31 - column_bit);
                //System.out.println( AllS[row][column] );
                SCount--;
            }
        }
        return ret;
    }

    // ----------------------------------------------------------------------------
    // print AllS in console
    // ----------------------------------------------------------------------------
    public void printAllS() {
        int[] p = new int[tway];
        for (int i = 0; i < coverMain; i++) {
            p = ALG.cal_num2combine(i, parameter, tway);
            System.out.print("{ ");
            for (int m = 0; m < tway; m++)
                System.out.print(p[m] + " ");
            System.out.print("} : ");

            int allcomb = 1;
            for (int q = 0; q < tway; q++)
                allcomb = allcomb * value[p[q]];
            int column = (int) Math.ceil((double) allcomb / (double) 32);

            int out = 0;
            for (int column_index = 0; column_index < column; column_index++) {
                int ac = AllS[i][column_index];
                for (int c = 0; c < 32 && out < allcomb; c++) {
                    // 循环左移一位
                    int b = ac >>> 31;
                    ac = ac << 1;
                    ac = ac | b;

                    if ((ac & 0x00000001) == 0x00000001)
                        System.out.print(1 + " ");
                    else
                        System.out.print(0 + " ");
                    out++;
                }
            }
            System.out.print("\n");
        }
    }
}