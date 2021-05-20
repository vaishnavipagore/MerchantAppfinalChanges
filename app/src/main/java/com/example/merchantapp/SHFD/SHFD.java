package com.example.merchantapp.SHFD;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SHFD extends Application {

    private static SHFD sInstance;
    SharedPreferences preferences;

    public static synchronized SHFD getInstance() {
        return sInstance;
    }

    public static Context getAppContext() {
        return sInstance.getApplicationContext();
    }

    public static void saveToPreferences(Context context, String preferenceName, String preferenceValue) {
        if (context != null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(preferenceName, preferenceValue);
            editor.apply();
        }
    }

    public static void saveToPreferences(Context context, String preferenceName, boolean preferenceValue) {
        if (context != null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(preferenceName, preferenceValue);
            editor.apply();
        }
    }

    public static String readFromPreferences(Context context, String preferenceName, String defaultValue) {
        if (context != null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
            return sharedPreferences.getString(preferenceName, defaultValue);
        }
        return "";
    }

    public static boolean readFromPreferences(Context context, String preferenceName, boolean defaultValue) {
        if (context != null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
            return sharedPreferences.getBoolean(preferenceName, defaultValue);
        }
        return false;
    }

    public static void saveToPreferences(Context context, String preferenceName, int preferenceValue) {
        if (context != null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(preferenceName, preferenceValue);
            editor.apply();
        }
    }

    public static int readFromPreferences(Context context, String preferenceName, int defaultValue) {
        if (context == null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
            return sharedPreferences.getInt(preferenceName, defaultValue);
        }
        return 0;
    }

    public static void clearData(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }


    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }
}
