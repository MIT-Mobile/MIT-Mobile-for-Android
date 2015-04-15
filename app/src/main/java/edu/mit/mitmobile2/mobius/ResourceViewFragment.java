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

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import edu.mit.mitmobile2.MITAPIClient;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.mobius.model.ResourceAttribute;
import edu.mit.mitmobile2.mobius.model.ResourceItem;
import edu.mit.mitmobile2.mobius.model.RoomsetHours;
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
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        TabHost tabHost;
        final ResourceItem resourceItem = getArguments().getParcelable("resource");
        Timber.d("room = " + resourceItem.getName());
        Timber.d(getArguments().toString());
        Timber.d("resourc is null = " + (resourceItem == null));
        View v = inflater.inflate(R.layout.fragment_resource_view, container, false);

        tabHost = (TabHost) v.findViewById(android.R.id.tabhost);

        // setup must be called if not a TabActivity
        tabHost.setup();

        // Resource Image
        String imgUrl = "https://kairos.mit.edu/image/";

        if (MITAPIClient.environment.equalsIgnoreCase("dev")) {
            imgUrl = "https://kairos-dev.mit.edu/image/";
        } else if (MITAPIClient.environment.equalsIgnoreCase("test")) {
            imgUrl = "https://kairos-test.mit.edu/image/";
        }

        if (resourceItem.getImages() != null) {
            imgUrl += resourceItem.getImages()[0] + "?size=large";
            Timber.d("image url = " + imgUrl);
            ImageView resourceImage = (ImageView) v.findViewById(R.id.resource_image);
            Ion.with(resourceImage).load(imgUrl);
        }

        // Resource Name
        TextView resourceName = (TextView) v.findViewById(R.id.resource_view_name);
        resourceName.setText(resourceItem.getName());

        // Resource Status
        TextView resource_online = (TextView)v.findViewById(R.id.resource_online);
        TextView resource_offline = (TextView)v.findViewById(R.id.resource_offline);

        if (resourceItem.getStatus().equalsIgnoreCase(ResourceItem.ONLINE)) {
            resource_online.setVisibility(View.VISIBLE);
            resource_offline.setVisibility(View.GONE);
        }
        else {
            resource_online.setVisibility(View.GONE);
            resource_offline.setVisibility(View.VISIBLE);
        }
        // add shop info tab
        tabHost.addTab(tabHost.newTabSpec("Shop").setIndicator("Shop").setContent(new TabHost.TabContentFactory() {
            public View createTabContent(String arg0) {
                LinearLayout tab1 = (LinearLayout) inflater.inflate(R.layout.resource_shop_tab, null);

                // Resource Hours
                TableLayout resourceHours = (TableLayout)tab1.findViewById(R.id.resource_hours);
                // loop through the roomset hours and combine repeating hours where possible

                HashMap<String, String> hoursMap = new HashMap<String, String>();
                if (resourceItem.getHours() != null) {
                    String h;
                    Timber.d("num hours = " + resourceItem.getHours().size());
                    for (int i = 0; i < resourceItem.getHours().size(); i++) {
                        RoomsetHours rh = resourceItem.getHours().get(i);
                        Timber.d("hours = " + rh.getDay() + " " + rh.getStart_time() + " " + rh.getEnd_time());

                        if (hoursMap.containsKey(rh.getDay())) {
                            h = hoursMap.get(rh.getDay()) + '\n' + rh.getStart_time() + " - " + rh.getEnd_time();
                            hoursMap.put(rh.getDay(),h);
                        }
                        else {
                            hoursMap.put(rh.getDay(), rh.getStart_time() + " - " + rh.getEnd_time());
                        }
                    }

                    for (int d = 0; d < RoomsetHours.DAY_ARRAY.length; d++) {
                        if (hoursMap.containsKey(RoomsetHours.DAY_ARRAY[d])) {
                            // check for consecutive hours
                            TableRow tr = (TableRow) inflater.inflate(R.layout.row_label_value, null);

                            //Label
                            TextView label = (TextView) tr.findViewById(R.id.row_label);
                            label.setText(RoomsetHours.DAY_ARRAY[d]);

                            //Value
                            TextView value = (TextView) tr.findViewById(R.id.row_value);
                            value.setText(hoursMap.get(RoomsetHours.DAY_ARRAY[d]));

                            resourceHours.addView(tr);
                        }
                    }
                }

                // Resource Roomset
                TextView resourceRoomset = (TextView) tab1.findViewById(R.id.resource_roomset);
                resourceRoomset.setText(resourceItem.getRoomset_name());

                // Resource Room
                TextView resourceRoom = (TextView) tab1.findViewById(R.id.resource_room);
                resourceRoom.setText(resourceItem.getRoom());

                //View Map
                ImageView resourceViewMap = (ImageView)tab1.findViewById(R.id.resource_view_map);
                resourceViewMap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            JSONObject params = new JSONObject("{'where':[]}");
                                JSONObject criteria = new JSONObject();
                                criteria.put("field", "_id");
                                criteria.put("value",resourceItem.get_id());
                                params.getJSONArray("where").put(criteria);
                                Intent i = new Intent(getActivity(),ResourceListActivity.class);
                                i.putExtra("params",params.toString());
                                startActivity(i);
                        } catch (JSONException e) {
                            Timber.d(e.getMessage());
                        }
                   }
                });
                return tab1;
            }
        }));

        // add machine specs tab
        tabHost.addTab(tabHost.newTabSpec("Specs").setIndicator("Specs").setContent(new TabHost.TabContentFactory() {
            public View createTabContent(String arg0) {
                LinearLayout tab2 = (LinearLayout) inflater.inflate(R.layout.resource_specs_tab, null);

                // Resource Attributes
                TableLayout resourceAttributeTable = (TableLayout) tab2.findViewById(R.id.resource_view_attribute_table);

                // add attributes to the attribute view
                if (resourceItem.getAttributes() != null) {
                    for (int i = 0; i < resourceItem.getAttributes().size(); i++) {
                        ResourceAttribute a = resourceItem.getAttributes().get(i);
                        Timber.d("attribute " + i + " = " + a.get_id());

                        TableRow tr = (TableRow) inflater.inflate(R.layout.row_label_value, null);

                        //Label
                        TextView label = (TextView) tr.findViewById(R.id.row_label);
                        label.setText(a.getLabel());

                        //Value
                        TextView value = (TextView) tr.findViewById(R.id.row_value);
                        String valueString = "";
                        if (a.getValue() != null) {
                            for (int j = 0; j < a.getValue().length; j++) {
                                String s = (String) a.getValue()[j];
                                if (!s.trim().equals("")) {
                                    valueString += s + "\n";
                                }
                            }
                        }

                        value.setText(valueString);

                        // only add the attribute if the value is not empty
                        if (!valueString.isEmpty()) {
                            resourceAttributeTable.addView(tr);
                        }
                    }
                }

                return tab2;
            }
        }));

        return v;
    }
}