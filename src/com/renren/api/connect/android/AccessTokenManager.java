package com.renren.api.connect.android;

import org.json.JSONException;
import org.json.JSONObject;

import com.renren.api.connect.android.exception.RenrenError;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;

/**
 * 用来存取AccessToken和SesionKey；开发者不会直接使用该类。
 * 
 * @author 李勇(yong.li@opi-corp.com) 2011-2-24
 */
class AccessTokenManager {

    private static final String SESSION_KEY_URL = "http://graph.renren.com/renren_api/session_key";

    private static final String RENREN_SDK_CONFIG = "renren_sdk_config";

    private static final String RENREN_SDK_CONFIG_PROP_ACCESS_TOKEN = "renren_sdk_config_prop_access_token";

    private static final String RENREN_SDK_CONFIG_PROP_CREATE_TIME = "renren_sdk_config_prop_create_time";

    private static final long ONE_HOUR = 1000 * 60 * 60;

    private Context context;

    private String accessToken = null;

    private String sessionKey;

    private String sessionSecret = null;

    AccessTokenManager(Context context) {
        this.context = context;
    }

    String getSessionKey() {
        return this.sessionKey;
    }

    String getSessionSecret() {
        return this.sessionSecret;
    }

    /**
     * 更新accessToken；如果换取sessionKey出现错误抛出RenrenException。
     * 
     * @param accessToken
     */
    void updateAccessToken(String accessToken) {
        this.updateAccessToken(accessToken, true);
    }

    void restoreSessionKey() {
        this.accessToken = this.restoreAccessToken();
        try {
            this.exchangeSessionKey(this.accessToken);
        } catch (Exception e) {
            e.printStackTrace();
            this.clearPersistSession();
            this.accessToken = null;
            this.sessionKey = null;
            this.sessionSecret = null;
        }
    }

    void clearPersistSession() {
        Editor editor = context.getSharedPreferences(RENREN_SDK_CONFIG, Context.MODE_PRIVATE)
                .edit();
        editor.remove(RENREN_SDK_CONFIG_PROP_ACCESS_TOKEN);
        editor.remove(RENREN_SDK_CONFIG_PROP_CREATE_TIME);
        editor.commit();
    }

    private void updateAccessToken(String accessToken, boolean isPersist) {
        if (accessToken == null || accessToken.length() < 1) {
            return;
        }
        this.exchangeSessionKey(accessToken);

        this.accessToken = accessToken;
        if (isPersist) {
            this.storeAccessToken(accessToken);
        } else {
            this.clearPersistSession();
        }
    }

    private void exchangeSessionKey(String accessToken) {
        if (accessToken == null || accessToken.length() < 1) {
            return;
        }
        Bundle params = new Bundle();
        params.putString("oauth_token", accessToken);
        String sk = Util.openUrl(SESSION_KEY_URL, "POST", params);
        try {
            JSONObject obj = new JSONObject(sk);
            String error = obj.optString("error", null);
            if (error != null) {
                throw new RenrenError(obj.toString());
            }
            sessionKey = obj.getJSONObject("renren_token").getString("session_key");
            sessionSecret = obj.getJSONObject("renren_token").getString("session_secret");
            long expires = obj.getJSONObject("renren_token").getLong("expires_in");
            Log.i(Util.LOG_TAG, "---login success sessionKey:" + sessionKey + " expires:" + expires
                    + " sessionSecret:" + sessionSecret);
        } catch (JSONException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private void storeAccessToken(String accessToken) {
        Editor editor = context.getSharedPreferences(RENREN_SDK_CONFIG, Context.MODE_PRIVATE)
                .edit();
        if (accessToken != null) {
            editor.putString(RENREN_SDK_CONFIG_PROP_ACCESS_TOKEN, accessToken);
            editor.putLong(RENREN_SDK_CONFIG_PROP_CREATE_TIME, System.currentTimeMillis());
        } else {
            this.clearPersistSession();
        }
        editor.commit();
    }

    private String restoreAccessToken() {
        SharedPreferences sp = context
                .getSharedPreferences(RENREN_SDK_CONFIG, Context.MODE_PRIVATE);
        String accessToken = sp.getString(RENREN_SDK_CONFIG_PROP_ACCESS_TOKEN, null);
        if (accessToken == null) {
            return null;
        }
        long createTime = sp.getLong(RENREN_SDK_CONFIG_PROP_CREATE_TIME, 0);
        long life = Long.parseLong(accessToken.split("\\.")[2]) * 1000;
        long currenct = System.currentTimeMillis();
        if ((createTime + life) < (currenct - ONE_HOUR)) {
            this.clearPersistSession();
            return null;
        }
        return accessToken;
    }
}
