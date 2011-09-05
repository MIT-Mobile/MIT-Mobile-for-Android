package edu.mit.mitmobile2.libraries;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.mit.mitmobile2.libraries.BookItem.Holding;
import edu.mit.mitmobile2.libraries.LibraryActivity.LinkItem;
import edu.mit.mitmobile2.libraries.LibraryItem.Hours;
import edu.mit.mitmobile2.libraries.LibraryItem.Schedule;

public class LibraryParser {
    static ArrayList<LibraryItem> parseLibrary(JSONArray array) {
        ArrayList<LibraryItem> libraries = new ArrayList<LibraryItem>();

        try {
            for (int index = 0; index < array.length(); index++) {
                JSONObject object = array.getJSONObject(index);
                LibraryItem library = new LibraryItem();
                library.library = object.getString("library");
                library.status = object.getString("status");
                libraries.add(library);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            throw new RuntimeException("Error parsing libraries");
        }

        return libraries;
    }
    
    
    static void parseLibraryDetail(JSONObject object, LibraryItem container) {
        try {
            container.hoursToday = object.getString("hours_today");
            container.url = object.getString("url");
            container.tel = object.getString("tel");
            container.location = object.getString("location");
            JSONObject temp = object.getJSONObject("schedule");
            if(temp.has("current_term")) {
                container.currentTerm = getSchedule(temp.getJSONObject("current_term"), true);
            }
            if(temp.has("previous_terms")) {
                container.previousTerms = getPreviousTerms(temp.getJSONArray("previous_terms"));
            }
            
            container.isDetailLoaded = true;
        } catch (JSONException e) {
            e.printStackTrace();
            throw new RuntimeException("Error parsing library details");
        }
    }
    
    
    private static Schedule getSchedule(JSONObject object, boolean isCurrentTerm) throws JSONException {
        Schedule schedule = new Schedule();
        schedule.range_start = new Date(object.getJSONObject("range").getLong("start") * 1000);
        schedule.range_end = new Date(object.getJSONObject("range").getLong("end") * 1000);
        schedule.name = object.getString("name");
        if(!isCurrentTerm) {
            schedule.termday = object.getString("termday");
        }
        
        JSONArray hoursArray = object.getJSONArray("hours");
        ArrayList<Hours> hoursList = new ArrayList<Hours>();
        for(int index=0; index < hoursArray.length(); index++) {
        	JSONObject hoursJson = hoursArray.getJSONObject(index);
        	Hours hours = new Hours();
        	hours.title = hoursJson.getString("title");
        	hours.description = hoursJson.getString("description");
        	hoursList.add(hours);
        }
        schedule.hours = hoursList;
        return schedule;
    }
    
    
    private static List<Schedule> getPreviousTerms(JSONArray array) throws JSONException {
        ArrayList<Schedule> terms = new ArrayList<Schedule>();
        
        for(int index = 0; index < array.length(); index++) {
            terms.add(getSchedule(array.getJSONObject(index), false));
        }
        
        return terms;
    }
    
    
    static ArrayList<BookItem> parseBooks(JSONArray array) {
        ArrayList<BookItem> books = new ArrayList<BookItem>();

        try {
            for (int index = 0; index < array.length(); index++) {
                JSONObject object = array.getJSONObject(index);
                
                BookItem book = new BookItem();
                book.id = object.getString("id");
                book.title = object.getString("title");
                book.image = object.getString("image");
                if(object.has("author")) {
                    book.author = getArray(object.getJSONArray("author"));
                }
                if(object.has("year")) {
                    book.year = getArray(object.getJSONArray("year"));
                }
                if(object.has("publisher")) {
                    book.publisher = getArray(object.getJSONArray("publisher"));
                }
                if(object.has("isbn")) {
                    book.isbn = getArray(object.getJSONArray("isbn"));
                }
                
                books.add(book);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            throw new RuntimeException("Error parsing book search results");
        }

        return books;
    }
    
    private static ArrayList<String> getArray(JSONArray array) throws JSONException {
        ArrayList<String> list = new ArrayList<String>();
        for(int index1 = 0; index1 < array.length(); index1++) {
            list.add(array.getString(index1));
        }
        return list; 
    }
    
    static void parseBookDetail(JSONObject object, BookItem book) {
        try {
            book.subjects = getArray(object.getJSONArray("subject"));
            book.lang = getArray(object.getJSONArray("lang"));
            book.extent = getArray(object.getJSONArray("extent"));
            book.summary = getArray(object.getJSONArray("summary"));
            book.address = getArray(object.getJSONArray("address"));
            book.holdings = parseHoldings(object.getJSONArray("holdings"));
            
            
        } catch (JSONException e) {
            e.printStackTrace();
            throw new RuntimeException("Error parsing book details");
        }
    }
    
    private static ArrayList<Holding> parseHoldings(JSONArray array) throws JSONException {
        ArrayList<Holding> list = new ArrayList<Holding>();
        
        for(int index = 0; index < array.length(); index++) {
            JSONObject object = array.getJSONObject(index);
            Holding holding = new Holding();
            holding.library = object.getString("library");
            holding.address = object.getString("address");
            holding.url = object.getString("url");
            
            list.add(holding);
        }
        
        return list;
    }
    
    
    static ArrayList<LinkItem> parseLinks(JSONArray array) {
        ArrayList<LinkItem> list = new ArrayList<LinkItem>();
        try {
            for (int index = 0; index < array.length(); index++) {
                LinkItem link = new LinkItem();
                JSONObject object = array.getJSONObject(index);
                link.title = object.getString("title");
                link.url = object.getString("url");
                
                
                list.add(link);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            throw new RuntimeException("Error parsing links");
        }

        return list;
    }
    
}
