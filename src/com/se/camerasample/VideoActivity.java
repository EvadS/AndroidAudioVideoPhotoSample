package com.se.camerasample;

/**
 * implement tutorial 
 * http://startandroid.ru/ru/uroki/vse-uroki-spiskom/266-urok-133-kamera-delaem-snimok-i-pishem-video.html
 */
import java.io.File;
import java.io.FileOutputStream;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class VideoActivity extends Activity {

	SurfaceView surfaceView;
	Camera camera;
	MediaRecorder mediaRecorder;

	File photoFile;
	File videoFile;
	String pictures;// = Environment.getExternalStorageDirectory() + "/" + getResources().getString(R.string.app_name) + "/";
	
	
	private boolean prepareDirectory() {
		try {
			if (makedirs()) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	

	
	private boolean makedirs() {
		File tempdir = new File(pictures);
		if (!tempdir.exists())
			tempdir.mkdirs();

		return (tempdir.isDirectory());
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video);
		 pictures= Environment.getExternalStorageDirectory() + "/" + getResources().getString(R.string.app_name) + "/";
			
		prepareDirectory();
		
	    photoFile = new File(pictures, "myphoto.jpg");
		videoFile = new File(pictures, "myvideo.3gp");

		surfaceView = (SurfaceView) findViewById(R.id.surfaceView);

		SurfaceHolder holder = surfaceView.getHolder();
		holder.addCallback(new SurfaceHolder.Callback() {
			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				try {
					camera.setPreviewDisplay(holder);
					camera.startPreview();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			}

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
			}
		});

	}

	@Override
	protected void onResume() {
		super.onResume();
		camera = Camera.open();
	}

	@Override
	protected void onPause() {
		super.onPause();
		releaseMediaRecorder();
	
		if (camera != null)
			camera.release();
		camera = null;
	}

	public void onClickPicture(View view) {
		camera.takePicture(null, null, new PictureCallback() {
			@Override
			public void onPictureTaken(byte[] data, Camera camera) {
				try {
					FileOutputStream fos = new FileOutputStream(photoFile);
					fos.write(data);
					fos.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			
			
		});

	}
	
	public void  ShutterCallback(){
		
	}

	public void onClickStartRecord(View view) {
		if (prepareVideoRecorder()) {
			mediaRecorder.start();
		} else {
			releaseMediaRecorder();
		}
	}

	public void onClickStopRecord(View view) {
		if (mediaRecorder != null) {
			mediaRecorder.stop();
			releaseMediaRecorder();
		}
	}

	private boolean prepareVideoRecorder() {

		camera.unlock();

		mediaRecorder = new MediaRecorder();

		mediaRecorder.setCamera(camera);
		mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
		mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
		mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
		mediaRecorder.setOutputFile(videoFile.getAbsolutePath());
		mediaRecorder.setPreviewDisplay(surfaceView.getHolder().getSurface());

		try {
			mediaRecorder.prepare();
		} catch (Exception e) {
			e.printStackTrace();
			releaseMediaRecorder();
			return false;
		}
		return true;
	}

	private void releaseMediaRecorder() {
		if (mediaRecorder != null) {
			mediaRecorder.reset();
			mediaRecorder.release();
			mediaRecorder = null;
			camera.lock();
		}
	}

}
