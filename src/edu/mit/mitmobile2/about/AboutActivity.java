package edu.mit.mitmobile2.about;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.zip.CRC32;
import java.util.zip.Deflater;

import edu.mit.mitmobile2.CommonActions;
import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.ModuleActivity;
import edu.mit.mitmobile2.PrefsActivity;
import edu.mit.mitmobile2.R;

import android.content.Context;
import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class AboutActivity extends ModuleActivity implements OnGesturePerformedListener {

	//private static int sSquareSize; // must be an even number
	private static final int sSquareSize = 10;
	private static BitmapDrawable sBitmap;
	
	private ImageView mAboutImage;
	private View mBuildSettingsView;
	//private LinearLayout mHeader;
	private Context mContext = this;
	private GestureLibrary mLibrary;
	
	static final int MENU_HOME = Menu.FIRST;
	
	/****************************************************/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.about);
		
		setTitle("About");
		
		TextView tv = (TextView) findViewById(R.id.aboutAppTitle);
		tv.setText(this.getResources().getString(R.string.app_name));

		tv = (TextView) findViewById(R.id.aboutAppVersion);
		tv.setText("v" + BuildSettings.VERSION_NAME);
		
		mAboutImage = (ImageView) findViewById(R.id.aboutVersionImage);
		if (sBitmap == null) {
			sBitmap = getVersionBitmap();
		}
		mAboutImage.setImageDrawable(sBitmap);
		
		mBuildSettingsView = findViewById(R.id.aboutSettingsList);
		
		tv = (TextView) findViewById(R.id.aboutVersionId);
		String buildId = BuildSettings.BUILD_ID;
		if(buildId.length() > 6) {
			buildId = buildId.substring(0, 6);
		}
		tv.setText("Version: " + buildId);
		
		TextView buildDateTV = (TextView) findViewById(R.id.aboutBuildDate);
		String dateString = DateFormat.getDateTimeInstance().format(new Date(BuildSettings.BUILD_TIME));
		buildDateTV.setText("Built: " + dateString);
		
		TextView buildSourceTV = (TextView) findViewById(R.id.aboutBuildSource);
		String buildSourceText;
		if(BuildSettings.BUILD_SOURCE.equals("repository")) {
			buildSourceText = BuildSettings.TAG;
		} else {
			buildSourceText = BuildSettings.BUILD_SOURCE;
		}
		buildSourceTV.setText("Built from: " + buildSourceText);
		
		TextView buildTagTV = (TextView) findViewById(R.id.aboutBuildTag);
		buildTagTV.setText("Built for: " + BuildSettings.BUILD_TAG);
		
		TextView serverDomainTV = (TextView) findViewById(R.id.aboutServerDomain);
		//serverDomainTV.setText("Server: " + BuildSettings.MOBILE_WEB_DOMAIN);
		serverDomainTV.setText("Server: " + app.getMobileWebDomain());

		//mHeader = (LinearLayout) findViewById(R.id.aboutHeader);
		//mHeader.setOnClickListener(newListener(true));
		mAboutImage.setOnClickListener(newListener(true));
			
		View aboutCreditsTV = findViewById(R.id.aboutCreditsTV);
		aboutCreditsTV.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, AboutCreditsActivity.class);
				startActivity(intent);
			}
		});

		View aboutMitTV = findViewById(R.id.aboutMITTV);
		aboutMitTV.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, AboutMITActivity.class);
				startActivity(intent);
			}
		});
		
		View sendFeedback = findViewById(R.id.send_feedback);
		sendFeedback.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CommonActions.composeEmail(mContext, getString(R.string.feedback_email));
			}
		});

		// GESTURE
		mLibrary = GestureLibraries.fromRawResource(this, R.raw.gestures);
        if (!mLibrary.load()) {
        	finish();
        }
	
	    GestureOverlayView gestures = (GestureOverlayView) findViewById(R.id.gestures);
	    gestures.addOnGesturePerformedListener(this);
	    // GESTURE
	}
	
	private OnClickListener newListener(boolean toggleVersionOn) {
		if (toggleVersionOn) {
			return new OnClickListener() {
				@Override
				public void onClick(View v) {
					mBuildSettingsView.setVisibility(View.VISIBLE);
					mAboutImage.setOnClickListener(newListener(false));
					//mHeader.setOnClickListener(newListener(false));
				}
			};
		} else {
			return new OnClickListener() {
				@Override
				public void onClick(View v) {
					mBuildSettingsView.setVisibility(View.GONE);
					mAboutImage.setOnClickListener(newListener(true));
					//mHeader.setOnClickListener(newListener(true));
				}
			};
		}
	}
	
	@Override
	public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
		ArrayList  predictions = mLibrary.recognize(gesture);

	    // We want at least one prediction
	    if (predictions.size() > 0) {
	        Prediction prediction = (Prediction)predictions.get(0);
	        // We want at least some confidence in the result
	        if (prediction.score > 1.0) {
	            // Show the selection
	        	startActivity( new Intent(this, PrefsActivity.class) );
	            //Toast.makeText(this, prediction.name, Toast.LENGTH_SHORT).show();
	        }
	    }
	}
	private BitmapDrawable getVersionBitmap() {
		String dirpath = "/data/data/" + BuildSettings.release_project_name + "/about";
		String buildId = BuildSettings.BUILD_ID;
		File dir = new File(dirpath);
		if (!dir.exists())
			dir.mkdir();
		
		Log.d("AboutActivity","buildId = " + BuildSettings.BUILD_ID);
		String path = dir + "/" + buildId.substring(0, 6) + ".png";
		Log.d("Global","path = " + path);
		File file = new File(path);
		if (!file.exists()) {
			for (File oldFile : dir.listFiles()) {
				oldFile.delete();
			}
			
			//sSquareSize = 2 * (Math.min(display.getWidth(), display.getHeight()) / 36);
			
			int hashLen = 3 * 4 * 4;
			
			while (buildId.length() < hashLen) {
				buildId += buildId;
			}
			buildId = buildId.substring(0, hashLen);
			
			try {
				file.createNewFile();
				FileOutputStream outStream = new FileOutputStream(file);

				// png signature
				outStream.write(new byte[] {
						(byte)0x89,	(byte)0x50, (byte)0x4e, (byte)0x47,
						(byte)0x0d,	(byte)0x0a, (byte)0x1a,	(byte)0x0a
				});
				
				// header chunk
				ByteBuffer buf = ByteBuffer.allocate(4 + 4 + 5);
				buf.put(splitInt(sSquareSize * 4)); // width
				buf.put(splitInt(sSquareSize * 4)); //height
				buf.put(new byte[] {
						(byte)0x04, (byte)0x03, (byte)0x0, (byte)0x0, (byte)0x0}
				); // bitdpth, colrtyp, cmprss, fltr, intrlce
				writeChunk(outStream, "IHDR", buf.array());
				
				// palette chunk
				byte[] palette = new byte[hashLen];
				for (int i = 0; i < hashLen; i++) {
					// want f(x) s.t. f('0') = 0, f('9') = 9; f('a') = 10, ...
					// since ord('0') = 48 and ord('a') = 97 and 
					// 97 ~ (48 + 10) ~ 9 (mod 39), can use f(x) = ord(x) % 39 - 9
					int c = ((int)buildId.charAt(i) % 39 - 9) * 16;
					palette[i] = (byte)c;
				}
				writeChunk(outStream, "PLTE", palette);

				// image data chunk
				buf = ByteBuffer.allocate(4 * 4 * sSquareSize * sSquareSize + 4 * sSquareSize);
				int curPos = 0;
				for (int bigRow = 0; bigRow < 4; bigRow++) {
					for (int smallRow = 0; smallRow < sSquareSize; smallRow++) {
						buf.put((byte)0);
						for (int bigCol = 0; bigCol < 4; bigCol++) {
							for (int smallCol = 0; smallCol < sSquareSize / 2; smallCol++) {
								int pixel = (bigRow * 4 + bigCol);
								// repeat the same pixel twice in one byte
								buf.put((byte)(pixel*16 + pixel));
								curPos++;
							}
						}
					}
				}
				
				Deflater deflater = new Deflater();
				deflater.setInput(buf.array());
				deflater.finish();
				byte[] zipped = new byte[buf.capacity()];
				int numBytes = deflater.deflate(zipped);
				buf = ByteBuffer.allocate(numBytes);
				buf.put(zipped, 0, numBytes);
				byte[] compressed = buf.array();
				writeChunk(outStream, "IDAT", compressed);
				
				writeChunk(outStream, "IEND", new byte[0]);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return new BitmapDrawable(getResources(), path);
		
	}
	
	private byte[] splitInt(long num) {
		return new byte[] {
				(byte)((num >> 24) & 255), (byte)((num >> 16) & 255),
				(byte)((num >> 8) & 255), (byte)(num & 255) };
	}
	
	private void writeChunk(FileOutputStream stream, String header, byte[] content) throws IOException {
		stream.write(splitInt(content.length));
		int len = header.length() + content.length;
		ByteBuffer buf = ByteBuffer.allocate(len);
		buf.put(new byte[] {
				(byte)header.charAt(0), (byte)header.charAt(1),
				(byte)header.charAt(2), (byte)header.charAt(3) });
		buf.put(content);
		CRC32 crc32 = new CRC32();
		crc32.update(buf.array(), 0, len);
		
		stream.write(buf.array());
		stream.write(splitInt(crc32.getValue()));
	}

	@Override
	protected Module getModule() {
		return new AboutModule();
	}

	@Override
	public boolean isModuleHomeActivity() {
		return true;
	}

	@Override
	protected void prepareActivityOptionsMenu(Menu menu) { }	
}
