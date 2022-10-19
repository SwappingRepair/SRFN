package swap;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class FileHandler {

	public static String PATH = "data/";

	public Database readData(String input) {
		Database db = new Database();

		try {
			FileReader fr = new FileReader(PATH + input);
			BufferedReader br = new BufferedReader(fr);

			String line = null;
			String[] vals = null;

			String title = br.readLine();
			int attrNum = title.split(",").length;
			db.setAttrNum(attrNum);

			int rowIndex = 0;
			while ((line = br.readLine()) != null) {
				vals = line.split(",");
				String[] data = new String[attrNum];
				for (int i = 0; i < attrNum; ++i) {
					data[i] = vals[i];
				}
				Tuple tp = new Tuple(attrNum);
				tp.buildTuple(rowIndex, data);
				db.addTuple(tp);
				rowIndex++;
			}

			br.close();
			fr.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return db;
	}

}
