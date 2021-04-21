package com.yjfshop123.live.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class ConfigUtil {

    private final String SHARE_NAME = "chat_preference";
    private Context ctx;
    private ConfigUtil(Context ctx) {
        this.ctx = ctx;
    }

    private static ConfigUtil conf = null;
    private static SharedPreferences.Editor edit = null;
    private static Object syncObj = new Object();

    public static synchronized ConfigUtil ins(Context ctx) {
        if (ctx == null)
            return null;
        if (conf == null) {
            conf = new ConfigUtil(ctx);
        }
        return conf;
    }

    private SharedPreferences getPreference(String name) {

        if (name == null || name.length() <= 0) {
            name = SHARE_NAME;
        }
        return ctx.getSharedPreferences(name, Activity.MODE_PRIVATE);
    }

    public boolean storeShareDataWithCommit(String key, String value) {
        storeShareData(key, value);
        return commit();
    }

    public boolean storeShareData(String key, String value) {
        return storeShareData(key, value, null);
    }

    public boolean storeShareData(String key, String value, String name) {
        if (key == null)
            return false;
        if (ctx != null) {
            synchronized (syncObj) {
                if (edit == null) {
                    edit = getPreference(name).edit();
                }
                edit.putString(key, value);
                return true;
            }
        }
        return false;
    }

    public boolean commit() {
        if (edit != null) {
            synchronized (syncObj) {
                boolean ret = edit.commit();
                if (ret) {
                    edit = null;
                }
                return ret;
            }
        }
        return false;
    }

    public boolean isCommit() {
        return (edit == null);
    }

    public String getStringShareData(String key) {
        return getStringShareData(key, "");
    }

    public String getStringShareData(String key, String name) {
        if (key == null) {
            return "";
        }
        return getPreference(name).getString(key, "");
    }

    public boolean getBooleanShareData(String key, boolean defValue) {
        return getBooleanShareData(key, defValue, null);
    }

    public boolean getBooleanShareData(String key, boolean defValue, String name) {
        return getPreference(name).getBoolean(key, defValue);
    }

    public boolean storeBooleanShareData(String key, boolean value) {
        return storeBooleanShareData(key, value, null);
    }

    public boolean storeBooleanShareData(String key, boolean value, String name) {
        boolean ret = false;
        if (ctx != null) {
            synchronized (syncObj) {
                if (edit == null) {
                    edit = getPreference(name).edit();
                }
                edit.putBoolean(key, value);
                edit.commit();
                ret = true;
            }
        }
        return ret;
    }

    public int getIntShareData(String key, int defValue) {
        return getIntShareData(key, defValue, null);
    }

    public int getIntShareData(String key, int defValue, String name) {
        return getPreference(name).getInt(key, defValue);
    }

    public boolean storeIntShareData(String key, int value) {
        return storeIntShareData(key, value, null);
    }

    public boolean storeIntShareData(String key, int value, String name) {
        boolean ret = false;
        if (ctx != null) {
            synchronized (syncObj) {
                if (edit == null) {
                    edit = getPreference(name).edit();
                }
                edit.putInt(key, value);
                edit.commit();
                ret = true;
            }
        }
        return ret;
    }
}
