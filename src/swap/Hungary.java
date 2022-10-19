package swap;

public class Hungary {

	private static int n;
	private static double p[][];
	private static double q[][];
	private static int row[], col[];
	private static int r[][];
	private static int x[], y[];

	private static void countZero() {
		for (int i = 0; i < n; i++) {
			row[i] = 0;
			col[i] = 0;
			for (int j = 0; j < n; j++) {
				r[i][j] = 0;
			}
		}

		for (int i = 0; i < n; ++i) {
			for (int j = 0; j < n; ++j) {
				if (p[i][j] == 0) {
					row[i]++;
					col[j]++;
				}
			}
		}
	}

	private static int drawLine() {
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				q[i][j] = 0;
			}
		}

		for (int i = 0; i < n; i++) {
			x[i] = 1;
			y[i] = 0;
		}

		for (int i = 0; i < n; ++i) {
			for (int j = 0; j < n; ++j) {
				if (r[i][j] == 2) {
					x[i] = 0;

					break;
				}
			}
		}

		boolean is = true;
		while (is) {
			is = false;

			for (int i = 0; i < n; ++i) {
				if (x[i] == 1) {
					for (int j = 0; j < n; ++j) {
						if (p[i][j] == 0 && y[j] == 0) {
							y[j] = 1;

							is = true;
						}
					}
				}
			}

			for (int j = 0; j < n; ++j) {
				if (y[j] == 1) {
					for (int i = 0; i < n; ++i) {
						if (p[i][j] == 0 && x[i] == 0 && r[i][j] == 2) {
							x[i] = 1;

							is = true;
						}
					}
				}
			}

		}

		int line = 0;
		for (int i = 0; i < n; ++i) {
			if (x[i] == 0) {
				for (int j = 0; j < n; ++j) {
					q[i][j]++;
				}
				line++;
			}

			if (y[i] == 1) {
				for (int j = 0; j < n; ++j) {
					q[j][i]++;
				}
				line++;
			}
		}

		return line;
	}

	private static int find() {
		countZero();

		int zero = 0;

		while (true) {
			for (int i = 0; i < n; ++i) {
				if (row[i] == 0)
					row[i] = Integer.MAX_VALUE;
				if (col[i] == 0)
					col[i] = Integer.MAX_VALUE;
			}

			boolean stop = true;

			if (min_element(row) <= min_element(col)) {
				int tmp = Integer.MAX_VALUE, index = -1;
				for (int i = 0; i < n; ++i) {
					if (tmp > row[i]) {
						tmp = row[i];
						index = i;
					}
				}

				if (index == -1) {
					break;
				}

				int index2 = -1;
				for (int i = 0; i < n; ++i)
					if (p[index][i] == 0 && col[i] != Integer.MAX_VALUE) {
						index2 = i;
						stop = false;
						zero++;
						break;
					}

				if (stop)
					break;

				row[index] = col[index2] = Integer.MAX_VALUE;
				r[index][index2] = 1;

				for (int i = 0; i < n; ++i) {
					if (p[index][i] == 0 && col[i] != Integer.MAX_VALUE) {
						col[i]--;
					}
				}
				for (int i = 0; i < n; ++i) {
					if (p[i][index2] == 0 && row[i] != Integer.MAX_VALUE)
						row[i]--;
				}
			} else {
				int tmp = Integer.MAX_VALUE, index = -1;
				for (int i = 0; i < n; ++i) {
					if (tmp > col[i]) {
						tmp = col[i];
						index = i;
					}
				}

				int index2 = -1;
				for (int i = 0; i < n; ++i)
					if (p[i][index] == 0 && row[i] != Integer.MAX_VALUE) {
						index2 = i;
						stop = false;
						zero++;
						break;
					}

				if (stop)
					break;

				row[index2] = col[index] = Integer.MAX_VALUE;
				r[index2][index] = 1;

				for (int i = 0; i < n; ++i)
					if (p[index2][i] == 0 && col[i] != Integer.MAX_VALUE)
						col[i]--;
				for (int i = 0; i < n; ++i)
					if (p[i][index] == 0 && row[i] != Integer.MAX_VALUE)
						row[i]--;
			}
		}
		for (int i = 0; i < n; ++i) {
			for (int j = 0; j < n; ++j) {
				if (p[i][j] == 0) {
					r[i][j]++;
				}
			}
		}

		return zero;
	}

	private static int min_element(int[] array) {
		int len = array.length;
		int minVal = Integer.MAX_VALUE;
		for (int i = 0; i < len; i++) {
			if (array[i] < minVal) {
				minVal = array[i];
			}
		}
		return minVal;
	}

	public static int[][] appoint(double[][] s) {
		n = s.length;
		p = new double[n][n];
		q = new double[n][n];
		row = new int[n];
		col = new int[n];
		r = new int[n][n];
		x = new int[n];
		y = new int[n];

		for (int i = 0; i < n; ++i) {
			double min = Double.MAX_VALUE;
			for (int j = 0; j < n; ++j) {
				if (s[i][j] < min) {
					min = s[i][j];
				}
			}
			for (int j = 0; j < n; j++) {
				p[i][j] = s[i][j] - min;
			}
		}

		for (int j = 0; j < n; j++) {
			double min = Double.MAX_VALUE;
			for (int i = 0; i < n; i++) {
				if (p[i][j] < min) {
					min = p[i][j];
				}
			}
			if (min == 0) {
				continue;
			}
			for (int i = 0; i < n; i++) {
				p[i][j] -= min;
			}
		}

		int find = find();
		int iterNumber = 0;
		while (find < n && iterNumber < 100) {
			drawLine();

			double min = Double.MAX_VALUE;
			for (int i = 0; i < n; ++i)
				for (int j = 0; j < n; ++j)
					if (q[i][j] == 0 && min > p[i][j])
						min = p[i][j];

			for (int i = 0; i < n; ++i)
				for (int j = 0; j < n; ++j)
					if (q[i][j] == 0)
						p[i][j] -= min;
					else if (q[i][j] == 2)
						p[i][j] += min;
			find = find();
			iterNumber++;
		}

		return r;
	}

}
