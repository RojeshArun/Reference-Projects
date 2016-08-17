package it.configure.imageloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

final class Utils 
{
	
	/**
	 * Rotates the bitmap and saves the resulted one
	 * 
	 * @param bitmap
	 *            bitmap to rotate
	 * @param degrees
	 *            angle to rotate in degrees
	 * @return rotated bitmap
	 */
	public static Bitmap rotateBitmap(Bitmap bitmap, int degrees) {
		Bitmap rotatedBitmap = bitmap;

		try {
			Matrix matrix = new Matrix();
			matrix.postRotate(degrees);

			rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
					bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		} catch (Exception e) {
			rotatedBitmap = bitmap;
		}

		return rotatedBitmap;
	}
	
	public static Bitmap decodeFile(String filePath, int reqWidth, int reqHeight) {
		try {
			return decodeFile(new File(filePath), reqWidth, reqHeight);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			// Calculate ratios of height and width to requested height and
			// width
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			// Choose the smallest ratio as inSampleSize value, this will
			// guarantee
			// a final image with both dimensions larger than or equal to the
			// requested height and width.
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		return inSampleSize <= 0 ? 1 : inSampleSize;
	}
	
	public static Bitmap decodeUri(ContentResolver contentResolver, Uri uri, int reqWidth, int reqHeight)
	{
		Bitmap bitmap = null;
		try {

			if (uri ==null)
				return null;

			BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
			bitmapOptions.inJustDecodeBounds = true;

			InputStream inputStream = contentResolver.openInputStream(uri);
			BitmapFactory.decodeStream(inputStream, null, bitmapOptions);

			try {
				inputStream.close();
			} catch (Exception e) {

			}

			if (bitmapOptions.outWidth <= 0 || bitmapOptions.outHeight <= 0)
				return null;

			bitmapOptions = new BitmapFactory.Options();
			bitmapOptions.inSampleSize = calculateInSampleSize(bitmapOptions,
					reqWidth, reqHeight);

			/*
			 * o2.inJustDecodeBounds = false; o2.inPurgeable = true; o2.inDither
			 * = false; o2.inScaled = false; o2.inPreferredConfig =
			 * Bitmap.Config.ARGB_8888;
			 */

			inputStream = contentResolver.openInputStream(uri);

			bitmap = BitmapFactory.decodeStream(inputStream, null,
					bitmapOptions);

			try {
				inputStream.close();
			} catch (Exception e) {

			}

			try {

				int width = bitmap.getWidth();
				int height = bitmap.getHeight();

				if (bitmapOptions.inSampleSize == 1
						&& (width > reqWidth || height > reqHeight)) {

					int dstWidth = 0;
					int dstHeight = 0;

					if (width > reqWidth && height > reqHeight) {
						dstWidth = reqWidth;
						dstHeight = reqHeight;
					} else if (width > reqWidth) {
						dstWidth = reqWidth;
						dstHeight = (dstWidth * height) / width;

						if (dstHeight > reqHeight)
							dstHeight = reqHeight;
					} else // 99% this case will not come
					{
						dstHeight = reqHeight;
						dstWidth = (dstHeight * width) / height;

						if (dstWidth > reqWidth)
							dstWidth = reqWidth;
					}

					bitmap = Bitmap.createScaledBitmap(bitmap, dstWidth,
							dstHeight, false);
				}

			} catch (OutOfMemoryError E) {

			}

			return bitmap;

		} catch (Exception e) {
		}
		return bitmap;
	}
	
	public static Bitmap decodeFile(File f, int reqWidth, int reqHeight) {
		Bitmap bitmap = null;
		try {

			if (!f.exists())
				return null;

			BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
			bitmapOptions.inJustDecodeBounds = true;

			InputStream inputStream = new FileInputStream(f);
			BitmapFactory.decodeStream(inputStream, null, bitmapOptions);

			try {
				inputStream.close();
			} catch (Exception e) {

			}

			if (bitmapOptions.outWidth <= 0 || bitmapOptions.outHeight <= 0)
				return null;

			bitmapOptions = new BitmapFactory.Options();
			bitmapOptions.inSampleSize = calculateInSampleSize(bitmapOptions,
					reqWidth, reqHeight);

			/*
			 * o2.inJustDecodeBounds = false; o2.inPurgeable = true; o2.inDither
			 * = false; o2.inScaled = false; o2.inPreferredConfig =
			 * Bitmap.Config.ARGB_8888;
			 */

			inputStream = new FileInputStream(f);

			bitmap = BitmapFactory.decodeStream(inputStream, null,
					bitmapOptions);

			try {
				inputStream.close();
			} catch (Exception e) {

			}

			try {

				int width = bitmap.getWidth();
				int height = bitmap.getHeight();

				if (bitmapOptions.inSampleSize == 1
						&& (width > reqWidth || height > reqHeight)) {

					int dstWidth = 0;
					int dstHeight = 0;

					if (width > reqWidth && height > reqHeight) {
						dstWidth = reqWidth;
						dstHeight = reqHeight;
					} else if (width > reqWidth) {
						dstWidth = reqWidth;
						dstHeight = (dstWidth * height) / width;

						if (dstHeight > reqHeight)
							dstHeight = reqHeight;
					} else // 99% this case will not come
					{
						dstHeight = reqHeight;
						dstWidth = (dstHeight * width) / height;

						if (dstWidth > reqWidth)
							dstWidth = reqWidth;
					}

					bitmap = Bitmap.createScaledBitmap(bitmap, dstWidth,
							dstHeight, false);
				}

				ExifInterface ei = new ExifInterface(f.getAbsolutePath());
				int orientation = ei.getAttributeInt(
						ExifInterface.TAG_ORIENTATION,
						ExifInterface.ORIENTATION_NORMAL);
				switch (orientation) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					bitmap = rotateBitmap(bitmap, 90);
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					bitmap = rotateBitmap(bitmap, 180);
					break;

				case ExifInterface.ORIENTATION_ROTATE_270:
					bitmap = rotateBitmap(bitmap, 270);
					break;
				}
			} catch (OutOfMemoryError E) {

			}

			return bitmap;

		} catch (Exception e) {
		  Log.e("error","decode file");
		}
		return bitmap;
	}

	
	public static boolean isUserOnline(Context context) {
		try {
			ConnectivityManager nConManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (nConManager != null) {
				NetworkInfo nNetworkinfo = nConManager.getActiveNetworkInfo();

				if (nNetworkinfo != null) {
					return nNetworkinfo.isConnected();
				}
			}
		} catch (Exception e) {
		}
		return false;
	}
	
    public static void CopyStream(InputStream is, OutputStream os)
    {
        final int buffer_size=1024;
        try
        {
            byte[] bytes=new byte[buffer_size];
            for(;;)
            {
              int count=is.read(bytes, 0, buffer_size);
              if(count==-1)
                  break;
              os.write(bytes, 0, count);
            }
        }
        catch(Exception ex){}
    }
	/**
	 * @return <b>true</b> if sd card is available and Mounted<br/>
	 *         <b>false</b> if sd card is not available or Shared <br/>
	 * <br/>
	 */
	public static boolean isSdCardAvailabe() {
		return Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
	}
}