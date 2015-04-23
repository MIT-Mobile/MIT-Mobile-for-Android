package edu.mit.mitmobile2.news.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;

import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.MitMobileApplication;
import edu.mit.mitmobile2.OttoBusEvent;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.news.models.MITNewsGalleryImage;

public class MITNewsGalleryFragment extends Fragment {

    public MITNewsGalleryFragment() {
    }

    public static MITNewsGalleryFragment newInstance(MITNewsGalleryImage galleryImage) {
        MITNewsGalleryFragment fragment = new MITNewsGalleryFragment();
        Bundle args = new Bundle();
        args.putParcelable(Constants.News.IMAGES_KEY, galleryImage);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_gallery, null);
        MITNewsGalleryImage galleryImage = getArguments().getParcelable(Constants.News.IMAGES_KEY);

        ImageView imageView = (ImageView) view.findViewById(R.id.gallery_image_view);
        Picasso.with(getActivity()).load(galleryImage.getRepresentations().get(0).getUrl()).fit().centerInside().into(imageView);

        RelativeLayout rootView = (RelativeLayout) view.findViewById(R.id.gallery_image_root_layout);
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MitMobileApplication.bus.post(new OttoBusEvent.ToggleDescriptionEvent());
            }
        });

        return view;
    }
}
