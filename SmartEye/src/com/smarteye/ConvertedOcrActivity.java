package com.smarteye;

import java.io.File;

import com.google.api.GoogleAPIException;
import com.google.api.translate.Language;
import com.google.api.translate.Translate;
import com.google.api.translate.TranslateV1;
import com.google.api.translate.TranslateV2;
import com.googlecode.tesseract.android.TessBaseAPI;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

public class ConvertedOcrActivity extends Activity{
	Uri tempFileUri = null;
	File tempFile = null;
	String tempFilePath = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.context);
		
		Intent intent = getIntent();
		tempFilePath = intent.getStringExtra("tem");
		Log.v("NNNNNNNNNNNNN", tempFilePath);
		tempFile = new File(tempFilePath);
		
		tempFileUri = Uri.fromFile(tempFile);
		
		TextView ctv = (TextView) findViewById(R.id.ctv);
		
		String txt = image2Text();
		String lang = intent.getStringExtra("lang");
		
		if(lang == "hin"){
			TranslateV2 t = new TranslateV2();
			t.setHttpReferrer("http//www.dmathieu.com");
			//t.execute("Bonjour le monde", Language.FRENCH, Language.ENGLISH);
			
	        try {
	        	txt = t.execute("Language", Language.ENGLISH, Language.HINDI);
			} catch (GoogleAPIException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		ctv.setText(txt);
		
	}
	
	String image2Text() {
		String dataPath = "/mnt/sdcard/tesseract/";
		File tessdata = new File(dataPath);
		if (!tessdata.exists()) {
			throw new IllegalArgumentException(
					"Data path must contain subfolder tessdata!");
		}
		TessBaseAPI baseApi = new TessBaseAPI();
		baseApi.init(dataPath, "eng");
		baseApi.setImage(tempFile);
		String recognizedText = baseApi.getUTF8Text();
		baseApi.end();

		return recognizedText;
	}
	
	public void goHome(View View) {
		Intent intent = new Intent(this, MainActivity.class);
	    startActivity(intent);
	}
}
