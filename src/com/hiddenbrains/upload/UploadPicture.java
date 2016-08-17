package com.hiddenbrains.upload;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;

public class UploadPicture {
	FragmentActivity act;
	String brand = Build.BRAND;
	String newFolder;
	String extStorageDirectory;

	public UploadPicture(FragmentActivity acti) {

		newFolder = "/barter";
		extStorageDirectory = Environment.getExternalStorageDirectory()
				.toString();
		File myNewFolder = new File(extStorageDirectory + newFolder);
		if (!myNewFolder.isDirectory())
			myNewFolder.mkdir();

		// File directory = new File(Environment.getExternalStorageDirectory()
		// + File.separator + "SCLIimages");
		// directory.mkdir();

		act = acti;
	}

	public String mCurrentPhotoPath;

	public void dispatchTakePictureIntent(int actionCode) {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File f = null;
		try {
			f = setUpPhotoFile();
			mCurrentPhotoPath = f.getAbsolutePath();
			takePictureIntent
					.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
		} catch (Exception e) {
			e.printStackTrace();
			f = null;
			mCurrentPhotoPath = null;
		}
		act.startActivityForResult(takePictureIntent, actionCode);
	}

	public void dispatchTakeVideoIntent(int actionCode) {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
		File f = null;
		try {
			f = setUpVideoFile();
			mCurrentPhotoPath = f.getAbsolutePath();
			takePictureIntent
					.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
		} catch (Exception e) {
			e.printStackTrace();
			f = null;
			mCurrentPhotoPath = null;
		}
		act.startActivityForResult(takePictureIntent, actionCode);
	}

	private File setUpPhotoFile() {
		try {
			long current = System.currentTimeMillis();
			File file = new File(extStorageDirectory + newFolder + current
					+ ".jpg");
			return file;
		} catch (Exception e) {
			return null;
		}
	}

	private File setUpVideoFile() {
		try {
			long current = System.currentTimeMillis();
			File file = new File("/mnt/sdcard/barter/" + current + ".mp4");
			return file;
		} catch (Exception e) {
			return null;
		}
	}

	public Bitmap setPic() {

		/* There isn't enough memory to open up more than a couple camera photos */
		/* So pre-scale the target bitmap into which the file is decoded */

		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

		/* Figure out which way needs to be reduced less */
		final int REQUIRED_SIZE = 140;

		// Find the correct scale value. It should be the power of 2.
		int width_tmp = bmOptions.outWidth, height_tmp = bmOptions.outHeight;
		int scale = 1;
		while (true) {
			if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE) {
				break;
			}
			width_tmp /= 2;
			height_tmp /= 2;
			scale *= 2;
		}
		/* Set bitmap options to scale the image decode target */
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scale;
		Bitmap bitmap = null;  
		/* Decode the JPEG file into a Bitmap */
		try {
			Matrix matrix = new Matrix();
			matrix.setRotate(getCameraPhotoOrientation(act,
					Uri.fromFile(new File(mCurrentPhotoPath)),
					mCurrentPhotoPath));
			bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath,
					bmOptions);
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
					bitmap.getHeight(), matrix, false);

			return bitmap;

			// mImageView.setImageBitmap(bitmap);
		} catch (Exception e) {
			e.getMessage();
		}
		return bitmap;
	}

	public Bitmap decodeUri(Uri selectedImage) throws FileNotFoundException {

		String[] filePathColumn = { MediaStore.Images.Media.DATA };

		Cursor cursor = act.getContentResolver().query(selectedImage,
				filePathColumn, null, null, null);
		cursor.moveToFirst();

		int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
		String filePath = cursor.getString(columnIndex);
		cursor.close();
		mCurrentPhotoPath = filePath;

		// Decode image size

		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(
				act.getContentResolver().openInputStream(selectedImage), null,
				o);

		// The new size we want to scale to
		final int REQUIRED_SIZE = 140;

		// Find the correct scale value. It should be the power of 2.
		int width_tmp = o.outWidth, height_tmp = o.outHeight;
		int scale = 1;
		while (true) {
			if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE) {
				break;
			}
			width_tmp /= 2;
			height_tmp /= 2;
			scale *= 2;
		}

		// Decode with inSampleSize
		BitmapFactory.Options o2 = new BitmapFactory.Options();
		o2.inSampleSize = scale;
		return BitmapFactory.decodeStream(act.getContentResolver()
				.openInputStream(selectedImage), null, o2);

	}

	// public String getImagePath(Bitmap inImage, Context inContext) {
	//
	// ByteArrayOutputStream bytes = new ByteArrayOutputStream();
	// inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
	// String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
	// .format(new Date());
	// String path = Images.Media.insertImage(inContext.getContentResolver(),
	// inImage, "Title" + timeStamp + ".jpg", null);
	// return path;
	//
	// }

	public static File bitmapToFile(Bitmap bitmap) {

		try {
			long current = System.currentTimeMillis();
			File file = new File(Environment.getExternalStorageDirectory()
					+ "/image" + current + ".jpg");
			FileOutputStream fOut;
			fOut = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
			fOut.flush();
			fOut.close();
			return file;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private int getCameraPhotoOrientation(Context context, Uri imageUri,
			String imagePath) {
		int rotate = 0;
		try {
			context.getContentResolver().notifyChange(imageUri, null);
			File imageFile = new File(imagePath);
			ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
			int orientation = exif.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);

			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_270:
				rotate = 270;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				rotate = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_90:
				rotate = 90;
				break;
			case ExifInterface.ORIENTATION_NORMAL:
				rotate = 0;
				break;
			}

			String manufacturer = Build.MANUFACTURER;
			if (manufacturer.contains("samsung")) {
//				rotate = rotate + 90;
			}
			// Log.v("", "Exif orientation: " + orientation);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rotate;
	}

	public Bitmap setPic(String path) {

		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, bmOptions);

		final int REQUIRED_SIZE = 140;

		int width_tmp = bmOptions.outWidth, height_tmp = bmOptions.outHeight;
		int scale = 1;
		while (true) {
			if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE) {
				break;
			}
			width_tmp /= 2;
			height_tmp /= 2;
			scale *= 2;
		}

		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scale;

		try {
			return BitmapFactory.decodeFile(path, bmOptions);
		} catch (Exception e) {
			e.getMessage();
			return null;
		}
	}
}