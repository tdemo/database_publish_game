package cn.sdu.online.findteam.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

/**
 * Created by wn on 2015/8/13.
 */
public class PrefsUtil {
    private static final String PREFRENCE_NAME = "imkit_prefs";

    private SharedPreferences mSharedPrefs;

    private PrefsUtil(){

    }

    private static class InstanceHolder {
        public static PrefsUtil sInstance = new PrefsUtil();
    }

    public static PrefsUtil getInstance() {
        return InstanceHolder.sInstance;
    }

    public void init(Context context){
        mSharedPrefs = context.getSharedPreferences(PREFRENCE_NAME, Context.MODE_PRIVATE);
    }

    public String getString(String key){
        if(mSharedPrefs == null) return null;
        return mSharedPrefs.getString(key, null);
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public void putString(String key, String value){
        if(mSharedPrefs == null) return;
        SharedPreferences.Editor editor = mSharedPrefs.edit();
        editor.putString(key, value);
        if(Build.VERSION.SDK_INT >= 9)
            editor.apply();
        else
            editor.commit();
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public void remove(String key){
        SharedPreferences.Editor editor = mSharedPrefs.edit();
        editor.remove(key);

        if(Build.VERSION.SDK_INT >= 9) {
            editor.apply();
        } else {
            editor.commit();
        }
    }
}

