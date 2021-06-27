public class NumoebaGraph2 extends NumoebaGraph {

    public static void main(String[] s) {
	NumoebaGraph g = new NumoebaGraph2();
	g.init();
	g.start();
    }

    protected GraphPanel createGraphPanel(NumoebaGraph ng) {
	return new GraphPanel2(this);
    }
}
