package cn.sdu.online.findteam.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import cn.sdu.online.findteam.aliwukong.avatar.AvatarShaper;

/**
 * Created by wn on 2015/8/13.
 */
public class ThumbnailUtil {
    private static final int WIFI_WIDTH = 800;
    private static final int WIFI_HEIGHT = 1200;
    public static final int TARGET_SIZE = 640;
    public static final int TARGET_SIZE_MINI = 380;
    public static final int WIFI_BITMAP_LENGTH = 110;
    public static final int COMPRESS_MIN_SIZE = 1024000;//1M大小
    private static final String TAG = "ThumbnailUtil";

    /**
     * 普通图片压缩采用等比压缩方式，将图片宽度压缩为640像素，采用75quality jpg压缩格式。
     * 长图（宽高或高宽比大于等于3，并小边大于640）时，将不压缩图片尺寸，而采用35的quality进行压缩。
     * @param context
     * @param path
     * @return
     */
    public static String compressAndRotateToThumbFile(Context context,String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }

        //todo:读取文件信息判断类型，过滤gif
        String fileType = path.substring(path.lastIndexOf("."));
        String nameString = UUID.randomUUID().toString() + fileType;
        String pathString = FileUtil.getCacheDir(context).getAbsolutePath();
        String fullPath = pathString+ File.separator+nameString;

        long time = System.nanoTime();
        Bitmap bm = compressFileToBitmapThumb(path);
        Log.d(TAG, "compressFileToBitmapThumb : " + (System.nanoTime() - time) / 1000000);
        if (bm == null)
            return null;

        int degree = getOrientation(context, path, null);
        Bitmap rotate = rotateBitmap(bm, degree);
        if(rotate == null)
            return null;
        int width = rotate.getWidth();
        int height = rotate.getHeight();
        int smallSide = Math.min(width,height);

        time = System.nanoTime();
        if((smallSide > TARGET_SIZE) && (height / width >= 3 || width/height >= 3)){
            writeBitmap(pathString, nameString, rotate, 35);
            Log.d("TestPicSize","35%   rotate size= "+ getBitmapsize(rotate));
        } else if ( getBitmapsize(rotate) > COMPRESS_MIN_SIZE &&  (smallSide <= TARGET_SIZE_MINI)){//大于1M,且小图模式才压缩60
            writeBitmap(pathString, nameString, rotate, 60);
            Log.d("TestPicSize","60% rotate size= "+getBitmapsize(rotate));
        } else {
            writeBitmap(pathString, nameString, rotate, 75);
            Log.d("TestPicSize","75% rotate size= "+getBitmapsize(rotate));
        }

        Log.d(TAG, "writeBitmap : " + (System.nanoTime() - time) / 1000000);
        return fullPath;
    }

    /**
     * 普通图片压缩采用等比压缩方式，将图片宽度压缩为640像素
     * 长图（宽高或高宽比大于等于3，并小边大于640）时，将不压缩图片尺寸
     * @param filePath
     * @return
     */
    public static Bitmap compressFileToBitmapThumb(String filePath){
        if (TextUtils.isEmpty(filePath)) {
            return null;
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        try {
            BitmapFactory.decodeFile(filePath, options);
        } catch (Exception e) {
            Log.e(TAG, "" + e.getMessage());
            return null;
        }

        if (options.mCancel || options.outWidth == -1
                || options.outHeight == -1) {
            return null;
        }

        int width = options.outWidth;
        int height = options.outHeight;
        int small = Math.min(width,height);

        options.inJustDecodeBounds = false;
        options.inDither = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        //长图
        if((small > TARGET_SIZE) && (height / width >= 3 || width/height >= 3)){
            try {
                options.inSampleSize = 1;
                Bitmap bm = BitmapFactory.decodeFile(filePath,options);
                return bm;
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            boolean verticalPic = true;
            if (options.outHeight < options.outWidth) {
                verticalPic = false;
            }

            if(small > TARGET_SIZE){
                //计算采样率
                options.inSampleSize = (small/TARGET_SIZE/2)*2;
                if(options.inSampleSize != 0) {
                    //采样后的图片大小
                    small = small / options.inSampleSize;
                }
            }

            try {
                long time = System.nanoTime();
                Bitmap bm = BitmapFactory.decodeFile(filePath,options);

                int w = bm.getWidth();
                int h = bm.getHeight();

                if(w > TARGET_SIZE) {
                    if (verticalPic) {
                        int newHeight = (int)((float) TARGET_SIZE / w * h);
                        bm = Bitmap.createScaledBitmap(bm, TARGET_SIZE, newHeight, false);
                    } else {
                        int newWidth = (int)((float) TARGET_SIZE / h * w);
                        bm = Bitmap.createScaledBitmap(bm, newWidth, TARGET_SIZE, false);
                    }
                }
                bm.setDensity(1);
                Log.e(TAG,"decodeFile : "+(System.nanoTime()-time)/1000000);
                return bm;
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }

        return null;
    }

    /**
     * 获取照片的角度 两种方式:1.根据绝对路径或根据Uri
     *
     * @param imagePath
     *            照片的路径
     * @param context
     * @param photoUri
     *
     * */
    public static int getOrientation(Context context, String imagePath,
                                     Uri photoUri) {
        int nOrientation = 0;
        if (!TextUtils.isEmpty(imagePath)) {
            try {
                ExifInterface exif = new ExifInterface(imagePath);
                nOrientation = exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION, 1);
                switch (nOrientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        return 90;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        return 270;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        return 180;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (context != null && photoUri != null) {
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver()
                        .query(photoUri,
                                new String[] { MediaStore.Images.ImageColumns.ORIENTATION },
                                null, null, null);

                if (cursor == null || cursor.getCount() != 1) {
                    return 0;
                }

                cursor.moveToFirst();
                int ret = cursor.getInt(0);
                return ret;
            } catch (Throwable t) {
                t.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                    cursor = null;
                }
            }
        }
        return 0;
    }

    public static Bitmap rotateBitmap(Bitmap b, int degrees) {
        if (degrees != 0 && b != null) {
            Matrix m = new Matrix();
            m.setRotate(degrees, b.getWidth() / 2, b.getHeight() / 2);
            try {
                Bitmap b2 = Bitmap.createBitmap(b, 0, 0, b.getWidth(),
                        b.getHeight(), m, true);
                // b.recycle();
                // b = null;
                return b2;
            } catch (Exception e) {
                e.printStackTrace();
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }
        return b;
    }

    public static boolean writeBitmap(String path, String name,Bitmap bitmap,
                                      int compressRate) {
        if (null == bitmap || TextUtils.isEmpty(path) || TextUtils.isEmpty(name))
            return false;
        boolean bPng = false;
        if (name.endsWith(".png")) {
            bPng = true;
        }

        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }

        File _file = new File(path,name);
        boolean bNew = true;
        if (_file.exists()) {
            bNew = false;
            _file = new File(path,name + ".tmp");
            _file.delete();
        }
        FileOutputStream fos = null;
        boolean bOK = false;
        try {
            fos = new FileOutputStream(_file);
            if (bPng) {
                bitmap.compress(Bitmap.CompressFormat.PNG, compressRate, fos);
            } else {
                bitmap.compress(Bitmap.CompressFormat.JPEG, compressRate, fos);
            }

            bOK = true;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                    if (bNew == false && bOK) {
                        _file.renameTo(new File(path,name));
                    }
                } catch (IOException e) {
                }
            }
        }
        return false;
    }

    public static int getBitmapsize(Bitmap bitmap){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            return bitmap.getByteCount();
        }
        // Pre HC-MR1
        return bitmap.getRowBytes() * bitmap.getHeight();

    }

    /**
     * 判断文件是否已经存在
     *
     * @param file
     * @return
     */
    public static boolean isExist(String file) {
        if (TextUtils.isEmpty(file)) {
            return false;
        }
        if (new File(file).exists()) {
            return true;
        }
        return false;
    }

    public static String copyImageToThumb(Context context,String srcFile){
        String destName = UUID.randomUUID().toString() + ".jpg";
        String destDir = FileUtil.getCacheDir(context).getAbsolutePath();
        String destFile = destDir + File.separator +destName;

        File fileDir = new File(destDir);
        if (!fileDir.exists()) {
            fileDir.mkdir();
        }

        try{
            InputStream streamFrom = new FileInputStream(srcFile);
            OutputStream streamTo = new FileOutputStream(destFile);
            byte buffer[]=new byte[1024];
            int len;
            while ((len= streamFrom.read(buffer)) > 0){
                streamTo.write(buffer, 0, len);
            }
            streamFrom.close();
            streamTo.close();
            return destFile;
        } catch(Exception ex){
            return null;
        }
    }

    /**
     * 判定SDCard是否可读写
     *
     * @return
     */
    public static boolean isCanUseSDCard() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static byte[] Bitmap2Bytes(Bitmap bm){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    public static byte[] getBytesFromFile(File file){
        if (file== null){
            return null;
        }

        try{
            FileInputStream stream = new FileInputStream(file);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int n;

            while ((n = stream.read(b)) != -1)
                out.write(b, 0, n);

            stream.close();
            out.close();
            return out.toByteArray();
        } catch (IOException e){
            e.printStackTrace();
        }

        return null;
    }

    public Bitmap combineBitmaps( AvatarShaper shaper,int columns,Bitmap... bitmaps) {
        if (columns <= 0 || bitmaps == null || bitmaps.length == 0) {
            throw new IllegalArgumentException(
                    "Wrong parameters: columns must > 0 and bitmaps.length must > 0.");
        }

        int widthPerImg = 21;
        int heightPerImg = 21;

        for (Bitmap b : bitmaps) {
            widthPerImg = widthPerImg > b.getWidth() ? widthPerImg : b.getWidth();
            heightPerImg = heightPerImg > b.getHeight() ? heightPerImg : b.getHeight();
        }

        int rows = 0;
        if (columns >= bitmaps.length) {
            rows = 1;
            columns = bitmaps.length;
        } else {
            rows = bitmaps.length % columns == 0 ? bitmaps.length / columns
                    : bitmaps.length / columns + 1;
        }

        Bitmap newBitmap = Bitmap.createBitmap(columns * widthPerImg, rows
                * heightPerImg, Bitmap.Config.ARGB_8888);

        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < columns; y++) {
                int index = x * columns + y;
                if (index >= bitmaps.length)
                    break;

                newBitmap = mixtureBitmap(newBitmap,shaper.ShapeAvatar( bitmaps[index]),
                        new PointF(y * widthPerImg, x * heightPerImg));
            }
        }
        return newBitmap;
    }

    public Bitmap mixtureBitmap(Bitmap first, Bitmap second,PointF fromPoint) {
        if (first == null || second == null || fromPoint == null) {
            return null;
        }
        Bitmap newBitmap = Bitmap.createBitmap(first.getWidth(),
                first.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas cv = new Canvas(newBitmap);
        cv.drawBitmap(first, 0, 0, null);
        cv.drawBitmap(second, fromPoint.x, fromPoint.y, null);
        cv.save(Canvas.ALL_SAVE_FLAG);
        cv.restore();
        return newBitmap;
    }


    public static Bitmap defaultAvatar(){
        int d = 60;

        //new一个Canvas在bitmap上面画图
        Bitmap output = Bitmap.createBitmap(d,d, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        //设置边缘光滑，去掉锯齿,相交模式为取交集
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.GRAY);
        paint.setStyle(Paint.Style.FILL);

        //宽高相等，即正方形
        RectF rect = new RectF(0, 0, d, d);
        canvas.drawRect(rect, paint);

        return output;
    }
}

