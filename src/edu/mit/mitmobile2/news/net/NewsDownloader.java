package edu.mit.mitmobile2.news.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
import android.os.AsyncTask;
//import android.support.v4.util.LruCache;
import android.util.Log;
//import android.widget.ImageView;
import edu.mit.mitmobile2.news.beans.NewsCategory;
import edu.mit.mitmobile2.news.beans.NewsStory;


//import android.support.v4.util.LruCache;

public class NewsDownloader {
	final int MAX_STORIES_PER_CAREGORY = 200;
	
	final long STORY_CACHE_TIME = 14400000; //(4 * 3600 * 1000) 4 hours (in milliseconds)
	final long CATEGORY_CACHE_TIME = 86400000; //(24 * 3600 * 1000) 24 hours (in milliseconds)
	private final Object mSqlCacheLock = new Object();
	
	private NewsDbHelper dbHelper;
	private SQLiteDatabase database;
	
	public static final String NEWS_PATH = "http://mobile-dev.mit.edu/latestStable/apis/news";
	
	private LinkedHashMap<String, String> categories = new LinkedHashMap<String, String>();
	//private LruCache<String, Bitmap> mMemoryCache;
	
	private static NewsDownloader instance;
	
	public static NewsDownloader getInstance(Context context){
		if(instance==null){
			instance = new NewsDownloader(context);
		}
		return instance;
	}
	
	private NewsDownloader(Context context) {
		//mContext = context;
		dbHelper = NewsDbHelper.getInstance(context);
		
		//final int maxMemory = (int) (Runtime.getRuntime().maxMemory()/1024);
		//Log.d("NEWS", "Memory is: "+maxMemory);
		// Use 1/2th of the available memory for this memory cache.
	    //final int cacheSize = maxMemory / 5;
		/*mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
	        @Override
	        protected int sizeOf(String key, Bitmap bitmap) {
	            // The cache size will be measured in kilobytes rather than
	            // number of items.
	            return bitmap.getRowBytes()*bitmap.getHeight() / 1024;
	        }
	    };*/
		openDatabase();
	}
	
	private void openDatabase(){
		if(database == null || !database.isOpen()){
			synchronized(mSqlCacheLock){
				database = dbHelper.getWritableDatabase();
				mSqlCacheLock.notifyAll();
			}
		}
		gcCache();
	}
	private void gcCache(){
		int noRows = 0;
		//openDatabase(); //make sure database is open
		long time = (int)(System.currentTimeMillis() / 3600000); //1000 * 60 * 60
		synchronized(mSqlCacheLock){
			noRows = database.delete(NewsDbHelper.TABLE_NAME, NewsDbHelper.INSERTED+" < ?", new String[]{String.valueOf(time)});
			mSqlCacheLock.notifyAll();
		}
		Log.d("NEWS_CACHE","CLEARED "+noRows+" records");
	}
	
	private String getCachedResponse(String url){
		openDatabase(); //make sure database is open
		String ret = null;
		synchronized(mSqlCacheLock){
			Cursor cursor = database.query(NewsDbHelper.TABLE_NAME, new String[] {NewsDbHelper.VALUE}, NewsDbHelper.KEY+" like ?" , new String[]{url}, null, null, null);
			if(cursor!=null && cursor.getCount()>0 && cursor.moveToFirst()){
				ret = cursor.getString(cursor.getColumnIndex(NewsDbHelper.VALUE));
				cursor.close();
				mSqlCacheLock.notifyAll();
			}
		}
		return ret;
	}
	private synchronized void setCachedResponse(String url, String data, long cache_time){
		openDatabase(); //make sure database is open
		long time = (int)(System.currentTimeMillis() / 3600000); //1000 * 60 * 60
		ContentValues cv = new ContentValues();
		cv.put(NewsDbHelper.KEY, url);
		cv.put(NewsDbHelper.VALUE, data);
		cv.put(NewsDbHelper.INSERTED, cache_time + time);
		if(url!=null && data!=null){
			synchronized(mSqlCacheLock){
				int r = database.delete(NewsDbHelper.TABLE_NAME, NewsDbHelper.KEY+" = ?", new String[]{url});
				Log.d("NEWS_CACHE","Remove rows: "+ r);
			
				database.insert(NewsDbHelper.TABLE_NAME, null, cv);
				mSqlCacheLock.notifyAll();
			}
		}
	}
	
	private InputStream OpenHttpConnection(String urlString) throws IOException
	{
		InputStream in = null; int response = -1;
		URL url = new URL(urlString); 
		URLConnection conn = url.openConnection();
		if (!(conn instanceof HttpURLConnection))
			throw new IOException("Not an HTTP connection");
		try{
			HttpURLConnection httpConn = (HttpURLConnection) conn;
			httpConn.setUseCaches(true);
			httpConn.setAllowUserInteraction(false); 
			httpConn.setInstanceFollowRedirects(true); 
			httpConn.setRequestMethod("GET");
			httpConn.connect();
			response = httpConn.getResponseCode();
			if (response == HttpURLConnection.HTTP_OK) {
				in = httpConn.getInputStream(); 
			}else{
				throw new IOException("Bad request: "+response);
			}
		}catch (Exception ex) {
			Log.d("Networking", ex.getLocalizedMessage());
			throw new IOException("Error connecting"); 
		}
		return in; 
	}
	private String downloadText(String URL) {
		String str = getCachedResponse(URL);
		if(str!=null){
			return str;
		}
		InputStream in = null;
		try {
			in = OpenHttpConnection(URL);
			if(in!=null){
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		        StringBuilder stringBuilder = new StringBuilder();
		 
		        String line = null;
		        try {
		            while ((line = reader.readLine()) != null) {
		                stringBuilder.append(line + "\n");
		            }
		        } catch (IOException e) {
		            Log.d("Networking",e.getLocalizedMessage());
		            return null;
		        } finally {
		            try {
		                in.close();
		            } catch (IOException e) {
		                e.printStackTrace();
		            }
		        }
		        str = stringBuilder.toString();
			}else{
				throw new IOException("No InputStream");
			}
		} catch (IOException e) {
			Log.d("Networking", e.getLocalizedMessage());
			return null; 
		}
		
		setCachedResponse(URL,str,STORY_CACHE_TIME);
		return str; 
	}
	
	/*private Bitmap downloadImage(String URL){
		
		Bitmap bitmap = this.getBitmapFromMemCache(URL);
		if(bitmap!=null){
			return bitmap;
		}
		InputStream in = null;
		try{
			in = OpenHttpConnection(URL);
			if(in!=null){
				bitmap = BitmapFactory.decodeStream(in);
				in.close();
				this.addBitmapToMemoryCache(URL, bitmap);
			}else{
				throw new IOException("No InputStream");
			}
		}catch(IOException e1){
			Log.d("NetworkingActivity",e1.getLocalizedMessage());
			return null;
		}catch(OutOfMemoryError e2){
			Log.d("NEWS",e2.getLocalizedMessage());
			return null;
		}
		return bitmap;
	}
	
	public class DownloadImageTask extends AsyncTask<String, Bitmap, Long>{
		ImageProgressListener ipl;
		ImageView view;
		public void setListener(ImageProgressListener ipl){
			this.ipl = ipl;
		}
		public void setViewer(ImageView view){
			this.view = view;
		}
		@Override
		protected Long doInBackground(String... urls){
			long imagesCount = 0;
			for(int i=0;i<urls.length;i++){
				Bitmap imageDownloaded = downloadImage(urls[i]);
				if(imageDownloaded!=null){
					imagesCount++;
					publishProgress(imageDownloaded);
				}
			}
			return imagesCount;
		}
		
		@Override
		protected void onProgressUpdate(Bitmap... bitmap){
			if(ipl!=null){
				ipl.onProgressUpdateBitmap(bitmap);
			}
			if(view!=null && bitmap!=null && bitmap.length>0){
				view.setImageBitmap(bitmap[0]);
			}
		}
		@Override
		protected void onPostExecute(Long imagesDownloaded){
			if(ipl!=null)
				ipl.onPostExecuteBitmap(imagesDownloaded);
		}
	}*/
	
	public class DownloadTextTask extends AsyncTask<String, String, Long> {
		TextProgressListener jpl;
		public void setListener(TextProgressListener jpl){
			this.jpl = jpl;
		}
		@Override
		protected Long doInBackground(String... urls) {
			
			long textCount = 0;
			for(int i=0;i<urls.length;i++){
				String textDownloaded = downloadText(urls[i]);
				if(textDownloaded!=null){
					textCount++;
					publishProgress(textDownloaded);
				}
			}
			return textCount;
		}
		@Override
		protected void onProgressUpdate(String... text){
			jpl.onProgressUpdateText(text);
		}
		@Override
		protected void onPostExecute(Long textDownloaded) { 
			jpl.onPostExecuteText(textDownloaded);
		}
	}
	
	public class DownloadCategoriesTask extends AsyncTask<Void, Void, ArrayList<NewsCategory>> {
		CategoryProgressListener cpl;
		public DownloadCategoriesTask(CategoryProgressListener c){
			this.cpl = c;
		}
		@Override
		protected ArrayList<NewsCategory> doInBackground(Void...v) {
			Log.d("NEWS", "URL: "+NewsDownloader.NEWS_PATH+"/categories/");
			String ret = downloadText(NEWS_PATH+"/categories/");
			CategoryParser cp = new CategoryParser();
			ArrayList<NewsCategory> nc = cp.parseArrayFromString(ret);
			return nc;
		}
		
		@Override
		protected void onPostExecute(ArrayList<NewsCategory> list) { 
			cpl.onPostExecute(list);
		}
	}
	public class DownloadStoryTask extends AsyncTask<String, NewsStory, Long> {
		StoryProgressListener spl;
		public DownloadStoryTask(StoryProgressListener c){
			this.spl = c;
		}
		@Override
		protected Long doInBackground(String... ids) {
			long nr = 0;
			StoryParser sp = new StoryParser();
			for(int i=0;i<ids.length;i++){
				String url = NewsDownloader.NEWS_PATH+"/stories/"+ids[i];
				Log.d("NEWS", "URL: "+url);
				String ret = downloadText(url);
				NewsStory st = sp.parseObjectFromString(ret);
				publishProgress(st);
			}
			return nr;
		}
		
		@Override
		protected void onProgressUpdate(NewsStory...st){
			spl.onProgressUpdate(st);
		}
		@Override
		protected void onPostExecute(Long nr) { 
			spl.onPostExecute(nr);
		}
	}
	
	public class DownloadStoriesTask extends AsyncTask<String, ArrayList<NewsStory>, Long> {
		StoriesProgressListener spl;
		/* type : 
		 * "category" we are passing category id
		 * "search" we are passing search query
		 * "urls" we are passing full urls (default)
		 */
		String type = "urls";
		int offset; // default 0
		int limit; // default 20
		
		public DownloadStoriesTask(StoriesProgressListener spl){
			this(spl,"urls",0,20);
		}
		
		public DownloadStoriesTask(StoriesProgressListener spl,String type){
			this(spl,type,0,20);
		}
		
		public DownloadStoriesTask(StoriesProgressListener spl,String type, int offset, int limit){
			this.spl = spl;
			this.type = type;
			this.offset = offset;
			this.limit = limit;
		}
		@SuppressWarnings("unchecked")
		@Override
		protected Long doInBackground(String... urls) {
			String pre_url = "";
			long nr = 0;
			if(this.type.equals("category")){
				pre_url = NewsDownloader.NEWS_PATH+"/stories/?offset="+offset+"&limit="+limit+"&category=";
			}else if(this.type.equals("ids")){
				pre_url = NewsDownloader.NEWS_PATH+"/stories/";
			}else if(this.type.equals("search")){
				pre_url = NewsDownloader.NEWS_PATH+"/stories/?offset="+offset+"&limit="+limit+"&q=";
			}
			
			StoryParser sp = new StoryParser();
			for(int i=0;i<urls.length;i++){
				ArrayList<NewsStory> al = new ArrayList<NewsStory>();
				Log.d("NEWS", "URL: "+pre_url + urls[i]);
				String textDownloaded = downloadText(pre_url + urls[i]);
				if(textDownloaded!=null){
					if(this.type.equals("ids")){
						NewsStory s = sp.parseObjectFromString(textDownloaded);
						al.add(s);
					}else{
						al.addAll(sp.parseArrayFromString(textDownloaded));
					}
				}
				publishProgress(al);
				nr ++;
			}
			return nr;
		}
		@Override
		protected void onProgressUpdate(ArrayList<NewsStory>...list){
			spl.onProgressUpdate(list);
		}
		@Override
		protected void onPostExecute(Long nr) { 
			spl.onPostExecute(nr);
		}
	}
	
	/*
	public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
	    if (getBitmapFromMemCache(key) == null && key!=null && bitmap!=null) {
	        mMemoryCache.put(key, bitmap);
	        Log.d("NEWS", "Image added to memory with size(kB): "+(bitmap.getRowBytes()*bitmap.getHeight()/1024));
	    }
	}
	
	public Bitmap getBitmapFromMemCache(String key) {
		if(key!=null)
			return mMemoryCache.get(key);
		return null;
	}
	*/
	
	public void setCategory(String key, String value){
		this.categories.put(key, value);
	}
	
	public String getCategory(String key){
		return this.categories.get(key);
	}
	
	public LinkedHashMap<String, String> getAllCategories(){
		return this.categories;
	}
}
