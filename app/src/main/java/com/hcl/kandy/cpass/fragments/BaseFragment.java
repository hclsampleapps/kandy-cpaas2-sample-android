package com.hcl.kandy.cpass.fragments;

import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.widget.EditText;

/**
 * Created by Ashish Goel on 2/4/2019.
 */
public class BaseFragment extends Fragment {
    public void showMessage(EditText mEt, String message) {
        Snackbar.make(mEt, message, Snackbar.LENGTH_SHORT).show();
    }
}
