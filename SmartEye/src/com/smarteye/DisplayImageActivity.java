package com.smarteye;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.googlecode.tesseract.android.TessBaseAPI;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class DisplayImageActivity extends Activity {
	Uri tempFileUri = null;
	File tempFile = null;
	String tempFilePath = null;
	ImageView im = null;
	final int PIC_CROP = 3;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.displayimg);
		
		Intent intent = getIntent();
		tempFilePath = intent.getStringExtra("tempFilePath");
		
		tempFile = new File(tempFilePath);
		
		tempFileUri = Uri.fromFile(tempFile);
		
		im = (ImageView) findViewById(R.id.imagePreview);
		
		
		showPhoto(tempFile);
		
		
	}
	
	public void cropImage(View View) {
		performCrop(tempFileUri);
	}
	
	private void performCrop(Uri picUri){
		try {
			Bitmap bmp = BitmapFactory.decodeFile(picUri.getPath());
			//call the standard crop action intent (the user device may not support it)
			Intent cropIntent = new Intent("com.android.camera.action.CROP");
			    //indicate image type and Uri
			cropIntent.setDataAndType(picUri, "image/*");
			    //set crop properties
			cropIntent.putExtra("crop", "true");
			    //indicate aspect of desired crop
			cropIntent.putExtra("aspectX", 1);
			cropIntent.putExtra("aspectY", 1);
			    //indicate output X and Y
			cropIntent.putExtra("scale", true);
			//cropIntent.putExtra("outputX", bmp.getWidth());
			//cropIntent.putExtra("outputY", bmp.getHeight());
			    //retrieve data on return
			cropIntent.putExtra("return-data", true);
			    //start the activity - we handle returning in onActivityResult
			startActivityForResult(cropIntent, PIC_CROP);
		}
		catch(ActivityNotFoundException anfe){
		    //display an error message
		    String errorMessage = "Whoops - your device doesn't support the crop action!";
		    Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
		    toast.show();
		}
	}
	
	private void showPhoto(File photo) {
		File imageFile = photo;
		if (imageFile.exists()) {
			Drawable oldDrawable = 
				im.getDrawable();
			if (oldDrawable != null) {
				((BitmapDrawable) oldDrawable).getBitmap().recycle();
			}
			Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
			BitmapDrawable drawable = new BitmapDrawable(this.getResources(),
					bitmap);
			im.setScaleType(ImageView.ScaleType.FIT_CENTER);
			im.setImageDrawable(drawable);
		}
	}
	
		@Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
			//user is returning from cropping the image
			if(requestCode == PIC_CROP && resultCode == RESULT_OK){
				//get the returned data
				Bundle extras = data.getExtras();
				//get the cropped bitmap
				Bitmap thePic = extras.getParcelable("data");
				//display the returned cropped image
				saveBmp(thePic);
				showPhoto(tempFile);
			}
			
		}
		

		private void saveBmp(Bitmap bmp){
			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			bmp.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
			try {
				//you can create a new file name "test.jpg" in sdcard folder.
				File f = tempFile;
				//write the bytes in file
				FileOutputStream fo;
				fo = new FileOutputStream(f);
				fo.write(bytes.toByteArray());
				// remember close the FileOutput
				fo.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		

		
		public void parseImageEng(View view){
			//parsedText.setText(image2Text());
			Intent intent = new Intent(this, ConvertedOcrActivity.class);
			intent.putExtra("tem", tempFilePath);
			intent.putExtra("lang", "eng");
		    startActivity(intent);
		}
		
		public void parseImageHin(View view){
			//parsedText.setText(image2Text());
			Intent intent = new Intent(this, ConvertedOcrActivity.class);
			intent.putExtra("tem", tempFilePath);
			intent.putExtra("lang", "hin");
		    startActivity(intent);
		}
		
	}
