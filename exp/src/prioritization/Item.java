package prioritization;

public class Item {

    /*
     *  the orders that can be examined
     */
    public enum ORDER {
        RANDOM("random"),
        COVERAGE("coverage"),
        GREEDY("switch-greedy"),
        LKH("switch-lkh"),
        HYBRID("hybrid");

        private final String text;

        private ORDER(String t) {
            this.text = t;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    // independent variables: the SUT
    public int P ;
    public int V ;
    public int T ;
    public int Type ;
    public double R ;

    // dependent variable: the ft-measure value of each order
    public ORDER[] orders ;
    public double[] data ;
    public ORDER best ;

    public Item() {}
    public Item( ORDER[] l, int p, int v, int t, int type, double r) {
        this.P = p ;
        this.V = v ;
        this.T = t ;
        this.Type = type ;
        this.R = r ;

        this.orders = new ORDER[l.length];
        System.arraycopy(l, 0, this.orders, 0, l.length);
        this.data = new double[l.length];
        for( int i=0 ; i<l.length ; i++ )
            this.data[i] = 0.0 ;
        this.best = null ;
    }

    public String name() {
        return "p = " + P + ", v = " + V + ", t = " + T + ", type = " +
                Type + ", ratio = " + R + ", orders " + orders.length ;
    }

    @Override
    public int hashCode() {
        return this.name().hashCode();
    }

    @Override
    public boolean equals( Object obj ) {
        if (this == obj)
            return true;
        else if (obj != null && obj instanceof Item ) {
            Item a = (Item)obj;
            if( this.P == a.P && this.V == a.V && this.T == a.T &&
                    this.Type == a.Type && this.R == a.R )
                return true ;
            else
                return false ;
        }
        else
            return false ;
    }

    /*
     *  update which order is the best
     */
    public void updateBestOrder() {
        best = orders[0] ;
        double best_value = data[0] ;
        for (int i = 1; i < data.length; i++ ) {
            if (data[i] < best_value) {
                best = orders[i] ;
                best_value = data[i] ;
            }
        }
    }

    /*
     *  get the name of the best order
     */
    public String getBestOrder() {
        return best.toString() ;
    }

    /*
     *  get the data based on csv format
     *  OUTPUT: "P, V, T, Type, R, Best"
     */
    public static String getColumnName() {
        return "P,V,T,Type,R,TOP" ;
    }
    public String getRowData() {
        return P + ", " + V + ", " + T + ", " + Type + ", " + R + ", " + getBestOrder() ;
    }

    public String getData() {
        String str = "" ;
        for (int i = 0 ; i < orders.length ; i++ ) {
            str += orders[i].toString() + " = " + data[i] + " " ;
        }
        return str ;
    }



}
