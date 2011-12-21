package edu.mit.mitmobile2.libraries;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import edu.mit.mitmobile2.MITClient;
import edu.mit.mitmobile2.MITErrorEntity;
import edu.mit.mitmobile2.classes.FineData;
import edu.mit.mitmobile2.classes.HoldData;
import edu.mit.mitmobile2.classes.LoanData;
import edu.mit.mitmobile2.classes.RenewBookResponse;
import edu.mit.mitmobile2.libraries.BookItem.Holding;
import edu.mit.mitmobile2.libraries.LibraryActivity.LinkItem;
import edu.mit.mitmobile2.libraries.LibraryItem.Hours;
import edu.mit.mitmobile2.libraries.LibraryItem.Schedule;
import edu.mit.mitmobile2.objs.FineListItem;
import edu.mit.mitmobile2.objs.HoldListItem;
import edu.mit.mitmobile2.objs.LoanListItem;
import edu.mit.mitmobile2.objs.RenewResponseItem;

public class LibraryParser {

	public static final String TAG = "LibraryParser";

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
                container.previousTerms = getTerms(temp.getJSONArray("previous_terms"));
            }
            if(temp.has("next_terms")) {
                container.nextTerms = getTerms(temp.getJSONArray("next_terms"));
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
    
    
    private static List<Schedule> getTerms(JSONArray array) throws JSONException {
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
                    book.author = getArray(object, "author");
                }
                if(object.has("year")) {
                    book.year = getArray(object, "year");
                }
                if(object.has("publisher")) {
                    book.publisher = getArray(object, "publisher");
                }
                if(object.has("isbn")) {
                    book.isbn = getArray(object, "isbn");
                }
                
                books.add(book);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            throw new RuntimeException("Error parsing book search results");
        }

        return books;
    }
    
    private static ArrayList<String> getArray(JSONObject object, String field) throws JSONException {
    	JSONArray array = object.getJSONArray(field);
        ArrayList<String> list = new ArrayList<String>();
        for(int index1 = 0; index1 < array.length(); index1++) {
            list.add(array.getString(index1));
        }
        return list; 
    }
    
    private static ArrayList<String> getOptArray(JSONObject object, String field) throws JSONException {
    	if (object.has(field)) {
    		return getArray(object, field);
    	} else {
    		return null;
    	}
    }
    
    static void parseBookDetail(JSONObject object, BookItem book) throws JSONException {
            book.url = object.getString("url");
            book.emailAndCiteMessage = object.getString("composed-html");
            book.subjects = getOptArray(object, "subject");
            book.lang = getOptArray(object, "lang");
            book.extent = getOptArray(object, "extent");
            book.format = getOptArray(object, "format");
            book.summary = getOptArray(object, "summary");
            book.address = getOptArray(object, "address");
            book.editions = getOptArray(object, "edition");
            book.holdings = parseHoldings(object.getJSONArray("holdings"));
            book.detailsLoaded = true;
    }
    
    private static ArrayList<Holding> parseHoldings(JSONArray array) throws JSONException {
        ArrayList<Holding> list = new ArrayList<Holding>();
        
        for(int index = 0; index < array.length(); index++) {
            JSONObject object = array.getJSONObject(index);
            Holding holding = new Holding();
            holding.library = object.getString("library");
            holding.address = object.getString("address");
            holding.url = object.optString("url");
            holding.code = object.getString("code");
            holding.count = object.getInt("count");
            
            JSONArray availabilityArray = object.optJSONArray("availability");
            if (availabilityArray != null) {
            	for(int i = 0; i < availabilityArray.length(); i++) {
            		JSONObject availabilityObject = availabilityArray.getJSONObject(i);            		
            		holding.addAvailibility(
            			availabilityObject.getBoolean("available"),
            			availabilityObject.getString("call-no"),
            			availabilityObject.getString("location"),
            			availabilityObject.getString("status")
            		);
            	}
            }
            
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
  
    static LoanData parseLoans(JSONObject object) {
    	String overDue = "";
    	String longOverdue = "";
    	Log.d(TAG,"parseLoans");
        LoanData loanData = new LoanData();

        
        try {
        	// "{\"result\":\"AUTH_ERROR\"
        	if (object.optString("result").equalsIgnoreCase("AUTH_ERROR")) {
        		Log.d(TAG,"AUTH_ERROR JSON");
        	}
        	else {
		        loanData.setNumLoan(object.getInt("total"));
		        loanData.setNumOverdue(object.getInt("overdue"));
	        	JSONArray items = object.getJSONArray("items");
				for (int l = 0; l < items.length(); l++) {
					LoanListItem item = new LoanListItem();
					JSONObject tmpItem = items.getJSONObject(l);
		
					// Index
					item.setIndex(l);
	
					// Author
					item.setAuthor(tmpItem.optString("author",""));
					
					// Doc-Number
					item.setDocNumber(tmpItem.optString("doc-number",""));
				
					// Material
					item.setMaterial(tmpItem.optString("material",""));
				
					// Sub-library
					item.setSubLibrary(tmpItem.optString("sub-library",""));
	
					// bar code
					item.setBarcode(tmpItem.optString("barcode",""));
		
					// Status
					item.setStatus(tmpItem.optString("status",""));
				
					// Load Date
					item.setLoanDate(tmpItem.optString("loan-date",""));
				
					// Due Date
					item.setDueDate(tmpItem.optString("due-date",""));
		
					// Returned Date
					item.setReturnedDate(tmpItem.optString("returned-date",""));
		
					// Call No
					item.setCallNo(tmpItem.optString("call-no",""));
		
					// Year
					Log.d(TAG,"year = " + tmpItem.optString("year",""));
					item.setYear(tmpItem.optString("year",""));
		
					// Title
					item.setTitle(tmpItem.optString("title",""));
		
					// Imprint
					item.setImprint(tmpItem.optString("imprint",""));
		
					// ISBN ISSN Display / Type
					JSONObject isbn = tmpItem.optJSONObject("isbn-issn");
					if (isbn != null) {
						item.setIsbnIssnDisplay(isbn.optString("display",""));				
						item.setIsbnIssnType(isbn.optString("type",""));
					}
					
					// Overdue
					overDue = tmpItem.optString("overdue","");
					item.setOverdue(overDue.equalsIgnoreCase("TRUE"));
					Log.d(TAG,"overDue = " + item.isOverdue());
					
					// Long Overdue
					longOverdue = tmpItem.optString("long-overdue","");
					item.setLongOverdue(longOverdue.equalsIgnoreCase("TRUE"));
					Log.d(TAG,"long overDue = " + item.isLongOverdue());
		
					// Display Pending Fine
					item.setDisplayPendingFine(tmpItem.optString("display-pending-fine",""));
					
					// Pending Fine
					item.setPendingFine(tmpItem.optString("pending-fine",""));
		
					// Has Hold
					item.setHasHold(tmpItem.optString("has-hold","").equalsIgnoreCase("TRUE"));
					
					// Due Text
					item.setDueText(tmpItem.optString("dueText",""));
		
		
					//Log.d(TAG,item.title);
					loanData.getLoans().add(item);
				}
        	}
        }
		catch (JSONException e) {
			Log.d(TAG,e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error parsing libraries");			
		}
		return loanData;
    }
    
    static HoldData parseHolds(JSONObject object) {
        HoldData holdData = new HoldData();

        try {
	        holdData.setNumRequest(object.optInt("total",0));
	        holdData.setNumReady(object.optInt("ready",0));
        	Log.d(TAG,"num request = " + holdData.getNumRequest() + " num ready = " + holdData.getNumReady());
	        JSONArray items = object.getJSONArray("items");
			for (int l = 0; l < items.length(); l++) {
				HoldListItem item = new HoldListItem();
				JSONObject tmpItem = items.getJSONObject(l);
	
				// Index
				item.setIndex(l);
				
				// Author
				item.setAuthor(tmpItem.optString("author",""));
				
				// Doc-Number
				item.setDocNumber(tmpItem.optString("doc-number",""));
			
				// Material
				item.setMaterial(tmpItem.optString("material",""));
			
				// Sub-library
				item.setSubLibrary(tmpItem.optString("sub-library",""));
		
				// Status
				item.setStatus(tmpItem.optString("status",""));
				
				// Call No
				item.setCallNo(tmpItem.optString("call-no",""));
					
				// Bar Code 
				item.setBarCode(tmpItem.optString("barcode",""));

				// Year
				item.setYear(tmpItem.optString("year",""));
	
				// Title
				item.setTitle(tmpItem.optString("title",""));
	
				// Imprint
				item.setImprint(tmpItem.optString("imprint",""));
				
				// ISBN ISSN Display / Type
				JSONObject isbn = tmpItem.optJSONObject("isbn-issn");
				Log.d(TAG,"isbn = " + isbn);
				if (isbn != null) {
					item.setIsbnIssnDisplay(isbn.optString("display",""));				
					item.setIsbnIssnType(isbn.optString("type",""));
				}
	
				// Description
				item.setDescription(tmpItem.optString("description",""));

				// Pickup Locatiom
				item.setPickupLocation(tmpItem.optString("pickup-location",""));

				// End Hold Date
				item.setEndHoldDate(tmpItem.optString("end-hold-date",""));

				// Ready
				item.setReady(tmpItem.optString("ready",""));
	
				//Log.d(TAG,item.title);
				holdData.getHolds().add(item);
			}
        }
		catch (JSONException e) {
            e.printStackTrace();
            throw new RuntimeException("Error parsing libraries");			
		}
		return holdData;
    }

    static FineData parseFines(JSONObject object) {
        FineData fineData = new FineData();

        try {
	        fineData.setBalance(object.getString("balance"));
	        //fineData.setFineDate(new Date(object.getInt("fine-date")));
	      JSONArray items = object.getJSONArray("items");
	      Log.d(TAG,"number of fines = " + items.length());
			for (int l = 0; l < items.length(); l++) {
				FineListItem item = new FineListItem();
				JSONObject tmpItem = items.getJSONObject(l);
	
				// Index
				item.setIndex(l);
				
				// Author
				item.setAuthor(tmpItem.optString("author",""));
				
				// Doc-Number
				item.setDocNumber(tmpItem.optString("doc-number",""));
			
				// Material
				item.setMaterial(tmpItem.optString("material",""));
			
				// Sub-library
				item.setSubLibrary(tmpItem.optString("sub-library",""));
		
				// Status
				item.setStatus(tmpItem.optString("status",""));
				
				// Call No
				item.setCallNo(tmpItem.optString("call-no",""));
	
				// Year
				item.setYear(tmpItem.optString("year",""));
	
				// Title
				item.setTitle(tmpItem.optString("title",""));
	
				// Imprint
				item.setImprint(tmpItem.optString("imprint",""));
	
				// ISBN ISSN Display / Type
				JSONObject isbn = tmpItem.optJSONObject("isbn-issn");
				Log.d(TAG,"isbn = " + isbn);
				if (isbn != null) {
					item.setIsbnIssnDisplay(isbn.optString("display",""));				
					item.setIsbnIssnType(isbn.optString("type",""));
				}
				
				// Display Amount
				item.setDisplayAmount(tmpItem.optString("display-amount",""));

				// Amount
				item.setAmount(tmpItem.optString("amount",""));

				// Fine Date
				String fineDate = tmpItem.optString("fine-date","");
				if (fineDate.length() > 0) {
			        long timestamp = Long.parseLong(fineDate) * 1000;
			    	java.util.Date d = new java.util.Date(timestamp);  
			    	Format formatter = new SimpleDateFormat("MM/dd/yyyy");
			    	item.setFineDate(formatter.format(d));		
				}

				fineData.getFines().add(item);
			}
			
			Log.d(TAG,"size of fines = " + fineData.getFines().size());
        }
		catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG,e.getMessage());
            throw new RuntimeException("Error parsing libraries");			
		}
		return fineData;
    }

    static RenewBookResponse parseRenewBookResponse(JSONArray array) {
        RenewBookResponse response = new RenewBookResponse();

        try {
        	for (int i = 0; i < array.length(); i++) {
	        	JSONObject object = array.getJSONObject(i);
	        	RenewResponseItem item = new RenewResponseItem();
		
					// Success Msg
					item.setSuccessMsg(object.optString("success",""));
	
					// Error Msg
					item.setErrorMsg(object.optString("error",""));
					
					// Reply
					item.setReply(object.optString("reply",""));
	
					// DETAILS
					JSONObject details = object.optJSONObject("details");
	
					// Author
					item.setAuthor(details.optString("author",""));
					
					// Doc-Number
					item.setDocNumber(details.optString("doc-number",""));
				
					// Material
					item.setMaterial(details.optString("material",""));
				
					// Sub-library
					item.setSubLibrary(details.optString("sub-library",""));
		
					// bar code
					item.setBarcode(details.optString("barcode",""));
					
					// Load Date
					item.setLoanDate(details.optString("loan-date",""));
				
					// Due Date
					item.setDueDate(details.optString("due-date",""));
		
					// Returned Date
					item.setReturnedDate(details.optString("returned-date",""));
		
					// Call No
					item.setCallNo(details.optString("call-no",""));
		
					// Year
					item.setYear(details.optString("year",""));
		
					// Title
					item.setTitle(details.optString("title",""));
		
					// Imprint
					item.setImprint(details.optString("imprint",""));
		
					// ISBN ISSN Display and Type
					JSONObject isbn = details.optJSONObject("isbn-issn");
					if (isbn != null) {
						item.setIsbnIssnDisplay(isbn.optString("display",""));
						item.setIsbnIssnType(isbn.optString("type",""));					
					}
					
					// Overdue
					item.setOverdue(details.optString("overdue","").equalsIgnoreCase("TRUE"));
		
					// Long Overdue
					item.setLongOverdue(details.optString("long-overdue","").equalsIgnoreCase("TRUE"));
		
					// Display Pending Fine
					item.setDisplayPendingFine(details.optString("display-pending-fine",""));
					
					// Pending Fine
					item.setPendingFine(details.optString("pending-fine",""));
		
					// Has Hold
					item.setLongOverdue(details.optString("has-hold","").equalsIgnoreCase("TRUE"));
					
					// Due Text
					item.setDueText(details.optString("dueText",""));
			
					//Log.d(TAG,item.title);
					response.getRenewResponse().add(item);
        	}
        }
		catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG,e.getMessage());
            throw new RuntimeException("Error parsing libraries");			
		}
		return response;
    }

        
}
