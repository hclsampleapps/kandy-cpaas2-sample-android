package com.hcl.kandy.cpass.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import androidx.core.content.ContextCompat;
import android.widget.Toast;

import com.hcl.kandy.cpass.R;

/**
 * Created by I Ball on 7/11/2016.
 */
public class MarshMallowPermission {
    public static final int PERMISSION_ALL_REQUEST_CODE = 9;
    Activity activity;

    public MarshMallowPermission(Activity activity) {
        this.activity = activity;
    }


    public boolean checkAllPermissions() {
        int result1 = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int result2 = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result1 == PackageManager.PERMISSION_GRANTED
                && result2 == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public void requestALLPermissions() {
        String[] PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (activity.shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                && activity.shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(activity, activity.getString(R.string.common_message_permission_all_required),
                    Toast.LENGTH_LONG).show();
        } else {
            activity.requestPermissions(PERMISSIONS, PERMISSION_ALL_REQUEST_CODE);
        }
    }
}
