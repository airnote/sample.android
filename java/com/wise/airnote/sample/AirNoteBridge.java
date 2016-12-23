package com.wise.airnote.sample;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

public class AirNoteBridge {

	public static Intent createEditIndent(String content) {
        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        intent.setType("text/html");
        intent.putExtra(Intent.EXTRA_HTML_TEXT, content);
        return intent;
	}

	public static String getEditResult(Intent data) {
    	Bundle ex = data.getExtras();
    	String htmlContent = (ex == null) ? null : ex.getString(Intent.EXTRA_HTML_TEXT);
    	//Log.d("airnote", "edit result: " + html);
    	return htmlContent;
	}

	public static boolean isAirNoteInstalled(Context ctx) {
        PackageManager pm = ctx.getPackageManager();
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

	public static Intent createPickFileIntent(String title) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        intent.setType("text/html");
        return Intent.createChooser(intent, title);        
	}
}
