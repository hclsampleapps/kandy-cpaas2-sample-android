package com.hcl.kandy.cpass.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.hcl.kandy.cpass.App;
import com.hcl.kandy.cpass.R;
import com.rbbn.cpaas.mobile.CPaaS;
import com.rbbn.cpaas.mobile.addressbook.api.AddContactCallback;
import com.rbbn.cpaas.mobile.addressbook.api.AddressBookService;
import com.rbbn.cpaas.mobile.addressbook.api.RetrieveContactsCallback;
import com.rbbn.cpaas.mobile.addressbook.model.Contact;
import com.rbbn.cpaas.mobile.utilities.exception.MobileError;

import java.util.List;

public class AddressbookFragment extends BaseFragment implements View.OnClickListener {
    private AddressBookService mAddressBookService;
    private EditText mEtPrimaryContact;
    private EditText mEtFirstName;
    private EditText mEtLastName;
    private EditText mEtEmail;
    private EditText mEtBuisPhoneNo;
    private EditText mEtHomePhoneNo;
    private EditText mEtMobilePhoneNo;

    public AddressbookFragment() {
    }

    public static AddressbookFragment newInstance() {
        return new AddressbookFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = getContext();
        if (context != null)
            initAddressBookService(context);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fragment_addressbook_add, container, false);
        View mBtnCreateContact = inflate.findViewById(R.id.button_create_contact);
        mEtPrimaryContact = inflate.findViewById(R.id.et_primary_contact);
        mEtFirstName = inflate.findViewById(R.id.et_first_name);
        mEtLastName = inflate.findViewById(R.id.et_last_name);
        mEtEmail = inflate.findViewById(R.id.et_email);
        mEtBuisPhoneNo = inflate.findViewById(R.id.et_b_phone_no);
        mEtHomePhoneNo = inflate.findViewById(R.id.et_h_phone_no);
        mEtMobilePhoneNo = inflate.findViewById(R.id.et_m_phone_no);
        mBtnCreateContact.setOnClickListener(this);

        getAllContact();
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
            case R.id.button_create_contact:
                addContact();
                break;
        }
    }

    private void addContact() {

        Contact contact = new Contact();
        contact.setPrimaryContact(mEtPrimaryContact.getText().toString());
        contact.setFirstName(mEtFirstName.getText().toString());
        contact.setLastName(mEtLastName.getText().toString());
        contact.setEmailAddress(mEtEmail.getText().toString());
        contact.setBusinessPhoneNumber(mEtBuisPhoneNo.getText().toString());
        contact.setHomePhoneNumber(mEtHomePhoneNo.getText().toString());
        contact.setMobilePhoneNumber(mEtMobilePhoneNo.getText().toString());
        contact.setBuddy(true);

        mAddressBookService.addContact(contact, "default", new AddContactCallback() {
            @Override
            public void onSuccess(Contact contact) {
                Log.d("HCL", "Addressbook contact add success");
            }

            @Override
            public void onFail(MobileError mobileError) {
                Log.d("HCL", "Addressbook contact add fail");
            }
        });
    }

    private void getAllContact() {
        mAddressBookService.retrieveContactList("default", new RetrieveContactsCallback() {
            @Override
            public void onSuccess(List<Contact> list) {
                Log.d("HCL", "got list of conatct");
                for (Contact item :
                        list) {
                    Log.d("HCL", item.getEmailAddress());
                }
            }

            @Override
            public void onFail(MobileError mobileError) {
                Log.d("HCL", "fail list of conatct");
            }
        });
    }
}
