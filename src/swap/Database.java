package swap;

import java.util.ArrayList;

public class Database {

	private int attrNum;
	private ArrayList<Tuple> tpList;

	public Database() {
		setTpList(new ArrayList<>());
	}

	public Database(int attrNum, ArrayList<Tuple> tpList) {
		setAttrNum(attrNum);
		setTpList(tpList);
	}

	public int getAttrNum() {
		return attrNum;
	}

	public void setAttrNum(int attrNum) {
		this.attrNum = attrNum;
	}

	public ArrayList<Tuple> getTpList() {
		return tpList;
	}

	public void setTpList(ArrayList<Tuple> tpList) {
		this.tpList = tpList;
	}

	public void addTuple(Tuple tp) {
		this.tpList.add(tp);
	}

	public Tuple getTupleByRowIndex(int rowIndex) {
		for (Tuple tp : tpList) {
			if (tp.getRowIndex() == rowIndex) {
				return tp;
			}
		}
		return null;
	}

	public int getLength() {
		return tpList.size();
	}

}
