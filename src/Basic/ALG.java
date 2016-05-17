package Basic;

/**
 *  some useful functions
 */
public class ALG {

    /*
     *  C(n. m)
     */
    public static int cal_combine(int n, int m) {
        int ret = 1;
        int p = n;
        for (int x = 1; x <= m; x++, p--) {
            ret = ret * p;
            ret = ret / x;
        }
        return ret;
    }

    /*
     *  the number of possible value combinations among par[]
     */
    public static int cal_combineValue(final int[] par, final int[] value) {
        int comb = 1 ;
        for( int k=0 ; k<par.length ; k++ )
            comb = comb * value[par[k]] ;
        return comb ;
    }

    /*
     *  Get the index of c[] in all possible parameter combinations of C(n, m),
     *  where index starts at 0
     *
     *  For example: cal_combine2num({1, 2}, 4, 2) = 3,
     *  because C(4,2) is as 0 1 , 0 2 , 0 3 , 1 2 , 1 3 , 2 3
     */
    public static int cal_combine2num(final int[] c, int n, int m) {
        int ret = cal_combine(n, m);
        for (int i = 0; i < m; i++) {
            ret -= cal_combine(n - c[i] - 1, m - i);
        }
        return ret - 1;
    }


    /*
     *  Get the t-th parameter combination of C(n, m), where index starts at 0
     *
     *  For example: cal_num2combine(2, 4, 2) = {0, 3}
     *  because C(4,2) is as 0 1 , 0 2 , 0 3 , 1 2 , 1 3 , 2 3
     */
    public static int[] cal_num2combine(int t, int n, int m) {
        int[] ret = new int[m];
        t = t + 1;
        int j = 1, k;

        for (int i = 0; i < m; ret[i++] = j++) {
            for (; t > (k = cal_combine(n - j, m - i - 1)); t -= k, j++) ;
        }

        for (int p = 0; p < m; p++)
            ret[p] = ret[p] - 1;
        return ret;
    }

    /*
     *  Get all parameter combinations of C(n, m)
     *
     *  OUTPUT:
     *  data[][], which has cal_combine(n, m) rows and each row's length is m
     *
     *  For example:
     *  cal_num2combine(4, 2) = {{0, 1}, {0, 2}, {0, 3}, {1, 2}, {1, 3}, {2, 3}}
     */
    public static int[][] cal_allC(int n, int m) {
        int[] temp = new int[m];        // current combination
        int[] temp_max = new int[m];    // the maximum value of each element
        for (int k = 0; k < m; k++) {
            temp[k] = k;
            temp_max[k] = n - m + k;
        }
        int end = m - 1;

        int[][] data = new int[cal_combine(n, m)][m];
        int already = 0;
        while (already < cal_combine(n, m)) {
            // add current combination
            System.arraycopy(temp, 0, data[already], 0, m);

            // calculate the next combination
            temp[end] = temp[end] + 1; // 末位加1
            int ptr = end;
            while (ptr > 0) {
                if (temp[ptr] > temp_max[ptr]) { // 超过该位允许最大值
                    temp[ptr-1] = temp[ptr-1] + 1;   // 前一位加1
                    ptr--;
                } else {
                    break;
                }
            }
            if (temp[ptr] <= temp_max[ptr]) { // 若该位值不是最大，后面每位在前一位基础上加1
                for (int i = ptr+1; i < m; i++) {
                    temp[i] = temp[i-1] + 1;
                }
            }
            already++;
        }
        return data ;
    }


    /*
     *  Get the index of a t-way value combination sch[] among parameters pos[],
     *  where index starts at 0
     *
     *  For example:
     *  cal_val2num({0, 1}, {1, 2}, 2, {3, 3, 3, 3}) = 5, as the orders of all
     *  3^2 value combinations among parameters {0, 1} are 0 0, 0 1, 0 2, 1 0,
     *  1 1, 1 2, 2 0, 2 1, 2 2
     */
    public static int cal_val2num(final int[] pos, final int[] sch, int t, final int[] value) {
        int com = 1;
        int ret = 0;
        for (int k = t - 1; k >= 0; k--) {
            ret += com * sch[k];
            com = com * value[pos[k]];
        }
        return ret;
    }


    /*
     *  Get the i-th t-way value combination among parameter pos[], where index
     *  starts at 0
     *
     *  For example:
     *  cal_num2val(4, {1, 2}, 2, {3, 3, 3, 3}) = {1, 1}
     */
    public static int[] cal_num2val(int i, final int[] pos, int t, final int[] value) {
        int[] ret = new int[t];

        int div = 1;
        for (int k = t - 1; k > 0; k--)
            div = div * value[pos[k]];

        for (int k = 0; k < t - 1; k++) {
            ret[k] = i / div;
            i = i - ret[k] * div;
            div = div / value[pos[k + 1]];
        }
        ret[t - 1] = i / div;
        return ret;
    }


    /*
     *  Get all value combinations among a fixed number of parameters
     *
     *  OUTPUT:
     *  data[][], which has (v^t) rows and each row's length is t
     *
     *  For example:
     *  cal_allV({0, 1}, 2, {3, 3, 3, 3}) = {{0, 0}, {0, 1}, {0, 2}, {1, 0}, {1, 1}, {1, 2},
     *                                       {2, 0}, {2, 1}, {2, 2}}
     */
    public static int[][] cal_allV(final int[] pos, int t, final int[] value) {
        int[] counter = new int[t];         // current combination
        int[] counter_max = new int[t];     // the maximum value of each element
        int comb = 1 ;
        for( int k=0 ; k<t ; k++ ) {
            counter[k] = 0;
            counter_max[k] = value[pos[k]] - 1;
            comb = comb * value[pos[k]];
        }
        int end = t-1 ;

        int[][] data = new int[comb][t];
        for( int i=0 ; i<comb ; i++ ) {
            // assign data[i]
            for( int k=0 ; k<t ; k++)
                data[i][k] = counter[k] ;

            // move counter to the next one
            counter[end] = counter[end] + 1;
            int ptr = end;
            while( ptr > 0 ) {
                if(counter[ptr] > counter_max[ptr]) {
                    counter[ptr] = 0 ;
                    counter[ptr-1] = counter[ptr-1] + 1;
                    ptr--;
                }
                else
                    break;
            }
        }
        return data;
    }

    /*
     *  Given two sorted arrays p1 (v1) and p2 (v2), combine them into a new
     *  sorted array pos (val).
     */
    public static void combineArray(int[] p1, int[] v1, int[] p2, int[] v2, int[] pos, int[] sch) {
        int i ; // index of p1
        int j ; // index of p2
        int k ; // index of pos
        for( i = 0 , j = 0 , k = 0 ; i < p1.length && j < p2.length ; ) {
            if( p1[i] < p2[j] ) {
                pos[k] = p1[i] ;
                sch[k] = v1[i] ;
                i++ ;
                k++ ;
            }
            else {
                pos[k] = p2[j] ;
                sch[k] = v2[j] ;
                j++ ;
                k++ ;
            }
        }
        if( i < p1.length ) {
            for( ; i<p1.length ; i++, k++ ) {
                pos[k] = p1[i];
                sch[k] = v1[i];
            }
        }
        if( j < p2.length ) {
            for( ; j<p2.length ; j++, k++ ) {
                pos[k] = p2[j];
                sch[k] = v2[j];
            }
        }
    }


    // quick sort array a, ascending solution
    public static void sortArray(int[] a, int left, int right) {
        int i, j, temp;
        if (left < right) {
            i = left;
            j = right;
            temp = a[i];
            while (i != j) {
                while (a[j] >= temp && i < j) {
                    j--;
                }
                if (i < j) {
                    a[i] = a[j];
                    i++;
                }
                while (a[i] <= temp && i < j) {
                    i++;
                }
                if (i < j) {
                    a[j] = a[i];
                    j--;
                }
            }
            a[i] = temp;
            sortArray(a, left, i - 1);
            sortArray(a, i + 1, right);
        }
    }


    /*
     *  quick sort an array a and swap corresponding elements in b simultaneously
     *  version = 0: ascending solution
     *  version = 1: descending solution
     */
    public static void sortArray(int[] a, int[] b, int version) {
        sortArray(a, b, 0, a.length - 1, version);
    }
    public static void sortArray(int[] a, int[] b, int left, int right, int version) {
        int i, j;
        int temp, temp_1;
        if (version == 0 && left < right) {
            i = left;
            j = right;
            temp = a[i];
            temp_1 = b[i];
            while (i != j) {
                while (a[j] >= temp && i < j) {
                    j--;
                }
                if (i < j) {
                    a[i] = a[j];
                    b[i] = b[j];
                    i++;
                }
                while (a[i] <= temp && i < j) {
                    i++;
                }
                if (i < j) {
                    a[j] = a[i];
                    b[j] = b[i];
                    j--;
                }
            }
            a[i] = temp;
            b[i] = temp_1;
            sortArray(a, b, left, i - 1, version);
            sortArray(a, b, i + 1, right, version);
        }

        if (version == 1 && left < right) {
            i = left;
            j = right;
            temp = a[i];
            temp_1 = b[i];
            while (i != j) {
                while (a[j] <= temp && i < j) {
                    j--;
                }
                if (i < j) {
                    a[i] = a[j];
                    b[i] = b[j];
                    i++;
                }
                while (a[i] >= temp && i < j) {
                    i++;
                }
                if (i < j) {
                    a[j] = a[i];
                    b[j] = b[i];
                    j--;
                }
            }
            a[i] = temp;
            b[i] = temp_1;
            sortArray(a, b, left, i - 1, version);
            sortArray(a, b, i + 1, right, version);
        }
    }


    /*
     *  particularly designed for reduction
     *  quick sort an array and swap corresponding elements in 2D-array simultaneously
     */
    public static void sortArray2D( int[] a, int[][] b ) {
        sortArray2D(a, b, 0, a.length - 1);
    }
    public static void sortArray2D( int[] a, int[][] b, int left, int right )
    {
        int i, j ;
        int temp ;
        int len = b[0].length ;
        if( left < right )
        {
            i = left ;
            j = right ;
            temp = a[i] ;
            int[] tpc = new int[len];
            for( int k=0 ; k<len ; k++ )
                tpc[k] = b[i][k];

            while( i != j )
            {
                while( a[j] > temp && i < j )
                    j-- ;
                if( i < j )
                {
                    a[i] = a[j] ;
                    for( int k=0 ; k<len ; k++ )
                        b[i][k] = b[j][k] ;
                    i++ ;
                }
                while( a[i] < temp && i < j )
                    i++ ;
                if( i < j )
                {
                    a[j] = a[i] ;
                    for( int k=0 ; k<len ; k++ )
                        b[j][k] = b[i][k] ;
                    j-- ;
                }
            }
            a[i] = temp ;
            for( int k=0 ; k<len ; k++ )
                b[i][k] = tpc[k];

            sortArray2D(a, b, left, i - 1);
            sortArray2D(a, b, i+1 , right);
        }
    }

}

