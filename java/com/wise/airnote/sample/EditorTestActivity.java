/*
 *  Copyright (C) InterWise, Inc. and 9Folders, Inc. - All Rights Reserved
 *
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *
 */

package com.wise.airnote.sample;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
        super.setTitle("AirNote Editor integration sample");

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
        	Intent intent = AirNoteBridge.createEditIndent("");
            startActivityForResult(intent, REQ_EDIT_HTML);
        } 
        else if (id == R.id.load_file) {
        	Intent intent = AirNoteBridge.createPickFileIntent("Choose HTML File");
            startActivityForResult(intent, REQ_PICK_FILE);
        }
        else if (id == R.id.edit_content) {
        	Intent intent = AirNoteBridge.createEditIndent(this.htmlContent);
            startActivityForResult(intent, REQ_EDIT_HTML);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	Log.d("airnote", "res : " + (resultCode == RESULT_OK) + " req: " + requestCode);
    	String html = null;
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == REQ_PICK_FILE) {
            	html = IOUtils.readContent(data.getData(), this.getContentResolver());
            }
            else if (requestCode == REQ_EDIT_HTML) {
            	html = AirNoteBridge.getEditResult(data);
            }
            
        	if (html != null) {
        		this.htmlContent = html;
        		this.mWebView.loadData(html, "text/html; charset=utf-8", "utf-8");
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
        
        if (!AirNoteBridge.isAirNoteInstalled(this)) {
            this.htmlContent = "<htm><body><br><br><H1 style='text-align:center'>Result View</H1></body></html>";
            this.findViewById(R.id.toolbar).setVisibility(View.VISIBLE);
        }
        else {
            this.htmlContent = "<htm><body><br><br><H3 style='text-align:center'>AirNote must be installed<br>to test this sample application.<br><br>"
            		+ "<A href='https://play.google.com/store/apps/details?id=com.wise.airnote.demo'>Go to the download page.</a></H3></body></html>";
        }
    	this.mWebView.loadData(htmlContent, "text/html; charset=utf-8", "utf-8");
        
    }

}
