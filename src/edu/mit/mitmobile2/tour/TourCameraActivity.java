package edu.mit.mitmobile2.tour;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import edu.mit.mitmobile2.R;

public class TourCameraActivity extends Activity {

	static final int MENU_TAKE_PIC = Menu.FIRST;
	static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1;
    static final String MIT_TAG = "MIT Tour";
	
    private static Uri imageUri;
	
	private TourImageAdapter ta;
	private Gallery galleryView;
	private TextView photoInfo;
	
    private Cursor cur_img;
    

    @SuppressWarnings("unused")
	private long thumbMicroId,origId,thumbMiniId;
	
    static final String[] proj_thumb = { MediaStore.Images.Thumbnails._ID, MediaStore.Images.Thumbnails.IMAGE_ID };

    // TODO drop either TITLE or DISPLAY_NAME
    static final String[] proj_img = { 
    		MediaStore.Images.Media._ID, MediaStore.Images.Media.MINI_THUMB_MAGIC, 
    		MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.TITLE,
			MediaStore.Images.Media.DATA, MediaStore.Images.Media.DESCRIPTION,  MediaStore.Images.Media.DATE_ADDED};
	
	
	
	/***************************************************************************************/
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    setContentView(R.layout.tour_camera);

	    

	    Display display = getWindowManager().getDefaultDisplay(); 
	    int width = display.getWidth();
	    int height = display.getHeight();
	    float smaller = (width<height)?width:height;
	    final int focused_size = (int) (smaller * 0.7);
	    final int unfocused_size = (int) (smaller * 0.5);

	    ta = new TourImageAdapter(this,unfocused_size);
	   
	    photoInfo = (TextView) findViewById(R.id.tourGalleryTV);
	    
	    galleryView =  (Gallery)  findViewById(R.id.gallery);
	    galleryView.setAdapter(ta);
	    
	    galleryView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(@SuppressWarnings("rawtypes") AdapterView parent, View v,int position, long id) {
		        String p = ta.filepaths.get(position);
				// FIXME
				// #1
				//Intent intent = new Intent(getApplicationContext(), ViewImage.class);
				//intent.putExtra("filename", i);
				// #2
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_VIEW);
				intent.setDataAndType(Uri.fromFile(new File(p)), "image/png");
				startActivity(intent);
			}
	    });
	    
	    galleryView.setOnItemSelectedListener(new OnItemSelectedListener() {
	    	View prevView;  // need to revert when losing focus
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				
				String text = "this will contain photo #" + position + " info";
				photoInfo.setText(text);
				//photoInfo.setText(ta.titles.get(position));
				
				//#1
				//Animation big_ani = AnimationUtils.loadAnimation(ctx, R.anim.bigger);
				//view.startAnimation(big_ani);
				// #2
				//view.setLayoutParams(new Gallery.LayoutParams(300, 250));
				view.setLayoutParams(new Gallery.LayoutParams(focused_size, focused_size));
				if (prevView!=null) {
					//prevView.setLayoutParams(new Gallery.LayoutParams(200, 150));
					prevView.setLayoutParams(new Gallery.LayoutParams(unfocused_size, unfocused_size));
					prevView.requestLayout();
				}
				prevView = view;
				view.requestLayout();
				galleryView.invalidate();
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
	    });
	    
	    addImageFilenames();

	}
	/*****************************************************************************/
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case MENU_TAKE_PIC:
			//long curTime = System.currentTimeMillis();
			//takePict("mit" +  String.valueOf(curTime) + ".jpg");
			takePict(this,"this could be Stop name");
			break;
		}
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		menu.add(0, MENU_TAKE_PIC, Menu.NONE, "Take Pic")
			.setIcon(R.drawable.menu_camera);
		return true;
	}
	
	/***************************************************************************************/
	private void addImageFilenames() {
		
		// Query Images and build filenames
		
		String sort = MediaStore.Images.Media.DATE_ADDED;
		String[] selectArgs = new String[] {MIT_TAG};

		cur_img = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, proj_img, 
				MediaStore.Images.Media.DESCRIPTION+"=?", selectArgs, sort);  
		
		int col_mini_id = cur_img.getColumnIndexOrThrow(MediaStore.Images.Media.MINI_THUMB_MAGIC);
		int col_title   = cur_img.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE);
		int col_file    = cur_img.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		
		String filename,title;

		ta.reset();
		cur_img.moveToFirst();
		while (!cur_img.isAfterLast()) {
			
			filename = cur_img.getString(col_file);
			title    = cur_img.getString(col_title);
			thumbMiniId =  cur_img.getLong(col_mini_id);
			
			ta.addImage(filename,title);
			
			cur_img.moveToNext();
		}
	    cur_img.close();
		
    	ta.notifyDataSetChanged();
    	galleryView.invalidate();
    		
	}
	/***************************************************************************************/
	public static void takePict(Activity a, String fileName) {
		
		ContentValues values = new ContentValues();
		values.put(MediaStore.Images.Media.TITLE, fileName);
		values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
		values.put(MediaStore.Images.Media.DESCRIPTION,MIT_TAG);
        
		imageUri = a.getContentResolver().insert(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
		
		// start external camera app...
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);  // if specified, image written here, else Bitmap returned
		intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
		intent.putExtra(MediaStore.EXTRA_FINISH_ON_COMPLETION, false);
		//intent.putExtra(MediaStore.EXTRA_SHOW_ACTION_ICONS, true); // froyo?
		
		a.startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

	}

	/*****************************************************/
	static void handleCameraResult(final Activity a) {

		AlertDialog.Builder builder = new AlertDialog.Builder(a);
		
		builder
		   .setMessage("Take another?")
		   .setCancelable(false)
	       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	           @Override
			public void onClick(DialogInterface dialog, int id) {
	        	    takePict(a, "another");
	           }
	       })
	       .setNegativeButton("No", new DialogInterface.OnClickListener() {
	           @Override
			public void onClick(DialogInterface dialog, int id) {
	                dialog.cancel();
	           }
	       });

		AlertDialog alert = builder.create();

		alert.show();
		
	}
	/***************************************************************************************/
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
		  
			if (resultCode == RESULT_OK) {

		    	if (imageUri!=null) {


		    		
		    		//
		    		// Ideally, we would query the Thumbnails database and update the adapter.
		    		// However, as of this time, there are several SDK bugs preventing return
		    		// of updated thumbnail (both query() and getThumbnail() fail)
		    		// so we will just make our own thumbnails from the Image file
		    		//

		    		// Get thumbId from Image
		    		Cursor cur_img = managedQuery(imageUri, proj_img, null, null, ""); 
		    		if (cur_img.getCount()==0) {
		    			return;
		    		}
		    		cur_img.moveToFirst();
		    		
		    		//int col_mini_id = cur_img.getColumnIndexOrThrow(MediaStore.Images.Media.MINI_THUMB_MAGIC);
		    		int col_file    = cur_img.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		    		int col_title   = cur_img.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE);
		    		
		    		String filename = cur_img.getString(col_file);
					String title    = cur_img.getString(col_title);
		    		//thumbMiniId =  cur_img.getLong(col_mini_id);
		    	    cur_img.close();
		    	
		    		ta.addImage(filename,title);
		        	galleryView.invalidate();

		    	}
		    	
		    	handleCameraResult(this);
		    	
		    } else if (resultCode == RESULT_CANCELED) {
		        Toast.makeText(this, "Picture was not taken", Toast.LENGTH_SHORT);
		    } else {
		        Toast.makeText(this, "Unexpected Activity result", Toast.LENGTH_SHORT);
		    }
			
		}
		
	}
	
}
