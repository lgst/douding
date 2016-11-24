package com.ddgj.dd.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
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
    private String mPrivateCache;

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



	/**
	 * 图片压缩
	 * @param fileUri
     * @return
     */
	public static File scal(Uri fileUri, File dir){

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
			outputFile = new File(createImageFile(dir).getPath());
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(outputFile);
                //旋转图片
                Bitmap bitmap1 = rotateBitmapByDegree(bitmap, getBitmapDegree(path));
                bitmap1.compress(CompressFormat.JPEG, 100, fos);
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
				outputFile = new File(createImageFile(dir).getPath());
				copyFileUsingFileChannels(tempFile, outputFile);
			}

		}
		return outputFile;

    }

    public static Uri createImageFile(File dir) {
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
					dir      /* directory */
			);
		} catch (IOException e) {
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
				e.printStackTrace();
			}
		} finally {
			try {
				inputChannel.close();
				outputChannel.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

    /**
     * 将图片按照某个角度进行旋转
     *
     * @param bm
     *            需要旋转的图片
     * @param degree
     *            旋转角度
     * @return 旋转后的图片
     */
    public static Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
        Bitmap returnBm = null;

        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
        }
        if (returnBm == null) {
            returnBm = bm;
        }
        if (bm != returnBm) {
            bm.recycle();
        }
        return returnBm;
    }

    /**
     * 读取图片的旋转的角度
     *
     * @param path
     *            图片绝对路径
     * @return 图片的旋转角度
     */
    public static int getBitmapDegree(String path) {
        int degree = 0;
        try {
            // 从指定路径下读取图片，并获取其EXIF信息
            ExifInterface exifInterface = new ExifInterface(path);
            // 获取图片的旋转信息
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
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
        mPrivateCache = mContext.getCacheDir().getAbsolutePath() + "/";
//        Log.i("lgst", mPrivateCache);
    }

    /**获取缓存大小*/
    public long getCacheSize() {
        File imageCache = new File(mImageCache);
        File tempCache = new File(mTempCache);
        File privateCache = new File(mPrivateCache);
        long size = 0;
        if (imageCache.exists()) {
            size += getFolderSize(imageCache);
        }
        if (tempCache.exists()) {
            size += getFolderSize(tempCache);
        }
        if (privateCache.exists())
            size += getFolderSize(privateCache);
        return size;
    }

    public void clearCache() {
        File file1 = new File(mImageCache);
        File file2 = new File(mTempCache);
        File file3 = new File(mPrivateCache);
        File[] files1 = file1.listFiles();
        File[] files2 = file2.listFiles();
        File[] files3 = file3.listFiles();
        for (int i = 0; i < files1.length; i++) {
            deleteFile(files1[i]);
        }
        for (int i = 0; i < files2.length; i++) {
            deleteFile(files2[i]);
        }
        for (int i = 0; i < files3.length; i++) {
            deleteFile(files3[i]);
        }
    }

    /**
     * 获取文件夹大小
     *
     * @param file File实例
     * @return long
     */
    public static long getFolderSize(java.io.File file) {

        long size = 0;
        try {
            java.io.File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                if (fileList[i].isDirectory()) {
                    size = size + getFolderSize(fileList[i]);

                } else {
                    size = size + fileList[i].length();

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //return size/1048576;
        return size;
    }

    /**
     * 文件删除
     */
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
    public String getmTempLogCache() {
        return mTempCache+"log/";
    }


    /**
     * 写入缓存，json数据，文件名是实体类名
     */
    public static void saveJsonToCache(String json, String fileName) {
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
    public static String readJsonFromCache(String fileName) {
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
