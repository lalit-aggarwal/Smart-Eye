package com.smarteye;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.googlecode.tesseract.android.TessBaseAPI;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private static final int REQUEST_CAMERA = 1;
	private static int RESULT_LOAD_IMAGE = 2;
	
	private Bitmap bitmap;
	private static final String TAG = "CallCamera";
	private ImageView imageView;
	TextView tv;
	Uri tempFileUri = null;
	File tempFile = null;
	String tempFilePath = null;
	TextView parsedText = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		//imageView = (ImageView) findViewById(R.id.result);
		//imageView.setImageDrawable(null);
		//tv = (TextView) findViewById(R.id.textView1);
		//parsedText = (TextView) findViewById(R.id.parsedText);
		
		tempFile = getOutputPhotoFile();//File.createTempFile("smarteye", "jpg", getApplicationContext().getCacheDir());
		tempFileUri = Uri.fromFile(tempFile);
		tempFilePath = tempFile.getAbsolutePath();
	}

	public void openCamera(View View) {
		Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		i.putExtra(MediaStore.EXTRA_OUTPUT, tempFileUri);
		startActivityForResult(i, REQUEST_CAMERA);
	}

	public void openGallery(View View) {
		Intent i = new Intent(Intent.ACTION_PICK,
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(i, RESULT_LOAD_IMAGE);
	}
	
	public void exitApp(View View) {
		finish();
        System.exit(0);
	}

	private File getOutputPhotoFile() {
		File directory = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				getPackageName());
		if (!directory.exists()) {
			if (!directory.mkdirs()) {
				Log.e(TAG, "Failed to create storage directory.");
				return null;
			}
		}
		File file = new File(directory.getPath() + File.separator + "sd234r$#4hh.jpg");
		if(file.exists())
			file.delete();
		return file;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CAMERA) {
			if (resultCode == RESULT_OK) {
				Uri photoUri = null;
				if (data == null) {
					// A known bug here! The image should have saved in fileUri
					Toast.makeText(this, "Image saved successfully",
							Toast.LENGTH_LONG).show();
				} else {
					File photo = new File(data.getData().getPath());
					try {
						copy(photo, tempFile);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Toast.makeText(this,
							"Image saved successfully from: " + photo.getAbsolutePath(),
							Toast.LENGTH_LONG).show();
				}
				//tv.setText(tempFileUri.getPath());
				Toast.makeText(this, tempFileUri.getPath(), Toast.LENGTH_LONG).show();
				//showPhoto(tempFile);
				
				
				
				Intent intent = new Intent(this, DisplayImageActivity.class);
				intent.putExtra("tempFilePath", tempFilePath);
			    startActivity(intent);
				
				
				
				
				//performCrop(tempFileUri);
			} else if (resultCode == RESULT_CANCELED) {
				Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(this, "Callout for image capture failed!",
						Toast.LENGTH_LONG).show();
			}
		} else if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK
				&& null != data) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			cursor.close();
			//tv.setText(picturePath);
			
			Toast.makeText(this, picturePath, Toast.LENGTH_LONG).show();
			try {
				copy(new File(picturePath),tempFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//showPhoto(tempFile);
			
			Intent intent = new Intent(this, DisplayImageActivity.class);
			intent.putExtra("tempFilePath", tempFile.getAbsolutePath());
		    startActivity(intent);
		}
	}
	
	public void copy(File src, File dst) throws IOException {
	    InputStream in = new FileInputStream(src);
	    OutputStream out = new FileOutputStream(dst);

	    // Transfer bytes from in to out
	    byte[] buf = new byte[1024];
	    int len;
	    while ((len = in.read(buf)) > 0) {
	        out.write(buf, 0, len);
	    }
	    in.close();
	    out.close();
	}	
	

}