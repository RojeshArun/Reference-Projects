package it.configure.imageloader;

import it.configure.imageloader.zoom.PhotoViewAttacher;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.text.TextUtils;
import android.util.Base64;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;


/**
 * @author hb
 * 
 *         ImageLoader loads the image from http url or a file and displays it
 *         in an ImageView
 * 
 */
public class ImageLoader {

	MemoryCache memoryCache;
	FileCache fileCache;
	private Map<ImageView, String> imageViews = Collections
			.synchronizedMap(new WeakHashMap<ImageView, String>());
	// ExecutorService executorService;

	private Drawable presetDrawable = null;
	private int loadingImageResId;
	private int reqWidth, reqHeight;
	private boolean zoom = false;

	private Activity a;

	private boolean isNetworkAvailable = false;

	/**
	 * @param activity
	 *            Activity context
	 */
	
	@TargetApi(13)
	public ImageLoader(Activity activity) {
		memoryCache = MemoryCache.getInstance();

		a = activity;

		isNetworkAvailable = Utils.isUserOnline(a);

		fileCache = new FileCache(activity);
		// executorService = Executors.newFixedThreadPool(5);

		WindowManager windowManager = activity.getWindowManager();

		if (Build.VERSION.SDK_INT >= 13) 
		{
			Point size = new Point();
			windowManager.getDefaultDisplay().getSize(size);
			setReqWidth(size.x);
			setReqHeight(size.y);

		} else {
			Display display = windowManager.getDefaultDisplay();
			setReqWidth(display.getWidth());
			setReqHeight(display.getHeight());
		}
	}

	/**
	 * @param activity
	 *            Activity context
	 * @param loadingImageResId
	 *            the resource id of the drawable that is to be displayed until
	 *            the image is loaded
	 * @param zoom
	 *            true if you want to zoom the image
	 */
	public ImageLoader(Activity activity, int loadingImageResId, boolean zoom) {
		this(activity);
		this.zoom = zoom;
		setLoadingImage(loadingImageResId);
	}

	/**
	 * @param f
	 *            File object of image
	 * @param imageView
	 *            imageView in which you want to display the above image
	 */
	public void DisplayImage(File f, ImageView imageView) {
		DisplayImage(f, imageView, null);
	}

	public void DisplayImage(String url, ImageView imageView,boolean isCircle) {
		DisplayImage(url, imageView, null,isCircle);
	}
	
	/**
	 * @param url
	 *            url of the image
	 * @param imageView
	 *            imageView in which you want to display the above image
	 */
	public void DisplayImage(String url, ImageView imageView) {
		DisplayImage(url, imageView, null,false);
	}

	/**
	 * @param f
	 *            File object of image
	 * @param imageView
	 *            imageView in which you want to display the above image
	 * @param progressBar
	 *            progress bar will be shown until the image is loaded
	 */
	public void DisplayImage(File f, ImageView imageView,
			ProgressBar progressBar) {
		if (!f.exists()) {
			return;
		}

		String url = f.getAbsolutePath();

		imageViews.put(imageView, url);

		Bitmap bitmap = memoryCache.get(url);
		if (bitmap != null) {
			if (progressBar != null)
				progressBar.setVisibility(View.GONE);

			imageView.setImageBitmap(bitmap);

			if (zoom) {
				PhotoViewAttacher attacher = new PhotoViewAttacher(imageView);
				attacher.update();
			}
		} else {

			if (presetDrawable != null) {
				imageView.setImageDrawable(presetDrawable);
			} else {
				imageView.setImageDrawable(null);

				if (progressBar != null)
					progressBar.setVisibility(View.VISIBLE);
			}

			queuePhoto(f, imageView, progressBar);
		}

	}

	private static final String ALLOWED_URI_CHARS = "@#&=*+-_.,:!?()/~'%";

	/**
	 * @param url
	 *            url of the image
	 * @param imageView
	 *            imageView in which you want to display the above image
	 * @param progressBar
	 *            progress bar will be shown until the image is loaded
	 */
	public void DisplayImageWithURLCacheEnabled(String url, ImageView imageView,
			ProgressBar progressBar,boolean isCircle) {

		if (TextUtils.isEmpty(url) || !url.trim().startsWith("http")) {
			return;
		}

		url = Uri.encode(url, ALLOWED_URI_CHARS);
		imageViews.put(imageView, url);
		{
			if (presetDrawable != null) {
				imageView.setImageDrawable(presetDrawable);
			} else {
				imageView.setImageDrawable(null);

				if (progressBar != null)
					progressBar.setVisibility(View.VISIBLE);
			}

			queuePhoto(url, imageView, progressBar,true,isCircle);
		}
	}
	
	
	/**
	 * @param url
	 *            url of the image
	 * @param imageView
	 *            imageView in which you want to display the above image
	 * @param progressBar
	 *            progress bar will be shown until the image is loaded
	 */
	public void DisplayImage(String url, ImageView imageView,
			ProgressBar progressBar)
			{
		       DisplayImage(url, imageView, progressBar, false);
			}
	
	/**
	 * @param url
	 *            url of the image
	 * @param imageView
	 *            imageView in which you want to display the above image
	 * @param progressBar
	 *            progress bar will be shown until the image is loaded
	 */
	public void DisplayImage(String url, ImageView imageView,
			ProgressBar progressBar,boolean isCircle) {

		if (TextUtils.isEmpty(url) || !url.trim().startsWith("http")) {
			return;
		}

		url = Uri.encode(url, ALLOWED_URI_CHARS);
		imageViews.put(imageView, url);

		Bitmap bitmap = memoryCache.get(url);
		if (bitmap != null) {
			if (progressBar != null)
				progressBar.setVisibility(View.GONE);

			if(isCircle)
			{
				bitmap = transform(bitmap);
			}
			
			imageView.setImageBitmap(bitmap);

			if (zoom) {
				PhotoViewAttacher attacher = new PhotoViewAttacher(imageView);
				attacher.update();
			}
		} else {
			if (presetDrawable != null) {
				imageView.setImageDrawable(presetDrawable);
			} else {
				imageView.setImageDrawable(null);

				if (progressBar != null)
					progressBar.setVisibility(View.VISIBLE);
			}

			queuePhoto(url, imageView, progressBar,false,isCircle);
		}
	}

	/**
	 * @param url
	 * @param imageView
	 * @param progressBar
	 */
	@TargetApi(11)
	private void queuePhoto(String url, ImageView imageView,
			ProgressBar progressBar,boolean urlCacheEnabled,boolean isCircle) {
		PhotoToLoad p = new PhotoToLoad(url, imageView, progressBar,urlCacheEnabled,isCircle);
		downloadImage(p);
	}

	/**
	 * @param f
	 * @param imageView
	 * @param progressBar
	 */
	private void queuePhoto(File f, ImageView imageView, ProgressBar progressBar) {
		PhotoToLoad p = new PhotoToLoad(f, imageView, progressBar);
		downloadImage(p);
	}

	@TargetApi(11)
	private void downloadImage(PhotoToLoad p) {
		// executorService.submit(new PhotosLoader(p));

		PhotosLoaderTask photosLoaderTask = new PhotosLoaderTask();
		if (Build.VERSION.SDK_INT >= 11) {
			photosLoaderTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
					new PhotoToLoad[] { p });
		} else {
			photosLoaderTask.execute(new PhotoToLoad[] { p });
		}
	}

	private String getCredentials(String usernamePassword) {
		try {
			// String usernamePassword = "hbcleweb:hiddenbrains";
			String authorization = "Basic "
					+ Base64.encodeToString(usernamePassword.getBytes("UTF-8"),
							Base64.NO_WRAP);
			return authorization;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * @param url
	 *            url of image to download
	 * @return bitmap of the above specified image url
	 */
	public Bitmap getBitmap(String url,boolean urlCacheEnabled,boolean isCircle) 
	{
		if (TextUtils.isEmpty(url) || !url.trim().startsWith("http")) 
		{
			return null;
		}
		
		File f = fileCache.getFile(url);

		// from SD cache
		Bitmap b = decodeFile(f);
		if (b != null)
		{
			if(!urlCacheEnabled)
			{
				if(isCircle)
				{
					b = transform(b);
				}
				return b;
			}
		}

		if (!isNetworkAvailable)
		{
			if(isCircle)
			{
				b = transform(b);
			}
			return urlCacheEnabled?b:null;
		}
		
		try 
		{
			// url =
			// "http://hbcleweb:hiddenbrains@www.clewebinar.com/umkc/img/p/63-91-home.jpg";
		
			// TODO Main Logic
			
			Bitmap bitmap = null;
			URL imageUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) imageUrl
					.openConnection();
			
			long latestModifiedDate = conn.getLastModified();
			
				if(urlCacheEnabled)
				{
					try
				{
					long previousModifiedDate = fileCache.getLatModifiedDate(url);
					
					if(latestModifiedDate>previousModifiedDate)
					{
						// modified so download again
						
						deleteFileData(f, url);
						f = fileCache.getFile(url);
					}
					else
					{
						if(b!=null)
						{
							if(isCircle)
							{
								b = transform(b);
							}
							return b;
						}
						else
						{
							deleteFileData(f, url);
							f = fileCache.getFile(url);
						}
					}
					
				} catch (Exception e) {
					deleteFileData(f, url);
					f = fileCache.getFile(url);
				}
				}
			
			if (!TextUtils.isEmpty(imageUrl.getUserInfo())
					&& imageUrl.getUserInfo().contains(":")) {
				conn.setRequestProperty("Authorization",
						getCredentials(imageUrl.getUserInfo()));
			}
			conn.setConnectTimeout(0);
			conn.setReadTimeout(0);
			conn.setInstanceFollowRedirects(true);
			
			InputStream is = conn.getInputStream();
			
			String contentType = getContentType(conn.getContentType());
			if(!TextUtils.isEmpty(contentType))
			{	
				String extension = "." + contentType;
				
				String newPath = f.getAbsolutePath() + extension;
				
				deleteFileData(f, url);
				
				f = new File(newPath);
				fileCache.saveFileExtension(url, extension);
			}
			
			OutputStream os = new FileOutputStream(f);
			CopyStream(is, os);
			os.close();
			bitmap = decodeFile(f);
			
			if(urlCacheEnabled && bitmap!=null)
			{
				fileCache.saveLastModifiedDate(url, latestModifiedDate);
			}
			
			
			if(isCircle)
			{
				bitmap = transform(bitmap);
			}
			
			return bitmap;
		} catch (Throwable ex) {
			return null;
		}
	}
	
	private String getContentType(String contentType) 
	{
		try
		{
			return contentType.split("/")[1].trim();
		}
		catch(Exception e)
		{
			
		}
		return null;
	}

	/**
	 * @param is
	 * @param os
	 */
	private void CopyStream(InputStream is, OutputStream os) {
		final int buffer_size = 1024;
		try {
			byte[] bytes = new byte[buffer_size];
			for (;;) {
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1)
					break;
				os.write(bytes, 0, count);
			}
		} catch (Exception ex) {
		}
	}

	/**
	 * @param file
	 * @return
	 */
	private Bitmap getBitmap(File file) 
	{
		File f = fileCache.getFile(file.getAbsolutePath());

		// from SD cache
		Bitmap b = decodeFile(f);
		if (b != null)
			return b;

		// from file
		try {
			b = decodeFile(file);
			return b;
		} catch (Throwable ex) {
			return null;
		}
	}

	// decodes image and scales it to reduce memory consumption
	/**
	 * @param f
	 * @return
	 */
	public Bitmap decodeFile(File f) {
		return Utils.decodeFile(f, reqWidth, reqHeight);
	}
	
	
	// decodes image and scales it to reduce memory consumption
		/**
		 * @param uri
		 * @return
		 */
		public Bitmap decodeUri(Uri uri) {
			return Utils.decodeUri(a.getContentResolver(),uri, reqWidth, reqHeight);
		}


	// Task for the queue
	/**
	 * @author hb
	 * 
	 */
	private class PhotoToLoad {
		public String url;
		public ImageView imageView;
		public ProgressBar progressBar;
		public File file;
		public boolean urlCacheEnabled=false,isCircle=false;

		public PhotoToLoad(String u,
				ImageView i, ProgressBar p,boolean urlCacheEnabled,boolean isCircle) {
			url = u;
			imageView = i;
			progressBar = p;
			this.urlCacheEnabled = urlCacheEnabled;
			this.isCircle = isCircle;
		}
		
	

		public PhotoToLoad(File f, ImageView i, ProgressBar p) {
			file = f;
			imageView = i;
			progressBar = p;
		}
	}

	private class PhotosLoaderTask extends
			AsyncTask<PhotoToLoad, Bitmap, Bitmap> {
		PhotoToLoad photoToLoad;

		@Override
		protected Bitmap doInBackground(PhotoToLoad... params) {
			photoToLoad = params[0];

			if (imageViewReused(photoToLoad))
				return null;

			Bitmap bmp = null;

			if (TextUtils.isEmpty(photoToLoad.url)) {
				bmp = getBitmap(photoToLoad.file);

				if (bmp == null)
					return null;

				memoryCache.put(photoToLoad.file.getAbsolutePath(), bmp);
			} else 
			{
				bmp = getBitmap(photoToLoad.url,photoToLoad.urlCacheEnabled,photoToLoad.isCircle);

				if (bmp == null)
					return null;

				memoryCache.put(photoToLoad.url, bmp);
			}

			return bmp;
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			super.onPostExecute(bitmap);

			if (imageViewReused(photoToLoad)) {
				return;
			}

			if (bitmap != null) {
				if (photoToLoad.progressBar != null)
					photoToLoad.progressBar.setVisibility(View.GONE);

				if(photoToLoad.isCircle)
				{
						bitmap = transform(bitmap);
				}
				
				photoToLoad.imageView.setImageBitmap(bitmap);

				if (zoom) {
					PhotoViewAttacher attacher = new PhotoViewAttacher(
							photoToLoad.imageView);
					attacher.update();
				}
			} else {
				if (presetDrawable == null) {
					photoToLoad.imageView.setImageDrawable(null);

					if (photoToLoad.progressBar != null)
						photoToLoad.progressBar.setVisibility(View.GONE);
				} else {
					photoToLoad.imageView.setImageDrawable(presetDrawable);
				}
			}
		}
	}

	/**
	 * @param photoToLoad
	 * @return
	 */
	private boolean imageViewReused(PhotoToLoad photoToLoad) {
		String tag = imageViews.get(photoToLoad.imageView);
		String test = TextUtils.isEmpty(photoToLoad.url) ? photoToLoad.file
				.getAbsolutePath() : photoToLoad.url;

		if (tag == null || !tag.equals(test))
			return true;
		return false;
	}

	/**
	 * 
	 */
	public void clearCache() {
		memoryCache.clear();
		fileCache.clear();
	}

	/**
	 * @return
	 */
	public int getLoadingImage() {
		return loadingImageResId;
	}

	/**
	 * @param loadingImageResId
	 */
	protected void setLoadingImage(int loadingImageResId) {
		this.loadingImageResId = loadingImageResId;

		try {
			presetDrawable = a.getResources().getDrawable(loadingImageResId);
		} catch (Exception e) {
		}
	}

	/**
	 *    
	 *   Used in ImageView
	 *
	 * @param imageUrl
	 *            url of the image to download
	 * @return path of the image if it is saved in cache
	 */
	public String getPath(String imageUrl) {
		try 
		{
			return fileCache.getFile(imageUrl).getAbsolutePath();
		} catch (Exception e) {
		}
		return "";
	}
	
	
	 
	/**
	 * 
	 *  Used in HBFBUtility
	 * 
	 * 
	 * @param imageUrl
	 *            url of the image to download
	 * @return file path of the image after saving it to sd card
	 */
	public String downloadImage(String url) 
	{
		try 
		{
			// TODO Main Logic
			
			if (TextUtils.isEmpty(url) || !url.trim().startsWith("http")) {
				return "";
			}
			
			File f = fileCache.getFile(url);

			// from SD cache
			Bitmap b = decodeFile(f);
			if (b != null)
				return f.getAbsolutePath();

			if (!isNetworkAvailable)
				return null;
			try {
				URL imageUrl = new URL(url);
				HttpURLConnection conn = (HttpURLConnection) imageUrl
						.openConnection();
				if (!TextUtils.isEmpty(imageUrl.getUserInfo())
						&& imageUrl.getUserInfo().contains(":")) {
					conn.setRequestProperty("Authorization",
							getCredentials(imageUrl.getUserInfo()));
				}
				conn.setConnectTimeout(0);
				conn.setReadTimeout(0);
				conn.setInstanceFollowRedirects(true);
				InputStream is = conn.getInputStream();
				
				
				String contentType = getContentType(conn.getContentType());
				if(!TextUtils.isEmpty(contentType))
				{	
					String extension = "." + contentType;
					
					String newPath = f.getAbsolutePath() + extension;
					
					deleteFileData(f, url);
					
					
					f = new File(newPath);
					fileCache.saveFileExtension(url, extension);
				}
				
				OutputStream os = new FileOutputStream(f);
				CopyStream(is, os);
				os.close();
				return f.getAbsolutePath();
			} catch (Throwable ex) {
				return null;
			}
		} catch (Exception e) {
		}
		return "";
	}
	
	 public Bitmap transform(Bitmap source) 
	 {
	        Bitmap bitmap = null;
			try {
				int size = Math.min(source.getWidth(), source.getHeight());
 
				int x = (source.getWidth() - size) / 2;
				int y = (source.getHeight() - size) / 2;
 
				Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
				if (squaredBitmap != source) {
				    source.recycle();
				}
 
				bitmap = Bitmap.createBitmap(size, size, source.getConfig());
 
				Canvas canvas = new Canvas(bitmap);
				Paint paint = new Paint();
				BitmapShader shader = new BitmapShader(squaredBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
				paint.setShader(shader);
				paint.setAntiAlias(true);
 
				float r = size/2f;
				canvas.drawCircle(r, r, r, paint);
 
				squaredBitmap.recycle();
			} catch (Exception e) {
			}
	        return bitmap==null?source:bitmap;
	    }
	/**
	 * @return cache directory where the cache images are stored
	 */
	public File getCacheDir() {
		return fileCache.getCacheDir();
	}

	/**
	 * @param zoom
	 *            true if you want to enable zoom functionality
	 */
	public void setZoomEnabled(boolean zoom) {
		this.zoom = zoom;
	}

	public int getReqWidth() {
		return reqWidth;
	}

	public void setReqWidth(int reqWidth) {
		this.reqWidth = reqWidth;
	}

	public int getReqHeight() {
		return reqHeight;
	}

	public void setReqHeight(int reqHeight) {
		this.reqHeight = reqHeight;
	}
	
	
	private void deleteFileData(File f,String url)
	{
		f.delete();
		
		try 
		{
			String filename = String.valueOf(url.hashCode()) + FileCache.LAST_MODIFIED_TIME_STAMP + ".txt";
			File extensionFile = new File(fileCache.getCacheDir(), filename);
			extensionFile.delete();
			
		} catch (Exception e1) {
		}
		
		try 
		{
			String filename = String.valueOf(url.hashCode()) + FileCache.FILE_TIME_STAMP + ".txt";
			File extensionFile = new File(fileCache.getCacheDir(), filename);
			extensionFile.delete();
			
		} catch (Exception e2) {
		}
	}
}
