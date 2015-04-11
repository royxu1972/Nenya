package Basic;

/**
 *  some useful functions
 */
public class ALG {
    public ALG() {}

    // get C(n,m)
    public static int cal_combine(int n, int m) {
        int ret = 1;
        int p = n;
        for (int x = 1; x <= m; x++, p--) {
            ret = ret * p;
            ret = ret / x;
        }
        return ret;
    }

    // get the index of c[] in all possible combinations of C(n, m)
    // e.g. cal_combine2num( [1 2] , 4 , 2 ) = 3
    //      the order of C(4,2) : 0 1 , 0 2 , 0 3 , 1 2 , 1 3 , 2 3
    public static int cal_combine2num(final int[] c, int n, int m) {
        int ret = cal_combine(n, m);
        for (int i = 0; i < m; i++) {
            ret -= cal_combine(n - c[i] - 1, m - i);
        }
        ret--;
        return ret;
    }

    // get the t-th (start from 0) combination of C(n, m)
    // e.g. cal_num2combine( 2 , 4 , 2 ) = [0 3]
    public static int[] cal_num2combine(int t, int n, int m) {
        int[] ret = new int[m];
        t++;                        // input+1
        int j = 1, k;
        for (int i = 0; i < m; ret[i++] = j++) {
            for (; t > (k = cal_combine(n - j, m - i - 1)); t -= k, j++) ;
            ;
        }
        for (int p = 0; p < m; p++) { // output-1
            ret[p]--;
        }
        return ret;
    }

    // get the index of t-way combination val[] in position pos[]
    // e.g. cal_val2num( [0 1] , [1 2] , 2 , [3 3 3 3] ) = 5
    //      the order of all 3*3 combinations in position 0 and 1: 0 0 , 0 1 , 0 2 , 1 0 , 1 1 , 1 2 , ...
    public static int cal_val2num(final int[] pos, final int[] val, int t, final int[] value) {
        int com = 1;
        int ret = 0;
        for (int k = t - 1; k >= 0; k--) {
            ret += com * val[k];
            com = com * value[pos[k]];
        }
        return ret;
    }

    // get the i-th t-way combination in position pos[]
    // cal_num2val( 4 , [1 2] , 2 , [3 3 3 3] ) = [1 1]
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

    // quick sort array a, ascending order
    public static void cal_sortArray(int[] a, int left, int right) {
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
            cal_sortArray(a, left, i - 1);
            cal_sortArray(a, i + 1, right);
        }
    }


    // quick sort array a, and swap corresponding elements in b simultaneously
    // version = 0: ascending order
    // version = 1: descending order
    public static void cal_sortArray(int[] a, int[] b, int left, int right, int version) {
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
            cal_sortArray(a, b, left, i - 1, version);
            cal_sortArray(a, b, i + 1, right, version);
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
            cal_sortArray(a, b, left, i - 1, version);
            cal_sortArray(a, b, i + 1, right, version);
        }
    }

}

