package teamname;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.text.html.HTMLDocument.Iterator;

public class Table implements Serializable {
	String name;
	Hashtable<String, String> NameType;
	Hashtable<String, String> refrences;
	String key;
	int pages;
	public Table(String name, Hashtable<String, String> nameType,
			Hashtable<String, String> refrences, String key) {
		pages=0;
		Hashtable<String, Object> main = new Hashtable<String, Object>();
		this.name = name;
		NameType = nameType;
		this.refrences = refrences;
		this.key = key;
	}

	
	
	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}
   
	


	public Hashtable<String, String> getNameType() {
		return NameType;
	}



	public void setNameType(Hashtable<String, String> nameType) {
		NameType = nameType;
	}



	public Hashtable<String, String> getRefrences() {
		return refrences;
	}



	public void setRefrences(Hashtable<String, String> refrences) {
		this.refrences = refrences;
	}



	public String getKey() {
		return key;
	}



	public void setKey(String key) {
		this.key = key;
	}
	
	




	
	
	
	

}
