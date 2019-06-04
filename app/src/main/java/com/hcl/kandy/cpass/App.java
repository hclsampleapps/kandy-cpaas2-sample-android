package com.hcl.kandy.cpass;

import android.app.Application;
import android.content.Context;

import com.hcl.kandy.cpass.activities.HomeActivity;
import com.hcl.kandy.cpass.call.CPaaSCallManager;
import com.hcl.kandy.cpass.utils.CpassSubscribe;
import com.rbbn.cpaas.mobile.CPaaS;
import com.rbbn.cpaas.mobile.utilities.Configuration;
import com.rbbn.cpaas.mobile.utilities.Globals;
import com.rbbn.cpaas.mobile.utilities.exception.MobileException;
import com.rbbn.cpaas.mobile.utilities.logging.LogLevel;

/**
 * Created by Ashish Goel on 2/1/2019.
 */
public class App extends Application {

    private CPaaS mCpaas;


    private CPaaSCallManager cPaaSCallManager = new CPaaSCallManager();

    public CPaaSCallManager getcPaaSCallManager() {
        return cPaaSCallManager;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public void setCpass(String baseUrl, String mAccessToken, String idToken, HomeActivity.CpassListner cpassListner) {
        Context context = getApplicationContext();

        Configuration.getInstance().setUseSecureConnection(true);
        Configuration.getInstance().setRestServerUrl(baseUrl);
//        Configuration.getInstance().setRestServerPort(8080);
        Configuration.getInstance().setLogLevel(LogLevel.TRACE);
        ConfigurationHelper.setConfigurations(baseUrl);
        Globals.setApplicationContext(context);
        cPaaSCallManager.setContext(context);

        mCpaas = CpassSubscribe.initKandyService(mAccessToken, idToken, cpassListner);
        try {
            this.mCpaas.getCallService().setCallApplicationListener(cPaaSCallManager);
        } catch (MobileException e) {
            e.printStackTrace();
        }

    }

    public CPaaS getCpass() {
        return mCpaas;
    }
}
