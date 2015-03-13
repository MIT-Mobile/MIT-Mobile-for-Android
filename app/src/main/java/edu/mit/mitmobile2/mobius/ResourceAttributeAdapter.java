package edu.mit.mitmobile2.mobius;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import edu.mit.mitmobile2.R;

/**
 * Created by sseligma on 1/23/15.
 */
public class ResourceAttributeAdapter extends ArrayAdapter<ResourceAttribute> {
    private Context mContext;
    private List<ResourceAttribute> resourceAttributes;

    public ResourceAttributeAdapter(Context context, int layoutResourceId, List<ResourceAttribute> resourceAttributes) {
        super(context, layoutResourceId, resourceAttributes);
        this.mContext = context;
        this.resourceAttributes = resourceAttributes;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.row_label_value, null);
        }

        ResourceAttribute attribute = (ResourceAttribute) getItem(position);

        if (attribute != null) {

            // LABEL
            TextView row_label = (TextView) v.findViewById(R.id.row_label);
            row_label.setText(attribute.getLabel());

            // VALUE
            TextView row_value = (TextView) v.findViewById(R.id.row_value);
            String valueText = "";
            if (!attribute.getValue().isEmpty()) {
                for (int i = 0; i < attribute.getValue().size(); i++) {
                    valueText += attribute.getValue().get(i) + "\n";
                }
            }
            row_value.setText(valueText);

        }

        return v;
    }

}
