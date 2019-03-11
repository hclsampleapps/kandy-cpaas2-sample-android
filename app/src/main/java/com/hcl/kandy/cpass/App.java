package com.hcl.kandy.cpass;

import android.app.Application;


import com.hcl.kandy.cpass.activities.HomeActivity;
import com.hcl.kandy.cpass.utils.CpassSubscribe;
import com.rbbn.cpaas.mobile.CPaaS;
import com.rbbn.cpaas.mobile.utilities.Configuration;
import com.rbbn.cpaas.mobile.utilities.Globals;
import com.rbbn.cpaas.mobile.utilities.logging.LogLevel;

/**
 * Created by Ashish Goel on 2/1/2019.
 */
public class App extends Application {

    private CPaaS mCpaas;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public void setCpass(String baseUrl, String mAccessToken, String idToken, HomeActivity.CpassListner cpassListner) {

        Configuration.getInstance().setUseSecureConnection(true);
        Configuration.getInstance().setRestServerUrl(baseUrl);
//        Configuration.getInstance().setRestServerPort(8080);
        Configuration.getInstance().setLogLevel(LogLevel.TRACE);
        Globals.setApplicationContext(getApplicationContext());

        mCpaas = CpassSubscribe.initKandyService(mAccessToken, idToken, cpassListner);
    }

    public CPaaS getCpass() {
        return mCpaas;
    }
}
