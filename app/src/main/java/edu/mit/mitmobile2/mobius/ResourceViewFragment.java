package edu.mit.mitmobile2.mobius;/*
 * Copyright 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.mobius.model.ResourceItem;
import timber.log.Timber;

/**
 * A fragment representing a single step in a wizard. The fragment shows a dummy title indicating
 * the page number, along with some dummy text.
 *
 *
 */
public class ResourceViewFragment extends Fragment {

    public static final ResourceViewFragment newInstance(ResourceItem resourceItem) {
        ResourceViewFragment f = new ResourceViewFragment();
        Bundle bdl = new Bundle(1);
        bdl.putParcelable("resource", resourceItem);
        f.setArguments(bdl);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        ResourceItem resourceItem = getArguments().getParcelable("resource");
        Timber.d("room = " + resourceItem.getName());
        Timber.d(getArguments().toString());
        Timber.d("resourc is null = " + (resourceItem == null));
        View v = inflater.inflate(R.layout.fragment_resource_view, container, false);

        if (resourceItem.getImages().length > 0) {
            String imgUrl = "http://kairos-dev.mit.edu/image/" + resourceItem.getImages()[0] + "?size=small";
            Timber.d("image url = " + imgUrl);
            ImageView resourceImage = (ImageView)v.findViewById(R.id.resource_image);
            Ion.with(resourceImage).load(imgUrl);
        }


        TextView resourceName = (TextView)v.findViewById(R.id.resource_view_name);
        resourceName.setText(resourceItem.getName());

        return v;
    }
}