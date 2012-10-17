package edu.mit.mitmobile2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import edu.mit.mitmobile2.about.BuildSettings;
import edu.mit.mitmobile2.tour.Tour.TourItem;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Environment;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;

public class AudioPlayer implements OnCompletionListener, OnErrorListener {
	
	class DownloadMsg {
		String url;
		int origPage;
		ImageButton ib;
		boolean kill_thread = false;
	}
	
	interface DownloadListener {
		void done();
	}
	
	private Context ctx;
	
	private MediaPlayer mp;
	
	private static AudioPlayer instance;

	DownloadListener callback;
	
	static String TAG = "AudioPlayer";
	
	private boolean hasSDcard = true;

	private ImageButton curAudioButton;
	private String curFilename;
	private int curPos = 0;
	
	private String root;
	
	private HashMap<String,String> fetching = new HashMap<String,String>();
	private HashMap<String,Integer> seekTimes = new HashMap<String,Integer>();
	
	private LinkedList<DownloadMsg> downloadQueue = new LinkedList<DownloadMsg>();
	private Thread downloadThread;

	private int curPage = -1;
	
	/*****************************************************/
	public AudioPlayer() {

		mp = new MediaPlayer();
		mp.setOnCompletionListener(this);
		mp.setOnErrorListener(this);
		//mp.setOnBufferingUpdateListener(this);
	
	}
	/*****************************************************/
	public void init(Context ctx, DownloadListener callback) {

		this.ctx = ctx;
		this.callback = callback;
		
		if (instance==null) {
			instance = new AudioPlayer();
		}
		setupCache();
		
	}
	/*****************************************************/
	void downloadFileThread(final String url, ImageButton ib) {

		if (fetching.containsKey(url)) {
			return;
		}

		Log.v(TAG, "audio_debug: downloadFileThread");
		
    	fetching.put(url, null);
    	
		DownloadMsg dm = new DownloadMsg();
		dm.origPage = curPage;
		dm.url = url;
		dm.ib = ib;
		
		if (ib!=null) {
			// FIXME
			//ib.setImageResource(R.drawable.sm_busybox);  // this is xml
			ib.setImageResource(R.drawable.sm_busybox_01);
			Animation spinner = AnimationUtils.loadAnimation(ctx,R.anim.spin);
			ib.startAnimation(spinner);
		}
		
		downloadQueue.addLast(dm);
		
		if ((downloadThread==null)|| !downloadThread.isAlive()){
			
			Log.v(TAG, "audio_debug: downloadFileThread - new Thread");
			downloadThread = new Thread() {
				@Override
				public void run() {
					while (!downloadQueue.isEmpty()) {
						Log.v(TAG, "audio_debug: downloadFileThread - remove");
						final DownloadMsg dm = downloadQueue.removeFirst();
						if (dm.kill_thread) break;
						downloadFile(url);
						if (dm.ib!=null) {
							// Stop spin...
							dm.ib.post(new Runnable() {
								@Override
								public void run() {
									dm.ib.setImageResource(R.drawable.audio_pause);
									dm.ib.setAnimation(null);
								}
							});
						}
						final String filename = safeFilenameEncode(url);
						if (filename.equalsIgnoreCase(curFilename)) {
							if (dm.origPage==curPage) play(filename);  // TODO auto-play last pressed button 
							//play(filename);  // auto-play last pressed button 
						}
				    	//if (callback!=null) callback.done();
					}
				}
			};
			downloadThread.start();
		}
	}
	
	void close() {
		Log.v(TAG, "audio_debug: close");
		mp.release();
		// FIXME race?
		DownloadMsg dm = new DownloadMsg();
		dm.kill_thread = true;
		//downloadThread.stop();  // deprecated and considered unsafe?
		downloadQueue.add(dm);
		downloadThread = null;
	}
	/*****************************************************/
	void downloadFile(final String url) {
		
    	// TODO stop spinning arrow on error

		Log.v(TAG, "audio_debug: downloadFile");
		
    	DefaultHttpClient httpClient = new DefaultHttpClient();
    	HttpGet request = new HttpGet(url);
    	try {
    		HttpResponse response = httpClient.execute(request);
    		save(response.getEntity().getContent(),safeFilenameEncode(url));
    	} catch (ClientProtocolException e) {
    		e.printStackTrace();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
		fetching.remove(url);

    	
	}
	/*****************************************************/
	boolean isFileAvailable(String filename) {
		
		if (!fetching.containsKey(filename)) {
			String path = root + "/" + filename;
			File file = new File(path, "");
	    	if (file.exists()) return true;
		}
		
		return false;
	}

	/*****************************************************/
	private void setupCache() {
	
		String subpath = "/Android/data/" + BuildSettings.release_project_name + "/cache/";  // TODO for API 7 or lower, this dir will get deleted on uninstall
		
    	String state = Environment.getExternalStorageState();

		hasSDcard = false;
    	root = "/data/data/edu.mit.mitmobile2/mitmobile2audio";  // default to Internal file
    	
    	if (Environment.MEDIA_MOUNTED.equals(state)) {
    		
    		hasSDcard = true;
        	root = Environment.getExternalStorageDirectory().toString() + subpath;
        	//root = Environment.getExternalStorageDirectory().toString() + "/mitmobile2audio";
        	
	    	File file = new File(root, "");
	    	if (!file.exists()) {
		    	boolean success = file.mkdirs();  // makes whole path
		    	if (!success) {
		    		hasSDcard = false;
		    		Log.e("Audio","Audio: mkdir failed");
		    	}
	    	}
			
       	} 

	}
	/***********************************************************/
	public void togglePlay(String url, ImageButton audioButton) {

		Log.v(TAG, "audio_debug: togglePlay");
		
		boolean playNew = false;
		final String filename = safeFilenameEncode(url);
		
		if (mp.isPlaying()) {
			
			// Remember current...
			curPos = mp.getCurrentPosition();
			seekTimes.put(curFilename, curPos);
			
			// Maybe start new...
			if (filename.equals(curFilename)) {  
				// Same so pause
				audioButton.setImageResource(R.drawable.audio_speaker);
				mp.pause();
			} else {
				// Different so stop old...
				curAudioButton.setImageResource(R.drawable.audio_speaker);
				mp.stop();
				// ... and start new
				playNew = true;
			}
			
		} else {
			// TODO check if paused?
			playNew = true;
		} 
		
		if (playNew) {
			mp.reset();
			if (!isFileAvailable(filename)) {
				downloadFileThread(url, audioButton);
			} else {
				audioButton.setImageResource(R.drawable.audio_pause);
				play(filename);
			}
		}
		
		curFilename = filename;
		curAudioButton = audioButton;
		
	}
	/*****************************************************/
	public void play(String fname) {

		Log.v(TAG, "audio_debug: play");
		
		curFilename = fname;
		
	    try {
	    	
			String path = root + "/" + curFilename;
			mp.reset();
    		mp.setDataSource(path);
			mp.prepare();
		    mp.start();
		    
		    // Continue from last paused?
			Integer seekTime = seekTimes.get(curFilename);
			if (seekTime!=null) {
				curPos = seekTime;
				mp.seekTo(curPos);
			}
		    
		    
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	/*****************************************************/
	private void save(InputStream in, String fname) {

		File file = null;
		OutputStream fos = null;

		Log.v(TAG, "audio_debug: save");
    	try {
    		
    		// Open file...
			if (hasSDcard) {
				String path = root + "/" + fname;
		    	file = new File(path, "");
				fos = new FileOutputStream(file);
			}
			else {
				fos = ctx.openFileOutput(fname, Context.MODE_PRIVATE);
			}

			// Write file...
		    byte[] buffer = new byte[1024];
		    int len1 = 0;
			while ( (len1 = in.read(buffer)) > 0 ) {
				fos.write(buffer,0, len1);
			}

			fos.flush();
			fos.close();
			
    	} catch (FileNotFoundException e) {
    		e.printStackTrace();
    	} catch (IOException e) {
    		// delete bad data
	    	if (file!=null) file.delete();
    		e.printStackTrace();
    	}
    
    	
	}
	/*****************************************************/
	public void stop() {
		Log.v(TAG, "audio_debug: stop");
		if (mp.isPlaying()) {
			mp.stop();
			// Reset button...
			if (curAudioButton!=null) 
				curAudioButton.setImageResource(R.drawable.audio_speaker);
		}
		seekTimes.put(curFilename, null);
	}
	/*****************************************************/
	@Override
	public void onCompletion(MediaPlayer mp) {
		if (curAudioButton!=null) {
			curAudioButton.setImageResource(R.drawable.audio_speaker);
		}
		//mp.reset();
		seekTimes.put(curFilename, null);
	}
	
	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		if (curAudioButton!=null) {
			curAudioButton.setImageResource(R.drawable.audio_speaker);
		}
		seekTimes.put(curFilename, null);
		return false;
	}
	
	/*****************************************************/
	public void downloadTourItemAudio(TourItem tourItem) {
		if(tourItem.getAudioUrl() != null) {
			downloadFileThread(tourItem.getAudioUrl(), null);
		}
	}
	
	/*
	 * this is just the simplest safe filename encoding scheme i could think
	 * of to avoid having unsafe characters such as '/' in the file name
	 * 
	 * just replace all characters with hex version of the codepoint
	 */
	private static String safeFilenameEncode(String url) {
		String safeFilename = "";
		boolean isFirst = true;

		for(int i=0; i < url.length(); i++) {
			if(isFirst) {
				isFirst = false;
			} else {
				safeFilename += "-";
			}
			safeFilename += Integer.toHexString(url.codePointAt(i));
		}

		return safeFilename;
	}
	
	public void setPage(int newPage) {
		this.curPage = newPage;
	}
}
