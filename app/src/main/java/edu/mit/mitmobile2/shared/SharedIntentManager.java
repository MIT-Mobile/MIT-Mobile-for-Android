package edu.mit.mitmobile2.shared;

import java.util.ArrayList;
import java.util.List;

import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.support.annotation.NonNull;

import edu.mit.mitmobile2.shared.android.IntentValueSet;

/**
 * Shared intent manager, this is often used for External intends but may also be used for partitioned data handling
 * internally in the future. For internal activity calling {@see SharedActivityManager}
 */
public class SharedIntentManager {
    private static Context context;

    public static void setContext(Context ctx) {
        if (context == null && ctx != null)
            context = ctx;
    }

    public static boolean canHandleIntent(@NonNull Intent intent) {
        List list = context.getPackageManager().queryIntentActivities(intent, 0);
        return (list != null && list.size() > 0);
    }

    public static Intent createTelephoneCallIntent(@NonNull Context packageContext, int stringId) {
        return createTelephoneCallIntent(packageContext.getString(stringId));
    }

    public static Intent createTelephoneCallIntent(@NonNull String number) {
        return new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", number, null));
    }

    public static Intent createSendEmailIntent(String value) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, value);
        return intent;
    }

    public static Intent createMapIntent(String address) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.fromParts("geo", "0,0?q="+Uri.encode(address), ""));

        if (canHandleIntent(intent)) return intent;

        intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("http://maps.google.com/maps?q=" + Uri.encode(address)));
        return intent;
    }

    public static Intent createBrowserIntent(String url) {
        Intent intent = null;
        if (url.startsWith("http://") || url.startsWith("https://")) {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
        } else {
            intent = new Intent(Intent.ACTION_WEB_SEARCH);
            intent.putExtra(SearchManager.QUERY, url);
        }
        return intent;
    }

    public static IntentValueSet createEditContactIntent() {
        IntentValueSet intent = new IntentValueSet(Intent.ACTION_EDIT);
        intent.setType(Contacts.CONTENT_TYPE);
        return intent;
    }

    public static IntentValueSet createInsertContactIntent() {
        IntentValueSet intent = new IntentValueSet(Intent.ACTION_INSERT);
        intent.setType(Contacts.CONTENT_TYPE);
        return intent;
    }

    public static IntentValueSet createInsertEditContactIntent() {
        IntentValueSet intent = new IntentValueSet(Intent.ACTION_INSERT_OR_EDIT);
        intent.setType(Contacts.CONTENT_TYPE);
        return intent;
    }

    public static boolean supportsEditBulkContact() {
        return ContactIntents.supportsEditBulkContact();
    }

    public static boolean supportsInsertBulkContact() {
        return ContactIntents.supportsInsertBulkContact();
    }

    public static boolean supportsInsertEditBulkContact() {
        return ContactIntents.supportsInsertEditBulkContact();
    }

    private static class ContactIntents {
        /* Wrapped booleans support a 3rd value, null, used here to detect if weve run detection yet */
        private static Boolean CACHED_SUPPORT_EDIT = null;
        private static Boolean CACHED_SUPPORT_INSERT = null;
        private static Boolean CACHED_SUPPORT_INSERT_EDIT = null;

        private static boolean supportsEditBulkContact() {
            if (CACHED_SUPPORT_EDIT == null) {
                IntentValueSet ivs = createEditContactIntent();
                addFakeInfo(ivs);
                CACHED_SUPPORT_EDIT = canHandleIntent(ivs);
            }
            return CACHED_SUPPORT_EDIT;
        }

        private static boolean supportsInsertBulkContact() {
            if (CACHED_SUPPORT_INSERT == null) {
                IntentValueSet ivs = createInsertContactIntent();
                addFakeInfo(ivs);
                CACHED_SUPPORT_INSERT = canHandleIntent(ivs);
            }
            return CACHED_SUPPORT_INSERT;
        }

        private static boolean supportsInsertEditBulkContact() {
            if (CACHED_SUPPORT_INSERT_EDIT == null) {
                IntentValueSet ivs = createInsertEditContactIntent();
                addFakeInfo(ivs);
                CACHED_SUPPORT_INSERT_EDIT = canHandleIntent(ivs);
            }
            return CACHED_SUPPORT_INSERT_EDIT;
        }

        private static void addFakeInfo(IntentValueSet ivs) {
            ArrayList<ContentValues> data = new ArrayList<ContentValues>(3);

            ContentValues cvs = new ContentValues();
            cvs.put(Data.MIMETYPE, Email.CONTENT_ITEM_TYPE);
            cvs.put(Email.TYPE, Email.TYPE_WORK);
            cvs.put(Email.ADDRESS, "som_guy@some_sub.example.com");
            data.add(cvs);
            cvs = new ContentValues();
            cvs.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
            cvs.put(Phone.TYPE, Phone.TYPE_OTHER);
            cvs.put(Phone.NUMBER, "555-555-5555");
            data.add(cvs);
            cvs = new ContentValues();
            cvs.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
            cvs.put(Phone.TYPE, Phone.TYPE_FAX_WORK);
            cvs.put(Phone.NUMBER, "555-555-5556");
            data.add(cvs);

            ivs.putExtra(ContactsContract.Intents.Insert.NAME, "Example Q. Smith");
            ivs.putParcelableArrayListExtra(ContactsContract.Intents.Insert.DATA, data);
        }
    }
}
