package edu.mit.mitmobile2.news.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.mit.mitmobile2.R;
import timber.log.Timber;

public class NewsUtils {
    public static String formatNewsPublishedTime(String publishedTime) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy");

        Date date;
        String formattedDate = null;
        try {
            date = inputFormat.parse(publishedTime);
            formattedDate = outputFormat.format(date);

        } catch (ParseException e) {
            Timber.d("************error************", e);
        }

        return formattedDate;
    }

    public static void openWebsiteDialog(final Context context, final String url) {
        new AlertDialog.Builder(context)
                .setTitle(context.getResources().getString(R.string.open_in_browser_q))
                .setMessage(url)
                .setPositiveButton(context.getResources().getString(R.string.open_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    }
                })
                .setNegativeButton(context.getResources().getString(R.string.cancel_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }
}
