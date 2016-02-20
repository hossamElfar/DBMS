package teamname;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;

public class Page implements Serializable{
	ArrayList<Hashtable<String, Object>> data;
	String name = null;

	public Page(String name) {
		data = new ArrayList<Hashtable<String,Object>>();
		this.name = name;
	}

	public ArrayList<Hashtable<String, Object>> getData() {
		return data;
	}

	public void setData(ArrayList<Hashtable<String, Object>> data) {
		this.data = data;
	}

	public int numberOfRecords() {
		return this.data.size();
	}

}
