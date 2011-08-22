package edu.mit.mitmobile2.libraries;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import edu.mit.mitmobile2.CommonActions;
import edu.mit.mitmobile2.FullScreenLoader;
import edu.mit.mitmobile2.LockingScrollView;
import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SliderInterface;
import edu.mit.mitmobile2.TwoLineActionRow;

public class LibraryDetailView extends LockingScrollView implements SliderInterface {

    private Activity mActivity;
    private LibraryItem mLibraryItem;

    private FullScreenLoader mLoaderView;
    private TwoLineActionRow mDetailTitle;
    private TwoLineActionRow mDetailPhone;
    private TwoLineActionRow mDetailRoom;
    private TextView mDetailInfo;

    public LibraryDetailView(Activity activity, LibraryItem libraryItem) {
        super(activity);

        mActivity = activity;
        mLibraryItem = libraryItem;

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.library_detail, this);

        mLoaderView = (FullScreenLoader) findViewById(R.id.libraryDetailLoading);

        mDetailTitle = (TwoLineActionRow) findViewById(R.id.libraryDetailTitle);

        mDetailPhone = (TwoLineActionRow) findViewById(R.id.libraryDetailPhone);
        ImageView phoneImage = (ImageView) mDetailPhone.findViewById(R.id.simpleRowActionIcon);
        phoneImage.setImageResource(R.drawable.action_phone);
        mDetailPhone.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                CommonActions.callPhone(mActivity, mLibraryItem.tel);
            }
        });

        mDetailRoom = (TwoLineActionRow) findViewById(R.id.libraryDetailRoom);
        ImageView mapImage = (ImageView) mDetailRoom.findViewById(R.id.simpleRowActionIcon);
        mapImage.setImageResource(R.drawable.action_map);
        mDetailRoom.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                CommonActions.searchMap(mActivity, mLibraryItem.location);
            }
        });

        mDetailInfo = (TextView) findViewById(R.id.libraryDetailInfo);

    }

    @Override
    public LockingScrollView getVerticalScrollView() {
        return this;
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onSelected() {
        if (mLibraryItem.isDetailLoaded) {
            return;
        }

//        mLoaderView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        mLoaderView.setVisibility(VISIBLE);
        mLoaderView.showLoading();
        LibraryModel.fetchLibraryDetail(mLibraryItem, mActivity, new Handler() {

            @Override
            public void handleMessage(Message message) {

                if (message.arg1 == MobileWebApi.SUCCESS) {
                    mLoaderView.setVisibility(GONE);
                    mDetailTitle.setTitle(mLibraryItem.library);
                    mDetailTitle.setVisibility(VISIBLE);
                    mDetailPhone.setTitle(mLibraryItem.tel);
                    mDetailPhone.setVisibility(VISIBLE);
                    mDetailRoom.setTitle("Room " + mLibraryItem.location);
                    mDetailRoom.setVisibility(VISIBLE);
                    mDetailInfo.setText(Html.fromHtml(composeDetailInfo()));
                    mDetailInfo.setVisibility(VISIBLE);
                } else {
                    mLoaderView.showError();
                }
            }
        });

    }

    @Override
    public void updateView() {
    }

    private String composeDetailInfo() {
        StringBuilder builder = new StringBuilder();
        builder.append("<br /><b>Today's hours:</b>");
        builder.append(" ");
        builder.append(mLibraryItem.hoursToday);
        builder.append("<br /><br />");
        builder.append("<b>");
        builder.append(mLibraryItem.currentTerm.name);
        builder.append(" Hours(");

        DateFormat formatter = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US);
        String start = formatter.format(new Date(Long.parseLong(mLibraryItem.currentTerm.range_start)));
        builder.append(start.substring(0, start.indexOf(",")));
        builder.append("-");
        builder.append(formatter.format(new Date(Long.parseLong(mLibraryItem.currentTerm.range_end))));
        builder.append("):</b><br />");
        for (String key : mLibraryItem.currentTerm.hours.keySet()) {
            builder.append(key);
            builder.append(" ");
            builder.append(mLibraryItem.currentTerm.hours.get(key));
            builder.append("<br />");
        }
        return builder.toString();
    }

}
