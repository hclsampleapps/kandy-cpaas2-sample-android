package com.hcl.kandy.cpass.activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

public class AddContactActivity extends BaseActivity implements View.OnClickListener {

    private AddressBookService mAddressBookService;
    private EditText mEtPrimaryContact;
    private EditText mEtFirstName;
    private EditText mEtLastName;
    private EditText mEtEmail;
    private EditText mEtBuisPhoneNo;
    private EditText mEtHomePhoneNo;
    private EditText mEtMobilePhoneNo;

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
}
