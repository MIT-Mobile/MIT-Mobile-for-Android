package edu.mit.mitmobile2.facilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cocosw.bottomsheet.BottomSheet;
import com.squareup.picasso.Picasso;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.facilities.activity.LocationActivity;
import edu.mit.mitmobile2.facilities.activity.ProblemTypesActivity;
import edu.mit.mitmobile2.facilities.activity.RoomDetailActivity;
import edu.mit.mitmobile2.shared.logging.LoggingManager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class FacilitiesFragment extends Fragment {

    private static final String MY_DIR = "MyDir";
    private static final String IMG_PREFIX = "img_";
    private static final String IMG_SUFFIX = ".jpg";
    private static final String BASE64_PREFIX = "data:image/png;base64,";

    private static final int PHOTO_REQUEST_CODE = 1;

    private Uri outputFileUri;
    private Uri editedPhotoUri;
    private boolean isAttached = false;
    private SharedPreferences prefs;
    private String photo;
    private String location;
    private String room;
    private String problem;
    private String email;
    private String description;

    private Menu optionMenu;
    private SharedPreferences.Editor editor;

    @InjectView(R.id.attach_remove_photo_text_view)
    TextView attachOrRemovePhotoTextView;

    @InjectView(R.id.photo_image_view)
    ImageView photoImageView;
    @InjectView(R.id.location_text_view)
    TextView locationTextView;
    @InjectView(R.id.problem_type_text_view)
    TextView problemTextView;
    @InjectView(R.id.room_layout)
    LinearLayout roomLayout;
    @InjectView(R.id.room_text_view)
    TextView roomTextView;
    @InjectView(R.id.email_edit_text)
    EditText emailEditText;
    @InjectView(R.id.description_edit_text)
    EditText descriptionEditText;

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
        Intent intent = new Intent(getActivity(), RoomDetailActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.problem_type_layout)
    public void selectProblemType() {
        Intent intent = new Intent(getActivity(), ProblemTypesActivity.class);
        startActivity(intent);
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

        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getActivity().setTitle(getResources().getString(R.string.facilities_title));
        setHasOptionsMenu(true);

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = prefs.edit();

        updateProblemValues();
        updateProblemViews();

        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                editor.putString(Constants.FACILITIES_EMAIL, s.toString());
                editor.commit();
                updateSubmitButtonStatus();
            }
        });
        descriptionEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                editor.putString(Constants.FACILITIES_DESCRIPTION, s.toString());
                editor.commit();
                updateSubmitButtonStatus();
            }
        });

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

    private void getNewPhotoFromActivity(Intent data) throws IOException {
        final boolean isCamera;
        if (data.toString().equals("Intent {  }")) {
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

    private void updateSubmitButtonStatus() {
        MenuItem item = optionMenu.findItem(R.id.submit);
        item.setEnabled(false);
        if (!location.isEmpty() && !room.isEmpty() && !problem.isEmpty()
                && !email.isEmpty() && !description.isEmpty()) {
            item.setEnabled(true);
            item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    submitProblem();
                    return false;
                }
            });
        } else {
            item.setEnabled(false);
        }
    }

    private void submitProblem() {
        FacilitiesManager.postProblem(email, location, room, problem, description, photo, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.report_submit),
                        Toast.LENGTH_SHORT).show();
                resetFacilitiesHome();
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });
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
        savePhotoStatus(null);
        photoImageView.setImageDrawable(null);
        photoImageView.setVisibility(View.GONE);
        attachOrRemovePhotoTextView.setText(getResources().getString(R.string.facilities_attach_photo));
    }

    private void convertImage(Uri uri) throws IOException {
        InputStream is = getActivity().getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(is);
        is.close();
        new Base64Task().execute(bitmap);

        photoImageView.setVisibility(View.VISIBLE);

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        int photoImageViewWidth = display.getWidth();
        int photoImageViewHeight = (int) getResources().getDimension(R.dimen.faciliteis_image_view);

        int photoWidth = (photoImageViewWidth > bitmap.getWidth()) ? bitmap.getWidth() : photoImageViewWidth;
        int photoHeight = (photoImageViewHeight > bitmap.getHeight()) ? bitmap.getHeight() : photoImageViewHeight;

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

            String base64String = Base64.encodeToString(b, Base64.DEFAULT);
            savePhotoStatus(base64String);

            return BASE64_PREFIX + Base64.encodeToString(b, Base64.DEFAULT);
        }

        @Override
        protected void onPostExecute(String result) {
        }
    }

    private void resetFacilitiesHome() {
        isAttached = false;
        updateSubmitButtonStatus();
        editor.remove(Constants.FACILITIES_EMAIL);
        editor.remove(Constants.FACILITIES_LOCATION);
        editor.remove(Constants.FACILITIES_ROOM_NUMBER);
        editor.remove(Constants.FACILITIES_PROBLEM_TYPE);
        editor.remove(Constants.FACILITIES_DESCRIPTION);
        editor.remove(Constants.FACILITIES_PHOTO);
        editor.commit();

        updateProblemValues();
        updateProblemViews();
    }

    private void updateProblemValues() {
        location = prefs.getString(Constants.FACILITIES_LOCATION, "");
        problem = prefs.getString(Constants.FACILITIES_PROBLEM_TYPE, "");
        room = prefs.getString(Constants.FACILITIES_ROOM_NUMBER, "");
        photo = prefs.getString(Constants.FACILITIES_PHOTO, "");
        email = prefs.getString(Constants.FACILITIES_EMAIL, "");
        description = prefs.getString(Constants.FACILITIES_DESCRIPTION, "");
    }

    private void updateProblemViews() {
        if (!photo.isEmpty()) {
            try {
                photoImageView.setVisibility(View.VISIBLE);
                byte[] decodedString = Base64.decode(photo, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                photoImageView.setImageBitmap(bitmap);
                isAttached = true;
            } catch (Resources.NotFoundException e) {
                LoggingManager.Timber.d("____________photo error____________", e);
            }
        } else {
            photoImageView.setVisibility(View.GONE);
        }

        emailEditText.setText((email.isEmpty()) ? null : email);
        roomTextView.setText((room.isEmpty()) ? null : room);
        problemTextView.setText((problem.isEmpty()) ? null : problem);
        locationTextView.setText((location.isEmpty()) ? null : location);
        descriptionEditText.setText((description.isEmpty()) ? null : description);
        roomLayout.setVisibility((location.isEmpty()) ? View.GONE : View.VISIBLE);

        attachOrRemovePhotoTextView.setText((isAttached) ? getResources().getString(R.string.facilities_remove_photo) : getResources().getString(R.string.facilities_attach_photo));

    }

    private void savePhotoStatus(String base64String) {
        if (isAttached) {
            editor.putString(Constants.FACILITIES_PHOTO, base64String);
        } else {
            editor.remove(Constants.FACILITIES_PHOTO);
        }
        editor.commit();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_facilities, menu);
        optionMenu = menu;
        updateSubmitButtonStatus();

        super.onCreateOptionsMenu(menu, inflater);
    }
}
