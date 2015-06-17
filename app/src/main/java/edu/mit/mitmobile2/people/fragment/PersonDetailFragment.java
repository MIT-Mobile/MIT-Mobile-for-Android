package edu.mit.mitmobile2.people.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredPostal;
import android.provider.ContactsContract.CommonDataKinds.Website;
import android.provider.ContactsContract.Data;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnItemClick;
import edu.mit.mitmobile2.BuildConfig;
import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.MITMainActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.people.PeopleDirectoryManager;
import edu.mit.mitmobile2.people.PeopleDirectoryManager.DirectoryDisplayProperty;
import edu.mit.mitmobile2.people.activity.PersonDetailActivity;
import edu.mit.mitmobile2.people.adapter.MITPeopleDirectoryPersonAdapter;
import edu.mit.mitmobile2.people.model.MITContactInformation;
import edu.mit.mitmobile2.people.model.MITPerson;
import edu.mit.mitmobile2.people.model.MITPersonAttribute;
import edu.mit.mitmobile2.people.model.MITPersonIndexPath;
import edu.mit.mitmobile2.shared.SharedIntentManager;
import edu.mit.mitmobile2.shared.adapter.MITSimpleTaggedActionAdapter;
import edu.mit.mitmobile2.shared.android.BundleUtils;
import edu.mit.mitmobile2.shared.android.ContentValuesUtils;
import edu.mit.mitmobile2.shared.android.ContentValuesWrapperSet;
import edu.mit.mitmobile2.shared.android.IntentValueSet;
import edu.mit.mitmobile2.shared.android.ParcelableUtils;
import edu.mit.mitmobile2.shared.android.ValueSetUtils;
import edu.mit.mitmobile2.shared.logging.LoggingManager;
import edu.mit.mitmobile2.shared.logging.LoggingManager.Log;
import edu.mit.mitmobile2.shared.model.MITSimpleTaggedActionItem;

import static butterknife.ButterKnife.inject;
import static edu.mit.mitmobile2.shared.android.ValueSetUtils.addFieldIfValid;
import static edu.mit.mitmobile2.shared.functional.CommonTests.IS_STRING_EMPTY_TEST;
import static edu.mit.mitmobile2.shared.functional.CommonTests.IS_VALID_EMAIL_STRING_TEST;
import static edu.mit.mitmobile2.shared.functional.CommonTests.IS_VALID_URL_STRING_TEST;
import static edu.mit.mitmobile2.shared.functional.InvertTest.invert;

/**
 * Created by grmartin on 4/17/15.
 */
public class PersonDetailFragment extends Fragment {
    private static final String TAG = "PersonDetailFrag";

    public static final String PERSON_KEY = PersonDetailActivity.PERSON_KEY;
    private static final int CREATE_NEW_CONTACT_TAG = 1504201617;
    private static final int ADD_TO_EXISTING_TAG = 1504201618;
    private static final int ADD_TO_FAVORITES_TAG = 1504201619;
    private static final int REMOVE_FROM_FAVORITES_TAG = 1504201620;
    private static final int ADD_TO_EXISTING_OR_INSERT_NEW_TAG = 1504221011;

    private MITPerson person;

    @InjectView(R.id.summary)
    protected TextView personSummary;

    @SuppressWarnings("FieldCanBeLocal")
    protected MITPeopleDirectoryPersonAdapter contactInformationListAdapter;
    @InjectView(R.id.contact_information_list)
    protected ListView contactInformationList;

    protected MITSimpleTaggedActionAdapter contactManagementListAdapter;
    @InjectView(R.id.contact_management_actions_list)
    protected ListView contactManagementList;

    private String resolvedContactTitle;

    public PersonDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (BuildConfig.DEBUG) {
            LoggingManager.enableKey(TAG);
            LoggingManager.enableKey(BundleUtils.LOGGER_KEY);
            LoggingManager.enableKey(ContentValuesUtils.LOGGER_KEY);
            LoggingManager.enableKey(ValueSetUtils.LOGGER_KEY);
        }

        View rootView = inflater.inflate(R.layout.fragment_people_person_detail, container, false);

        initializeComponents(rootView);

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (getArguments() != null) {
            person = getArguments().getParcelable(PERSON_KEY);
        }

        if (person == null) {
            throw new IllegalArgumentException("You must supply a person (MITPerson model) for the fragment to bind to.");
        }

        // This might be best to abstract out a bit, but for now... ...
        String title = person.getName();

        if (TextUtils.isEmpty(title)) {
            title = person.getTitle();

            if (TextUtils.isEmpty(title)) {
                title = person.getDept();
            }
        }

        if (!TextUtils.isEmpty(title))
            if (getActivity() != null) {
                this.resolvedContactTitle = title;
                this.getActivity().setTitle(title);
            }

        this.personSummary.setText(person.getAffiliation());

        /* Lets make sure this bit is up to date. */
        if (!this.person.isFavorite() && PeopleDirectoryManager.isOnFavoritesList(this.person.getUid())) {
            this.person.setFavorite(true);
        }

        reloadContactList();
        reloadContactManagementOptions();
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    private void initializeComponents(View rootView) {
        inject(this, rootView);

        this.personSummary.setText(R.string.one_line_text_placeholder_long);

        this.contactInformationListAdapter = new MITPeopleDirectoryPersonAdapter();
        this.contactInformationList.setAdapter(contactInformationListAdapter);

        this.contactManagementListAdapter = new MITSimpleTaggedActionAdapter();
        this.contactManagementList.setAdapter(contactManagementListAdapter);

    }

    private void reloadContactManagementOptions() {
        List<MITSimpleTaggedActionItem> actionItems = new ArrayList<>(4);

        boolean supportsCreate = SharedIntentManager.supportsInsertBulkContact();
        boolean supportsEdit = SharedIntentManager.supportsEditBulkContact();
        boolean supportsCreateAndEdit = SharedIntentManager.supportsInsertEditBulkContact();
        //noinspection UnnecessaryLocalVariable This may seem redundant but its for legibility.
        boolean enableEdit = supportsEdit;
        boolean enableCreate = supportsCreate;
        boolean enableCreateAndEdit = supportsCreateAndEdit;

        if (enableCreate && enableEdit) {
            enableCreateAndEdit = false;
        } else if (enableCreate && enableCreateAndEdit) {
            enableCreate = false;
        }

        if (enableCreate)
            actionItems.add(new MITSimpleTaggedActionItem(getString(R.string.fragment_people_person_detail_contact_management_create_new), CREATE_NEW_CONTACT_TAG));
        if (enableEdit)
            actionItems.add(new MITSimpleTaggedActionItem(getString(R.string.fragment_people_person_detail_contact_management_add_existing), ADD_TO_EXISTING_TAG));
        if (enableCreateAndEdit)
            actionItems.add(new MITSimpleTaggedActionItem(getString(R.string.fragment_people_person_detail_contact_management_add_or_edit_existing), ADD_TO_EXISTING_OR_INSERT_NEW_TAG));

        if (person.isFavorite()) {
            actionItems.add(new MITSimpleTaggedActionItem(getString(R.string.fragment_people_person_detail_contact_management_remove_favorites), REMOVE_FROM_FAVORITES_TAG));
        } else {
            actionItems.add(new MITSimpleTaggedActionItem(getString(R.string.fragment_people_person_detail_contact_management_add_favorites), ADD_TO_FAVORITES_TAG));
        }

        this.contactManagementListAdapter.updateItems(actionItems);
    }

    private void reloadContactList() {
        this.contactInformationListAdapter.updateItems(generateContactListDisplayInformation());
    }

    private List<MITContactInformation> generateContactListDisplayInformation() {
        MITPerson person = ParcelableUtils.copyParcelable(this.person);

        List<MITContactInformation> list = new LinkedList<>();

        for (MITPersonAttribute attr : MITPersonAttribute.getAttributesOn(this.person)) {
            Object item = null;
            try {
                item = attr.invokeGetterOn(this.person);
            } catch (NoSuchMethodException e) {
                Log.e(TAG, "NS Method Exception => " + this.person, e);
            } catch (InvocationTargetException e) {
                Log.e(TAG, "Target Exception => " + this.person, e);
            } catch (IllegalAccessException e) {
                Log.e(TAG, "Access Exception => " + this.person, e);
            }

            if (item == null) continue;

            if (!(item instanceof List) && item instanceof String) {
                ArrayList<String> litem = new ArrayList<String>();
                litem.add((String) item);
                item = litem;
            }

            if (item instanceof List && ((List) item).size() >= 1) {
                List subjectList = (List) item;
                final int sz = subjectList.size();

                for (int i = 0; sz > i; i++) {
                    list.add(new MITContactInformation(this.person, new MITPersonIndexPath(attr, i)));
                }
            }
        }

        /* The adapter will automatically use a RandomAccess list of the right size, so return our LL<T> */
        return list;
    }

    @OnItemClick(R.id.contact_information_list)
    protected void onContactInformationItemClicked(AdapterView<?> parent, View view, int position, long id) {
        MITContactInformation contactInfo = (MITContactInformation) this.contactInformationListAdapter.getItem(position);

        DirectoryDisplayProperty displayProperty = DirectoryDisplayProperty.byKeyAttribute(contactInfo.getAttributeType());

        assert displayProperty != null;

        if (displayProperty == DirectoryDisplayProperty.OFFICE) {
            Intent intent = new Intent(getActivity(), MITMainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(Constants.LOCATION_KEY, contactInfo.getValue());
            intent.putExtra(Constants.LOCATION_SHOULD_SANITIZE_QUERY_KEY, true);
            startActivity(intent);
        } else {
            startActivity(displayProperty.getActionIcon().generateIntent(contactInfo.getValue()));
        }
    }

    @OnItemClick(R.id.contact_management_actions_list)
    protected void onManagementActionItemClicked(AdapterView<?> parent, View view, int position, long id) {
        MITSimpleTaggedActionItem item = (MITSimpleTaggedActionItem) this.contactManagementListAdapter.getItem(position);

        switch (item.getTag()) {
            case ADD_TO_EXISTING_TAG: {
                IntentValueSet intent = SharedIntentManager.createEditContactIntent();
                addContactInformation(intent);
                startActivity(intent);
            }
            break;
            case CREATE_NEW_CONTACT_TAG: {
                IntentValueSet intent = SharedIntentManager.createInsertContactIntent();
                addContactInformation(intent);
                startActivity(intent);
            }
            break;
            case ADD_TO_EXISTING_OR_INSERT_NEW_TAG: {
                IntentValueSet intent = SharedIntentManager.createInsertEditContactIntent();
                addContactInformation(intent);
                startActivity(intent);
            }
            break;
            case ADD_TO_FAVORITES_TAG:
                this.person.setFavorite(true);
                PeopleDirectoryManager.addUpdate(this.person);
                reloadContactManagementOptions();
                break;
            case REMOVE_FROM_FAVORITES_TAG:
                this.person.setFavorite(false);
                PeopleDirectoryManager.addUpdate(this.person);
                reloadContactManagementOptions();
                break;
        }
    }


    private void addContactInformation(IntentValueSet intent) {
        List<ContentValuesWrapperSet> data = new LinkedList<ContentValuesWrapperSet>();

        for (MITContactInformation conInfo : generateContactListDisplayInformation()) {
            ContentValuesWrapperSet set = new ContentValuesWrapperSet();

            switch (conInfo.getAttributeType()) {
                case EMAIL:
                    set.put(Data.MIMETYPE, Email.CONTENT_ITEM_TYPE);
                    set.put(Email.TYPE, Email.TYPE_WORK);
                    set.shouldDestroyIfFails(addFieldIfValid(set, IS_VALID_EMAIL_STRING_TEST, Email.ADDRESS, conInfo.getValue()));
                    break;
                case PHONE:
                    set.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
                    set.put(Phone.TYPE, Phone.TYPE_OTHER);
                    set.shouldDestroyIfFails(addFieldIfValid(set, invert(IS_STRING_EMPTY_TEST), Phone.NUMBER, conInfo.getValue()));
                    break;
                case FAX:
                    set.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
                    set.put(Email.TYPE, Phone.TYPE_FAX_WORK);
                    set.shouldDestroyIfFails(addFieldIfValid(set, invert(IS_STRING_EMPTY_TEST), Phone.NUMBER, conInfo.getValue()));
                    break;
                case HOMEPHONE:
                    set.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
                    set.put(Phone.TYPE, Phone.TYPE_HOME);
                    set.shouldDestroyIfFails(addFieldIfValid(set, invert(IS_STRING_EMPTY_TEST), Phone.NUMBER, conInfo.getValue()));
                    break;
                case OFFICE:
                    set.put(Data.MIMETYPE, StructuredPostal.CONTENT_ITEM_TYPE);
                    set.put(StructuredPostal.TYPE, StructuredPostal.TYPE_WORK);
                    set.shouldDestroyIfFails(addFieldIfValid(set, invert(IS_STRING_EMPTY_TEST), StructuredPostal.FORMATTED_ADDRESS, conInfo.getValue()));
                    break;
                case ADDRESS:
                    set.put(Data.MIMETYPE, StructuredPostal.CONTENT_ITEM_TYPE);
                    set.put(StructuredPostal.TYPE, StructuredPostal.TYPE_HOME);
                    set.shouldDestroyIfFails(addFieldIfValid(set, invert(IS_STRING_EMPTY_TEST), StructuredPostal.FORMATTED_ADDRESS, conInfo.getValue()));
                    break;
                case WEBSITE:
                    set.put(Data.MIMETYPE, Website.CONTENT_ITEM_TYPE);
                    set.shouldDestroyIfFails(addFieldIfValid(set, IS_VALID_URL_STRING_TEST, Website.URL, conInfo.getValue()));
                    break;
            }

            if (!set.isDestroyed()) {
                data.add(set);
            } else {
                Log.d(TAG, "addContactInformation(...) Invalidated & Destoryed => " + set);
            }
        }

        addFieldIfValid(intent, invert(IS_STRING_EMPTY_TEST), ContactsContract.Intents.Insert.NAME, person.getName(), this.resolvedContactTitle);
        addFieldIfValid(intent, invert(IS_STRING_EMPTY_TEST), ContactsContract.Intents.Insert.NOTES, person.getAffiliation());

        ArrayList<ContentValues> out = new ArrayList<ContentValues>();

        for (ContentValuesWrapperSet wrapper : data) {
            out.add(wrapper.returnDestroyContentValues());
        }

        if (BuildConfig.DEBUG) assert data.size() == out.size();

        intent.putExtra(ContactsContract.Intents.Insert.NAME, person.getName());

        if (out.size() > 0) {
            intent.putParcelableArrayListExtra(ContactsContract.Intents.Insert.DATA, out);
        }

    }
}