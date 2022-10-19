package swap;

public class ErrorTuple {
	private int rowIndex;
	private int attrNum;
	private String[] dirtys;
	private String[] modifys;
	private double cost;

	public ErrorTuple(int attrNum) {
		setAttrNum(attrNum);
		dirtys = new String[attrNum];
		modifys = new String[attrNum];
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public void buildErrorTuple(int rowIndex, String[] dirtys, String[] modifys) {
		setRowIndex(rowIndex);
		if (dirtys.length != modifys.length) {
			System.out.println("inconsistent misplaced !");
		}

		for (int i = 0; i < attrNum; i++) {
			this.dirtys[i] = dirtys[i];
			this.modifys[i] = modifys[i];
		}
	}

	public void setAttrNum(int attrNum) {
		this.attrNum = attrNum;
	}

	public int getRowIndex() {
		return rowIndex;
	}

	public void setRowIndex(int rowIndex) {
		this.rowIndex = rowIndex;
	}

	public String[] getDirtys() {
		return dirtys;
	}

	public void setDirtys(String[] dirtys) {
		this.dirtys = dirtys;
	}

	public String[] getModifys() {
		return modifys;
	}

	public void setModifys(String[] modifys) {
		for (int i = 0; i < attrNum; i++) {
			this.modifys[i] = modifys[i];
		}
	}

	public void setModifyByIndex(int attrIndex, String modify) {
		this.modifys[attrIndex] = modify;
	}

}
