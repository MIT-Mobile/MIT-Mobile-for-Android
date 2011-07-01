package edu.mit.mitmobile2.facilities;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import edu.mit.mitmobile2.AttributesParser;
import edu.mit.mitmobile2.Global;
import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.ModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.TwoLineActionRow;

public class FacilitiesDetailsActivity extends ModuleActivity {

	public static final String TAG = "FacilitiesProblemTypeActivity";
	private static final int MENU_INFO = 0;
	private static String ATTACH_PHOTO = "Attach Photo";
	private static String CHANGE_PHOTO = "Change Photo";

	private Context mContext;	
	private TextView problemStringTextView;
	private EditText mProblemDescriptionEditText;
	private TwoLineActionRow mAddAPhotoActionRow;
	private EditText sendAsEditText;
    private static final int CAMERA_PIC_REQUEST = 1;
    private static final int PIC_SELECTION = 2;    
    private TwoLineActionRow submitActionRow;
	private ImageView selectedImage;
	private Uri mCapturedImageUri;
	private Uri mSelectedImageUri;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);  
        this.mContext = this;
        createViews();
	}

	public void createViews() {
        setContentView(R.layout.facilities_details);        
    	
    	// Set problem string
        problemStringTextView = (TextView)findViewById(R.id.facilitiesProblemString);
        String problemString = "I'm reporting a problem with the " + Global.sharedData.getFacilitiesData().getProblemType();
        
        if (Global.sharedData.getFacilitiesData().getUserAssignedLocationName() != null) {
        	problemString += " in " + Global.sharedData.getFacilitiesData().getUserAssignedLocationName();
        }
        else if (Global.sharedData.getFacilitiesData().getUserAssignedRoomName() != null) {
        	problemString += " at " + Global.sharedData.getFacilitiesData().getBuildingNumber() + " in " + Global.sharedData.getFacilitiesData().getUserAssignedRoomName();      	
        }
        else if (Global.sharedData.getFacilitiesData().getBuildingRoomName().equalsIgnoreCase("INSIDE")) {
        	problemString += " inside " + Global.sharedData.getFacilitiesData().getLocationId();
        }
        else if (Global.sharedData.getFacilitiesData().getBuildingRoomName().equalsIgnoreCase("OUTSIDE")) {
        	problemString += " outside " + Global.sharedData.getFacilitiesData().getLocationId();
        }
        else {
        	problemString += " at " + Global.sharedData.getFacilitiesData().getBuildingNumber() + " in " + Global.sharedData.getFacilitiesData().getBuildingRoomName();        	
        }

        TextWatcher textWatcher = new TextWatcher() {

			@Override
			public void afterTextChanged(Editable editable) {
				boolean emailExists = sendAsEditText.getText().toString().trim().length() > 0;
				boolean descriptionExists = mProblemDescriptionEditText.getText().toString().trim().length() > 0;
				submitActionRow.setEnabled(emailExists && descriptionExists);
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
        };
        
        problemStringTextView.setText(problemString);
        
        mProblemDescriptionEditText = (EditText) findViewById(R.id.problemDescription);
        mProblemDescriptionEditText.addTextChangedListener(textWatcher);
        mProblemDescriptionEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus) {
					String problemText = mProblemDescriptionEditText.getText().toString();
					mProblemDescriptionEditText.setText(problemText.trim());
				}
			}
		});
        
        initDescriptionPadding();
        sendAsEditText = (EditText) findViewById(R.id.facilitiesSendAs);
        sendAsEditText.addTextChangedListener(textWatcher);
        sendAsEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus) {
					String sendAsText = sendAsEditText.getText().toString();
					sendAsEditText.setText(sendAsText.trim());
				}
			}
		});
        
        // Add A Photo
    	mAddAPhotoActionRow = (TwoLineActionRow)findViewById(R.id.facilitiesAddAPhotoActionRow);
    	mAddAPhotoActionRow.setTitle(ATTACH_PHOTO);
    	mAddAPhotoActionRow.setActionIconResource(R.drawable.photoopp);
	
    	mAddAPhotoActionRow.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
				builder.setTitle("Choose Action");
				String[] choices;
				
				final int takePhoto = 0;
				final int pickExistingPhoto = 1;
				final int detachPhoto = 2;
				
				if(mSelectedImageUri != null) {
					choices = new String[] {"Take a Photo", "Pick Existing Photo", "Detach Photo"};
				} else {
					choices = new String[] {"Take a Photo", "Pick Existing Photo"};
				}
				builder.setItems(choices, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
							case takePhoto:
								takePhoto();
								break;
							case pickExistingPhoto:
								pickExistingPhoto();
								break;
							case detachPhoto:
								detachPhoto();
								break;
						}
						
					}
				});
				
				AlertDialog dialog = builder.create();
				dialog.show();
				
			}
		});
    	
    	// selected Image
    	selectedImage = (ImageView)findViewById(R.id.selectedImage);
    	
    	// Submit form
    	submitActionRow = (TwoLineActionRow)findViewById(R.id.facilitiesSubmitActionRow);
    	submitActionRow.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {				
				submitForm();
			}
		});
    	submitActionRow.setEnabled(false);
    	submitActionRow.setFocusableInTouchMode(true);
    	submitActionRow.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);	
					imm.hideSoftInputFromWindow(submitActionRow.getWindowToken(), 0);
				}
			}
		});
	}
	
	int mPaddingLeft;
	int mPaddingTop;
	int mPaddingRightNoPicture;
	int mPaddingRightPicture;
	int mPaddingBottom;
	
	private void initDescriptionPadding() {
		mPaddingLeft = mProblemDescriptionEditText.getPaddingLeft();
		mPaddingTop = mProblemDescriptionEditText.getPaddingTop();
		mPaddingRightNoPicture = mProblemDescriptionEditText.getPaddingRight();
		mPaddingRightPicture = AttributesParser.parseDimension("104dip", mContext);
		mPaddingBottom = mProblemDescriptionEditText.getPaddingBottom();
	}
	
	private void takePhoto() {
	    ContentValues values = new ContentValues();   
	    mCapturedImageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);  
	        
    	Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); // Normally you would populate this with your custom intent.
    	cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageUri);
    	startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);  
	}
	
	private void pickExistingPhoto() {
		Intent choosePhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
		startActivityForResult(choosePhoto, PIC_SELECTION);
	}
	
	private void detachPhoto() {
        selectedImage = (ImageView)findViewById(R.id.selectedImage);
        selectedImage.setVisibility(View.GONE);
		selectedImage.setImageBitmap(null);
		mProblemDescriptionEditText.setPadding(mPaddingLeft, mPaddingTop, mPaddingRightNoPicture, mPaddingBottom);
		mSelectedImageUri = null;
		mAddAPhotoActionRow.setTitle(ATTACH_PHOTO);
	}
	
	
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == CAMERA_PIC_REQUEST) {
    			mSelectedImageUri = mCapturedImageUri;          
        	} else if(requestCode == PIC_SELECTION) {
        		mSelectedImageUri = data.getData();
        	}
			mAddAPhotoActionRow.setTitle(CHANGE_PHOTO);
			
	        selectedImage.setVisibility(View.VISIBLE);
	        mProblemDescriptionEditText.setPadding(mPaddingLeft, mPaddingTop, mPaddingRightPicture, mPaddingBottom);
	        long imageId = ContentUris.parseId(mSelectedImageUri);
	        Bitmap thumbnail = MediaStore.Images.Thumbnails.getThumbnail(getContentResolver(), imageId, MediaStore.Images.Thumbnails.MINI_KIND, null);
	        selectedImage.setImageBitmap(thumbnail); 
    	} 
    }
    
	@Override
	protected Module getModule() {
		return new FacilitiesModule();
	}

	@Override
	public boolean isModuleHomeActivity() {
		return false;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_INFO:
			Intent intent = new Intent(mContext, FacilitiesInfoActivity.class);					
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	protected void prepareActivityOptionsMenu(Menu menu) { 
	}
	
	void submitForm() {
		FileUploader fileUploader = new FileUploader();
		fileUploader.execute();
	}
	
	private class FileUploader extends AsyncTask<Void, Long, Boolean> implements FileUploadListener {
		ProgressDialog mProgressDialog;
		long mMaxBytes;
		String mEmail;
		String mProblemDescription;
		CountingMultipartEntity mUploadEntity; 
		
		@Override
		protected void onPreExecute() {
			mProblemDescription = mProblemDescriptionEditText.getText().toString().trim();
			mEmail = sendAsEditText.getText().toString().trim();
			if(mProblemDescription.length() == 0 || mEmail.length() == 0) {
				Builder builder = new AlertDialog.Builder(mContext);
				builder.setMessage("Email and a description of the problem is required.");
				builder.setNeutralButton("Okay", null);
				AlertDialog dialog = builder.create();
				dialog.show();
				FileUploader.this.cancel(true);
				return;
			}
			
			mUploadEntity = new CountingMultipartEntity(this);
			mProgressDialog = new ProgressDialog(mContext);
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mProgressDialog.setMessage("Uploading Data");
			mProgressDialog.setIndeterminate(false);
			mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					mUploadEntity.cancel();	
					FileUploader.this.cancel(true);
				}
			});
			mProgressDialog.show();
		}
		
		@Override
		protected Boolean doInBackground(Void... unusedArgs) {
			try {
				HttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost("http://" + Global.getMobileWebDomain() + "/api/?module=facilities&command=upload");
				mMaxBytes = 500;
				if(mSelectedImageUri != null) {
					InputStream imageStream = getContentResolver().openInputStream(mSelectedImageUri);
					byte[] imageData = IOUtils.toByteArray(imageStream);
					mMaxBytes += imageData.length; // this an approximation of the total bytes to be transferred
					InputStreamBody imageStreamBody = new InputStreamBody(new ByteArrayInputStream(imageData), "image/jpeg", "image");
					mUploadEntity.addPart("image", imageStreamBody);
				}
				addField("email", mEmail);
				addField("message", mProblemDescription);
				addField("location", Global.sharedData.getFacilitiesData().getLocationId());
				addField("locationName", Global.sharedData.getFacilitiesData().getLocationName());
				addField("locationNameByUser",  Global.sharedData.getFacilitiesData().getUserAssignedLocationName());
				addField("buildingNumber", Global.sharedData.getFacilitiesData().getBuildingNumber());
				addField("roomName",  Global.sharedData.getFacilitiesData().getBuildingRoomName());
				addField("roomNameByUser", Global.sharedData.getFacilitiesData().getUserAssignedRoomName());
				addField("problemType",  Global.sharedData.getFacilitiesData().getProblemType());
				publishProgress(new Long(0)); // initialize the progress bar
				
				httpPost.setEntity(mUploadEntity);
				HttpResponse response;
				response = httpClient.execute(httpPost);
				String responseText = MobileWebApi.convertStreamToString(response.getEntity().getContent());
				JSONObject responseObject = new JSONObject(responseText);
				return responseObject.getBoolean("success");
				
			} catch (FileNotFoundException fileException)  {
				fileException.printStackTrace();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			} catch(JSONException jsonException) {
				jsonException.printStackTrace();
			}
			
			return false;
		}
		
		private void addField(String fieldName, String fieldValue) throws UnsupportedEncodingException {
			if(fieldValue != null) {
				mUploadEntity.addPart(fieldName, new StringBody(fieldValue));
			}
		}
			
		@Override
		protected void onPostExecute(Boolean success) {
			mProgressDialog.dismiss();
			if(success) {
				Intent intent = new Intent(mContext, FacilitiesUploadSuccessModuleActivity.class);
				mContext.startActivity(intent);
			} else {
				Toast.makeText(mContext, MobileWebApi.NETWORK_ERROR, Toast.LENGTH_SHORT).show();
			}
		}
		
		@Override
		public void onBytesTransfered(long transferred) {
			publishProgress(transferred);	
		}
		
		@Override
		public void onProgressUpdate(Long... progress) {
			long progressBytes = progress[0];
			if(progressBytes > mMaxBytes) {
				// this a hack to account for the fact
				// that maxKiloBytes is just an approximation
				progressBytes = mMaxBytes; 
			}
			if(mMaxBytes > 10000) {
				// use kilobytes	
				int maxKiloBytes = (int)(mMaxBytes / 1000);
				mProgressDialog.setMax(maxKiloBytes);
				int progressKiloBytes = (int)(progress[0] / 1000);
				mProgressDialog.setProgress(progressKiloBytes);
			} else {
				mProgressDialog.setMax((int)mMaxBytes);
				mProgressDialog.setProgress((int)progressBytes);
			}
		}
		
	}


	/*
	 * methods and classes to hook into count how many bytes of the image have been uploaded
	 */
	private interface FileUploadListener {
		void onBytesTransfered(long transferred);
	}
	
	private class CountingMultipartEntity extends MultipartEntity {		
		FileUploadListener mFileUploadListener;
		CountingOutputStream mCountingOutputStream;
		
		CountingMultipartEntity(FileUploadListener fileUploadListener) {
			mFileUploadListener = fileUploadListener;
		}
		
		@Override
		public void writeTo(final OutputStream outstream) throws IOException {
			mCountingOutputStream = new CountingOutputStream(outstream, mFileUploadListener);
			super.writeTo(mCountingOutputStream);
		}
		
		public void cancel() {
			mCountingOutputStream.cancel();
		}
	}
	
	public static class CountingOutputStream extends FilterOutputStream {

		private long mTransferred;
		FileUploadListener mFileUploadListener;
		CountingMultipartEntity mUploadEntity;
		boolean isCancelled = false;
		
	    public CountingOutputStream(final OutputStream out, FileUploadListener fileUploadListener) {
	    	super(out);
	        mTransferred = 0;
	        mFileUploadListener = fileUploadListener;
	    }

	    public void cancel() {
	    	isCancelled = true;	
	    }
	    
	    public void write(byte[] b, int off, int len) throws IOException {
	    	if(isCancelled) {
	    		throw new IOException("Upload was cancelled");
	    	}
	    	out.write(b, off, len);
	        mTransferred += len;
	        mFileUploadListener.onBytesTransfered(mTransferred);
	    }

	    public void write(int b) throws IOException {
	    	if(isCancelled) {
	    		throw new IOException("Upload was cancelled");
	    	}
	    	out.write(b);
	        mTransferred++;
	        mFileUploadListener.onBytesTransfered(mTransferred);
	    }
	}
}
	

	
	