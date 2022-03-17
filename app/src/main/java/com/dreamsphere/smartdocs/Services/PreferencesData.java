package com.dreamsphere.smartdocs.Services;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferencesData {

    static final String PREF_LOGGEDIN_USER_UID = "logged_in_uid";
    static final String PREF_USER_LOGGEDIN_STATUS = "logged_in_status";

    public static SharedPreferences getSharedPreferences(Context context)
    {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void setLoggedInUserEmail(Context context, String email)
    {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(PREF_LOGGEDIN_USER_UID, email);
        editor.commit();
    }

    public static String getLoggedInEmailUser(Context context)
    {
        return getSharedPreferences(context).getString(PREF_LOGGEDIN_USER_UID, "");
    }

    public static void setUserLoggedInStatus(Context context, boolean status)
    {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(PREF_USER_LOGGEDIN_STATUS, status);
        editor.commit();
    }

    public static boolean getUserLoggedInStatus(Context context)
    {
        return getSharedPreferences(context).getBoolean(PREF_USER_LOGGEDIN_STATUS, false);
    }

    public static void clearLoggedInEmailAddress(Context context)
    {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.remove(PREF_LOGGEDIN_USER_UID);
        editor.remove(PREF_USER_LOGGEDIN_STATUS);
        editor.commit();
    }

}
