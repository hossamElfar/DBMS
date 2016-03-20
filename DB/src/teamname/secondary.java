package teamname;

import java.io.Serializable;

public class secondary implements Serializable {
	int pageNum;
	int index;
	Object colName;

	public secondary(int pageNum, int index, Object colName) {
		this.pageNum = pageNum;
		this.index = index;
		this.colName = colName;
	}

}
