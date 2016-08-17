package it.configure.imageloader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.content.Context;
import android.text.TextUtils;

final class FileCache {

	private File cacheDir;
	public FileCache(Context context) {
		// Find the dir to save cached images

		
		cacheDir = android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED) ? android.os.Environment
				.getExternalStorageDirectory() : context.getCacheDir();

		cacheDir = new File(cacheDir, "HB"
				+ context.getPackageName().replace(".", "_"));

		if (!cacheDir.exists()) {
			cacheDir.mkdirs();
			cacheDir.setLastModified(System.currentTimeMillis());
		}
	}
	
	public long getLatModifiedDate(String url)
	{
		long extension = 0L;
		
		try
		{
			String filename = String.valueOf(url.hashCode()) + LAST_MODIFIED_TIME_STAMP + ".txt";
			File extensionFile = new File(cacheDir, filename);
			if(extensionFile.exists())
			{
				String lastModifiedDate  = readDataFromFile(extensionFile.getAbsolutePath());
				if(!TextUtils.isEmpty(lastModifiedDate))
					return Long.parseLong(lastModifiedDate);
			}
		}
		catch(Exception e)
		{
		   	
		}
		return extension;
	}

	public File getFile(String url) {
		// I identify images by hashcode. Not a perfect solution, good for the
		// demo.

		String filename = String.valueOf(url.hashCode())+ getFileExtension(url);       //+ ".jpg";
		// Another possible solution (thanks to grantland)
		// String filename = URLEncoder.encode(url);
		File f = new File(cacheDir, filename);
		return f;

	}
	
	/**
	 * 
	 * eg : ".png", ".jpeg" etc 
	 * 
	 * @param url
	 * @return extension with dot(.) as a prefix;
	 */
	public String getFileExtension(String url)
	{
		String extension = "";
		
		try
		{
			String filename = String.valueOf(url.hashCode()) + FILE_TIME_STAMP + ".txt";
			File extensionFile = new File(cacheDir, filename);
			if(extensionFile.exists())
			{
				extension =  readDataFromFile(extensionFile.getAbsolutePath());
				if(TextUtils.isEmpty(extension) || extension.equalsIgnoreCase("null"))
					extension = "";
			}
		}
		catch(Exception e)
		{
		   	
		}
		return extension;
	}
	
	/**
	 * 
	 * eg : ".png", ".jpeg" etc
	 * 
	 *  File extension will be saved with dot(.) as a prefix;
	 * 
	 * @param url
	 * @param extension
	 */
	public void saveFileExtension(String url,String extension)
	{
		try
		{
			if(!TextUtils.isEmpty(extension))
			{	
			 String filename = String.valueOf(url.hashCode()) +  FILE_TIME_STAMP + ".txt";
			 File extensionFile = new File(cacheDir, filename);
			 writeDataToFile(extensionFile.getAbsolutePath(), extension);
		    }
		}
		catch(Exception e)
		{
		   	
		}
	}
	
	public void saveLastModifiedDate(String url,long latestModifiedDate)
	{
		try
		{
			 String filename = String.valueOf(url.hashCode()) +  LAST_MODIFIED_TIME_STAMP + ".txt";
			 File extensionFile = new File(cacheDir, filename);
			 writeDataToFile(extensionFile.getAbsolutePath(), String.valueOf(latestModifiedDate));
		}
		catch(Exception e)
		{
		   	
		}
	}
	
	
	/**
	 * writes data into a file
	 * 
	 * @param filePath
	 *            path of file
	 * @param fileData
	 *            data which you want to insert into the above file
	 * @return <b>true</b> if data was written to the file<br/>
	 *         <b>false</b> if data was not written to the file<br/>
	 * <br/>
	 * 
	 *         <b>Note:</b> This requires permission
	 *         <b>"android.permission.WRITE_EXTERNAL_STORAGE"</b> This will
	 *         override the data which exists in the file
	 */
	public boolean writeDataToFile(String filePath, String fileData) {
		boolean success = false;

		if (!TextUtils.isEmpty(fileData) && !TextUtils.isEmpty(filePath)) {
			filePath = filePath.trim();
			fileData = fileData.trim();

			if (!TextUtils.isEmpty(fileData) && !TextUtils.isEmpty(filePath) && 
					!fileData.equalsIgnoreCase("null")) {
					try {
						FileOutputStream stream = new FileOutputStream(filePath);
						OutputStreamWriter out = new OutputStreamWriter(stream);
						out.write(fileData);
						out.close();
						stream.flush();
						stream.close();
						success = true;
					} catch (Exception e) {
						e.printStackTrace();
					}
			}
		}
		return success;
	}
	
	/**
	 * 
	 * reads the data from a file returns it as string
	 * 
	 * @param filePath
	 *            path of the file in most likely a text file
	 * 
	 * @return data of the file as String
	 */
	public String readDataFromFile(String filePath) {
		String data = "";
		try {
			File file = new File(filePath);
			InputStream inputStream = new FileInputStream(file);
			InputStreamReader inputStreamReader = new InputStreamReader(
					inputStream);
			BufferedReader bufferedReader = new BufferedReader(
					inputStreamReader);

			String line = null;
			StringBuffer stringBuffer = new StringBuffer();
			while ((line = bufferedReader.readLine()) != null) {
				stringBuffer.append(line);
			}
			bufferedReader.close();
			data = stringBuffer.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}
		
	public static final String FILE_TIME_STAMP = "_HB_FILE_TYPE"; 
	public static final String LAST_MODIFIED_TIME_STAMP = "_HB_LAST_MODIFIED_FILE_TYPE"; 
	

	public File getCacheDir() {
		return cacheDir;
	}

	public void clear() {
		File[] files = cacheDir.listFiles();
		if (files == null)
			return;
		for (File f : files)
			f.delete();
	}
}