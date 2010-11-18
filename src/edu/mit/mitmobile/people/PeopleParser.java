package edu.mit.mitmobile.people;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.mit.mitmobile.objs.PersonItem;

public class PeopleParser {
	
	
	static ArrayList<PersonItem> parseJSONArray(JSONArray jPeople) {
		try {
			Date currentDate = new Date();
			ArrayList<PersonItem> people = new ArrayList<PersonItem>();
			
			for(int index=0; index < jPeople.length(); index++) {
				JSONObject jPerson = jPeople.getJSONObject(index);
				PersonItem person = new PersonItem();
				
				// required fields
				person.uid = jPerson.getString("id");
				
				person.surname = getStringList(jPerson, "surname");
				person.givenname = getStringList(jPerson, "givenname");
				
				// optional fields
				person.dept = getStringList(jPerson, "dept");
				person.email = getStringList(jPerson, "email");
				person.fax = getStringList(jPerson, "fax");
				person.phone = getStringList(jPerson, "phone");
				person.office = getStringList(jPerson, "office");
				person.title = getStringList(jPerson, "title");
				
				// date stamp (for caching purposes) 
				person.lastUpdate = currentDate;
				
				people.add(person);			
			}
			
			return people;
			
		} catch (JSONException e) {
			e.printStackTrace();
			throw new RuntimeException("Error parsing people search results");
		}
	}

	private static List<String> getStringList(JSONObject jObject, String key) {
		try {
			if(jObject.has(key)) {
				JSONArray jArray = jObject.getJSONArray(key);
				ArrayList<String> strings = new ArrayList<String>();
				for(int index=0; index < jArray.length(); index++) {
					strings.add(jArray.getString(index));
				}
			
				return strings;
			} else {
				return Collections.emptyList();
			}
		} catch (JSONException e) {
			e.printStackTrace();
			throw new RuntimeException("Error parsing people search results");
		}
	}
}
