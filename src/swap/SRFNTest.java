package swap;

public class SRFNTest {

	public static void main(String[] args) {
		String dataset = "res";
		String filename = dataset + "-dirty.data";

		FileHandler fh = new FileHandler();
		Database db = fh.readData(filename);

		final int K = 3;
		int[] certainIndexes = { 0, 1 };
		int[] t0RowIndexes = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 };

		SRFN srfn = new SRFN(db);
		srfn.setParams(K, certainIndexes, t0RowIndexes);
		srfn.mainSRFN();
	}

}
