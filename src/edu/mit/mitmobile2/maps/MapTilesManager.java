package edu.mit.mitmobile2.maps;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import edu.mit.mitmobile2.Global;
import edu.mit.mitmobile2.ModoLog;
import edu.mit.mitmobile2.about.BuildSettings;

public class MapTilesManager {
	
	private Context ctx;
	
	private boolean mExternalStorageAvailable = false;
	@SuppressWarnings("unused")
	private boolean mExternalStorageWriteable = false;  // TODO drop? 
	
	private String SERVER = "http://" + Global.getMobileWebDomain() + "/api/map/tile2/";
	//static String SERVER = "http://maps.mit.edu/ArcGIS/rest/services/Mobile/WhereIs_MobileAll/MapServer/tile/";
	
	private static String SUBDIR_MITAPP = "/edu.mit.mitmobile2";
	
	private String path;
	private String subdir;
	private String oldDir;

	private String curlastUpdatedStr;
	private String retlastUpdatedStr;
	
    private final Map<String, SoftReference<Bitmap>> imagesMap;
    private final SmallBitmapCache imagesMapStrongRefs = SmallBitmapCache.getInstance();
    
    private final Map<String, Boolean> pendingImages;
    private final HashMap<String,Object> badMap;

	SharedPreferences pref;

	private static String FIRST_DIR = "first_tiles";
	
	private static int file_threads = 0;
	private  static int MAX_THREADS = 3;
	private  ArrayList<DownloadThread> mDownloadThreads;	
    
	static String KEY_LAST_UPDATED = "lastupdated";
	
	private MITMapView mv;
	
	private static long REST_VM_GC_TIME = 1500;	
	// initialize the last GC time so that we immediatly try to start decoding bitmaps
	private long mLastBitmapDecoderOutOfMemoryExceptionTime = System.currentTimeMillis() - REST_VM_GC_TIME - 1;
    
	private BitmapFactory.Options mBitmapOptions;
		
    public MapTilesManager(MITMapView mv, Context ctx) {
    	mBitmapOptions = new BitmapFactory.Options();
    	mBitmapOptions.inPurgeable = true;
    	
    	imagesMap = Collections.synchronizedMap(new CacheHashMap());
    	pendingImages = Collections.synchronizedMap(new HashMap<String, Boolean>());
    	
    	mDownloadThreads = new ArrayList<DownloadThread>();
		for(int index = 0; index < MAX_THREADS; index++) {
			DownloadThread downloadThread = new DownloadThread();
			downloadThread.start();
			mDownloadThreads.add(downloadThread);
		}
    	
    	badMap = new HashMap<String,Object>();
    	
    	this.mv = mv;
    	
    	this.ctx = ctx;
    	
    	//deleteAll();  // debug
    	
    	init();
    	
    	checkAge(); 
    	
    }
    /********************************************************/
	
    @SuppressWarnings("serial")
	private class TileOutOfBoundsException extends Exception {};
    
    private InputStream fetch(String urlString) throws MalformedURLException, IOException, TileOutOfBoundsException {
    	DefaultHttpClient httpClient = new DefaultHttpClient();
    	HttpGet request = new HttpGet(urlString);
    	HttpResponse response = httpClient.execute(request);
    	
    	// #1
    	//HttpEntity ent = response.getEntity();
    	//if (ent!=null) return null;
    	//else  return ent.getContent();
    	if(response.getStatusLine().getStatusCode() == 404) {
			throw new TileOutOfBoundsException();
    	}
    	
    	return response.getEntity().getContent();
    	// #2
    	//return EntityUtils.toByteArray(response.getEntity());
    	
    }
    
    /**
     * @throws RetryTileException 
     * @throws TileOutOfBoundsException ******************************************************/
    // this calls fetch()
    public Bitmap fetchBitmap(String urlString) throws RetryTileException, TileOutOfBoundsException {
    	
    	Log.d(this.getClass().getSimpleName(), "image url:" + urlString);
    	try {
    		Bitmap bm;
    		InputStream is = fetch(urlString);
    		
    		// #1
    		/*
    		BitmapFactory.Options options = new BitmapFactory.Options();
    		options.inSampleSize = 8;
    		bm = BitmapFactory.decodeStream(is,null,options);
    		if (bm==null) Log.d("MITMapView","MITMapView: wasted " + urlString);
    		*/
    		// #2
    		BitmapDrawable d = (BitmapDrawable) BitmapDrawable.createFromStream(is, "src");
    		bm = d.getBitmap();
    		d = null;

    		return bm;
    	} catch (OutOfMemoryError ome) {
         	Log.d("MITMapView","MITMapView: out of memory " + urlString);
         	System.gc();
         	mLastBitmapDecoderOutOfMemoryExceptionTime = System.currentTimeMillis();
    		throw new RetryTileException("IOException");
    	} catch (MalformedURLException e) {
    		Log.e(this.getClass().getSimpleName(), "fetchBitmap MalformedURLException", e);
    		//throw new RuntimeException("MalformedURLException");
         	//System.gc();
    		return null;
    	} catch (IOException e) {
    		Log.e(this.getClass().getSimpleName(), "fetchBitmap IOException", e);
         	//System.gc();
    		//throw new RetryTileException("IOException");
    		return null;
    	}
    }

    private static String imagesHashKey(int col, int row, int zoom) {
    	return zoom + "_" +  col + "_" + row;
    }
    
    public Bitmap getBitmap(int col, int row, int zoom, boolean keepStrongReference) {
    	String bitmapKey = imagesHashKey(col, row, zoom);
    	
    	Bitmap bitmap = imagesMapStrongRefs.get(bitmapKey);
    	if(bitmap != null) {
    		imagesMapStrongRefs.remove(bitmapKey);
    		imagesMapStrongRefs.put(bitmapKey, bitmap);
    		return bitmap;
    	}
    	
    	SoftReference<Bitmap> bitmapRef = imagesMap.get(bitmapKey);
    	if(bitmapRef != null) {
    		bitmap = bitmapRef.get();
    		if(bitmap != null && keepStrongReference) {
    			imagesMapStrongRefs.put(bitmapKey, bitmap);
    		}
    		return bitmap;
    	} else {
    		return null;
    	}
    }
    
    /********************************************************/
    // this calls fetchBitmap()
    public Bitmap fetchBitmapOnThread(int col, int row, int zoom, boolean block) {

    	Bitmap bm = null;
    	
    	final String name = imagesHashKey(col, row, zoom);
    	String filename = zoom + "/" +  row + "/" + col;
    	final String url = SERVER + filename;
    	
    	if (badMap.containsKey(name)) return null;
    	
    	// In memory cache?
    	
    	SoftReference<Bitmap> bitmapRef = imagesMap.get(name);
    	if (bitmapRef != null) {
        	bm = bitmapRef.get();
        	if(bm != null) {
        		return bm;
        	}
    	}
    	
    	if(block) {
    		// In file cache?
    		Bitmap b = (Bitmap) getTileFile(name);  
    		if (b!=null) {
    			// Put in cache...
    			imagesMap.put(name, new SoftReference<Bitmap>(b));   
    			mv.postInvalidate();
    			return b;
    		}
    	}
		
		// Don't download if still zooming
		if (mv.mPinchZoom) return null;
		
		
		// Download it...
		
		if(pendingImages.containsKey(name)) {
			// image already being downloaded
			// just exit now
			return null;
		}
		
		
		// a simple algorithm to load balance downloading
		int threadNumber = (row + col) % MAX_THREADS;

		
		Message message = Message.obtain();
		message.obj = new DownloadMessage(url, name);
		Handler handler = mDownloadThreads.get(threadNumber).getHandler();
		if(handler != null) {
			pendingImages.put(name, true);
			handler.sendMessage(message);
		}
		
		return bm;
    }


	/**************************************************/
	public void checkAge() {
		
		final MapAgeParser mp = new MapAgeParser();
		
		final Handler myHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				
				retlastUpdatedStr = mp.last_updated;
				
				if (retlastUpdatedStr==null) return;
				
				// TODO DEBUG - remove
				//retlastUpdatedStr = "first_time";  
				//retlastUpdatedStr = "12345678";
				//retlastUpdatedStr = "again";
				
				if (!retlastUpdatedStr.equals(curlastUpdatedStr)) {
					
					curlastUpdatedStr  = retlastUpdatedStr;
					
					SharedPreferences.Editor editor = pref.edit();
					editor.putString(KEY_LAST_UPDATED, curlastUpdatedStr);
					editor.commit();
					
					oldDir = path;
					subdir = curlastUpdatedStr;
					
					// TODO may need to create new dir before setting new path 
					
					init();  // re-init
					
					// TODO don't call if "first"?
					
					// TODO are we on UI thread??? if not then no need for thread
					//deleteOldFiles();
					Thread t = new Thread() {
			            public void run() {
			            	deleteOldFiles();
			            }
			        };
			        t.start();
			        
				}
				
			}

		};
		
		String params = "?command=tilesupdated";
		
		mp.getJSONThread(params, myHandler);
		
	}
	/**************************************************/
	void deleteAll() {

		boolean deleted;
		File f;
	    File dir;
	    String[] filesToDelete;
	    
		path = Environment.getExternalStorageDirectory().toString() + SUBDIR_MITAPP;
		
	    dir = new File(path);
	    
		filesToDelete = dir.list();
	    
	    if (filesToDelete==null) return;
	    
	    for (String fname : filesToDelete) {
	    	f = new File(dir, fname);
	    	deleted = f.delete();
	    	//f.deleteOnExit();  // TODO?
			if (!deleted) {
				Log.e("MapTilesManager", "failed to delete");
			}
	    }
	    
	}
	/**************************************************/
	void deleteOldFiles() {
		

		boolean deleted;
		File f;
	    File dir;
	    String[] filesToDelete;
	    
	    
	    // TODO minor: what if previously extern then internal? (card swapped)
	    
	    if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.ECLAIR_MR1) {
	    	// we should not have any tiles to delete if we are not using froyo;
	    	return;
	    }
	    
	    if (mExternalStorageAvailable) {
	    	
		    //dir = new File(getExternalFilesDir(ctx, null), oldSubdir);
		    dir = new File(oldDir);
			
		    if (!dir.isDirectory()) {
		    	return;
		    }
		    
		    filesToDelete = dir.list();
		    
		    if (filesToDelete==null) return;
		    
		    for (String fname : filesToDelete) {
		    	f = new File(dir, fname);
		    	deleted = f.delete();
		    	//f.deleteOnExit();  // TODO?
				if (!deleted) {
					Log.e("MapTilesManager", "failed to delete");
				}
		    }
		    
		    dir.delete();  // note: no internal dir to delete
		   
		} else {

			// TODO does this include preferences?
			
			filesToDelete = ctx.fileList(); // internal private files

		    if (filesToDelete==null) return;  // this may not be needed...
		    
			for (String fname : filesToDelete) {
				deleted = ctx.deleteFile(fname);
				if (!deleted) {
					Log.e("MapTilesManager", "failed to delete");
				}
		    }
			
		}
	}

	/**************************************************/
	public static File getExternalFilesDir(Context ctx, String type) {
		// need to use reflection because this method is not available on
		// pre froyo devices
		try {
			Method getExternalFilesDir_method = Context.class.getMethod("getExternalFilesDir", new Class[] {String.class});
			return (File) getExternalFilesDir_method.invoke(ctx, type);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			return Environment.getExternalStorageDirectory();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
    /********************************************************/
    void initFirstTime() {
    	
    	// TODO fetch a basic set
    	
    	int MIN_ROW = 0;
    	int MAX_ROW = 0;
    	int MIN_COL = 0;
    	int MAX_COL = 0;
    	
    	for (int row=MIN_ROW; row<MAX_ROW; row++) {
        	for (int col=MIN_COL; col<MAX_COL; col++) {
        		fetchBitmapOnThread(col, row, MITMapActivity.INIT_ZOOM, false);
        	}
    	}
    	
    }
    /********************************************************/
    void init() {
    	
    	// Subdir is based on last updated 
    	pref = ctx.getSharedPreferences(Global.PREFS_MAP,Context.MODE_PRIVATE);  
    	
    	curlastUpdatedStr = pref.getString(KEY_LAST_UPDATED, FIRST_DIR); // note: "first" is temp until can read "last updated" for first time

		subdir = SUBDIR_MITAPP + "/" + curlastUpdatedStr;  
		
    	
    	String state = Environment.getExternalStorageState();

    	path = "/data/data/edu.mit.mitmobile2";  // default to Internal file
    	
    	if (Environment.MEDIA_MOUNTED.equals(state)) {
    	    mExternalStorageAvailable = mExternalStorageWriteable = true;
        	path = Environment.getExternalStorageDirectory().toString();
        	//File f = ctx.getExternalCacheDir();  // TODO Froyo
        	path += subdir;

	    	File file = new File(path, "");
	    	if (!file.exists()) {
		    	boolean success = file.mkdir();
		    	if (!success) {
		    	    mExternalStorageAvailable = mExternalStorageWriteable = false;
	            	Log.e("MITMapView","MITMapView: mkdir failed");
		    	}
	    	}
			
    	} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
    		// FIXME 
    	    mExternalStorageAvailable = true;
    	    mExternalStorageWriteable = false;
    	} else {
    	    mExternalStorageAvailable = mExternalStorageWriteable = false;
    	}

    }

    /********************************************************/
    void saveTileFile(final String name) {

    	// FIXME remove from Thread? (already in thread)
    	
    	Thread thread = new Thread() {
    		@Override
    		public void run() {
    			OutputStream fos = null;
    			
            	//Log.d("MITMapView","MITMapView: saving file name="+name);
            	Log.d("MITMapView","MITMapView: saving file name="+name+"  threads="+file_threads++);

    	    	try {
    	    		
    	    		SoftReference<Bitmap> ref = imagesMap.get(name);
    	    		Bitmap bm = null;
    	    		if(ref != null) {
    	    			bm = ref.get();
    	    		}
    	    		
    	    		if (bm==null) return;  // FIXME need to synchronize after put()
    	    		
    	    		if (mExternalStorageAvailable) {
    	    	    	File file = new File(path, name);
    	    			fos = new FileOutputStream(file);
    	    		}
    	    		else {
    	    			fos = ctx.openFileOutput(name, Context.MODE_PRIVATE);
    	    		}
    	    		
    	    		bm.compress(Bitmap.CompressFormat.PNG, 100, fos);
    	    		file_threads--;
    	    		fos.flush();
    	    		fos.close();
    	    	} catch (FileNotFoundException e) {
    	    		e.printStackTrace();
    	    	} catch (IOException e) {
    	    		e.printStackTrace();
    	    	}
    		}
    	};
    	thread.start();
    	
		
    }
    /********************************************************/
    
    Bitmap getTileFile(String name) {
    	
    	Bitmap bm = null;
    	
    	if(System.currentTimeMillis() - mLastBitmapDecoderOutOfMemoryExceptionTime < REST_VM_GC_TIME) {
    		// give the VM time clear memory
    		return null;
    	}
    	
    	try {
    		if (mExternalStorageAvailable) {
    			String full_path = path + "/" + name;
    			bm = BitmapFactory.decodeFile(full_path, mBitmapOptions);
    		} else {
    			try {
    				// TODO maybe can also use full path...
    				FileInputStream fis = ctx.openFileInput(name);
    				bm = BitmapFactory.decodeStream(fis, null, mBitmapOptions);
    			} catch (FileNotFoundException e) {
    				Log.v("MITMapView","MITMapView: file miss");
    				//e.printStackTrace();
    			} 
    		}
    	} catch (OutOfMemoryError memoryException) {
    		memoryException.printStackTrace();
    		System.gc();
    		mLastBitmapDecoderOutOfMemoryExceptionTime = System.currentTimeMillis();
			return null;
		}
    	
		return bm;
    }
    
	 private static class DownloadMessage {
		private String mName;
		private String mUrl;
			 
		DownloadMessage(String url, String name) {
			mUrl = url;
			mName = name;
		}

		public String getUrl() {
			return mUrl;
		}
	  
		public String getName() {
			return mName;
		}
	 }
	
	private final static int STOP_DOWNLOAD_THREAD = 1;
	
	private class DownloadThread extends Thread {		  
	      public Handler mHandler;
	      
	      public Handler getHandler() {
	    	  return mHandler;
	      }
	      
	       
	      public void run() {
	          Looper.prepare();
	          
	          mHandler = new Handler() {
	              public void handleMessage(Message msg) {
	            	if(msg.arg1 == STOP_DOWNLOAD_THREAD) {
	            		Looper.myLooper().quit();
	            		return;
	            	}
	            	
	            	if(System.currentTimeMillis() - mLastBitmapDecoderOutOfMemoryExceptionTime < REST_VM_GC_TIME) {
	            		// give the VM time clear memory
	            		try {
							Thread.sleep(REST_VM_GC_TIME);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
	            	}
	            	
      				DownloadMessage downloadMessage = (DownloadMessage) msg.obj;
      				String url = downloadMessage.getUrl();
      				String name = downloadMessage.getName();
      				
      	    		Bitmap bitmap = (Bitmap) getTileFile(name);  
      	    		if (bitmap!=null) {
      	    			// Put in cache...
      	    			imagesMap.put(name, new SoftReference<Bitmap>(bitmap));  
		        		ModoLog.v("MapTilesManager", "Requesting mapView redraw");
      	    			mv.postInvalidate();
      	    			pendingImages.remove(name);
      	    			return;
      	    		}
      		    	
      		    	
    				try {
    					bitmap = fetchBitmap(url);
    					// Put in mem and file caches...
    					Log.d("MITMapView", "Putting in memory tile: " + name);
    	    	    	imagesMap.put(name, new SoftReference<Bitmap>(bitmap));
    		        	mv.postInvalidate();
    		    		saveTileFile(name);  
    				} catch (RetryTileException e) {
    					e.printStackTrace();
    	        		mv.postInvalidate();
    				} catch (TileOutOfBoundsException e) {
    					badMap.put(name, true);
    				}
    				
    	    		pendingImages.remove(name);
	              }
	          };
	          
	          Looper.loop();
	      }
	  }
	
	public boolean TileOutOfBounds(int col, int row, int zoom) {
		return badMap.containsKey(imagesHashKey(col, row, zoom));
	}
	
	public void stop() {
		for(DownloadThread thread : mDownloadThreads) {
			Handler handler = thread.getHandler();
			if(handler != null) {
				Message msg = Message.obtain();
				msg.arg1 = STOP_DOWNLOAD_THREAD;
				handler.sendMessageAtFrontOfQueue(msg);
			}
		}
		SmallBitmapCache.releaseInstance();
	}
	
	private static class CacheHashMap extends LinkedHashMap<String, SoftReference<Bitmap>> {
		private static final long serialVersionUID = 1L;
		protected static final int MAX_ENTRIES = 30;
		
		protected boolean removeEldestEntry(Map.Entry<String, SoftReference<Bitmap>> eldest) {
			boolean test = size() > MAX_ENTRIES;
			if (test) {
				//eldest.getValue().recycle();  // TODO error? this should help
			}
			return test;
		}
	}
	
	private static class SmallBitmapCache extends LinkedHashMap<String, Bitmap> {
		private static final long serialVersionUID = 1L;
		protected static final int MAX_ENTRIES = 15;
		
		private static int sReferenceCount = 0;
		private static SmallBitmapCache sInstance = null;
		
		public static SmallBitmapCache getInstance() {
			sReferenceCount++;
			
			if(sInstance != null) {
				return sInstance;
			} else {
				sInstance = new SmallBitmapCache();
				return sInstance;
			}
		}
		
		public static void releaseInstance() {
			sReferenceCount--;
			if(sReferenceCount == 0) {
				sInstance = null;
			}
		}
		
		protected boolean removeEldestEntry(Map.Entry<String, Bitmap> eldest) {
			boolean test = size() > MAX_ENTRIES;
			if (test) {
				//eldest.getValue().recycle();  // TODO error? this should help
			}
			return test;
		}
	}
}
