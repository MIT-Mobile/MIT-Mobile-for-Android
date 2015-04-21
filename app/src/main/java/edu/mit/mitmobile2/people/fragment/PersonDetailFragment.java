package edu.mit.mitmobile2.people.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import butterknife.InjectView;
import butterknife.OnItemClick;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.people.PeopleDirectoryManager;
import edu.mit.mitmobile2.people.activity.PersonDetailActivity;
import edu.mit.mitmobile2.people.adapter.MITPeopleDirectoryPersonAdapter;
import edu.mit.mitmobile2.people.model.MITContactInformation;
import edu.mit.mitmobile2.people.model.MITPerson;
import edu.mit.mitmobile2.shared.adapter.MITSimpleTaggedActionAdapter;
import edu.mit.mitmobile2.shared.model.MITSimpleTaggedActionItem;

import static butterknife.ButterKnife.inject;

/**
 * Created by grmartin on 4/17/15.
 */
public class PersonDetailFragment extends Fragment {
    public static final String PERSON_KEY = PersonDetailActivity.PERSON_KEY;
    private static final int CREATE_NEW_CONTACT_TAG = 1504201617;
    private static final int ADD_TO_EXISTING_TAG = 1504201618;
    private static final int ADD_TO_FAVORITES_TAG = 1504201619;
    private static final int REMOVE_FROM_FAVORITES_TAG = 1504201620;

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
    private DataSetObserver contactInformationListAdapterObserver;

    public PersonDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

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

        String title = person.getName();

        if (TextUtils.isEmpty(title)) {
            title = person.getTitle();

            if (TextUtils.isEmpty(title)) {
                title = person.getDept();
            }
        }

        if (!TextUtils.isEmpty(title))
            if (getActivity() != null && getActivity().getActionBar() != null)
                this.getActivity().getActionBar().setTitle(title);

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

        this.personSummary.setText("Morbi leo risus, porta ac consectetur ac, vestibulum at eros. Curabitur blandit tempus porttitor.");

        this.contactInformationListAdapter = new MITPeopleDirectoryPersonAdapter();
        this.contactInformationList.setAdapter(contactInformationListAdapter);

        this.contactManagementListAdapter = new MITSimpleTaggedActionAdapter();
        this.contactManagementList.setAdapter(contactManagementListAdapter);

    }

    private void reloadContactManagementOptions() {
        List<MITSimpleTaggedActionItem> actionItems = new ArrayList<>(4);

        actionItems.add(new MITSimpleTaggedActionItem(getString(R.string.fragment_people_person_detail_contact_management_create_new), CREATE_NEW_CONTACT_TAG));
        actionItems.add(new MITSimpleTaggedActionItem(getString(R.string.fragment_people_person_detail_contact_management_add_existing), ADD_TO_EXISTING_TAG));

        if (person.isFavorite()) {
            actionItems.add(new MITSimpleTaggedActionItem(getString(R.string.fragment_people_person_detail_contact_management_remove_favorites), REMOVE_FROM_FAVORITES_TAG));
        } else {
            actionItems.add(new MITSimpleTaggedActionItem(getString(R.string.fragment_people_person_detail_contact_management_add_favorites), ADD_TO_FAVORITES_TAG));
        }

        this.contactManagementListAdapter.updateItems(actionItems);
    }

    private void reloadContactList() {
        // TODO: IMPLEMENT
    }

    @OnItemClick(R.id.contact_information_list)
    protected void onContactInformationItemClicked(AdapterView<?> parent, View view, int position, long id) {
        MITContactInformation contactInfo = (MITContactInformation) this.contactInformationListAdapter.getItem(position);

        // TODO: IMPLEMENT
    }

    @OnItemClick(R.id.contact_management_actions_list)
    protected void onManagementActionItemClicked(AdapterView<?> parent, View view, int position, long id) {
        MITSimpleTaggedActionItem item = (MITSimpleTaggedActionItem) this.contactManagementListAdapter.getItem(position);

        // TODO: IMPLEMENT

        switch (item.getTag()) {
            case ADD_TO_EXISTING_TAG:

                break;
            case CREATE_NEW_CONTACT_TAG:
//                addContact();
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

//    private void addContact() {
//        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
//
//        ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
//                .withValue(Data.RAW_CONTACT_ID, getPersonId())
//                .withValue(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
//                .withValue(Phone.NUMBER, "1-800-GOOG-411")
//                .withValue(Phone.TYPE, Phone.TYPE_CUSTOM)
//                .withValue(Phone.LABEL, "free directory assistance")
//                .build());
//
//
//        try {
//            getActivity().getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        } catch (OperationApplicationException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public Object getPersonId() {
//        return personId;
//    }
}