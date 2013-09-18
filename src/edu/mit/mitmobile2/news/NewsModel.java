package edu.mit.mitmobile2.news;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
import edu.mit.mitmobile2.ConnectionWrapper;
import edu.mit.mitmobile2.ConnectionWrapper.ConnectionInterface;
import edu.mit.mitmobile2.ConnectionWrapper.ErrorType;
import edu.mit.mitmobile2.MobileWebApi.ServerResponseException;
import edu.mit.mitmobile2.FixedCache;
import edu.mit.mitmobile2.Global;
import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.about.Config;
import edu.mit.mitmobile2.objs.EventDetailsItem;
import edu.mit.mitmobile2.objs.NewsItem;
import edu.mit.mitmobile2.objs.NewsItem.Image;
import edu.mit.mitmobile2.objs.RouteItem;
import edu.mit.mitmobile2.objs.SearchResults;
import edu.mit.mitmobile2.objs.EventDetailsItem.Coord;
import edu.mit.mitmobile2.shuttles.RoutesParser;

public class NewsModel {
	// categorys
	public static final int TOP_NEWS     =  0;
	static final int CAMPUS       = 99;
	static final int ENGINEERING  =  1;
	static final int SCIENCE      =  2;
	static final int MANAGEMENT   =  3;
	static final int ARCHITECTURE =  5;
	static final int HUMANITIES   =  6;
	static final int BOOKMARKED = -2;
	
	static final int[] category_ids = {
		TOP_NEWS, CAMPUS, ENGINEERING, SCIENCE, MANAGEMENT, ARCHITECTURE, HUMANITIES
	};
	
	
	static final String[] category_titles = {
		"Top News", "Campus", "Engineering", "Science", "Management", "Architecture", "Humanities"
	};
	
	final static int MAX_STORIES_PER_CAREGORY = 200;
	
	public static final int FETCH_SUCCESSFUL = 1;
	public static final int FETCH_FAILED = 2;
	
	static final String NEWS_PREFERENCES_FILE = "NewsPreferencesFile";
	static final String LAST_CATEGORY_CLEARED_KEY_PREFIX = "LastCategoryClearedDate";
	static final long FRESH_TIME = 4 * 60 * 60 * 1000; // 4 hours (in miliseconds)
	
	private NewsDB mNewsDB;
	private Context mContext;
	private SharedPreferences mSharedPreferences;
	static String NEWS_PATH = "/" + Config.NEWS_OFFICE_PATH;
	public static final String BASE_PATH = "/apis";
	
	private static HashMap<String, SearchResults<NewsItem>> searchCache = new FixedCache<SearchResults<NewsItem>>(10);
	
	public NewsModel(Context context) {
		mNewsDB = NewsDB.getInstance(context);
		mContext = context;
		mSharedPreferences = mContext.getSharedPreferences(NEWS_PREFERENCES_FILE, Context.MODE_PRIVATE);
	}
	
	public String getCategoryTitle(int categoryId) {
		for(int i = 0; i < category_ids.length; i++) {
			if(category_ids[i] == categoryId) {
				return category_titles[i];
			}
		}
		
		return null;
	}
	
	public void setStoryBookmarkStatus(final NewsItem newsItem, final boolean bookmarkStatus) {
		new Thread() {	
			@Override
			public void run() {
				synchronized (mNewsDB) {
					mNewsDB.startTransaction();
					mNewsDB.saveNewsItem(newsItem, false);
					mNewsDB.updateBookmarkStatus(newsItem, bookmarkStatus);
					mNewsDB.endTransaction();
				}
			}
		}.start();
		
		String bookmarkStatusText = bookmarkStatus ? "Bookmark saved" : "Bookmark removed";
		Toast.makeText(mContext, bookmarkStatusText, Toast.LENGTH_LONG).show();
	}
	
	public void clearAllBookmarks(final Handler uiHandler) {
		new Thread() {
			@Override
			public void run() {
				mNewsDB.clearAllBookmarks();
				uiHandler.sendEmptyMessage(0);
			}
		}.start();
		
		Toast.makeText(mContext, "clearing bookmarks", Toast.LENGTH_LONG).show();
	}
	
	public boolean isBookmarked(NewsItem newsItem) {
		return mNewsDB.isBookmarked(newsItem.story_id);
	}
	
	private void clearAllStories() {
		mNewsDB.clearAllStories();
		Editor editor = mSharedPreferences.edit();
		for(int i = 0; i < category_ids.length; i++) {
			Integer categoryId = category_ids[i];
			editor.putLong(LAST_CATEGORY_CLEARED_KEY_PREFIX + categoryId.toString(), -1);
		}
		editor.commit();
	}
	
	private void clearCategory(int categoryId) {
		mNewsDB.clearCategory(categoryId);
		Editor editor = mSharedPreferences.edit();
		editor.putLong(LAST_CATEGORY_CLEARED_KEY_PREFIX + Integer.toString(categoryId), -1);
		editor.commit();
	}
	
	private void markCategoryAsFresh(int categoryId) {
		long currentTime = System.currentTimeMillis();
		Editor editor = mSharedPreferences.edit();
		editor.putLong(LAST_CATEGORY_CLEARED_KEY_PREFIX + Integer.toString(categoryId), currentTime);
		editor.commit();
	}
	
	boolean fetchTopTen(boolean silent, final Handler uiHandler) {
		return fetchCategory(TOP_NEWS, null, silent, uiHandler);
	}
	
	public boolean isTopTenFresh() {
		return isCategoryFresh(TOP_NEWS);
	}
	
	public Date getCategoryLastLoaded(int categoryId) {
		long lastClearTime = mSharedPreferences.getLong(LAST_CATEGORY_CLEARED_KEY_PREFIX + Integer.toString(categoryId), -1);
		if(lastClearTime > 0) {
			return new Date(lastClearTime);
		} else {
			return null;
		}
	}
	
	public boolean isCategoryFresh(int categoryId) {
		long lastClearTime = mSharedPreferences.getLong(LAST_CATEGORY_CLEARED_KEY_PREFIX + Integer.toString(categoryId), -1);
		if(lastClearTime < 0) {
			return false;
		}
		
		return  ((System.currentTimeMillis() - lastClearTime) < FRESH_TIME );
	}
	
	public List<NewsItem> getTopTen() {
		return mNewsDB.getTopTen();
	}
	
	public Cursor getNewsCursor(int category) {
		return mNewsDB.getNewsCursor(category);
	}
	
	public Cursor getBookmarksCursor() {
		return mNewsDB.getBookmarksCursor();
	}

	public void markAsRead(NewsItem newsItem) {
		newsItem.isRead = true;
		mNewsDB.markAsRead(newsItem);		
	}
	
	public void populateImages(NewsItem newsItem) {
		mNewsDB.populateImages(newsItem);
	}
	
	public NewsItem retreiveNewsItem(int storyId) {
		return mNewsDB.retrieveNewsItem(storyId);
	}
	
	/**************************************************/
	public void fetchThumbnail(final Handler uiHandler, final NewsItem newsItem, final boolean saveThumbnail) {	
		
		final Message fetchThumbnailMessage = Message.obtain();
		fetchThumbnailMessage.arg1 = FETCH_THUMBNAIL_CONTINUE;
		
		fetchThumbnailMessage.obj = new FetchThumbnailMessage(newsItem, uiHandler, saveThumbnail);
		
		final int threadKey = newsItem.story_id % FETCH_THUMBNAIL_THREADS_MAX;
		FetchThumbnailThread thumbnailThread = mThumbnailThreads[threadKey];
		if(thumbnailThread == null) {
			thumbnailThread = new FetchThumbnailThread();
			thumbnailThread.start();
			mThumbnailThreads[threadKey] = thumbnailThread; 
		}
		
		// use a delay to make sure the download thread
		// has been initialized first
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {				
				mThumbnailThreads[threadKey].getHandler().sendMessageAtFrontOfQueue(fetchThumbnailMessage);
			}
			
		}, 150);
	}	
	
	private static final int FETCH_THUMBNAIL_CONTINUE = 1;
	private static final int FETCH_THUMBNAIL_STOP = 2;
	
	private static class FetchThumbnailMessage {
		private NewsItem mNewsItem;
		private Handler mUIHandler;
		boolean mSaveThumbnail;
		
		FetchThumbnailMessage(NewsItem newsItem, Handler uiHandler, boolean saveThumbnail) {
			mNewsItem = newsItem;
			mUIHandler = uiHandler;
			mSaveThumbnail = saveThumbnail;
		}
		
		public NewsItem getNewsItem() {
			return mNewsItem;
		}
		
		public Handler getUIHandler() {
			return mUIHandler;
		}
		
		public boolean getSaveThumbnail() {
			return mSaveThumbnail;
		}
	}
	
	@SuppressLint("UseSparseArrays")
	private Map<Integer, Boolean> mPendingThumbnails = new HashMap<Integer, Boolean>();
	@SuppressLint("UseSparseArrays")
	private Map<Integer, Boolean> mRejectedThumbnails = new HashMap<Integer, Boolean>();
	
	private static int FETCH_THUMBNAIL_THREADS_MAX = 5;
	private FetchThumbnailThread[] mThumbnailThreads = new FetchThumbnailThread[FETCH_THUMBNAIL_THREADS_MAX];
	
	private class FetchThumbnailThread extends Thread {
			private Handler mHandler;
			
			public Handler getHandler() {
				return mHandler;
			}
			
			@Override
    		public void run() {			
				Looper.prepare();
				
				mHandler = new Handler() {
					
					@Override
					public void handleMessage(Message msg) {
						if(msg.arg1 == FETCH_THUMBNAIL_STOP) {
							Looper.myLooper().quit();
							return;
						}
						
						FetchThumbnailMessage fetchMessage = (FetchThumbnailMessage) msg.obj;
						NewsItem newsItem = fetchMessage.getNewsItem();
						
						synchronized (mPendingThumbnails) {
							if(mPendingThumbnails.containsKey(newsItem.story_id)) {
								// already being fetched
								return;
							} else if(mRejectedThumbnails.containsKey(newsItem.story_id)) {
								// already failed to decode this image
								// do not try again
								return;
							} else {
								mPendingThumbnails.put(newsItem.story_id, true);
							}
						}
						
						if(newsItem.thumbURL == null || newsItem.thumbURL.equals("")) {
								// no image to download, hence nothing to do
								return;
						}
							
						byte[] imageBytes = mNewsDB.retrieveThumbnailBytes(newsItem);
						
				
						if(imageBytes == null) {
							// not in db, so must retreive from network
				
							HttpClient httpclient = new DefaultHttpClient();
							
							// explicitly set buffer size for httpClient
							HttpParams httpParameters = httpclient.getParams();
							HttpConnectionParams.setSocketBufferSize(httpParameters, 8192);

							HttpGet httpget = new HttpGet(newsItem.thumbURL); 
							HttpResponse response;
		        
							try {
								response = httpclient.execute(httpget);
		            
								// Response status
								Log.i("NewsModel", "Downloading thumbnail response: " + response.getStatusLine().toString());
		 
								HttpEntity entity = response.getEntity();
		 
								if (entity != null) {
		 
									imageBytes = EntityUtils.toByteArray(entity);
			           
									if(fetchMessage.getSaveThumbnail()) {
										mNewsDB.saveThumbnail(newsItem, imageBytes);
									}
								}		                
							} catch (ClientProtocolException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
	            
						Message message = Message.obtain();
						
						// rest the CPU since decoding
						// images takes alot of CPU, this
						// slow down the retrieve of images
						// but keeps the UI thread responsive
						try {
							Thread.sleep(250);
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						
						if(imageBytes != null) {
							try {
								message.arg1 = FETCH_SUCCESSFUL;
								message.obj = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);	
								if(message.obj == null) {
									mRejectedThumbnails.put(newsItem.story_id, true);
								}
							} catch (OutOfMemoryError memoryException) {
								message.arg1 = FETCH_FAILED;
							}
						} else {
							message.arg1 = FETCH_FAILED;
						}
	            
						synchronized (mPendingThumbnails) {
							mPendingThumbnails.remove(newsItem.story_id);
						}
						
						fetchMessage.getUIHandler().sendMessage(message);
					}
				};
				
				Looper.loop();
				//System.gc();
			}
	}
	
	public static Message messageFetchSuccess() {
		Message message = Message.obtain();
		message.arg1 = FETCH_SUCCESSFUL;
		return message;
	}
	
	public List<NewsItem> executeLocalSearch(String searchTerm) {
		return searchCache.get(searchTerm).getResultsList();
	}
	
	public void stop() {
		for(int index = 0; index < FETCH_THUMBNAIL_THREADS_MAX; index++) {
			FetchThumbnailThread thread = mThumbnailThreads[index];
			if(thread != null) {
				if(thread.getHandler() != null) {
					Message msg = Message.obtain();
					msg.arg1 = FETCH_THUMBNAIL_STOP;
					thread.getHandler().sendMessage(msg);
				}
			}
		}
	}
	
	
	//////////////new////////////////////////
	
	public boolean fetchCategory(final int category, final Integer story_id, final boolean silent, final Handler uiHandler) {
		
		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put("category", Integer.toString(category));
		
		
		MobileWebApi webApi = new MobileWebApi(false, !silent, "News", mContext, uiHandler);
		boolean isStarted = webApi.requestJSONArray(story_id != null ? NEWS_PATH + "/" + story_id.toString() : NEWS_PATH, parameters, 
				new MobileWebApi.JSONArrayResponseListener(new MobileWebApi.DefaultErrorListener(uiHandler), null) {
					
			@Override
			public void onResponse(final JSONArray array) throws ServerResponseException, JSONException {
				new Thread() {
						
					@Override
					public void run() {
						
						synchronized(mNewsDB) {
							List<NewsItem> newsItems = parseDetailArray(array);
							
							mNewsDB.startTransaction();
							
							if(story_id == null && !newsItems.isEmpty()) {
								if(category == NewsModel.TOP_NEWS) {
									clearAllStories();								
								} else {
									clearCategory(category);
								}
							}
	
							for(NewsItem item : newsItems) {
								if( !(category == TOP_NEWS && story_id == null) ) {
									item.categories.clear();
								} 
								item.categories.add(category);
								mNewsDB.saveNewsItem(item, true);
							}
							mNewsDB.endTransaction();
							markCategoryAsFresh(category);
	
						}
					
						uiHandler.sendMessage(messageFetchSuccess());
					}
				}.start();
			}
		});	
		
		return isStarted;
	}

	private static List<NewsItem> parseDetailArray(JSONArray jArray) {
		ArrayList<NewsItem> events = new ArrayList<NewsItem>();
		for(int i = 0; i < jArray.length(); i++) {
			try {
				JSONObject jItem = (JSONObject) jArray.get(i);
				events.add(parseDetailItem(jItem));
				
			} catch (JSONException e) {
				e.printStackTrace();
				throw new RuntimeException("Failed to parsed EventItem");
			}					
		}	
		return events;		
	}
	
	private static NewsItem parseDetailItem(JSONObject jItem) {
		NewsItem item = new NewsItem();
        
        try {
        	item.story_id = jItem.getInt("id");
        	item.link = jItem.getString("source_url");
        	item.title = jItem.getString("title");
        	item.author = jItem.getString("author");
        	item.description = jItem.getString("dek");
        	
        	if (jItem.has("categories")) {
	        	JSONArray categories_array = jItem.getJSONArray("categories");
	        	HashSet<Integer> categories = new HashSet<Integer>();
	        	
	        	for(int j=0;j<categories_array.length();j++)
	        		categories.add(Integer.parseInt(categories_array.getString(j)));
	        	
	        	item.categories = categories;
        	}
        	
        	item.featured = jItem.getBoolean("featured");
        	item.body = jItem.getString("body");
        	
        	Image img = new Image();
        	JSONObject otherImg;
        	Image imgother;
        	
        	if (jItem.has("images")) {
        		
        		Object images = jItem.get("images");

	        	JSONArray imagesJsonArray = (JSONArray)images;
	        	JSONObject image = imagesJsonArray.getJSONObject(0);
        	

	        	ArrayList<Image> otherImgList = new ArrayList<Image>(imagesJsonArray.length()-1);
	        		
	        	if (imagesJsonArray.length() > 1) {
			    	for (int s = 1; s < imagesJsonArray.length(); s++) {
			    		otherImg = imagesJsonArray.getJSONObject(s);
			    		imgother = new Image();
			    		if (otherImg.has("caption"))
			    			imgother.imageCaption = otherImg.getString("caption");
			       		if (otherImg.has("credits"))
			       			imgother.imageCredits = otherImg.getString("credits");    			
			       			
			       		JSONObject representations = otherImg.getJSONObject("representations");
			       		
			       		if (representations.has("full")) {
			       			JSONObject full = representations.getJSONObject("full");
			       			imgother.fullURL = full.getString("url");
			       		}
			       		otherImgList.add(imgother);
			    	}
			    	item.otherImgs = otherImgList;
	        	}
	        	        	
				if (image.has("caption"))
	    			img.imageCaption = image.getString("caption");
	    		if (image.has("credits"))
	    			img.imageCredits = image.getString("credits");    			
	    			
	    		JSONObject representations = image.getJSONObject("representations");
	    		if (representations.has("thumb")) {
	    			JSONObject thumb = representations.getJSONObject("thumb");
	    			item.thumbURL = thumb.getString("url");
	    		}
	
	    		if (representations.has("small")) {
	    			JSONObject small = representations.getJSONObject("small");
	    			img.smallURL = small.getString("url");
	    		}
	    		if (representations.has("full")) {
	    			JSONObject full = representations.getJSONObject("full");
	    			img.fullURL = full.getString("url");
	    		}
	        	
	    		item.img = img;
        	}
        	
        	try {
				item.postDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").parse(jItem.getString("published_at"));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	        
    	} catch (JSONException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to parsed EventItem");
		}
    	
        return item;
	}
	
	public void executeSearch(final String searchTerm, final Handler uiHandler, int start) {
		// check cache
		if(searchCache.get(searchTerm) != null) {
			SearchResults<NewsItem> searchResults = searchCache.get(searchTerm);
			if (searchResults.getResultsList().size() > start) {
				MobileWebApi.sendSuccessMessage(uiHandler, searchResults);
				return;
			}
		}
		
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("q", searchTerm);
		params.put("offset", String.valueOf(start));
		params.put("limit", "50");
		
		
		MobileWebApi webApi = new MobileWebApi(false, false, "News", mContext, uiHandler);
		webApi.requestJSONObject(NEWS_PATH, params, 
				new MobileWebApi.JSONObjectResponseListener(new MobileWebApi.DefaultErrorListener(uiHandler), null) {
					
			@Override
			public void onResponse(final JSONObject object) throws ServerResponseException, JSONException {	
					new Thread() {
						@Override
						public void run() {
							SearchResults<NewsItem> results = parseNewsSearchResults(object, searchTerm);
							if (results != null) {
								SearchResults<NewsItem> lastResults = searchCache.get(searchTerm);
								if (null != lastResults) {
									lastResults.addMoreResults(results.getResultsList());
									results = lastResults;
								}
								searchCache.put(searchTerm, results);
								MobileWebApi.sendSuccessMessage(uiHandler, results);
							} else {
								MobileWebApi.sendErrorMessage(uiHandler);
							}
						}
					}.start();
			}
		});
	}
	
	private SearchResults<NewsItem> parseNewsSearchResults(JSONObject object, String searchTerm) {
		try {
			NewsHandler handler = new NewsHandler();
			List<NewsItem> news = parseDetailArray(object.optJSONArray("items"));
			handler.setNewsItems(news);
			handler.settotalResults(object.getInt("totalResult"));
			SearchResults<NewsItem> results = new SearchResults<NewsItem>(searchTerm, handler.getNewsItems());
			
			int a = handler.getNewsItems().size();
			if(handler.getNewsItems().size() != handler.totalResults()) {
				results.markAsPartialWithTotalCount(handler.totalResults());
			}
			return results;
			
		} catch (Exception e) {
			//throw new RuntimeException(e);
			Log.d("NewsParser", "RuntimeException");
			e.printStackTrace();
		} 
		
		return null;
	}
}
