package swap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class SRFN {

	private Database db;
	private ArrayList<Tuple> tpList;

	private String[][] dbVals;

	private int K;

	private int[][] allKnnIndexes;
	private ArrayList<Integer> certainAttrIndexList;
	private ArrayList<Integer> potenErrorAttrIndexList;

	private ArrayList<ErrorTuple> errorTuples;

	private ArrayList<Integer> rowIndexList;
	private ArrayList<Integer> t0RowIndexList;
	private ArrayList<Integer> refRowIndexList;

	private HashMap<Integer, HashMap<Integer, Double>> t0LowerBoundMap;
	private HashMap<Integer, Double> t0CostMap;
	private HashMap<Integer, String[]> t0ModifyMap;

	public SRFN(Database db) {
		// TODO Auto-generated constructor stub
		setDb(db);
		tpList = db.getTpList();

		errorTuples = new ArrayList<>();
		t0LowerBoundMap = new HashMap<>();
		t0CostMap = new HashMap<>();
		t0ModifyMap = new HashMap<>();
		potenErrorAttrIndexList = new ArrayList<>();
	}

	private void initPotenErrorAttrIndexList() {
		int attrNum = db.getAttrNum();
		for (int attri = 0; attri < attrNum; attri++) {
			if (!certainAttrIndexList.contains(attri)) {
				potenErrorAttrIndexList.add(attri);
			}
		}
	}

	public void mainSRFN() {
		initVals();

		genKNei();

		initLowerBoundMap();

		repair();
	}

	private void initVals() {
		int size = db.getLength();
		int attrNum = db.getAttrNum();

		dbVals = new String[size][attrNum];
		Tuple tp = null;

		rowIndexList = new ArrayList<>();
		refRowIndexList = new ArrayList<>();

		for (int i = 0; i < size; i++) {
			tp = tpList.get(i);
			String[] datas = tp.getAllData();
			rowIndexList.add(i);

			for (int j = 0; j < attrNum; j++) {
				dbVals[i][j] = datas[j];
			}

			if (!t0RowIndexList.contains(i)) {
				refRowIndexList.add(i);
			}
		}
	}

	private void genKNei() {
		int refRowNum = refRowIndexList.size();
		allKnnIndexes = new int[refRowNum][K];
		double[] subDistances = new double[refRowNum];
		double[] knnDistances = new double[K];

		for (int ri = 0; ri < refRowNum; ri++) {
			int refRowIndex = refRowIndexList.get(ri);
			calDisWithTuples(refRowIndex, subDistances);
			findKnn(subDistances, allKnnIndexes[ri], knnDistances);
		}
	}

	private void initLowerBoundMap() {
		int refRowNum = refRowIndexList.size();
		int t0RowNum = t0RowIndexList.size();
		int potenErrorAttrNum = potenErrorAttrIndexList.size();

		HashMap<Integer, Double> lowerBoundMap;
		int t0RowIndex, refRowIndex;
		String[] thisData, neiData;
		String thisVal, neiVal;
		double dis, totalCost;

		double[][] disMatrix = new double[potenErrorAttrNum][potenErrorAttrNum];

		for (int t0i = 0; t0i < t0RowNum; t0i++) {
			t0RowIndex = t0RowIndexList.get(t0i);
			thisData = dbVals[t0RowIndex];
			lowerBoundMap = new HashMap<>();
			t0LowerBoundMap.put(t0RowIndex, lowerBoundMap);

			for (int ri = 0; ri < refRowNum; ri++) {
				refRowIndex = refRowIndexList.get(ri);
				neiData = dbVals[refRowIndex];

				for (int attri = 0; attri < potenErrorAttrNum; attri++) {
					for (int attrj = 0; attrj < potenErrorAttrNum; attrj++) {
						disMatrix[attri][attrj] = 0;
					}
				}

				totalCost = 0;
				int attriIndex, attrjIndex;
				for (int attri = 0; attri < potenErrorAttrNum; attri++) {
					attriIndex = potenErrorAttrIndexList.get(attri);
					thisVal = thisData[attriIndex];
					for (int attrj = 0; attrj < potenErrorAttrNum; attrj++) {
						attrjIndex = potenErrorAttrIndexList.get(attrj);
						neiVal = neiData[attrjIndex];
						if (Assist.isNumber(thisVal) && Assist.isNumber(neiVal)) {
							dis = Assist.norNumDis(Double.parseDouble(thisVal), Double.parseDouble(neiVal));
						} else {
							dis = Assist.normStrDis(thisVal, neiVal);
						}
						disMatrix[attri][attrj] += dis;
					}
				}

				int[][] r = Hungary.appoint(disMatrix);

				for (int attri = 0; attri < potenErrorAttrNum; ++attri) {
					for (int attrj = 0; attrj < potenErrorAttrNum; ++attrj) {
						if (r[attri][attrj] == 2) {
							totalCost += disMatrix[attri][attrj];
						}
					}
				}

				lowerBoundMap.put(refRowIndex, totalCost);
			}
		}
	}

	private void repair() {
		int t0RowNum = t0RowIndexList.size();
		int refTupleNum = refRowIndexList.size();

		int potenErrorAttrNum = potenErrorAttrIndexList.size();
		int attrNum = db.getAttrNum();

		String[] thisData, neiData;
		String thisVal, neiVal;
		double totalCost, minTotalCost;
		String[] modify;
		double dis;

		double[][] disMatrix = new double[potenErrorAttrNum][potenErrorAttrNum];

		int t0RowIndex, neiRowIndex;

		int[] knnIndexes = allKnnIndexes[0];
		for (int t0i = 0; t0i < t0RowNum; t0i++) {
			t0RowIndex = t0RowIndexList.get(t0i);
			thisData = dbVals[t0RowIndex];

			for (int attri = 0; attri < potenErrorAttrNum; attri++) {
				for (int attrj = 0; attrj < potenErrorAttrNum; attrj++) {
					disMatrix[attri][attrj] = 0;
				}
			}

			totalCost = 0;
			int attriIndex, attrjIndex;
			for (int ki = 0; ki < K; ki++) {
				neiRowIndex = knnIndexes[ki];
				neiData = dbVals[neiRowIndex];
				for (int attri = 0; attri < potenErrorAttrNum; attri++) {
					attriIndex = potenErrorAttrIndexList.get(attri);
					neiVal = neiData[attriIndex];
					for (int attrj = 0; attrj < potenErrorAttrNum; attrj++) {
						attrjIndex = potenErrorAttrIndexList.get(attrj);
						thisVal = thisData[attrjIndex];

						if (Assist.isNumber(thisVal) && Assist.isNumber(neiVal)) {
							dis = Assist.norNumDis(Double.parseDouble(thisVal), Double.parseDouble(neiVal));
						} else {
							dis = Assist.normStrDis(thisVal, neiVal);
						}
						disMatrix[attri][attrj] += dis;
					}
				}
			}

			int[][] r = Hungary.appoint(disMatrix);

			modify = new String[attrNum];
			String[] misplaced = dbVals[t0RowIndex];
			for (int attri = 0; attri < attrNum; attri++) {
				modify[attri] = misplaced[attri];
			}

			for (int attri = 0; attri < potenErrorAttrNum; ++attri) {
				for (int attrj = 0; attrj < potenErrorAttrNum; ++attrj) {
					if (r[attri][attrj] == 2) {
						totalCost += disMatrix[attri][attrj];
						attriIndex = potenErrorAttrIndexList.get(attri);
						attrjIndex = potenErrorAttrIndexList.get(attrj);
						modify[attriIndex] = thisData[attrjIndex];
					}
				}
			}

			t0CostMap.put(t0RowIndex, totalCost);
			t0ModifyMap.put(t0RowIndex, modify);
		}

		double totalLowerBound;
		for (int ri = 1; ri < refTupleNum; ri++) {
			knnIndexes = allKnnIndexes[ri];

			for (int t0i = 0; t0i < t0RowNum; t0i++) {
				t0RowIndex = t0RowIndexList.get(t0i);
				thisData = dbVals[t0RowIndex];

				totalLowerBound = 0;
				for (int ki = 0; ki < K; ki++) {
					neiRowIndex = knnIndexes[ki];
					double tmpLowerBound = t0LowerBoundMap.get(t0RowIndex).get(neiRowIndex);
					totalLowerBound += tmpLowerBound;
				}
				minTotalCost = t0CostMap.get(t0RowIndex);
				if (totalLowerBound >= minTotalCost) {
					continue;
				}

				for (int attri = 0; attri < potenErrorAttrNum; attri++) {
					for (int attrj = 0; attrj < potenErrorAttrNum; attrj++) {
						disMatrix[attri][attrj] = 0;
					}
				}

				totalCost = 0;
				int attriIndex, attrjIndex;
				for (int ki = 0; ki < K; ki++) {
					neiRowIndex = knnIndexes[ki];
					neiData = dbVals[neiRowIndex];
					for (int attri = 0; attri < potenErrorAttrNum; attri++) {
						attriIndex = potenErrorAttrIndexList.get(attri);
						neiVal = neiData[attriIndex];
						for (int attrj = 0; attrj < potenErrorAttrNum; attrj++) {
							attrjIndex = potenErrorAttrIndexList.get(attrj);
							thisVal = thisData[attrjIndex];
							if (Assist.isNumber(thisVal) && Assist.isNumber(neiVal)) {
								dis = Assist.norNumDis(Double.parseDouble(thisVal), Double.parseDouble(neiVal));
							} else {
								dis = Assist.normStrDis(thisVal, neiVal);
							}
							disMatrix[attri][attrj] += dis;
						}
					}
				}

				int[][] r = Hungary.appoint(disMatrix);

				modify = new String[attrNum];
				String[] misplaced = dbVals[t0RowIndex];
				for (int attri = 0; attri < attrNum; attri++) {
					modify[attri] = misplaced[attri];
				}
				for (int attri = 0; attri < potenErrorAttrNum; ++attri) {
					for (int attrj = 0; attrj < potenErrorAttrNum; ++attrj) {
						if (r[attri][attrj] == 2) {
							totalCost += disMatrix[attri][attrj];
							attriIndex = potenErrorAttrIndexList.get(attri);
							attrjIndex = potenErrorAttrIndexList.get(attrj);
							modify[attriIndex] = thisData[attrjIndex];
						}
					}
				}

				if (totalCost < minTotalCost) {
					t0CostMap.put(t0RowIndex, totalCost);
					for (int ki = 0; ki < K; ki++) {
						neiRowIndex = knnIndexes[ki];
					}
					t0ModifyMap.put(t0RowIndex, modify);
				}
				
			}
		}

		for (int t0i = 0; t0i < t0RowNum; t0i++) {
			t0RowIndex = t0RowIndexList.get(t0i);
			String[] modifys = t0ModifyMap.get(t0RowIndex);
			String[] oriData = dbVals[t0RowIndex];
			boolean isError = false;
			for (int attri = 0; attri < attrNum; attri++) {
				if (!modifys[attri].equals(oriData[attri])) {
					isError = true;
					break;
				}
			}
			if (isError) {
				ErrorTuple errorTuple = new ErrorTuple(attrNum);
				errorTuple.buildErrorTuple(t0RowIndex, oriData, modifys);
				errorTuples.add(errorTuple);
				System.out.print(t0RowIndex+"\t");
				for (String tempModify : modifys) {
					System.out.print(tempModify+"\t");
				}
				System.out.println();
			}
		}

	}

	private void calDisWithTuples(int rowIndex, double[] distances) {
		int refRowNum = refRowIndexList.size();

		int neiRowIndex;
		double dis;

		for (int ri = 0; ri < refRowNum; ri++) {
			neiRowIndex = refRowIndexList.get(ri);
			if (neiRowIndex == rowIndex) {
				distances[ri] = 0;
			} else {
				dis = calDisWithOneRow(rowIndex, neiRowIndex);
				distances[ri] = dis;
			}
		}
	}

	private double calDisWithOneRow(int rowIndex1, int rowIndex2) {
		String[] vals1 = dbVals[rowIndex1];
		String[] vals2 = dbVals[rowIndex2];

		int attrNum = db.getAttrNum();

		double dis, sum = 0;
		double val1, val2;

		for (int attri = 0; attri < attrNum; attri++) {
			if (Assist.isNumber(vals1[attri]) && Assist.isNumber(vals2[attri])) {
				val1 = Double.parseDouble(vals1[attri]);
				val2 = Double.parseDouble(vals2[attri]);
				dis = Assist.norNumDis(val1, val2);
			} else {
				dis = Assist.normStrDis(vals1[attri], vals2[attri]);
			}
			sum += dis;
		}

		return sum;
	}

	private void findKnn(double[] distances, int[] knnIndexes, double[] knnDistances) {
		if (knnDistances.length == 0) {
			return;
		}

		int length = knnIndexes.length;
		if (length >= refRowIndexList.size()) {
			for (int i = 0; i < refRowIndexList.size(); i++) {
				int refRowIndex = refRowIndexList.get(i);
				knnIndexes[i] = refRowIndex;
				knnDistances[i] = distances[i];
			}
		} else {
			for (int i = 0; i < length; i++) {
				int refRowIndex = refRowIndexList.get(i);
				knnIndexes[i] = refRowIndex;
				knnDistances[i] = distances[i];
			}
			int maxIndex = getMaxIndexfromK(knnDistances);
			double maxVal = knnDistances[maxIndex];

			double dis;
			for (int i = length; i < refRowIndexList.size(); i++) {
				int refRowIndex = refRowIndexList.get(i);
				dis = distances[i];
				if (dis < maxVal) {
					knnIndexes[maxIndex] = refRowIndex;
					knnDistances[maxIndex] = dis;

					maxIndex = getMaxIndexfromK(knnDistances);
					maxVal = knnDistances[maxIndex];
				}
			}
		}

		ArrayList<KnnPair> kpList = new ArrayList<>();
		KnnPair kp = null;
		for (int i = 0; i < length; i++) {
			kp = new KnnPair(knnDistances[i], knnIndexes[i]);
			kpList.add(kp);
		}
		Collections.sort(kpList, new ComparatorKnnPair());

		for (int i = 0; i < length; i++) {
			kp = kpList.get(i);
			knnIndexes[i] = kp.getIndex();
			knnDistances[i] = kp.getDistance();
		}
	}

	private int getMaxIndexfromK(double[] vals) {
		int index = -1;
		double max = -1;

		for (int i = 0; i < vals.length; i++) {
			if (vals[i] > max) {
				max = vals[i];
				index = i;
			}
		}
		return index;
	}

	public void setK(int k) {
		K = k;
	}

	public void setCertainAttrIndexList(ArrayList<Integer> certainAttrIndexList) {
		this.certainAttrIndexList = certainAttrIndexList;
		initPotenErrorAttrIndexList();
	}

	public void setParams(int K, int[] certainIndexes, int[] t0RowIndexes) {
		setK(K);
		certainAttrIndexList = new ArrayList<>();
		for (int certainIndex : certainIndexes) {
			certainAttrIndexList.add(certainIndex);
		}
		initPotenErrorAttrIndexList();
		t0RowIndexList = new ArrayList<>();
		for (int t0RowIndex : t0RowIndexes) {
			t0RowIndexList.add(t0RowIndex);
		}
	}

	public ArrayList<Integer> getT0RowIndexList() {
		return t0RowIndexList;
	}

	public void setT0RowIndexList(ArrayList<Integer> t0RowIndexList) {
		this.t0RowIndexList = t0RowIndexList;
	}

	public Database getDb() {
		return db;
	}

	public void setDb(Database db) {
		this.db = db;
	}

}
