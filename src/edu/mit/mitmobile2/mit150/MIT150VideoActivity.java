package edu.mit.mitmobile2.mit150;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.about.Config;

public class MIT150VideoActivity extends Activity {

	VideoView videoPlayer;
	MediaController mediaCtrl;
	
	@Override
	protected void onPause() {
		mediaCtrl.setEnabled(false); 
		finish();
		super.onPause();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.video);

		final Context ctx = this;

		final Uri video = Uri.parse("android.resource://" + Config.release_project_name +"/raw/hockfield_150");
		
		videoPlayer = (VideoView) findViewById(R.id.videoView);
		
		mediaCtrl = new MediaController(this);
		mediaCtrl.setAnchorView(videoPlayer);

		videoPlayer.setMediaController(mediaCtrl);
		videoPlayer.setVideoURI(video);
		

		videoPlayer.setOnErrorListener(new OnErrorListener() {
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				// #1
				int pos = mp.getCurrentPosition();
				mp.reset();
				try {
					mp.setDataSource(ctx, video);
					mp.prepare();
				    mp.start();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				mp.seekTo(pos);
				// #2
				//mp.release();
				//finish();
				return true;  // false causes OnCompletion 
			}
		});
		

		videoPlayer.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				mediaCtrl.setEnabled(false); // needed to avoid Froyo hang bug?
				finish();
			}
		});
		
		videoPlayer.setOnPreparedListener(new OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
				mp.start();  // also need to wait before starting to avoid hanging bug...
			}
		});
		
		// maybe needed to avoid hanging?  (according to one forum post)
		videoPlayer.setFocusable(false);
		videoPlayer.setClickable(false);
		
		
	}
	
}
