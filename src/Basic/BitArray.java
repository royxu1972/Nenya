package Basic;

/**
 * A two dimensional array where each element is
 * implemented by one bit.
 */
public class BitArray {

    private byte[][] matrix ;

    public BitArray( int row ) {
        this.matrix = new byte[row][];
        for( int i=0 ; i<row ; i++ )
            this.matrix[i] = null ;
    }

    public BitArray( int row, int column ) {
        // get the actual number of columns
        int c = (int) Math.ceil((double) column / (double) 8);
        this.matrix = new byte[row][c];
        for( int i=0 ; i<row ; i++ ) {
            for( int j=0 ; j<c ; j++ ) {
                this.matrix[i][j] = 0x00 ;
            }
        }
    }

    /**
     * Initialize a single row, i.e. matrix[index], by zero.
     * @param index index of row
     * @param length number of elements in that row
     */
    public void initializeRow( int index, int length ) {
        // get the actual number of columns
        int c = (int) Math.ceil((double) length / (double) 8);

        if( matrix[index] == null )
            matrix[index] = new byte[c] ;

        for( int j=0 ; j<c ; j++ )
            matrix[index][j] = 0x00 ;
    }

    /**
     * Get the element (zero or one) in row i and column j.
     * @param i index of row
     * @param j index of column
     * @return zero or one
     */
    public int getElement( int i, int j ) {
        // get the element of the jBit-th bit of jByte-th byte
        int jByte = j / 8 ;
        int jBit = j % 8 ;
        return (matrix[i][jByte] >>> (7-jBit) & 0x01) == 0x01 ? 1 : 0 ;
    }

    /**
     * Set the element (zero or one) in row i and column j.
     * @param i index of row
     * @param j index of column
     * @param value new element value
     */
    public void setElement( int i, int j, int value ) {
        int jByte = j / 8 ;
        int jBit = j % 8 ;

        if ( value == 0 ) // set to zero
            matrix[i][jByte] = (byte)(matrix[i][jByte] & (0xFE << (7 - jBit)));
        else              // set to one
            matrix[i][jByte] = (byte)(matrix[i][jByte] | (0x01 << (7 - jBit)));
    }

    /**
     * Convert a specified row into a string representation.
     * @param index index of row
     * @param length number of elements in that row
     * @return string representation
     */
    public String getRow( int index , int length ) {
        StringBuilder sb = new StringBuilder();
        int out = 0 ;
        for (int i = 0; i < matrix[index].length; i++) {
            int ac = matrix[index][i];
            for (int c = 0; c < 8 && out < length; c++, out++) {
                // 循环左移 1 位
                // 当右移的运算数是 byte 和 short 类型时，将自动把这些类型扩大为 int 型
                int temp = ac & 0xFF;
                ac = (temp << 1) | (temp >>> 7);

                if ((ac & 0x01) == 0x01)
                    sb.append("1 ");
                else
                    sb.append("0 ");
            }
        }
        return sb.toString() ;
    }

}
