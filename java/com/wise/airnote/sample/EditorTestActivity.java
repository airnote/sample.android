/*
 *  Copyright (C) InterWise, Inc. and 9Folders, Inc. - All Rights Reserved
 *
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *
 */

package com.wise.airnote.sample;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
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
            Intent intent = new Intent(Intent.ACTION_EDIT);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            intent.setType("text/html");
            intent.putExtra("edit_content", "");
            startActivityForResult(intent, REQ_EDIT_HTML);
        } 
        else if (id == R.id.load_file) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            intent.setType("text/html");
            startActivityForResult(Intent.createChooser(intent, "Choose File"), REQ_PICK_FILE);
        }
        else if (id == R.id.edit_content) {
            Intent intent = new Intent(Intent.ACTION_EDIT);
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

        settings.setSaveFormData(false);

        //  Show always all images in HTML
        settings.setLoadsImagesAutomatically(true);

        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setBlockNetworkImage(false);
        settings.setLoadWithOverviewMode(true);
        //settings.setUseWideViewPort(true);		


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
            	return super.shouldOverrideUrlLoading(view, url);
            }
        });
        
        if (!this.isAirNoteInstalled()) {
            this.htmlContent = "<htm><body><br><br><H1 style='text-align:center'>Result View</H1></body></html>";
            this.findViewById(R.id.toolbar).setVisibility(View.VISIBLE);
        }
        else {
            this.htmlContent = "<htm><body><br><br><H3 style='text-align:center'>AirNote must be installed<br>to test this sample application.<br><br>"
            		+ "<A href='https://play.google.com/store/apps/details?id=com.wise.airnote.demo'>Go to the download page.</a></H3></body></html>";
        }
    	this.mWebView.loadData(htmlContent, "text/html; charset=utf-8", "utf-8");
        
    }

    private boolean isAirNoteInstalled() {
        PackageManager pm = getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo("com.wise.airnote.demo", PackageManager.GET_ACTIVITIES);
            app_installed = true;
        }
        catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }        
}
