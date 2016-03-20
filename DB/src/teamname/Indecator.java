package teamname;

import java.io.Serializable;

public class Indecator implements Serializable{
	String table;
	int pageNum;
	int recordIndex;

	public Indecator(String table, int pageNum, int recordIndex) {
		this.table = table;
		this.pageNum = pageNum;
		this.recordIndex = recordIndex;
	}

}
