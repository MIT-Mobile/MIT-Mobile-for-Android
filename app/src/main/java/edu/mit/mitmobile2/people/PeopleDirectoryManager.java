package edu.mit.mitmobile2.people;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import android.app.Activity;
import android.database.Cursor;
import android.support.annotation.Nullable;

import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.DBAdapter;
import edu.mit.mitmobile2.MITAPIClient;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.RetrofitManager;
import edu.mit.mitmobile2.Schema;
import edu.mit.mitmobile2.people.model.MITPerson;
import edu.mit.mitmobile2.people.model.MITPersonAttribute;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.GET;
import timber.log.Timber;

import static edu.mit.mitmobile2.DatabaseObject.createListFromCursor;
import static edu.mit.mitmobile2.DatabaseObject.getSchemaFieldForMethod;
import static edu.mit.mitmobile2.DatabaseObject.getSchemaTableForClass;
import static edu.mit.mitmobile2.DatabaseObject.getSchemaTableNameForClass;
import static edu.mit.mitmobile2.Schema.Table.getTableColumns;
import static edu.mit.mitmobile2.Schema.Table.getTableName;

/**
 * Created by grmartin on 4/16/15.
 */
public class PeopleDirectoryManager extends RetrofitManager {
    private static final MitPersonDirectoryService MIT_PEOPLE_DIR_SERVICE = MIT_REST_ADAPTER.create(MitPersonDirectoryService.class);

    // TODO: GRM: LOOK UP
    private static final int PHONE_ICON = R.drawable.phone;
    private static final int EMAIL_ICON = R.drawable.email;
    private static final int MAP_ICON = R.drawable.phone;
    private static final int EXTERNAL_ICON = R.drawable.phone;

    @SuppressWarnings("unused")
    public static void makeHttpCall(String apiType, String path, HashMap<String, String> pathParams, HashMap<String, String> queryParams, Object callback) throws NoSuchFieldException,
            NoSuchMethodException,
            ClassNotFoundException,
            IllegalAccessException,
            InvocationTargetException {

        String methodName = buildMethodName(path, pathParams, queryParams);

        Timber.d("Method name= " + methodName);

        Method m = MIT_PEOPLE_DIR_SERVICE.getClass().getDeclaredMethod(methodName, Callback.class);
        m.invoke(MIT_PEOPLE_DIR_SERVICE, callback);
    }

    @SuppressWarnings("unused")
    public static Object makeHttpCall(String apiType, String path, HashMap<String, String> pathParams, HashMap<String, String> queryParams) throws NoSuchFieldException,
            NoSuchMethodException,
            ClassNotFoundException,
            IllegalAccessException,
            InvocationTargetException {

        String methodName = buildMethodName(path, pathParams, queryParams);

        Timber.d("Method name= " + methodName);

        Method m = MIT_PEOPLE_DIR_SERVICE.getClass().getDeclaredMethod(methodName);
        return m.invoke(MIT_PEOPLE_DIR_SERVICE);
    }

    public static PeopleDirectoryManagerCall searchPeople(Activity activity, String query, Callback<List<MITPerson>> people) {
        PeopleDirectoryManagerCallWrapper<?> returnValue = new PeopleDirectoryManagerCallWrapper<>(new MITAPIClient(activity), people);

        final HashMap<String, String> params = new HashMap<>(1);
        params.put("q", query);
        returnValue.getClient().get(Constants.PEOPLE_DIRECTORY, Constants.People.PEOPLE_PATH, null, params, returnValue);

        return returnValue;
    }


    public static Integer getPersistantFavoritesCount() {
        return DBAdapter.getInstance().simpleCount(
            getSchemaTableNameForClass(MITPerson.class),
            getSchemaFieldForMethod(MITPerson.class, "isFavorite"),
            true);
    }

    public static List<MITPerson>  getPersistantFavoritesList() {
        List<MITPerson>  returnList = null;
        final DBAdapter db = DBAdapter.getInstance();

        Class<? extends Schema.Table> table = getSchemaTableForClass(MITPerson.class);

        if (table != null) {
            Cursor cur = db.simpleConditionedSelect(
                    getTableName(table),
                    getTableColumns(table),
                    getSchemaFieldForMethod(MITPerson.class, "isFavorite"),
                    true
            );

            returnList = createListFromCursor(MITPerson.class, cur, db);

            cur.close();
        }

        return returnList;
    }

    public interface MitPersonDirectoryService {
        @GET(Constants.People.PEOPLE_PATH)
        void _get(Callback<List<MITPerson>> callback);
        @GET(Constants.People.PERSON_PATH)
        void _getapisperson(Callback<List<MITPerson>> callback);
    }

    /**
     * Directory item display enum.
     *
     * <p>
     * This items purpose is to tell us what icon to show. We tried to duplicate the iOS implementation however there
     * are some places where Android just doesnt line up, notes at end.
     * </p>
     * <p>
     * The following notes were found in <tt>PeopleDetailsViewController.m</tt> within
     * </code>-(void) mapPersonAttributes</code> in the iOS project; at revision <tt>@6795143</tt> and are duplicated
     * here as they seem to apply to us.
     * </p>
     * <pre> key : display name : accessory icon
     * -----------------------------------
     * email     : email : email
     *
     * phone     : phone : phone
     * fax       : fax   : phone
     * homephone : home  : phone
     *
     * office            : office  : map
     * street/city/state : address : map
     *
     * website   : website : external </pre>
     * <p>
     * Our implementation leaves determination of what attributes are available to the {@see MITPersonAttribute} enum
     * and mapping of attributes to images to the {@see DirectoryDisplayProperty} enum. Since we do not have the
     * availability of <code>-(id)valueForKey:</code> the attribute maping enum uses reflection and normalized naming
     * conventions to get the value of a property.
     * </p>
     */
    public enum DirectoryDisplayProperty {
        EMAIL(      EMAIL_ICON, MITPersonAttribute.EMAIL     ),
        PHONE(      PHONE_ICON, MITPersonAttribute.PHONE     ),
        FAX(        PHONE_ICON, MITPersonAttribute.FAX       ),
        HOMEPHONE(  PHONE_ICON, MITPersonAttribute.HOMEPHONE ),
        OFFICE(       MAP_ICON, MITPersonAttribute.OFFICE    ),
        ADDRESS(      MAP_ICON, MITPersonAttribute.ADDRESS   ),
        WEBSITE( EXTERNAL_ICON, MITPersonAttribute.WEBSITE   );

        private final int iconId;
        private final MITPersonAttribute type;

        DirectoryDisplayProperty(int iconId, MITPersonAttribute type) {
            this.iconId = iconId;
            this.type = type;
        }

        public static DirectoryDisplayProperty getDefault() {
            return PHONE;
        }

        public static DirectoryDisplayProperty getPrimaryDisplayPropertyFor(MITPerson person) {
            MITPersonAttribute[] attrs = MITPersonAttribute.getAttributesOn(person);
            return attrs.length == 0 ? getDefault() : byKeyAttribute(attrs[0]);
        }

        @Nullable
        public static DirectoryDisplayProperty byKey(String key) {
            DirectoryDisplayProperty returnVal = null;

            for (DirectoryDisplayProperty prop : values()) {
                if (prop.getKeyAttribute().getKeyName().equals(key)) {
                    returnVal = prop;
                    break;
                }
            }

            return returnVal;
        }

        @Nullable
        public static DirectoryDisplayProperty byKeyAttribute(MITPersonAttribute key) {
            DirectoryDisplayProperty returnVal = null;

            for (DirectoryDisplayProperty prop : values()) {
                if (prop.getKeyAttribute().equals(key)) {
                    returnVal = prop;
                    break;
                }
            }

            return returnVal;
        }

        public MITPersonAttribute getKeyAttribute() {
            return type;
        }

        public int getIconId() {
            return iconId;
        }
    }

    public static class PeopleDirectoryManagerCallWrapper<T> implements PeopleDirectoryManagerCall, Callback<T> {
        private static int CALL_IDENTIFIER = 0;

        private final int callId;
        private final AtomicBoolean completed;
        private final AtomicBoolean errored;
        private final Callback<T> callback;
        private final MITAPIClient client;

        public PeopleDirectoryManagerCallWrapper(MITAPIClient client, Callback<T> callback) {
            this.callId = ++CALL_IDENTIFIER;
            this.completed = new AtomicBoolean(false);
            this.errored = new AtomicBoolean(false);
            this.client = client;
            this.callback = callback != null ? callback : new Callback<T>() {
                @Override public void success(T t, Response response) { }
                @Override public void failure(RetrofitError error) { }
            };
        }

        @Override
        public int getCallId() {
            return this.callId;
        }

        @Override
        public boolean isComplete() {
            return completed.get();
        }

        @Override
        public boolean hadError() {
            return errored.get();
        }

        @Override
        public void success(T t, Response response) {
            completed.set(true);
            errored.set(false);

            this.callback.success(t, response);
        }

        @Override
        public void failure(RetrofitError error) {
            completed.set(true);
            errored.set(true);

            this.callback.failure(error);
        }

        public MITAPIClient getClient() {
            return client;
        }

        @Override
        public String toString() {
            return "PeopleDirectoryManagerCallWrapper{" +
                    "callId=" + callId +
                    ", completed=" + completed +
                    ", errored=" + errored +
                    ", callback=" + callback +
                    ", client=" + client +
                    '}';
        }
    }

    public interface PeopleDirectoryManagerCall {
        int getCallId();
        boolean isComplete();
        boolean hadError();
    }
}