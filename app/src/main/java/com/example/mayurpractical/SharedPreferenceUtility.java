package com.example.mayurpractical;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceUtility {

    private SharedPreferences preferences = null;

    public SharedPreferenceUtility(Context context) {

        preferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
    }

    public void setString(String tagOrKey, String tagValue) {
        preferences.edit().putString(tagOrKey, tagValue).apply();
    }

    public String getString(String tagOrKey) {
        return preferences.getString(tagOrKey, "");
    }

    public void clearAll() {

        preferences.edit().clear().apply();

    }
}
