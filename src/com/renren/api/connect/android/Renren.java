/*
 * Copyright 2010 Renren, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.renren.api.connect.android;

import java.util.Set;
import java.util.TreeSet;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.CookieSyncManager;

import com.renren.api.connect.android.bean.FeedParam;
import com.renren.api.connect.android.exception.RenrenError;
import com.renren.api.connect.android.view.RenrenAuthListener;
import com.renren.api.connect.android.view.RenrenDialog;
import com.renren.api.connect.android.view.RenrenDialogListener;
import com.renren.api.connect.android.view.RenrenFeedListener;
import com.renren.api.connect.android.view.RenrenWidgetListener;

/**
 * 封装对人人的请求，如：显示登录界面、退出登录、请求 人人 APIs等。
 * 
 * @author yong.li@opi-corp.com
 */
public class Renren {

    /**
     * 如果服务器redirect该地址，SDK会认为用户做了取消操作。
     */
    public static final String CANCEL_URI = "rrconnect://cancel";

    /**
     * 如果服务器redirect该地址，SDK会认为用户做了确认操作。
     */
    public static final String SUCCESS_URI = "rrconnect://success";

    /**
     * 人人登录和授权的地址
     */
    public static final String AUTHORIZE_URL = "https://graph.renren.com/oauth/authorize";

    /**
     * 接口支持的数据格式
     */
    public static final String RESPONSE_FORMAT_JSON = "json";

    /**
     * 接口支持的数据格式
     */
    public static final String RESPONSE_FORMAT_XML = "xml";

    private static final String DEFAULT_REDIRECT_URI = "http://graph.renren.com/oauth/login_success.html";

    public static final String WIDGET_CALLBACK_URI = "http://widget.renren.com/callback.html";

    private static final String POST_FEED_URL = "http://www.connect.renren.com/feed/iphone/feedPrompt";

    private static final String RESTSERVER_URL = "http://api.renren.com/restserver.do";

    private String sessionKey = null;

    private String sessionSecret = null;

    private String apiKey;

    private AccessTokenManager accessTokenManager;

    /**
     * 构造Renren对象，开发者可以使用该类调用人人网提供的接口。
     * 
     * @param apiKey
     */
    public Renren(String apiKey) {
        if (apiKey == null) {
            throw new RuntimeException("apiKey必须提供");
        }
        this.apiKey = apiKey;
    }

    /**
     * 用户授权后更新accessToken和sessionKey。
     * 
     * @param accessToken
     * @throws RenrenError 换取sessionKey出现错误。
     * @throws RuntimeException 出现其他错误。
     */
    void updateAccessToken(String accessToken) throws RenrenError, RuntimeException {
        this.accessTokenManager.updateAccessToken(accessToken);
        this.updateSessionKey();
    }

    private void updateSessionKey() {
        this.sessionKey = this.accessTokenManager.getSessionKey();
        this.sessionSecret = this.accessTokenManager.getSessionSecret();
        if (this.sessionKey == null || this.sessionSecret == null) {
            Log.i(Util.LOG_TAG, "sessionKey sessionSecret is null!");
        }
    }

    /**
     * 尝试读取sessionKey；如果用户在一天内登录过并且没有退出返回true。
     * 
     * @param context
     * @return
     */
    public boolean restorSessionKey(Context context) {
        this.accessTokenManager = new AccessTokenManager(context);
        this.accessTokenManager.restoreSessionKey();
        this.updateSessionKey();
        if (this.sessionKey != null && this.sessionSecret != null) {
            return true;
        }
        return false;
    }

    /**
     * 用户登录和授权(Web Server Flow)。 用该方法完成的授权不能调用RenRes.request方法；对人人
     * API的调用需要通过开发者自己的服务器来代理。
     * 
     * @param activity
     * @param listener
     * @param redirectUrl 用来处理登录和授权的开发者的服务器地址。
     */
    public void authorize(Activity activity, final RenrenAuthListener listener, String redirectUrl) {
        this.authorize(activity, null, listener, redirectUrl);
    }

    /**
     * 用户登录和授权(Web Server Flow)。 用该方法完成的授权不能调用RenRes.request方法；对人人
     * API的调用需要通过开发者自己的服务器来代理。
     * 
     * @param activity
     * @param permissions 应用想拥有的权限列表。
     * @param listener
     * @param redirectUrl 用来处理登录和授权的开发者的服务器地址。
     */
    public void authorize(Activity activity, String[] permissions,
            final RenrenAuthListener listener, String redirectUrl) {
        if (this.isSessionKeyValid()) {
            listener.onComplete(new Bundle());
            return;
        }
        RenrenDialogListener rdl = RenrenListenerFactory.genWebServerFlowRenrenDialogListener(this,
                listener, redirectUrl);
        this.authorize(activity, permissions, rdl, redirectUrl, "code");
    }

    /**
     * 完成登录并获取sessionkey(User-Agent Flow)。
     * 
     * @param activity
     * @param listener
     */
    public void authorize(Activity activity, final RenrenAuthListener listener) {
        this.authorize(activity, null, listener);
    }

    /**
     * 完成登录并获取sessionkey(User-Agent Flow)。
     * 
     * @param activity
     * @param permissions 应用想拥有的权限列表。
     * @param listener
     */
    public void authorize(Activity activity, String[] permissions, final RenrenAuthListener listener) {
        if (this.isSessionKeyValid()) {
            listener.onComplete(new Bundle());
            return;
        }
        RenrenDialogListener rdl = RenrenListenerFactory.genUserAgentFlowRenrenDialogListener(this,
                listener, DEFAULT_REDIRECT_URI);
        this.authorize(activity, permissions, rdl, DEFAULT_REDIRECT_URI, "token");
    }

    private void authorize(Activity activity, String[] permissions,
            final RenrenDialogListener listener, String redirectUrl, String responseType) {
        // 调用CookieManager.getInstance之前
        // 必须先调用CookieSyncManager.createInstance
        CookieSyncManager.createInstance(activity);

        Bundle params = new Bundle();
        params.putString("client_id", apiKey);
        params.putString("redirect_uri", redirectUrl);
        params.putString("response_type", responseType);
        params.putString("display", "touch");
        if (permissions != null && permissions.length > 0) {
            String scope = TextUtils.join(" ", permissions);
            params.putString("scope", scope);
        }

        String url = AUTHORIZE_URL + "?" + Util.encodeUrl(params);
        if (activity.checkCallingOrSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            Util.showAlert(activity, "没有权限", "应用需要访问互联网的权限");
        } else {
            new RenrenDialog(activity, url, listener).show();
        }
    }

    /**
     * 退出登录
     * 
     * @param context
     * @return
     */
    public String logout(Context context) {
        Util.clearCookies(context);
        this.accessTokenManager.clearPersistSession();
        this.sessionKey = null;
        this.sessionSecret = null;
        return "true";
    }

    /**
     * 发送自定义发新鲜事。
     * 
     * @param context
     * @param feedParam templateData中的数据，必须和templateId模板中定义的一致。
     * @param listener
     */
    public void feed(Context context, FeedParam feedParam, final RenrenFeedListener listener) {
        Bundle params = new Bundle();
        params.putString("preview", "true");
        params.putString("callback", DEFAULT_REDIRECT_URI);
        params.putString("cancel_url", CANCEL_URI);
        params.putString("feed_target_type", "self_feed");
        params.putString("in_canvas", "0");
        params.putString("size", "2");
        params.putString("display", "android");

        params.putString("feed_info", feedParam.getFeedInfo());
        if (feedParam.getUserMessage() != null) {
            params.putString("user_message", feedParam.getUserMessage());
        }
        if (feedParam.getUserMessagePrompt() != null) {
            params.putString("user_message_prompt", feedParam.getUserMessagePrompt());
        }

        RenrenDialogListener rdl = RenrenListenerFactory
                .genFeedRenrenDialogListener(this, listener);
        this.dialog(context, rdl, params, POST_FEED_URL, true);
    }

    /**
     * 发送发新鲜事，不需要配置模板。
     * 
     * @param context
     * @param params
     * @param listener
     */
    public void feed2(Context context, Bundle params, final RenrenWidgetListener listener) {
        String url = "http://widget.renren.com/dialog/feed";
        this.widgetDialog(context, params, listener, url);
    }

    /**
     * 发送app邀请。
     * 
     * @param context
     * @param params
     * @param listener
     */
    public void appRequest(Context context, Bundle params, final RenrenWidgetListener listener) {
        String url = "http://widget.renren.com/dialog/request";
        this.widgetDialog(context, params, listener, url);
    }

    /**
     * 和widget相关的Dialog
     * 
     * @param context
     * @param params
     * @param listener
     * @param url
     */
    public void widgetDialog(Context context, Bundle params, final RenrenWidgetListener listener,
            String url) {
        params.putString("display", "touch");
        params.putString("return", "redirect");
        params.putString("redirect_uri", WIDGET_CALLBACK_URI);

        RenrenDialogListener rdl = RenrenListenerFactory.genRenrenWidgetDialogListener(this,
                listener);
        this.dialog(context, rdl, params, url, false);
    }

    /**
     * 显示人人网的对话框。
     * 
     * @param context
     * @param listener
     * @param params
     * @param url
     * @param showTitle
     */
    private void dialog(Context context, final RenrenDialogListener listener, Bundle params,
            String url, boolean showTitle) {
        params.putString("api_key", apiKey);
        if (sessionKey != null) {
            params.putString("session_key", sessionKey);
        }
        if (!params.containsKey("display")) {
            params.putString("display", "touch");
        }

        url = url + "?" + Util.encodeUrl(params);
        if (context.checkCallingOrSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            Util.showAlert(context, "没有权限", "应用需要访问互联网的权限");
        } else {
            //Util.logger("url:" + url);
            new RenrenDialog(context, url, listener, showTitle).show();
        }
    }

    /**
     * 调用 人人 APIs
     * 
     * @param parameters
     * @return 返回结果为xml格式
     */
    public String requestXML(Bundle parameters) {
        return this.request(parameters, "xml");
    }

    /**
     * 调用 人人 APIs
     * 
     * @param parameters
     * @return 返回结果为Json格式
     */
    public String requestJSON(Bundle parameters) {
        return this.request(parameters, "json");
    }

    /**
     * 上传照片到指定的相册。
     * 
     * @param albumId
     * @param photo
     * @param fileName
     * @param description
     * @param format
     * @return
     */
    public String uploadPhoto(long albumId, byte[] photo, String fileName, String description,
            String format) {
        Bundle params = new Bundle();
        params.putString("method", "photos.upload");
//        params.putString("aid", String.valueOf(albumId));
        params.putString("caption", description);

        String contentType = parseContentType(fileName);
        params.putString("format", format);
        if (isSessionKeyValid()) {
            params.putString("session_key", sessionKey);
        }
        this.prepareParams(params);
        return Util.uploadFile(RESTSERVER_URL, params, "upload", fileName, contentType, photo);
    }

    private String parseContentType(String fileName) {
        String contentType = "image/jpg";
        fileName = fileName.toLowerCase();
        if (fileName.endsWith(".jpg")) contentType = "image/jpg";
        else if (fileName.endsWith(".png")) contentType = "image/png";
        else if (fileName.endsWith(".jpeg")) contentType = "image/jpeg";
        else if (fileName.endsWith(".gif")) contentType = "image/gif";
        else if (fileName.endsWith(".bmp")) contentType = "image/bmp";
        else throw new RuntimeException("不支持的文件类型'" + fileName + "'(或没有文件扩展名)");
        return contentType;
    }
    
    /**
     * 调用 人人 APIs
     * 
     * @param parameters
     * @param format json or xml
     * @return
     */
    private String request(Bundle parameters, String format) {
        parameters.putString("format", format);
        if (isSessionKeyValid()) {
            parameters.putString("session_key", sessionKey);
        }
        this.prepareParams(parameters);
        return Util.openUrl(RESTSERVER_URL, "POST", parameters);
    }

    private void prepareParams(Bundle params) {
        params.putString("api_key", apiKey);
        params.putString("v", "1.0");
        params.putString("call_id", String.valueOf(System.currentTimeMillis()));
        params.putString("xn_ss", "1");//sessionSecret作为加密密钥

        StringBuffer sb = new StringBuffer();
        Set<String> keys = new TreeSet<String>(params.keySet());
        for (String key : keys) {
            sb.append(key);
            sb.append("=");
            sb.append(params.getString(key));
        }
        sb.append(this.sessionSecret);
        params.putString("sig", Util.md5(sb.toString()));
    }

    /**
     * 判断sessionKey是否有效。
     * 
     * @return boolean
     */
    public boolean isSessionKeyValid() {
        return (sessionKey != null && sessionSecret != null);
    }

    public String getSessionKey() {
        return sessionKey;
    }
}
