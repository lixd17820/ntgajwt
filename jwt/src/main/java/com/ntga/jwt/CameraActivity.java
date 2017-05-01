package com.ntga.jwt;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

public class CameraActivity extends Activity {
	protected static final String MEDIA_TYPE_IMAGE = null;
	private Camera mCamera;
	private CameraPreview mPreview;
	protected String TAG = "CameraActivity";
	boolean isTakePic = false;
	private Button captureButton, reCapture, savePic, cancelPic;
	private File picDir = new File("/sdcard/jwtpic");
	private String filePath = "";
	public static final String PIC_PATH = "pic_path";

	private BitmapFactory.Options options;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera_capture);

		options = new BitmapFactory.Options();
		options.inPurgeable = true;
		try {
			BitmapFactory.Options.class.getField("inNativeAlloc").setBoolean(
					options, true);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}

		// Create an instance of Camera
		mCamera = getCameraInstance();
		mPreview = new CameraPreview(this, mCamera);
		FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
		preview.addView(mPreview);

		captureButton = (Button) findViewById(R.id.button_capture);
		reCapture = (Button) findViewById(R.id.button_re_capture);
		savePic = (Button) findViewById(R.id.button_save_pic);
		cancelPic = (Button) findViewById(R.id.button_cancel_pic);
		changeButtonState();

		captureButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// get an image from the camera
				mCamera.takePicture(null, null, mPicture);

			}
		});

		findViewById(R.id.button_re_capture).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						File f = new File(filePath);
						if (f.exists())
							f.delete();
						filePath = "";
						isTakePic = false;
						changeButtonState();
						mCamera.startPreview();
					}
				});
		cancelPic.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				setResult(RESULT_CANCELED);
				finish();
			}
		});
		savePic.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (TextUtils.isEmpty(filePath)) {
					Toast.makeText(CameraActivity.this, "照片未拍摄成功！",
							Toast.LENGTH_LONG).show();
					return;
				}
				Intent i = new Intent();
				i.putExtra(PIC_PATH, filePath);
				setResult(RESULT_OK, i);
				finish();
			}
		});
	}

	private void changeButtonState() {
		reCapture.setEnabled(isTakePic);
		savePic.setEnabled(isTakePic);

	}

	/** A safe way to get an instance of the Camera object. */
	public static Camera getCameraInstance() {
		Camera c = null;
		try {
			c = Camera.open(); // attempt to get a Camera instance
		} catch (Exception e) {
			// Camera is not available (in use or does not exist)
		}
		return c; // returns null if camera is unavailable
	}

	private PictureCallback mPicture = new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {

			Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length,
					options);
			bitmap = Bitmap.createScaledBitmap(bitmap, 800, 600, true);
			filePath = savePicIntoSmall(bitmap);
			// File pictureFile = new File("/mnt/sdcard/abc.jpg");
			// try {

			// FileOutputStream fos = new FileOutputStream(pictureFile);
			// fos.write(data);
			// fos.close();
			// } catch (FileNotFoundException e) {
			// Log.d(TAG, "File not found: " + e.getMessage());
			// } catch (IOException e) {
			// Log.d(TAG, "Error accessing file: " + e.getMessage());
			// }
			isTakePic = true;
			changeButtonState();
			bitmap.recycle();
		}

		private String savePicIntoSmall(Bitmap smallImg) {
			String smallPath = "";
			if (smallImg != null) {
				try {
					if (!picDir.exists())
						picDir.mkdirs();
					File f = new File(picDir, System.currentTimeMillis()
							+ ".jpg");
					FileOutputStream fo = new FileOutputStream(f);
					smallImg.compress(CompressFormat.JPEG, 50, fo);
					fo.close();
					smallPath = f.getPath();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return smallPath;
		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		releaseCamera(); // release the camera immediately on pause event
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		releaseCamera(); // release the camera immediately on pause event
	}

	private void releaseCamera() {
		if (mCamera != null) {
			mCamera.release(); // release the camera for other applications
			mCamera = null;
		}
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		switch (event.getKeyCode()) {
		case KeyEvent.KEYCODE_CAMERA: // 拍照键
			// case KeyEvent.KEYCODE_FOCUS: // 拍照键半按的对焦状态
			// event.getAction() == KeyEvent.ACTION_UP
			// //Android123提示如果按键按下后弹起时触发
			// mCamera.takePicture(null, null, mPicture);
			return true; // 这些标记为处理过，则不在往内部传递
		default:
			break;

		}
		return super.dispatchKeyEvent(event);
	}

	@Override
	public void onBackPressed() {
		// super.onBackPressed();
	}

}