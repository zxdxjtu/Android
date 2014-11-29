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

package com.renren.api.connect.android.view;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.greenhand.your_choice.R;
import com.renren.api.connect.android.Util;

/**
 * 鎶婁汉浜鸿姹傜粨鏋滅敤Dialog褰㈠紡鏄剧ず缁欑敤鎴凤紙濡傜櫥褰曠晫闈級銆傚紑鍙戣�涓嶄細鐩存帴浣跨敤璇ョ被銆� * 
 * @author yong.li@opi-corp.com
 * 
 */
public class RenrenDialog extends Dialog {

    private static final int RENREN_BLUE = 0xFF005EAC;

    private static final float[] DIMENSIONS_LANDSCAPE = { 460, 260 };

    private static final float[] DIMENSIONS_PORTRAIT = { 280, 420 };

    private String mUrl;

    private RenrenDialogListener mListener;

    private ProgressDialog progress;

    private WebView webView;

    private LinearLayout content;

    private TextView title;

    private boolean showTitle = false;

    public RenrenDialog(Context context, String url, RenrenDialogListener listener) {
        this(context, url, listener, false);
    }

    public RenrenDialog(Context context, String url, RenrenDialogListener listener,
            boolean showTitle) {
        super(context);
        mUrl = url;
        mListener = listener;
        this.showTitle = showTitle;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progress = new ProgressDialog(getContext());
        progress.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progress.setMessage("Loading...");

        content = new LinearLayout(getContext());
        content.setOrientation(LinearLayout.VERTICAL);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (this.showTitle) {
            setUpTitle();
        }
        setUpWebView();

        Display display = getWindow().getWindowManager().getDefaultDisplay();
        float scale = getContext().getResources().getDisplayMetrics().density;
        float[] dimensions = display.getWidth() < display.getHeight() ? DIMENSIONS_PORTRAIT
                : DIMENSIONS_LANDSCAPE;
        addContentView(content, new FrameLayout.LayoutParams((int) (dimensions[0] * scale + 0.5f),
                (int) (dimensions[1] * scale + 0.5f)));
    }

    private void setUpTitle() {
        Drawable icon = getContext().getResources().getDrawable(
                R.drawable.renren_android_title_logo);
        title = new TextView(getContext());
        title.setText("涓庝汉浜鸿繛鎺");
        title.setTextColor(Color.WHITE);
        title.setGravity(Gravity.CENTER_VERTICAL);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setBackgroundColor(RENREN_BLUE);
        title.setBackgroundResource(R.drawable.renren_android_title_bg);
        title.setCompoundDrawablePadding(6);
        title.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
        content.addView(title);
    }

    private void setUpWebView() {
        webView = new WebView(getContext());
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setWebViewClient(new RenrenDialog.RenrenWebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(mUrl);
        FrameLayout.LayoutParams fill = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
        webView.setLayoutParams(fill);
        content.addView(webView);
    }

    private class RenrenWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.d(Util.LOG_TAG, "Redirect URL: " + url);
            int b = mListener.onPageBegin(url);
            switch (b) {
                case RenrenDialogListener.ACTION_PROCCESSED:
                    RenrenDialog.this.dismiss();
                    return true;
                case RenrenDialogListener.ACTION_DIALOG_PROCCESS:
                    return false;
            }
            getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Log.d(Util.LOG_TAG, "Webview loading URL: " + url);
            boolean b = mListener.onPageStart(url);
            if (b) {
                view.stopLoading();
                RenrenDialog.this.dismiss();
                return;
            }
            super.onPageStarted(view, url, favicon);
            progress.show();
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description,
                String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            mListener.onReceivedError(errorCode, description, failingUrl);
            progress.dismiss();
            RenrenDialog.this.dismiss();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            mListener.onPageFinished(url);
            if (showTitle) {
                String t = view.getTitle();
                if (t != null && t.length() > 0) {
                    title.setText(t);
                }
            }
            progress.dismiss();
        }
    }
}
