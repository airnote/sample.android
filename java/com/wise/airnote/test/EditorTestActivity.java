/*
 *  Copyright (C) InterWise, Inc. and 9Folders, Inc. - All Rights Reserved
 *
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *
 */

package com.wise.airnote.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Browser;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class EditorTestActivity extends Activity implements View.OnClickListener {
    private static final int REQ_PICK_FILE = 1001;
    private static final int REQ_EDIT_HTML = 1002;
    private WebView mWebView;
	private String htmlContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor_test);

        mWebView = (WebView)findViewById(R.id.webview);
        
        findViewById(R.id.create_editor).setOnClickListener(this);
        findViewById(R.id.load_file).setOnClickListener(this);
        findViewById(R.id.edit_content).setOnClickListener(this);

        initWebView(mWebView);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.create_editor) {
            Intent intent = new Intent(Intent.ACTION_INSERT);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            intent.setType("text/html");
            intent.putExtra("edit_content", "");
            startActivityForResult(intent, REQ_EDIT_HTML);
        	
            //Intent intent = new Intent(this, SignatureActivity.class);
            //this.startActivity(intent);
        } 
        else if (id == R.id.load_file) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            intent.setType("text/html");
            startActivityForResult(Intent.createChooser(intent, "Choose File"),
                    REQ_PICK_FILE);
        }
        else if (id == R.id.edit_content) {
            Intent intent = new Intent(Intent.ACTION_INSERT);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            intent.setType("text/html");
            intent.putExtra("edit_content", this.htmlContent);

            startActivityForResult(intent, REQ_EDIT_HTML);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	Log.d("airnote", "res : " + (resultCode == RESULT_OK) + " req: " + requestCode);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQ_PICK_FILE) {
                if (data != null && data.getData() != null) {
                    final Uri fileUri = data.getData();
                    
                    ContentResolver cr = this.getContentResolver();
                    InputStream is = null;
                    try {
                        is = cr.openInputStream(fileUri);
                        ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
                        IOUtils.copy(is, out);
                        out.flush();
                        out.close();

                        this.htmlContent = out.toString();
    	            	this.mWebView.loadData(htmlContent, "text/html; charset=utf-8", "utf-8");
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (is != null) {
                            try {
                                is.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } 	
            }
            else if (requestCode == REQ_EDIT_HTML) {
            	Bundle ex = data.getExtras();
            	if (ex != null) {
            		this.htmlContent = ex.getString("edit_result");
	            	Log.d("airnote", "edit result: " + htmlContent);
	            	this.mWebView.loadData(htmlContent, "text/html; charset=utf-8", "utf-8");
            	}
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @SuppressLint("NewApi")
	private void initWebView(WebView view) {
        WebSettings settings = view.getSettings();

        settings.setSavePassword(false);    //  Deprecated in API level 18.
        settings.setSaveFormData(false);

        //  Show always all images in HTML
        settings.setLoadsImagesAutomatically(true);

        //settings.setUseWideViewPort(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setBlockNetworkImage(false);
        settings.setLoadWithOverviewMode(true);
        //settings.setUseWideViewPort(true);		

        //  Debugging Android WebViews
        //  https://developers.google.com/chrome-developer-tools/docs/remote-debugging#debugging-webviews
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (0 != (getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE)) {
                WebView.setWebContentsDebuggingEnabled(true);
            }
        }

        view.setWebViewClient(new WebViewClient() {
            @Override
            public void onScaleChanged(WebView view, float oldScale, float newScale) {
                super.onScaleChanged(view, oldScale, newScale);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                final Intent intent;
                final Uri uri = Uri.parse(url);
                Activity activity = EditorTestActivity.this;
                intent = new Intent(Intent.ACTION_VIEW, uri);

                // If this is a mailto: uri, we want to set the account name in the intent so
                // the ComposeActivity can default to the current account
                intent.putExtra(Browser.EXTRA_APPLICATION_ID, activity.getPackageName());

                try {
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET
                            | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    activity.startActivity(intent);
                } catch (ActivityNotFoundException ex) {
                    // If no application can handle the URL, assume that the
                    // caller can handle it.
                }

                return true;
            }
        });
        
        this.htmlContent = "<htm><body><br><br><H1 style='text-align:center'>Result View</H1></body></html>";
    	this.mWebView.loadData(htmlContent, "text/html; charset=utf-8", "utf-8");
        
    }
}
