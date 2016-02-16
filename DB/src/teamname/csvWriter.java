package teamname;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

public class csvWriter {

	ArrayList<Table> table;
	private static final String COMMA_DELIMITER = ",";
	private static final String NEW_LINE_SEPARATOR = "\n";

	public csvWriter(ArrayList<Table> table) {
		this.table = table;
	}

	public void write(String fileName) throws IOException {
		FileWriter fileWriter = new FileWriter(fileName);
		for (int i = 0; i < table.size(); i++) {
			Table temp = table.get(i);
			Set<String> keys = temp.NameType.keySet();
			for (String key : keys) {
				
				fileWriter.append(temp.name+", "+key+" ,"+temp.NameType.get(key)+" ,"+temp.key+", false,"+temp.refrences.get(key));
				fileWriter.append("\n");
			}
			

		}
		fileWriter.flush();
	    fileWriter.close();
		
	}

}
