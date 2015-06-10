package edu.mit.mitmobile2.facilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cocosw.bottomsheet.BottomSheet;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import edu.mit.mitmobile2.MitMobileApplication;
import edu.mit.mitmobile2.OttoBusEvent;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.facilities.activity.LocationActivity;
import edu.mit.mitmobile2.facilities.model.FacilityPlace;
import edu.mit.mitmobile2.shared.logging.LoggingManager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class FacilitiesFragment extends Fragment {

    private static final String MY_DIR = "MyDir";
    private static final String IMG_PREFIX = "img_";
    private static final String IMG_SUFFIX = ".jpg";
    private static final int PHOTO_REQUEST_CODE = 1;
    private static final String BASE64_PREFIX = "data:image/png;base64,";

    private Uri outputFileUri;
    private Uri editedPhotoUri;
    private boolean isAttached = false;

    @InjectView(R.id.attach_remove_photo_text_view)
    TextView attachOrRemovePhotoTextView;
    @InjectView(R.id.photo_image_view)
    ImageView photoImageView;

    @OnClick(R.id.urgent_issues_text_view)
    public void openUrgentIssuesBottomSheet() {
        new BottomSheet.Builder(getActivity()).title(getResources().getString(R.string.facilities_contact_via)).
                sheet(R.menu.menu_facilities_bottom_sheet).listener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case R.id.phone_item:
                        sendUrgentIssuesByCall();
                        break;
                    case R.id.email_item:
                        sendUrgentIssuesByEmail();
                        break;
                }
            }
        }).show();
    }

    @OnClick(R.id.location_layout)
    public void selectLocation() {
        Intent intent = new Intent(getActivity(), LocationActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.room_layout)
    public void selectRoom() {
        //TODO : go to room screen
    }

    @OnClick(R.id.problem_type_layout)
    public void selectProblemType() {
        //TODO : go to problem type screen
    }

    @OnClick(R.id.attach_remove_photo_text_view)
    public void attachOrRemovePhoto() {
        if (!isAttached) {
            attachPhoto();
        } else {
            removePhoto();
        }
    }

    public FacilitiesFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_facilities, null);
        ButterKnife.inject(this, view);

        getActivity().setTitle(getResources().getString(R.string.facilities_title));
        setHasOptionsMenu(true);

        if (isAttached) {
            attachOrRemovePhotoTextView.setText(getResources().getString(R.string.facilities_remove_photo));
        } else {
            attachOrRemovePhotoTextView.setText(getResources().getString(R.string.facilities_attach_photo));
        }

        /*
        FacilitiesManager.getProblemTypes(getActivity(), new Callback<List<String>>() {

            @Override
            public void success(List<String> strings, Response response) {
                // TODO: handle response
            }

            @Override
            public void failure(RetrofitError error) {
                MitMobileApplication.bus.post(new OttoBusEvent.RetrofitFailureEvent(error));
            }
        });
        */

        /*
        FacilitiesManager.getLocationProperties(getActivity(), new Callback<HashMap<String, HashMap<String, String>>>() {

            @Override
            public void success(HashMap<String, HashMap<String, String>> stringHashMapHashMap, Response response) {
                // TODO: handle response
            }

            @Override
            public void failure(RetrofitError error) {
                MitMobileApplication.bus.post(new OttoBusEvent.RetrofitFailureEvent(error));
            }
        });
        */

        /*
        FacilitiesManager.getPlaces(getActivity(), new Callback<List<FacilityPlace>>() {
            @Override
            public void success(List<FacilityPlace> facilityPlaces, Response response) {
                // TODO: handle response
            }

            @Override
            public void failure(RetrofitError error) {
                MitMobileApplication.bus.post(new OttoBusEvent.RetrofitFailureEvent(error));
            }
        });
        */

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == PHOTO_REQUEST_CODE) {
            isAttached = true;
            photoImageView.setVisibility(View.VISIBLE);
            attachOrRemovePhotoTextView.setText(getResources().getString(R.string.facilities_remove_photo));
            try {
                getNewPhotoFromActivity(data);
            } catch (IOException e) {
                LoggingManager.Timber.d("____________photo error____________", e);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_facilities, menu);

        MenuItem item = menu.findItem(R.id.submit);
        item.setEnabled(false);

        super.onCreateOptionsMenu(menu, inflater);
    }

    private void sendUrgentIssuesByEmail() {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{getResources().getString(R.string.facilities_urgent_email_address)});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.facilities_urgent_email_subject));
        emailIntent.setType("message/rfc822");
        startActivity(Intent.createChooser(emailIntent, "Choose an email Client :"));
    }

    private void sendUrgentIssuesByCall() {
        new AlertDialog.Builder(getActivity())
                .setMessage(getResources().getString(R.string.facilities_urgent_call_info) + "?")
                .setPositiveButton(getResources().getString(R.string.ok_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String uri = "tel:" + getResources().getString(R.string.facilities_urgent_call_number).trim();
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse(uri));
                        startActivity(intent);
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    private void attachPhoto() {
        final File root = new File(Environment.getExternalStorageDirectory()
                + File.separator + MY_DIR + File.separator);
        root.mkdirs();
        final String fname = IMG_PREFIX + System.currentTimeMillis() + IMG_SUFFIX;
        final File sdImageMainDirectory = new File(root, fname);
        outputFileUri = Uri.fromFile(sdImageMainDirectory);

        final List<Intent> cameraIntents = new ArrayList<>();
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getActivity().getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            cameraIntents.add(intent);
        }

        final Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        final Intent chooserIntent = Intent.createChooser(galleryIntent, getResources().getString(R.string.select_image_source));
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[]{}));

        startActivityForResult(chooserIntent, PHOTO_REQUEST_CODE);
    }

    private void removePhoto() {
        isAttached = false;
        photoImageView.setImageDrawable(null);
        photoImageView.setVisibility(View.GONE);
        attachOrRemovePhotoTextView.setText(getResources().getString(R.string.facilities_attach_photo));
    }

    private void getNewPhotoFromActivity(Intent data) throws IOException {
        final boolean isCamera;
        if (data == null) {
            isCamera = true;
        } else {
            final String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }

        if (isCamera) {
            editedPhotoUri = outputFileUri;
        } else {
            editedPhotoUri = data.getData();
        }

        convertImage(editedPhotoUri);
    }

    private void convertImage(Uri uri) throws IOException {
        InputStream is = getActivity().getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(is);
        is.close();
        new Base64Task().execute(bitmap);

        int photoWidth = (photoImageView.getWidth() > bitmap.getWidth()) ? bitmap.getWidth() : photoImageView.getWidth();
        int photoHeight = (photoImageView.getHeight() > bitmap.getHeight()) ? bitmap.getHeight() : photoImageView.getHeight();

        Picasso.with(photoImageView.getContext())
                .load(editedPhotoUri.toString())
                .resize(photoWidth, photoHeight)
                .centerCrop()
                .into(photoImageView);
    }

    private class Base64Task extends AsyncTask<Bitmap, Void, String> {
        @Override
        protected String doInBackground(Bitmap... params) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            params[0].compress(Bitmap.CompressFormat.JPEG, 70, baos);
            byte[] b = baos.toByteArray();
            return BASE64_PREFIX + Base64.encodeToString(b, Base64.DEFAULT);
        }

        @Override
        protected void onPostExecute(String result) {
        }
    }
}
