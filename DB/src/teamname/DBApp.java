package teamname;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import javax.swing.text.StyledEditorKit.BoldAction;
import javax.xml.crypto.Data;

class DBAppException extends Exception implements Serializable{

}

class DBEngineException extends Exception {

}

public class DBApp {
	ArrayList<Table> tablesTemp;
	ArrayList<String> names;
    ArrayList<String> pagesNumber;
	public void init() throws IOException, ClassNotFoundException {
		pagesNumber=new ArrayList<String>();
		File x = new File("classes/array.ser");
		if(x.exists()){
		 FileInputStream fileIn = new FileInputStream("classes/array.ser");
         ObjectInputStream in = new ObjectInputStream(fileIn);
         tablesTemp = (ArrayList<Table>) in.readObject();
         in.close();
         fileIn.close();	
		}else{
			tablesTemp = new ArrayList<Table>();
			String fileLocation = "classes/array.ser"; //To be Modified
			FileOutputStream fileOut =
			         new FileOutputStream(fileLocation);
			ObjectOutputStream os = new ObjectOutputStream(fileOut);
			os.writeObject(tablesTemp);
		}
	}
	public ArrayList<Table> retrivetable() throws IOException, ClassNotFoundException{
		FileInputStream fileIn = new FileInputStream("classes/array.ser");
        ObjectInputStream in = new ObjectInputStream(fileIn);
        ArrayList<Table> t = (ArrayList<Table>) in.readObject();
        in.close();
        fileIn.close();	
		return t;
		
	}
	public void savetable(ArrayList<Table> t) throws IOException{
		String fileLocation = "classes/array.ser"; //To be Modified
		FileOutputStream fileOut =
		         new FileOutputStream(fileLocation);
		ObjectOutputStream os = new ObjectOutputStream(fileOut);
		os.writeObject(t);
	}
public void increment(String tbname) throws ClassNotFoundException, IOException{
	ArrayList<Table> t = retrivetable();
	for (int i = 0; i < t.size(); i++) {
		if(t.get(i).name.equals(tbname))
			t.get(i).pages=t.get(i).pages+1;
		
	}
	savetable(t);
}
	public void createTable(String strTableName,
			Hashtable<String, String> htblColNameType,
			Hashtable<String, String> htblColNameRefs, String strKeyColName)
			throws DBAppException, IOException, ClassNotFoundException {
		ArrayList<Table> tables = retrivetable();
		boolean flag = false;
		for(int i =0 ;i<tables.size();i++){
			if(tables.get(i).name.equals(strTableName)){
				flag = true;
				break;
			}
		}
		if(!flag){
			Table t1=new Table(strTableName, htblColNameType, htblColNameRefs, strKeyColName);
			tables.add(t1);
			String fileLocation = "classes/array.ser"; //To be Modified
			FileOutputStream fileOut =
			         new FileOutputStream(fileLocation);
			ObjectOutputStream os = new ObjectOutputStream(fileOut);
			os.writeObject(tables);
			System.out.println("The table "+strTableName+" created Succesfully");
		}else{
			System.out.println("The table "+strTableName+" is Already Exists");
		}
	}

	public void createIndex(String strTableName, String strColName)
			throws DBAppException, ClassNotFoundException, IOException {
        ArrayList<ArrayList<secondary>> sec =new ArrayList<ArrayList<secondary>>();
        ArrayList<Table> h = retrivetable();
        Table t = null;
        for (int i = 0; i < h.size(); i++) {
			if(h.get(i).name.equals(strTableName)){
				t=h.get(i);
				break;
			}
		}
        int pages=t.pages;
        for (int i = 0; i <pages; i++) {
        	Page p= retrivePage("classes/"+strTableName+""+(i+1));
        	ArrayList<Hashtable<String, Object>> arr = p.data;
        	for (int j = 0; j < arr.size(); j++) {
				Object ob=arr.get(j).get(strColName);
				boolean f = false;
				if(sec.isEmpty()){
					ArrayList<secondary> a = new ArrayList<secondary>();
					secondary s = new secondary(i+1, j, ob);
					a.add(s);
					sec.add(a);
				}else{
				for (int k = 0; k < sec.size(); k++) {
					if(sec.get(k).get(0).colName.equals(ob)){
						secondary s = new secondary(i+1, j, ob);
						sec.get(k).add(s);
						f=true;
					}
				}
				if(f==false){
					ArrayList<secondary> a = new ArrayList<secondary>();
					secondary s = new secondary(i+1, j, ob);
					a.add(s);
					sec.add(a);
				}
				}
			}
		}
        String name = strTableName+strColName;
        String fileLocation = "classes/"+name+".ser"; //To be Modified
		FileOutputStream fileOut =
		         new FileOutputStream(fileLocation);
		ObjectOutputStream os = new ObjectOutputStream(fileOut);
		os.writeObject(sec);
		System.out.println("The  "+strColName+" indexed Succesfully");
	}

	public void insertIntoTable(String strTableName,
			Hashtable<String, Object> htblColNameValue) throws DBAppException, ClassNotFoundException, IOException {
		Table temp =null;
		boolean flag = false;
		ArrayList<Table> tables = retrivetable();
		int indexOfTable = -1;
		for (int i = 0; i < tables.size(); i++) {
			if(tables.get(i).name.equals(strTableName)){
				temp = tables.get(i);
				indexOfTable=i;
				flag = true;
				break;
			}
		}
		if(flag){
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date date = new Date();
			System.out.println(dateFormat.format(date));
			htblColNameValue.put("TouchDate", dateFormat.format(date));
			saveRecords(strTableName,temp.pages , htblColNameValue,temp,indexOfTable);
			System.out.println("The record saved into the table "+strTableName);
		}else{
			System.out.println("The table "+strTableName+" is not exist");

		}
		Table x = tables.get(indexOfTable);
		ArrayList<String> types=new ArrayList<String>();
		Enumeration e = x.NameType.keys();
		// System.out.println(e.);
		    while (e.hasMoreElements()) {
		    	String key = (String) e.nextElement();
		    	 types.add(key);
		    }
		    for (int i = 0; i < types.size(); i++) {
		    	try{
			    	String cont = strTableName+types.get(i);
			    	FileInputStream fileIn = new FileInputStream("classes/"+cont+".ser");
			         ObjectInputStream in = new ObjectInputStream(fileIn);
			         createIndex(strTableName, types.get(i));
			         in.close();
			         fileIn.close();
			    }catch(Exception j){
			    	
			    }
			}
		    
		
		  //this.savetable(tables);
	}
	
	public void saveRecords(String name,int page,Hashtable<String, Object> record,Table temp,int tableIndex) throws ClassNotFoundException, IOException{
		String nameofpage = "classes/"+name +page;
		Page content=null;
		ArrayList<Table> tables = retrivetable();
		if(page!=0){
		content = retrivePage(nameofpage);
		Properties prop = new Properties();
		InputStream input = null;
		input = new FileInputStream("config/DBApp.properties");
		prop.load(input);
		 String value = prop.getProperty("MaximumRowsCountinPage");
		 int finalValue = Integer.parseInt(value);
		if(content.data.size()==finalValue){ // To be modified
			page++;
			nameofpage=name+page;
			for (int i = 0; i < tables.size(); i++) {
				if(tables.get(i).name.equals(name)){
					increment(name);
				}
			}
				Page x = new Page(nameofpage);
				x.data.add(record);
				Indecator ind = new Indecator(name, page, x.data.size()-1);
				//System.out.println(ind.pageNum);
				String s = null;
				if(record.get(temp.key) instanceof Integer){
					 s = ((Integer)record.get(temp.key)).intValue()+"";
				}else{
					 s = (String) record.get(temp.key);
				}
				//To be modified
				//System.out.println(s);
				ArrayList<Table> h = retrivetable();
				h.get(tableIndex).getBT().insert(s, ind);
				savetable(h);
				
				Indecator test = h.get(tableIndex).getBT().search(s);
				System.out.println(test.table);
				System.out.println(test.recordIndex);
				String fileLocation = "classes/"+nameofpage+".ser"; //To be Modified
				FileOutputStream fileOut =
				         new FileOutputStream(fileLocation);
				ObjectOutputStream os = new ObjectOutputStream(fileOut);
				os.writeObject(x);
				System.out.println("The page "+nameofpage+" created Succesfully");
			
		}else{
			
			Page y = retrivePage(nameofpage);
			y.data.add(record);
			Indecator ind = new Indecator(name, page, y.data.size()-1);
			//System.out.println(ind.pageNum);
			String s = null;
			if(record.get(temp.key) instanceof Integer){
				 s = ((Integer)record.get(temp.key)).intValue()+"";
			}else{
				 s = (String) record.get(temp.key);
			}
			//To be modified
			//System.out.println(s);
			ArrayList<Table> h = retrivetable();
			h.get(tableIndex).getBT().insert(s, ind);
			savetable(h);
			Indecator test = h.get(tableIndex).getBT().search(s);
			System.out.println(test.table);
			System.out.println(test.recordIndex);
			String fileLocation = nameofpage+".ser"; //To be Modified
			FileOutputStream fileOut =
			         new FileOutputStream(fileLocation);
			ObjectOutputStream os = new ObjectOutputStream(fileOut);
			os.writeObject(y);
			}}
		else{
			
			page++;
			nameofpage=name+page;
			for (int i = 0; i < tables.size(); i++) {
				if(tables.get(i).name.equals(name)){
					increment(name);
				}
			}
			
				Page x = new Page(nameofpage);
				x.data.add(record);
				Indecator ind = new Indecator(name, page, x.data.size()-1);
				System.out.println(ind.pageNum);
				String s = null;
				if(record.get(temp.key) instanceof Integer){
					 s = ((Integer)record.get(temp.key)).intValue()+"";
				}else{
					 s = (String) record.get(temp.key);
				}
				//To be modified
				//System.out.println(s);
				ArrayList<Table> h = retrivetable();
				h.get(tableIndex).getBT().insert(s, ind);
				savetable(h);
				Indecator test = h.get(tableIndex).getBT().search(s);
				System.out.println(test.table);
				System.out.println(test.recordIndex);
				String fileLocation = "classes/"+nameofpage+".ser"; //To be Modified
				FileOutputStream fileOut =
				         new FileOutputStream(fileLocation);
				ObjectOutputStream os = new ObjectOutputStream(fileOut);
				os.writeObject(x);
				System.out.println("The page "+nameofpage+" created Succesfully");
		}
	}
	public Page retrivePage(String name) throws IOException, ClassNotFoundException{
		Page array = null;
		File temp = new File(name+".ser");
		if(temp.exists()){
			 FileInputStream fileIn = new FileInputStream(name+".ser");
	         ObjectInputStream in = new ObjectInputStream(fileIn);
	         array = (Page) in.readObject();
	         in.close();
	         fileIn.close();
		}
		
			
         return array;
	}
	

	public void updateTable(String strTableName, String strKey,
			Hashtable<String, Object> htblColNameValue) throws DBAppException, IOException, ClassNotFoundException {
	
        ArrayList<Table> tables = retrivetable();
       // String key = null;
		Table table = null ;
		//int numberOfPages = 0;
		boolean flag = false;
		for(int i =0 ; i<tables.size();i++){
			if(tables.get(i).name.equals(strTableName)){
				table = tables.get(i);
				//key = table.key;
				//numberOfPages= table.pages;
				flag = true;
			}
		}
		if(flag){
			ArrayList<String> types = new ArrayList<String>(2);
			ArrayList<Object> values = new ArrayList<Object>(2);
			 Enumeration e = htblColNameValue.keys();
				// System.out.println(e.);
				    while (e.hasMoreElements()) {
				      String key = (String) e.nextElement();
				      //System.out.println(key);
				      types.add(key);
				      values.add(htblColNameValue.get(key));
				    }
				int numberOfPages = table.pages;
				//System.out.println(numberOfPages);
				
				if(table.key.equals(types.get(0))){
					try{
					BTree<String, Indecator> bt = table.getBT();
					Indecator ind = bt.search((String)values.get(0));
					String pageName = "classes/"+ind.table+ind.pageNum;
					System.out.println(pageName);
					Page finalPage = retrivePage(pageName);
					finalPage.data.remove(ind.recordIndex);
					bt.delete((String)values.get(0));
					Indecator newInd = new Indecator(strTableName, ind.pageNum, ind.recordIndex);
					bt.insert(""+htblColNameValue.get(table.key), newInd);
					finalPage.data.add(htblColNameValue);
					savetable(tables);
					//String fileLocation = "classes/array.ser"; //To be Modified
					FileOutputStream fileOut =
					         new FileOutputStream(pageName);
					ObjectOutputStream os = new ObjectOutputStream(fileOut);
					os.writeObject(finalPage);
					}catch(FileNotFoundException m){
						System.out.println("Record not found");
					}
				}else{
					try{
						FileInputStream fileIn3 = new FileInputStream("classes/"+table.name+types.get(0)+".ser");
				        ObjectInputStream in3 = new ObjectInputStream(fileIn3);
				        ArrayList<ArrayList<secondary>> sec1 = (ArrayList<ArrayList<secondary>>) in3.readObject();
				        ArrayList<secondary> sec3 = null;
				        secondary sec2 = null;
				        for (int i = 0; i < sec1.size(); i++) {
				        	
							if(sec1.get(i).get(0).colName.equals(values.get(0))){
								
								sec3= sec1.get(i);
								
							}
						}
				        System.out.println(sec3.size()+"Test");
				        for (int i = 0; i < sec3.size(); i++) {
				        	FileInputStream fileIn5 = new FileInputStream("classes/"+table.name+sec3.get(i).pageNum+".ser");
					         ObjectInputStream in5 = new ObjectInputStream(fileIn5);
					         
					         
				        	 Page p3 = (Page) in5.readObject();
				        	 //System.out.println(table.name+sec3.get(i).pageNum);
				        	 //System.out.println(p3.data);
						     p3.data.remove(sec3.get(i).index);
						     sec3.remove(i);
						     p3.data.add(htblColNameValue);
						     createIndex(strTableName,(String) htblColNameValue.get(strKey));
						     FileOutputStream fileOut =
							         new FileOutputStream("classes/"+table.name+types.get(0)+".ser");
						     //for secondary
							ObjectOutputStream os = new ObjectOutputStream(fileOut);
							os.writeObject(sec3);
							//for page
							FileOutputStream fileOut4 =
							         new FileOutputStream("classes/"+table.name+sec3.get(i).pageNum+".ser");
						     //for secondary
							ObjectOutputStream os4 = new ObjectOutputStream(fileOut4);
							os.writeObject(sec3);
				        	 in5.close();
					         fileIn5.close();
						}
				      
				        in3.close();
				        fileIn3.close();
					}catch(Exception k){
						createIndex(table.name, types.get(0));
						FileInputStream fileIn3 = new FileInputStream("classes/"+table.name+types.get(0)+".ser");
				        ObjectInputStream in3 = new ObjectInputStream(fileIn3);
				        ArrayList<ArrayList<secondary>> sec1 = (ArrayList<ArrayList<secondary>>) in3.readObject();
				        ArrayList<secondary> sec3 = null;
				        secondary sec2 = null;
				        for (int i = 0; i < sec1.size(); i++) {
				        	
							if(sec1.get(i).get(0).colName.equals(""+values.get(0))){
								System.out.println(values.get(0));
								sec3= sec1.get(i);
								System.out.println(sec3.size());
							}
						}
				        for (int i = 0; i < sec3.size(); i++) {
				        	FileInputStream fileIn5 = new FileInputStream("classes/"+table.name+sec3.get(i).pageNum+".ser");
					         ObjectInputStream in5 = new ObjectInputStream(fileIn5);
					         
					         
				        	 Page p3 = (Page) in5.readObject();
				        	 //System.out.println(table.name+sec3.get(i).pageNum);
				        	 //System.out.println(p3.data);
						     p3.data.remove(sec3.get(i).index);
						     sec3.remove(i);
						     p3.data.add(htblColNameValue);
						     createIndex(strTableName,(String) htblColNameValue.get(strKey));
						     FileOutputStream fileOut =
							         new FileOutputStream("classes/"+table.name+types.get(0)+".ser");
						     //for secondary
							ObjectOutputStream os = new ObjectOutputStream(fileOut);
							os.writeObject(sec3);
							//for page
							FileOutputStream fileOut4 =
							         new FileOutputStream("classes/"+table.name+sec3.get(i).pageNum+".ser");
						     //for secondary
							ObjectOutputStream os4 = new ObjectOutputStream(fileOut4);
							os.writeObject(sec3);
				        	 in5.close();
					         fileIn5.close();
						}
				      
				        in3.close();
				        fileIn3.close();	
					}
				}
				
			
			/*
			for (int i = 0; i < numberOfPages; i++) {
				Page p= retrivePage("classes/"+strTableName+""+(i+1));
				for(int j = 0;j<p.data.size();j++){
					Hashtable<String, Object> temp = p.data.get(j);
					  //System.out.println("test");
					
					String s = ""+temp.get(key);
					//System.out.println(s+" : "+strKey);
					if(s.equals(strKey)){
						
						System.out.println("Test");
						p.data.remove(j);
						p.data.add(j,htblColNameValue);
						System.out.println(p.data.get(j).toString());
						String fileLocation = "classes/"+strTableName+(i+1)+".ser"; //To be Modified
						System.out.println(fileLocation);
						FileOutputStream fileOut =
						         new FileOutputStream(fileLocation);
						ObjectOutputStream os = new ObjectOutputStream(fileOut);
						os.writeObject(p);
						
						os.close();
						fileOut.close();
						
					}
						
		            
				}
			}
			*/
		}else{
			System.out.println("The table "+strTableName+" Doesn,t exist");
		}
		
           
	}

	public void deleteFromTable(String strTableName,
			Hashtable<String, Object> htblColNameValue, String strOperator)
			throws DBEngineException, ClassNotFoundException, IOException, DBAppException {
		ArrayList<Hashtable<String, Object>> a1 = new ArrayList<Hashtable<String,Object>>();
		ArrayList<String> types = new ArrayList<String>(2);
		ArrayList<Object> values = new ArrayList<Object>(2);
		ArrayList<Table> tables = retrivetable();
		
		 FileInputStream fileIn = new FileInputStream("classes/array.ser");
         ObjectInputStream in = new ObjectInputStream(fileIn);
       // ArrayList<Table> ttt = (ArrayList<Table>) in.readObject();
         in.close();
         fileIn.close();
		
		Table table = null ;
		boolean flag = false;
		for(int i =0 ; i<tables.size();i++){
			if(tables.get(i).name.equals(strTableName)){
				table = tables.get(i);
				flag = true;
			}
		}
		if(flag){
			
			 Enumeration e = htblColNameValue.keys();
			// System.out.println(e.);
			    while (e.hasMoreElements()) {
			      String key = (String) e.nextElement();
			      //System.out.println(key);
			      types.add(key);
			      values.add(htblColNameValue.get(key));
			    }
			int numberOfPages = table.pages;
			//System.out.println(numberOfPages);
			
			if(table.key.equals(types.get(0))){
				try{
				BTree<String, Indecator> bt = table.getBT();
				Indecator ind = bt.search((String)values.get(0));
				String pageName = "classes/"+ind.table+ind.pageNum;
				System.out.println(pageName);
				Page finalPage = retrivePage(pageName);
				finalPage.data.remove(ind.recordIndex);
				bt.delete((String)values.get(0));
				savetable(tables);
				//String fileLocation = "classes/array.ser"; //To be Modified
				FileOutputStream fileOut =
				         new FileOutputStream(pageName);
				ObjectOutputStream os = new ObjectOutputStream(fileOut);
				os.writeObject(finalPage);
				}catch(Exception m){
					System.out.println("Record not found");
				}
			}else{
				try{
					FileInputStream fileIn3 = new FileInputStream("classes/"+table.name+types.get(0)+".ser");
			        ObjectInputStream in3 = new ObjectInputStream(fileIn3);
			        ArrayList<ArrayList<secondary>> sec1 = (ArrayList<ArrayList<secondary>>) in3.readObject();
			        ArrayList<secondary> sec3 = null;
			        secondary sec2 = null;
			        for (int i = 0; i < sec1.size(); i++) {
			        	
						if(sec1.get(i).get(0).colName.equals(values.get(0))){
							
							sec3= sec1.get(i);
							
						}
					}
			        System.out.println(sec3.size()+"Test");
			        for (int i = 0; i < sec3.size(); i++) {
			        	FileInputStream fileIn5 = new FileInputStream("classes/"+table.name+sec3.get(i).pageNum+".ser");
				         ObjectInputStream in5 = new ObjectInputStream(fileIn5);
				         
				         
			        	 Page p3 = (Page) in5.readObject();
			        	 //System.out.println(table.name+sec3.get(i).pageNum);
			        	 //System.out.println(p3.data);
					     p3.data.remove(sec3.get(i).index);
					     sec3.remove(i);
					     
					     FileOutputStream fileOut =
						         new FileOutputStream("classes/"+table.name+types.get(0)+".ser");
					     //for secondary
						ObjectOutputStream os = new ObjectOutputStream(fileOut);
						os.writeObject(sec3);
						//for page
						FileOutputStream fileOut4 =
						         new FileOutputStream("classes/"+table.name+sec3.get(i).pageNum+".ser");
					     //for secondary
						ObjectOutputStream os4 = new ObjectOutputStream(fileOut4);
						os.writeObject(sec3);
			        	 in5.close();
				         fileIn5.close();
					}
			      
			        in3.close();
			        fileIn3.close();
				}catch(Exception k){
					createIndex(table.name, types.get(0));
					FileInputStream fileIn3 = new FileInputStream("classes/"+table.name+types.get(0)+".ser");
			        ObjectInputStream in3 = new ObjectInputStream(fileIn3);
			        ArrayList<ArrayList<secondary>> sec1 = (ArrayList<ArrayList<secondary>>) in3.readObject();
			        ArrayList<secondary> sec3 = null;
			        secondary sec2 = null;
			        for (int i = 0; i < sec1.size(); i++) {
			        	
						if(sec1.get(i).get(0).colName.equals(""+values.get(0))){
							System.out.println(values.get(0));
							sec3= sec1.get(i);
							System.out.println(sec3.size());
						}
					}
			        for (int i = 0; i < sec3.size(); i++) {
			        	FileInputStream fileIn5 = new FileInputStream("classes/"+table.name+sec3.get(i).pageNum+".ser");
				         ObjectInputStream in5 = new ObjectInputStream(fileIn5);
				         
				         
			        	 Page p3 = (Page) in5.readObject();
			        	 //System.out.println(table.name+sec3.get(i).pageNum);
			        	 //System.out.println(p3.data);
					     p3.data.remove(sec3.get(i).index);
					     sec3.remove(i);
					     
					     FileOutputStream fileOut =
						         new FileOutputStream("classes/"+table.name+types.get(0)+".ser");
					     //for secondary
						ObjectOutputStream os = new ObjectOutputStream(fileOut);
						os.writeObject(sec3);
						//for page
						FileOutputStream fileOut4 =
						         new FileOutputStream("classes/"+table.name+sec3.get(i).pageNum+".ser");
					     //for secondary
						ObjectOutputStream os4 = new ObjectOutputStream(fileOut4);
						os.writeObject(sec3);
			        	 in5.close();
				         fileIn5.close();
					}
			      
			        in3.close();
			        fileIn3.close();	
				}
			}
			
			
			/*
			for(int i =0 ;i<numberOfPages;i++){
				//System.out.println("test1");
				Page p= retrivePage("classes/"+strTableName+(i+1));
				for(int j = 0;j<p.data.size();j++){
					Hashtable<String, Object> temp = p.data.get(j);
					  //System.out.println("test");
						if(strOperator.equals("AND")){
							//System.out.println("test AND");
							if(((p.data.get(j).get(types.get(0)).equals(values.get(0)))) &&(p.data.get(j).get(types.get(1)).equals(values.get(1)))) {
								System.out.println(p.data.get(j).toString()+" is removed");
								p.data.remove(j);
								
								String fileLocation = "classes/"+strTableName+(i+1)+".ser"; //To be Modified
								
								FileOutputStream fileOut =
								         new FileOutputStream(fileLocation);
								ObjectOutputStream os = new ObjectOutputStream(fileOut);
								os.writeObject(p);
								
							}
						}else{
							if(((p.data.get(j).get(types.get(0)).equals(values.get(0)))) || (p.data.get(j).get(types.get(1)).equals(values.get(1))))  {
								System.out.println(p.data.get(j).toString()+" is removed");
								p.data.remove(j);
								System.out.println(p.data.get(j).toString());
								String fileLocation = "classes/"+strTableName+(i+1)+".ser"; //To be Modified
								System.out.println(fileLocation);
								FileOutputStream fileOut =
								         new FileOutputStream(fileLocation);
								ObjectOutputStream os = new ObjectOutputStream(fileOut);
								os.writeObject(p);
							}
						}
						
		            
				}
				
			}
			*/
		}else{
			System.out.println("The table "+strTableName+" is Not Exist");
		}
		//Page p= retrivePage(strTable+"1");
		
		

	}

	public Iterator selectFromTable(String strTable,
			Hashtable<String, Object> htblColNameValue, String strOperator)
			throws DBEngineException, ClassNotFoundException, IOException, DBAppException {
		
		ArrayList<Hashtable<String, Object>> a1 = new ArrayList<Hashtable<String,Object>>();
		ArrayList<String> types = new ArrayList<String>(2);
		ArrayList<Object> values = new ArrayList<Object>(2);
		ArrayList<Table> tables = retrivetable();
		
		 FileInputStream fileIn = new FileInputStream("classes/array.ser");
         ObjectInputStream in = new ObjectInputStream(fileIn);
       // ArrayList<Table> ttt = (ArrayList<Table>) in.readObject();
         in.close();
         fileIn.close();
		
		Table table = null ;
		boolean flag = false;
		for(int i =0 ; i<tables.size();i++){
			if(tables.get(i).name.equals(strTable)){
				table = tables.get(i);
				flag = true;
			}
		}
		if(flag){
			
			 Enumeration e = htblColNameValue.keys();
			// System.out.println(e.);
			    while (e.hasMoreElements()) {
			      String key = (String) e.nextElement();
			      //System.out.println(key);
			      types.add(key);
			      values.add(htblColNameValue.get(key));
			    }
			int numberOfPages = table.pages;
			//System.out.println(numberOfPages);
			
			if(table.key.equals(types.get(0))){
				try{
				BTree<String, Indecator> bt = table.getBT();
				Indecator ind = bt.search((String)values.get(0));
				String pageName = "classes/"+ind.table+ind.pageNum;
				System.out.println(pageName);
				Page finalPage = retrivePage(pageName);
				a1.add(finalPage.data.get(ind.recordIndex));
				}catch(Exception m){
					System.out.println("Record not found");
				}
			}else{
				try{
					FileInputStream fileIn3 = new FileInputStream("classes/"+table.name+types.get(0)+".ser");
			        ObjectInputStream in3 = new ObjectInputStream(fileIn3);
			        ArrayList<ArrayList<secondary>> sec1 = (ArrayList<ArrayList<secondary>>) in3.readObject();
			        ArrayList<secondary> sec3 = null;
			        secondary sec2 = null;
			        for (int i = 0; i < sec1.size(); i++) {
			        	
						if(sec1.get(i).get(0).colName.equals(values.get(0))){
							
							sec3= sec1.get(i);
							
						}
					}
			        System.out.println(sec3.size()+"Test");
			        for (int i = 0; i < sec3.size(); i++) {
			        	FileInputStream fileIn5 = new FileInputStream("classes/"+table.name+sec3.get(i).pageNum+".ser");
				         ObjectInputStream in5 = new ObjectInputStream(fileIn5);
				         
				         
			        	 Page p3 = (Page) in5.readObject();
			        	 //System.out.println(table.name+sec3.get(i).pageNum);
			        	 //System.out.println(p3.data);
					     a1.add(p3.data.get(sec3.get(i).index));
			        	 in5.close();
				         fileIn5.close();
					}
			      
			        in3.close();
			        fileIn3.close();
				}catch(Exception k){
					createIndex(table.name, types.get(0));
					FileInputStream fileIn3 = new FileInputStream("classes/"+table.name+types.get(0)+".ser");
			        ObjectInputStream in3 = new ObjectInputStream(fileIn3);
			        ArrayList<ArrayList<secondary>> sec1 = (ArrayList<ArrayList<secondary>>) in3.readObject();
			        ArrayList<secondary> sec3 = null;
			        secondary sec2 = null;
			        for (int i = 0; i < sec1.size(); i++) {
			        	
						if(sec1.get(i).get(0).colName.equals(""+values.get(0))){
							System.out.println(values.get(0));
							sec3= sec1.get(i);
							System.out.println(sec3.size());
						}
					}
			        for (int i = 0; i < sec3.size(); i++) {
			        	FileInputStream fileIn5 = new FileInputStream("classes/"+table.name+sec3.get(i).pageNum+".ser");
				         ObjectInputStream in5 = new ObjectInputStream(fileIn5);
				         
				         
			        	 Page p3 = (Page) in5.readObject();
			        	 //System.out.println(table.name+sec3.get(i).pageNum);
			        	 //System.out.println(p3.data);
					     a1.add(p3.data.get(sec3.get(i).index));
			        	 in5.close();
				         fileIn5.close();
					}
			      
			        in3.close();
			        fileIn3.close();	
				}
			}
			/*
			for(int i =0 ;i<numberOfPages;i++){
				//System.out.println("test1");
				Page p= retrivePage("classes/"+strTable+(i+1));
				for(int j = 0;j<p.data.size();j++){
					Hashtable<String, Object> temp = p.data.get(j);
					  //System.out.println("test");
						if(strOperator.equals("AND")){
							//System.out.println("test AND");
							if(((p.data.get(j).get(types.get(0)).equals(values.get(0)))) &&(p.data.get(j).get(types.get(1)).equals(values.get(1)))) {
								a1.add(p.data.get(j));
							}
						}else{
							if(((p.data.get(j).get(types.get(0)).equals(values.get(0)))) || (p.data.get(j).get(types.get(1)).equals(values.get(1))))  {
                            	a1.add(p.data.get(j));	
							}
						}
						
		            
				}
				
			}
			*/
		}else{
			System.out.println("The table "+strTable+" is Not Exist");
		}
		//Page p= retrivePage(strTable+"1");
		
		Iterator it = a1.iterator();
		return it;
	}

	public static void main(String[] args) throws DBAppException,
			DBEngineException, IOException, ClassNotFoundException {
		// creat a new DBApp
		DBApp myDB = new DBApp();

		// initialize it
		myDB.init();

		// creating table "Faculty"

		Hashtable<String, String> fTblColNameType = new Hashtable<String, String>();
		fTblColNameType.put("ID", "Integer");
		fTblColNameType.put("Name", "String");

		Hashtable<String, String> fTblColNameRefs = new Hashtable<String, String>();

		myDB.createTable("Faculty", fTblColNameType, fTblColNameRefs, "ID");

		// creating table "Major"
/*
		Hashtable<String, String> mTblColNameType = new Hashtable<String, String>();
		fTblColNameType.put("ID", "Integer");
		fTblColNameType.put("Name", "String");
		fTblColNameType.put("Faculty_ID", "Integer");

		Hashtable<String, String> mTblColNameRefs = new Hashtable<String, String>();
		mTblColNameRefs.put("Faculty_ID", "Faculty.ID");

		myDB.createTable("Major", mTblColNameType, mTblColNameRefs, "ID");

		// creating table "Course"

		Hashtable<String, String> coTblColNameType = new Hashtable<String, String>();
		coTblColNameType.put("ID", "Integer");
		coTblColNameType.put("Name", "String");
		coTblColNameType.put("Code", "String");
		coTblColNameType.put("Hours", "Integer");
		coTblColNameType.put("Semester", "Integer");
		coTblColNameType.put("Major_ID", "Integer");

		Hashtable<String, String> coTblColNameRefs = new Hashtable<String, String>();
		coTblColNameRefs.put("Major_ID", "Major.ID");

		myDB.createTable("Course", coTblColNameType, coTblColNameRefs, "ID");

		// creating table "Student"

		Hashtable<String, String> stTblColNameType = new Hashtable<String, String>();
		stTblColNameType.put("ID", "Integer");
		stTblColNameType.put("First_Name", "String");
		stTblColNameType.put("Last_Name", "String");
		stTblColNameType.put("GPA", "Double");
		stTblColNameType.put("Age", "Integer");

		Hashtable<String, String> stTblColNameRefs = new Hashtable<String, String>();

		myDB.createTable("Student", stTblColNameType, stTblColNameRefs, "ID");

		// creating table "Student in Course"

		Hashtable<String, String> scTblColNameType = new Hashtable<String, String>();
		scTblColNameType.put("ID", "Integer");
		scTblColNameType.put("Student_ID", "Integer");
		scTblColNameType.put("Course_ID", "Integer");

		Hashtable<String, String> scTblColNameRefs = new Hashtable<String, String>();
		scTblColNameRefs.put("Student_ID", "Student.ID");
		scTblColNameRefs.put("Course_ID", "Course.ID");

		myDB.createTable("Student_in_Course", scTblColNameType,
				scTblColNameRefs, "ID");

		// insert in table "Faculty"

		
		Hashtable<String, Object> ftblColNameValue1 = new Hashtable<String, Object>();
		ftblColNameValue1.put("ID", Integer.valueOf("1004"));
		ftblColNameValue1.put("Name", "Media Engineering and Technology");
		myDB.insertIntoTable("Faculty", ftblColNameValue1);
/*
		Hashtable<String, Object> ftblColNameValue2 = new Hashtable<String, Object>();
		ftblColNameValue2.put("ID", Integer.valueOf("2"));
		ftblColNameValue2.put("Name", "Management Technology");
		myDB.insertIntoTable("Faculty", ftblColNameValue2);

		for (int i = 0; i < 1000; i++) {
			Hashtable<String, Object> ftblColNameValueI = new Hashtable<String, Object>();
			ftblColNameValueI.put("ID", Integer.valueOf(("" + (i + 2))));
			ftblColNameValueI.put("Name", "f" + (i + 2));
			myDB.insertIntoTable("Faculty", ftblColNameValueI);
		}

		// insert in table "Major"
/*
		Hashtable<String, Object> mtblColNameValue1 = new Hashtable<String, Object>();
		mtblColNameValue1.put("ID", Integer.valueOf("1"));
		mtblColNameValue1.put("Name", "Computer Science & Engineering");
		mtblColNameValue1.put("Faculty_ID", Integer.valueOf("1"));
		myDB.insertIntoTable("Major", mtblColNameValue1);

		Hashtable<String, Object> mtblColNameValue2 = new Hashtable<String, Object>();
		mtblColNameValue2.put("ID", Integer.valueOf("2"));
		mtblColNameValue2.put("Name", "Business Informatics");
		mtblColNameValue2.put("Faculty_ID", Integer.valueOf("2"));
		myDB.insertIntoTable("Major", mtblColNameValue2);

		for (int i = 0; i < 1000; i++) {
			Hashtable<String, Object> mtblColNameValueI = new Hashtable<String, Object>();
			mtblColNameValueI.put("ID", Integer.valueOf(("" + (i + 2))));
			mtblColNameValueI.put("Name", "m" + (i + 2));
			mtblColNameValueI
					.put("Faculty_ID", Integer.valueOf(("" + (i + 2))));
			myDB.insertIntoTable("Major", mtblColNameValueI);
		}

		// insert in table "Course"

		Hashtable<String, Object> ctblColNameValue1 = new Hashtable<String, Object>();
		ctblColNameValue1.put("ID", Integer.valueOf("1"));
		ctblColNameValue1.put("Name", "Data Bases II");
		ctblColNameValue1.put("Code", "CSEN 604");
		ctblColNameValue1.put("Hours", Integer.valueOf("4"));
		ctblColNameValue1.put("Semester", Integer.valueOf("6"));
		ctblColNameValue1.put("Major_ID", Integer.valueOf("1"));
		myDB.insertIntoTable("Course", mtblColNameValue1);

		Hashtable<String, Object> ctblColNameValue2 = new Hashtable<String, Object>();
		ctblColNameValue2.put("ID", Integer.valueOf("1"));
		ctblColNameValue2.put("Name", "Data Bases II");
		ctblColNameValue2.put("Code", "CSEN 604");
		ctblColNameValue2.put("Hours", Integer.valueOf("4"));
		ctblColNameValue2.put("Semester", Integer.valueOf("6"));
		ctblColNameValue2.put("Major_ID", Integer.valueOf("2"));
		myDB.insertIntoTable("Course", mtblColNameValue2);

		for (int i = 0; i < 1000; i++) {
			Hashtable<String, Object> ctblColNameValueI = new Hashtable<String, Object>();
			ctblColNameValueI.put("ID", Integer.valueOf(("" + (i + 2))));
			ctblColNameValueI.put("Name", "c" + (i + 2));
			ctblColNameValueI.put("Code", "co " + (i + 2));
			ctblColNameValueI.put("Hours", Integer.valueOf("4"));
			ctblColNameValueI.put("Semester", Integer.valueOf("6"));
			ctblColNameValueI.put("Major_ID", Integer.valueOf(("" + (i + 2))));
			myDB.insertIntoTable("Course", ctblColNameValueI);
		}

		// insert in table "Student"

		for (int i = 0; i < 1000; i++) {
			Hashtable<String, Object> sttblColNameValueI = new Hashtable<String, Object>();
			sttblColNameValueI.put("ID", Integer.valueOf(("" + i)));
			sttblColNameValueI.put("First_Name", "FN" + i);
			sttblColNameValueI.put("Last_Name", "LN" + i);
			sttblColNameValueI.put("GPA", Double.valueOf("0.7"));
			sttblColNameValueI.put("Age", Integer.valueOf("20"));
			myDB.insertIntoTable("Student", sttblColNameValueI);
			// changed it to student instead of course
		}

	
        Hashtable<String, Object> stblColNameValue = new Hashtable<String, Object>();
		stblColNameValue.put("ID", Integer.valueOf("2000"));
		stblColNameValue.put("Name", "Media Engineer");

		long startTime = System.currentTimeMillis();
		Iterator myIt = myDB
				.selectFromTable("Faculty", stblColNameValue, "OR");
		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println(totalTime);
		while (myIt.hasNext()) {
			System.out.println(myIt.next());
}
     */
		// feel free to add more tests
		Hashtable<String, Object> stblColNameValue3 = new Hashtable<String, Object>();
	stblColNameValue3.put("ID", "2");
	//stblColNameValue3.put("Name", "f300");

		long startTime2 = System.currentTimeMillis();
		Iterator myIt2 = myDB
				.selectFromTable("Faculty", stblColNameValue3, "AND");
		long endTime2 = System.currentTimeMillis();
		long totalTime2 = endTime2 - startTime2;
		System.out.println(totalTime2);
		while (myIt2.hasNext()) {
			System.out.println(myIt2.next());
		}
		Hashtable<String, Object> stblColNameValue4 = new Hashtable<String, Object>();
		stblColNameValue4.put("ID", "2");
		//stblColNameValue3.put("Name", "f300");

			long startTime4 = System.currentTimeMillis();
			 myDB.deleteFromTable("Faculty", stblColNameValue4, "AND");
			long endTime4 = System.currentTimeMillis();
			long totalTime4 = endTime4 - startTime4;
			System.out.println(totalTime4);
			//while (myIt2.hasNext()) {
				//System.out.println(myIt2.next());
			//}
			
			Hashtable<String, Object> stblColNameValue5 = new Hashtable<String, Object>();
			stblColNameValue5.put("ID", "2");
			//stblColNameValue3.put("Name", "f300");

				long startTime5 = System.currentTimeMillis();
				Iterator myIt5 = myDB
						.selectFromTable("Faculty", stblColNameValue5, "AND");
				long endTime5 = System.currentTimeMillis();
				long totalTime5 = endTime5 - startTime5;
				System.out.println(totalTime5);
				while (myIt5.hasNext()) {
					System.out.println(myIt5.next());
				}
			
		/*
		ArrayList<Table> csv = myDB.retrivetable();
		csvWriter csWriter = new csvWriter(csv);
		csWriter.write("data/meta.csv");

		Hashtable<String, Object> ftblColNameValue20 = new Hashtable<String, Object>();
		ftblColNameValue20.put("ID", Integer.valueOf("2000"));
		ftblColNameValue20.put("Name", "Media Engineering and Technology Test");
		myDB.updateTable("Faculty", "1", ftblColNameValue20);
		
		//selecting
		
		 Hashtable<String, Object> stblColNameValue5 = new Hashtable<String, Object>();
			stblColNameValue5.put("ID", Integer.valueOf("2000"));
			stblColNameValue5.put("Name", "Media Engineering and Technology Test");

			long startTime5 = System.currentTimeMillis();
			Iterator myIt5 = myDB
					.selectFromTable("Faculty", stblColNameValue5, "AND");
			long endTime5 = System.currentTimeMillis();
			long totalTime5 = endTime5 - startTime5;
			System.out.println(totalTime5);
			while (myIt5.hasNext()) {
				System.out.println(myIt5.next());
			}
			myDB.deleteFromTable("Faculty", stblColNameValue5, "AND");
			
		 Hashtable<String, Object> stblColNameValue5 = new Hashtable<String, Object>();
			stblColNameValue5.put("ID", Integer.valueOf("2000"));
			stblColNameValue5.put("Name", "Media Engineering and Technology Test");

			long startTime5 = System.currentTimeMillis();
			Iterator myIt5 = myDB
					.selectFromTable("Faculty", stblColNameValue5, "AND");
			long endTime5 = System.currentTimeMillis();
			long totalTime5 = endTime5 - startTime5;
			System.out.println(totalTime5);
			while (myIt5.hasNext()){
				System.out.println(myIt5.next());
			}
			
		 myDB.createIndex("Faculty", "Name");
		 FileInputStream fileIn = new FileInputStream("classes/FacultyName.ser");
         ObjectInputStream in = new ObjectInputStream(fileIn);
         ArrayList<ArrayList<Object>> test2 = (ArrayList<ArrayList<Object>>) in.readObject();
         System.out.println(((secondary)test2.get(260).get(0)).pageNum);
         in.close();
         fileIn.close();
         */
	}

	

}
