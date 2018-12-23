package com.earth.OsToolkit.Working;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import java.io.*;


public class FileWorking {
	public static boolean copyAssets2Cache(Context context, String fileName) {
		File file = new File(context.getCacheDir()
				                     .getAbsolutePath()
				                     + File.separator
				                     + fileName);
		try {
			InputStream inputStream = context.getAssets().open(fileName);
			if (!file.exists() || file.length() == 0) {
				FileOutputStream fileOutputStream = new FileOutputStream(file);
				byte[] buffer = new byte[1024];

				int len;
				while ((len = inputStream.read(buffer)) != -1) {
					fileOutputStream.write(buffer, 0, len);
				}

				fileOutputStream.flush();
				inputStream.close();
				fileOutputStream.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (file.exists()) {
			return true;
		} else {
			return false;
		}
	}

	public static String readFile(Context context,String fileName) {
		try {
			FileInputStream fileInputStream = new FileInputStream(new File(fileName));
			InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream,"utf-8");
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

			String line;
			line = bufferedReader.readLine();
			Log.i("line_file",line);

			fileInputStream.close();
			inputStreamReader.close();
			bufferedReader.close();

			return line;
		} catch (Exception e) {
			e.printStackTrace();
			return "Fail";
		}
	}

	public static boolean setScriptPermission(Context context, String fileName) {
		String path = context.getCacheDir().getAbsolutePath() + File.separator + fileName;
		File file = new File(path);

		file.setReadable(true);
		file.setWritable(true);
		file.setExecutable(true);

		return file.canExecute();
	}

	public static boolean removeAllFile(Activity activity) {
		File[] files = new File(activity.getCacheDir().getAbsolutePath()).listFiles();

		for (int i = 0; i < files.length; i++) {
			files[i].delete();
		}

		if (new File(activity.getCacheDir().getAbsolutePath()).listFiles().length == 0) {
			return true;
		} else {
			return false;
		}
	}
}