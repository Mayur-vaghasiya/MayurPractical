package com.example.mayurpractical.apploader;

import android.app.Application;

import com.example.mayurpractical.SharedPreferenceUtility;


/**
 * Created by peacock on 21/1/17.
 */

public class ApplicationLoader extends Application {

    public static final String TAG = ApplicationLoader.class.getSimpleName();
    private static ApplicationLoader applicationLoader;
    private SharedPreferenceUtility preferencesUtility;

    public static synchronized ApplicationLoader getAppLoader() {

        return applicationLoader;

    }

    @Override
    public void onCreate() {
        super.onCreate();

        applicationLoader = this;

        preferencesUtility = new SharedPreferenceUtility(this);

    }

    public SharedPreferenceUtility getPreferencesUtility() {

        if (preferencesUtility == null) {

            preferencesUtility = new SharedPreferenceUtility(this);

        }

        return preferencesUtility;

    }


}
