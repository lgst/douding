package com.ddgj.dd.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FileUtil {
    private Context mContext;
    /**
     * 临时缓存
     */
    private String mTempCache;
    /**
     * 图片缓存
     */
    private String mImageCache;

    private static FileUtil instance;

    private FileUtil() {
    }

    public static FileUtil getInstance() {
        if (instance == null) {
            instance = new FileUtil();
        }
        return instance;
    }


    /**
     * 将Bitmap 图片保存到本地路径，并返回路径
     *
     * @param c
     * @param fileName 文件名称
     * @param bitmap   图片
     * @return
     */
    public static String saveFile(Context c, String fileName, Bitmap bitmap) {
        return saveFile(c, "", fileName, bitmap);
    }

    public static String saveFile(Context c, String filePath, String fileName, Bitmap bitmap) {
        byte[] bytes = bitmapToBytes(bitmap);
        return saveFile(c, filePath, fileName, bytes);
    }

    public static byte[] bitmapToBytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

	public static String saveFile(Context c, String filePath, String fileName, byte[] bytes) {
		String fileFullName = "";
		FileOutputStream fos = null;
		String dateFolder = new SimpleDateFormat("yyyyMMdd", Locale.CHINA)
				.format(new Date());
		try {
			String suffix = "";
			if (filePath == null || filePath.trim().length() == 0) {
				filePath = Environment.getExternalStorageDirectory() + "/Fun/" + dateFolder + "/";
			}
			File file = new File(filePath);
			if (!file.exists()) {
				file.mkdirs();
			}
			File fullFile = new File(filePath, fileName + suffix);
			fileFullName = fullFile.getPath();
			fos = new FileOutputStream(new File(filePath, fileName + suffix));
			fos.write(bytes);
		} catch (Exception e) {
			fileFullName = "";
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					fileFullName = "";
				}
			}
		}
		return fileFullName;
	}

	/**
	 * 图片压缩
	 * @param fileUri
     * @return
     */
	public static File scal(Uri fileUri){
		String path = fileUri.getPath();
		File outputFile = new File(path);
		long fileSize = outputFile.length();
		final long fileMaxSize = 200 * 1024;
		if (fileSize >= fileMaxSize) {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(path, options);
			int height = options.outHeight;
			int width = options.outWidth;
			double scale = Math.sqrt((float) fileSize / fileMaxSize);
			options.outHeight = (int) (height / scale);
			options.outWidth = (int) (width / scale);
			options.inSampleSize = (int) (scale + 0.5);
			options.inJustDecodeBounds = false;

			Bitmap bitmap = BitmapFactory.decodeFile(path, options);
			outputFile = new File(createImageFile().getPath());
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(outputFile);
				bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fos);
				fos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.d("", "sss ok " + outputFile.length());
			if (!bitmap.isRecycled()) {
				bitmap.recycle();
			}else{
				File tempFile = outputFile;
				outputFile = new File(createImageFile().getPath());
				copyFileUsingFileChannels(tempFile, outputFile);
			}

		}
		return outputFile;

	}
	public static Uri createImageFile(){
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = "JPEG_" + timeStamp + "_";
		File storageDir = Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_PICTURES);
		File image = null;
		try {
			image = File.createTempFile(
					imageFileName,  /* prefix */
					".jpg",         /* suffix */
					storageDir      /* directory */
			);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Save a file: path for use with ACTION_VIEW intents
		return Uri.fromFile(image);
	}
	public static void copyFileUsingFileChannels(File source, File dest){
		FileChannel inputChannel = null;
		FileChannel outputChannel = null;
		try {
			try {
				inputChannel = new FileInputStream(source).getChannel();
				outputChannel = new FileOutputStream(dest).getChannel();
				outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} finally {
			try {
				inputChannel.close();
				outputChannel.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


    public static String saveFile(Context c, String filePath, String fileName, byte[] bytes) {
        String fileFullName = "";
        FileOutputStream fos = null;
        String dateFolder = new SimpleDateFormat("yyyyMMdd", Locale.CHINA)
                .format(new Date());
        try {
            String suffix = "";
            if (filePath == null || filePath.trim().length() == 0) {
                filePath = Environment.getExternalStorageDirectory() + "/Fun/" + dateFolder + "/";
            }
            File file = new File(filePath);
            if (!file.exists()) {
                file.mkdirs();
            }
            File fullFile = new File(filePath, fileName + suffix);
            fileFullName = fullFile.getPath();
            fos = new FileOutputStream(new File(filePath, fileName + suffix));
            fos.write(bytes);
        } catch (Exception e) {
            fileFullName = "";
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    fileFullName = "";
                }
            }
        }
        return fileFullName;
    }

    /**
     * lgst
     * 初始化
     */
    public void init(Context mContext) {
        if (this.mContext != null) {
            return;
        }
        this.mContext = mContext;
        mImageCache = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/";
        mTempCache = mContext.getExternalCacheDir().getAbsolutePath() + "/";
    }

    public void deleteUserIcon() {
        deleteFile(new File(mImageCache + "/" + "user_icon"));
    }

    /**获取缓存大小*/
    public long getCacheSize() {
        File imageCache = new File(mImageCache);
        File tempCache = new File(mTempCache);
        long size = 0;
        if (imageCache.exists()) {
            size += imageCache.length();
        }
        if (tempCache.exists()) {
            size += tempCache.length();
        }
        return size;
    }

    public void clearCache() {
        deleteFile(new File(mImageCache));
        deleteFile(new File(mTempCache));
    }

    /**文件删除*/
    public static void deleteFile(File file) {
        if (!file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                deleteFile(files[i]);
            }
        } else {
            file.delete();
            return;
        }
        file.delete();
    }

    /**
     * 文件复制
     */
    public static void copyFile(File source, File target) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = new FileInputStream(source);
            outputStream = new FileOutputStream(target);
            int len = 0;
            byte[] buf = new byte[1024 * 1024 * 3];//缓冲区 3MB
            while ((len = inputStream.read(buf)) > 0) {
                outputStream.write(buf, 0, len);
            }
            outputStream.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null)
                    outputStream.close();
                if (inputStream != null)
                    inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**图片缓存路径*/
    public String getmImageCache() {
        return mImageCache;
    }

    /*临时缓存路径*/
    public String getmTempCache() {
        return mTempCache;
    }


    /**
     * 写入缓存，json数据
     */
    public static void saveJsonToCacha(String json, String fileName) {
        OutputStream os = null;
        try {//缓存
            os = new FileOutputStream(FileUtil.getInstance().getmTempCache() + fileName);
            os.write(json.getBytes());
            os.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null)
                    os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 读取缓存，json数据
     */
    public static String readJsonFromCacha(String fileName) {
        FileReader reader = null;
        try {
            reader = new FileReader(FileUtil.getInstance().getmTempCache() + fileName);
            StringBuilder sb = new StringBuilder();
            int len = 0;
            char[] buf = new char[10000];//1w字符
            if ((len = reader.read(buf)) > 0) {
                sb.append(buf);
            }
            return sb.toString();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

}
