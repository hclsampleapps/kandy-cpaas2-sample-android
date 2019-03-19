package com.hcl.kandy.cpass.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hcl.kandy.cpass.App;
import com.hcl.kandy.cpass.R;
import com.rbbn.cpaas.mobile.CPaaS;
import com.rbbn.cpaas.mobile.addressbook.api.AddContactCallback;
import com.rbbn.cpaas.mobile.addressbook.api.AddressBookService;
import com.rbbn.cpaas.mobile.addressbook.api.UpdateContactCallback;
import com.rbbn.cpaas.mobile.addressbook.model.Contact;
import com.rbbn.cpaas.mobile.utilities.exception.MobileError;

public class AddContactActivity extends BaseActivity implements View.OnClickListener {

    private AddressBookService mAddressBookService;
    private EditText mEtPrimaryContact;
    private EditText mEtFirstName;
    private EditText mEtLastName;
    private EditText mEtEmail;
    private EditText mEtBuisPhoneNo;
    private EditText mEtHomePhoneNo;
    private EditText mEtMobilePhoneNo;
    private boolean mUpdateContact;
    private String mUpdateContactId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        View mBtnCreateContact = findViewById(R.id.button_create_contact);
        mEtPrimaryContact = findViewById(R.id.et_primary_contact);
        mEtFirstName = findViewById(R.id.et_first_name);
        mEtLastName = findViewById(R.id.et_last_name);
        mEtEmail = findViewById(R.id.et_email);
        mEtBuisPhoneNo = findViewById(R.id.et_b_phone_no);
        mEtHomePhoneNo = findViewById(R.id.et_h_phone_no);
        mEtMobilePhoneNo = findViewById(R.id.et_m_phone_no);
        mBtnCreateContact.setOnClickListener(this);

        initAddressBookService(this);
        getIntentExtras();
    }

    private void initAddressBookService(Context context) {
        App applicationContext = (App) context.getApplicationContext();
        CPaaS cpass = applicationContext.getCpass();
        mAddressBookService = cpass.getAddressBookService();
    }

    private void getIntentExtras() {
        try {
            Intent intent = getIntent();
            if (intent != null) {
                Bundle extras = intent.getExtras();
                if (extras != null && extras.containsKey("update")) {
                    mEtPrimaryContact.setText(extras.getString("primaryContact", ""));
                    mEtFirstName.setText(extras.getString("firstName", ""));
                    mEtLastName.setText(extras.getString("lastName", ""));
                    mEtEmail.setText(extras.getString("email", ""));
                    mEtBuisPhoneNo.setText(extras.getString("buisPNo", ""));
                    mEtHomePhoneNo.setText(extras.getString("homePNo", ""));
                    mEtMobilePhoneNo.setText(extras.getString("mobilePNo", ""));

                    mUpdateContactId = extras.getString("contactId", "");
                    mUpdateContact = true;
                    setTitle("Update Contact");
                    Button btn = findViewById(R.id.button_create_contact);
                    btn.setText("Update Contact");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_create_contact:
                if (mUpdateContact)
                    updateContact();
                else
                    addContact();
                break;
        }
    }

    private void updateContact() {

        showProgressBar("Updating Contact");
        Contact contact = new Contact(mUpdateContactId);
        contact.setPrimaryContact(mEtPrimaryContact.getText().toString());
        contact.setFirstName(mEtFirstName.getText().toString());
        contact.setLastName(mEtLastName.getText().toString());
        contact.setEmailAddress(mEtEmail.getText().toString());
        contact.setBusinessPhoneNumber(mEtBuisPhoneNo.getText().toString());
        contact.setHomePhoneNumber(mEtHomePhoneNo.getText().toString());
        contact.setMobilePhoneNumber(mEtMobilePhoneNo.getText().toString());
        contact.setBuddy(true);

        mAddressBookService.updateContact(contact, "default", new UpdateContactCallback() {
            @Override
            public void onSuccess(Contact contact) {
                showMessage(mEtPrimaryContact, "Contact Updated successfully");
                hideProgressBAr();
                Log.d("HCL", "Addressbook contact update success");
            }

            @Override
            public void onFail(MobileError mobileError) {
                showMessage(mEtPrimaryContact, "Contact Update Failed");
                hideProgressBAr();
                Log.d("HCL", "Addressbook update add fail");
            }
        });
    }

    private void addContact() {

        showProgressBar("Adding Contact");
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
                showMessage(mEtPrimaryContact, "Contact Added successfully");
                hideProgressBAr();
                Log.d("HCL", "Addressbook contact add success");
            }

            @Override
            public void onFail(MobileError mobileError) {
                showMessage(mEtPrimaryContact, "Contact Added Failed");
                hideProgressBAr();
                Log.d("HCL", "Addressbook contact add fail");
            }
        });
    }
}
