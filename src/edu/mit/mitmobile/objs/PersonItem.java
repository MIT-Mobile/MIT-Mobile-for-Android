package edu.mit.mitmobile.objs;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PersonItem {

	public String uid;
	public List<String> givenname;
	public List<String> surname;
	
	public List<String> dept;
	public List<String> email;
	public List<String> fax;
	public List<String> office;

	public List<String> phone;
	public List<String> title;
	public Date lastUpdate;
	public Date lastViewed = null;
	
	@Override
	public String toString() {
		return getName();
	}
	
	public String getName() {
		String name = "";
		
		if(!givenname.isEmpty()) {
			name += givenname.get(0);
		}
		
		if(!givenname.isEmpty() && !surname.isEmpty()) {
			name += " ";
		}
		
		if(!surname.isEmpty()) {
			name += surname.get(0);
		}
		
		return name;
	}
	
	public String getTitle() {
		if(!title.isEmpty()) {
			return title.get(0);
		} else {
			return null;
		}
	}
	
	public List<PersonDetailItem> getPersonDetails() {
		ArrayList<PersonDetailItem> items = new ArrayList<PersonDetailItem>();
		
		// perhaps this should be redone with a 2 level loop
		for(String value : dept) {
			items.add(new PersonDetailItem("dept", value));
		}
		
		for(String value : email) {
			items.add(new PersonDetailItem("email", value));
		}
		
		for(String value : fax) {
			items.add(new PersonDetailItem("fax", value));
		}
		
		for(String value : office) {
			items.add(new PersonDetailItem("office", value));
		}
		
		for(String value : phone) {
			items.add(new PersonDetailItem("phone", value));
		}
		
		return items;
	}
	
	public static class PersonDetailItem {
		private String mType;
		private String mValue;
		
		
		public PersonDetailItem(String type, String value) {
			mType = type;
			mValue = value;
		}
		
		public String getType() {
			return mType;
		}
		
		public String getValue() {
			return mValue;
		}
		
	}
	
	public static final class PersonDetailViewMode {
		public static final int SEARCH = 0;
		public static final int RECENT = 1;
	}
	
}
