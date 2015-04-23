package edu.mit.mitmobile2.mobius;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import edu.mit.mitmobile2.APIJsonResponse;
import edu.mit.mitmobile2.MITActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.mobius.model.Attribute;
import edu.mit.mitmobile2.mobius.model.ValuesetValue;
import edu.mit.mitmobile2.shared.logging.LoggingManager.Timber;

public class ResourceSearchActivity extends MITActivity   {

    int contentLayoutId = R.layout.content_resource_search;
    Context context;
    ArrayList<Attribute> attributes;
    List listData;
    ListView listView;
    LayoutInflater mInflater;
    LinearLayout researchSearchParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = this;
        setContentView(R.layout.content_resource_search);
        getSupportActionBar().setTitle("Advanced Search");
        attributes = new ArrayList();
        researchSearchParams = (LinearLayout)findViewById(R.id.resource_search_params);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        apiClient.getJson("attribute","",null,new Handler(){
            @Override
            public void handleMessage(Message msg) {
                APIJsonResponse response = (APIJsonResponse) msg.obj;
                if (response != null) {
                    JSONArray jsonArray = response.jsonArray;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            Attribute a = new Attribute(jsonArray.getJSONObject(i));
                            attributes.add(a);
                            researchSearchParams.addView(renderAttribute(a));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        });
    }

    private View renderAttribute(Attribute attribute) {
        if (attribute.getWidget_type().equalsIgnoreCase("radio")) {
            return renderRadioAttribute(attribute);
        }

        else {
            return renderTextAttribute(attribute);
        }
    }

    private View renderTextAttribute(Attribute attribute) {
        LinearLayout v = (LinearLayout)mInflater.inflate(R.layout.resource_search_text, null);
        TextView attributeLabel = (TextView)v.findViewById(R.id.attribute_label);
        attributeLabel.setText(attribute.getLabel());
        return v;
    }

    private View renderRadioAttribute(Attribute attribute) {
        LinearLayout v = (LinearLayout)mInflater.inflate(R.layout.resource_search_radio, null);
        TextView attributeLabel = (TextView)v.findViewById(R.id.attribute_label);
        attributeLabel.setText(attribute.getLabel());

        RadioGroup radioGroup = (RadioGroup)v.findViewById(R.id.attribute_value);

        ArrayList<ValuesetValue> values = attribute.get_valueset().getValues();
        for (int i = 0; i < values.size(); i++) {
            RadioButton radioButton = new RadioButton(context);
            radioButton.setText(values.get(i).getText());
            radioGroup.addView(radioButton);
        }
        return v;
    }

}
