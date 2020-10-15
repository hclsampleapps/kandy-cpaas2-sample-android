package com.hcl.kandy.cpass.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hcl.kandy.cpass.App;
import com.hcl.kandy.cpass.R;
import com.hcl.kandy.cpass.activities.AddContactActivity;
import com.hcl.kandy.cpass.adapters.AddressbookAdapter;
import com.rbbn.cpaas.mobile.CPaaS;
import com.rbbn.cpaas.mobile.addressbook.api.AddressBookService;
import com.rbbn.cpaas.mobile.addressbook.api.DeleteContactCallback;
import com.rbbn.cpaas.mobile.addressbook.api.RetrieveContactsCallback;
import com.rbbn.cpaas.mobile.addressbook.model.Contact;
import com.rbbn.cpaas.mobile.utilities.exception.MobileError;

import java.util.ArrayList;
import java.util.List;

public class AddressbookListFragment extends BaseFragment implements View.OnClickListener, AddressbookAdapter.AddressbookListner {
    private AddressBookService mAddressBookService;

    private RecyclerView mRecyclerView;
    private View mAddContact;
    private List<Contact> mContactList = new ArrayList<>();
    private AddressbookAdapter mAddressbookAdapter;

    public AddressbookListFragment() {
    }

    public static AddressbookListFragment newInstance() {
        return new AddressbookListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = getContext();
        if (context != null) {
            initAddressBookService(context);
            getAllContact();
        }
        mAddressbookAdapter = new AddressbookAdapter(mContactList, this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fragment_addressbook, container, false);
        mRecyclerView = inflate.findViewById(R.id.recycleView);
        mAddContact = inflate.findViewById(R.id.add_contact);

        mAddContact.setOnClickListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mAddressbookAdapter);
        return inflate;
    }

    private void initAddressBookService(Context context) {
        App applicationContext = (App) context.getApplicationContext();
        CPaaS cpass = applicationContext.getCpass();
        mAddressBookService = cpass.getAddressBookService();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_contact:
                addContact();
                break;
        }
    }

    private void addContact() {
        startActivity(new Intent(getActivity(), AddContactActivity.class));
    }

    private void getAllContact() {
        Context context = getContext();
        if (context != null)
            showProgressBar(context.getResources().getString(R.string.loading));
        mAddressBookService.retrieveContactList("default", new RetrieveContactsCallback() {
            @Override
            public void onSuccess(List<Contact> list) {
                Log.d("HCL", "Get the list of conatct");
                mContactList.clear();
                for (Contact item :
                        list) {
                    mContactList.add(item);
                    Log.d("HCL", item.getEmailAddress());
                }

                notifyList();
                hideProgressBAr();
            }

            @Override
            public void onFail(MobileError mobileError) {
                Log.d("HCL", "fail list of conatct");
                hideProgressBAr();
            }
        });
    }

    private void notifyList() {
        Context context = getContext();
        if (context != null) {
            Handler mainHandler = new Handler(context.getMainLooper());

            // This is your code
            Runnable myRunnable = () -> {
                Log.d("HCL", "notify list");
                mAddressbookAdapter.notifyDataSetChanged();
//                mRecyclerView.scrollToPosition(mContactList.size() - 1);
            };
            mainHandler.post(myRunnable);
        }

    }

    @Override
    public void onClickAddressBook(Contact contact) {
        Intent intent = new Intent(getActivity(), AddContactActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("primaryContact", contact.getPrimaryContact());
        bundle.putString("firstName", contact.getFirstName());
        bundle.putString("lastName", contact.getLastName());
        bundle.putString("email", contact.getEmailAddress());
        bundle.putString("buisPNo", contact.getBusinessPhoneNumber());
        bundle.putString("homePNo", contact.getHomePhoneNumber());
        bundle.putString("mobilePNo", contact.getMobilePhoneNumber());
        bundle.putString("contactId", contact.getContactId());

        bundle.putBoolean("update", true);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onDeleteAddressBook(Contact contact) {
        showProgressBar("Deleting contact");
        mAddressBookService.deleteContact(contact.getContactId(), "default", new DeleteContactCallback() {
            @Override
            public void onSuccess() {
                showMessage(mRecyclerView, "Contact Deleted successfully");
                hideProgressBAr();
                Log.d("HCL", "Addressbook contact delete success");
                getAllContact();
            }

            @Override
            public void onFail(MobileError mobileError) {
                showMessage(mRecyclerView, "Contact Deleted failed");
                hideProgressBAr();
                Log.d("HCL", "Addressbook contact delete failed");
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getAllContact();
    }
}
