package cn.sdu.online.findteam.aliwukong.avatar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;

import com.alibaba.doraemon.Doraemon;
import com.alibaba.doraemon.image.ImageDecoder;
import com.alibaba.doraemon.image.ImageInputStream;

import java.io.InputStream;

import cn.sdu.online.findteam.util.AndTools;

/**
 * Created by wn on 2015/8/14.
 */
public class AvatarImageDecoder implements ImageDecoder {
    public static final int SELFDECODERTAG = 2015070113;

    // TODO WKNEW
    @Override
    public BitmapDrawable decode(ImageInputStream imageInputStream, String url, int displayMode) {
        //(imageInputStream, imageInputStream.si)
        Bitmap bitmap =  decode(imageInputStream);
        return new BitmapDrawable(Doraemon.getContext().getResources(), bitmap);
    }

    public static class DecodeSize {
        public static final int SizeForAvatar = 120;
        public static final int SizeForAlbum = 240;
        public static final int SizeForChat = 450;
        public static final int MinSizeForChat = 80;
    }

    public enum DecodeCondition {
        Chat,
        Album
    }

    private Context mContext;
    private AvatarShaper mShaper;
    private int mMaxBitmapSize;
    private Mode mode;
    private Integer width; // 仅用于聊天图片的倒角

    public enum Mode {
        Right,
        Left,
        Normal
    }

//    public AvatarImageDecoder(Context context,DecodeCondition decodeCondition, Mode mode) {
//        if (decodeCondition.equals(DecodeCondition.Album)) {
//            this.mMaxBitmapSize = DecodeSize.SizeForAlbum;
//        } else {
//            this.mMaxBitmapSize = DecodeSize.SizeForChat;
//        }
//        this.mode = mode;
//        this.mContext = context;
//    }

    public AvatarImageDecoder(Context context,AvatarShaper shaper){
        this.mMaxBitmapSize = DecodeSize.SizeForAvatar;
        this.mode = Mode.Normal;
        this.mContext = context;
        this.mShaper = shaper;
    }

    private Bitmap decode(InputStream stream) {
        Bitmap ret = null;

        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 1;
            options.inJustDecodeBounds = true;

            //stream.mark(length);
            BitmapFactory.decodeStream(stream, null, options);
            stream.reset();

            int width = options.outWidth;
            int height = options.outHeight;

            int targetSize = Math.min(Math.min(width, height), mMaxBitmapSize);

            options.inJustDecodeBounds = false;
            options.inSampleSize = computeSampleSize(options, targetSize, -1);

            ret = BitmapFactory.decodeStream(stream, null, options);

        } catch (Exception e) {
            e.printStackTrace();
        }
//        return maskImage(ret, mode);
        return mShaper.ShapeAvatar(ret);
    }

    // TODO WKNEW
//    @Override
//    public BitmapDrawable decode(RequestInputStream stream, Resources resources, String url, String fileSuffix, String urlKey) {
//        Bitmap bm =  decode(stream, stream.length());
//        Bitmap bmRotated = null;
//        BitmapDrawable ret = null;
//        if (!url.startsWith("http")){
//            try {
//                ExifInterface exif = new ExifInterface(url);
//                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
//                bmRotated = AndTools.rotateBitmap(bm, orientation);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            if(null != bmRotated){
//                ret = new BitmapDrawable(resources, bmRotated);
//            }
//        } else {
//            if (null != bm) {
//                ret = new BitmapDrawable(resources, bm);
//            }
//        }
//
//        return ret;
//    }

    public static int computeSampleSize(BitmapFactory.Options options,
                                        int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels);

        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }

        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options,
                                                int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
                .sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math
                .floor(w / minSideLength), Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            return lowerBound;
        }

        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    public Bitmap maskImage(Bitmap original, Mode mode) {
        if (mode.equals(Mode.Normal)) {
            return original;
        }
        if (original == null)
            return null;

        Bitmap result = Bitmap.createBitmap(original.getWidth(), original.getHeight(),
                Bitmap.Config.ARGB_8888);
        this.width = result.getWidth();
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(android.graphics.Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        RectF rectF;
        Path pathRight = new Path();
        // 右下角开始画
        pathRight.moveTo(calc(result.getWidth()), result.getHeight());
        // 到左边
        pathRight.lineTo(calc(result.getWidth() - dp2px(CORNERWIDTHDP)), result.getHeight());
        // 从左下圆角转上
        rectF = new RectF(calf(result.getWidth() - dp2px(CORNERWIDTHDP) - dp2px(RADIUS),
                result.getHeight()
                        - dp2px(RADIUS) * 2,
                result.getWidth() - dp2px(CORNERWIDTHDP) + dp2px(RADIUS),
                result.getHeight()));
        pathRight.arcTo(rectF, cali(90), cald(-90));
        // 往上走
        pathRight.lineTo(calc(result.getWidth() - dp2px(CORNERWIDTHDP) + dp2px(RADIUS)),
                result.getHeight()
                        - dp2px(RADIUS) - dp2px(INNERHIGHT));

        // 拉出倒角
        pathRight.lineTo(calc(result.getWidth()), result.getHeight()
                - dp2px(RADIUS) - dp2px(INNERHIGHT) - dp2px(CORNERHEIGHTDP));

        // 拉入倒角
        pathRight.lineTo(calc(result.getWidth() - dp2px(CORNERWIDTHDP) + dp2px(RADIUS)),
                result.getHeight()
                        - dp2px(RADIUS) - dp2px(INNERHIGHT) - 2 * dp2px(CORNERHEIGHTDP));

        // 往上
        pathRight.lineTo(calc(result.getWidth() - dp2px(CORNERWIDTHDP) + dp2px(RADIUS)),
                dp2px(RADIUS));

        // 圆角往左上转
        rectF = new RectF(calf(result.getWidth() - dp2px(CORNERWIDTHDP) - dp2px(RADIUS),
                0,
                result.getWidth() - dp2px(CORNERWIDTHDP) + dp2px(RADIUS),
                dp2px(RADIUS) * 2));
        pathRight.arcTo(rectF, cali(0), cald(-90));

        // 回右上
        pathRight.lineTo(calc(result.getWidth()), 0);

        pathRight.close();

        Path pathLeft = new Path();
        // 左上出发
        pathLeft.moveTo(calc(0), 0);

        // 转左上
        pathLeft.lineTo(calc(dp2px(RADIUS)), 0);

        // 圆角往左下转
        rectF = new RectF(calf(0, 0, dp2px(RADIUS) * 2, dp2px(RADIUS) * 2));
        pathLeft.arcTo(rectF, cali(270), cald(-90));

        // 转左下
        pathLeft.lineTo(calc(0), result.getHeight() - dp2px(RADIUS));

        // 圆角往右下转
        rectF = new RectF(calf(0, result.getHeight() - 2 * dp2px(RADIUS), 2 * dp2px(RADIUS),
                result.getHeight()));
        pathLeft.arcTo(rectF, cali(180), cald(-90));

        // 回左下
        pathLeft.lineTo(calc(0), result.getHeight());

        pathLeft.close();

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));

        canvas.drawBitmap(original, 0, 0, null);

        canvas.drawPath(pathLeft, paint);
        canvas.drawPath(pathRight, paint);

        paint.setXfermode(null);
        return result;
    }

    private RectF calf(int x1, int y1, int x2, int y2) {
        if (mode.equals(Mode.Right)) {
            return new RectF(x1, y1, x2, y2);
        } else {
            return new RectF(width - x2, y1, width - x1, y2);
        }
    }

    private float cali(int i) {
        if (mode.equals(Mode.Right)) {
            return i;
        } else {
            if (i == 180 || i == 0) {
                return 180 - i;
            } else {
                return i;
            }
        }
    }

    private float cald(int i) {
        if (mode.equals(Mode.Right)) {
            return i;
        } else {
            return -i;
        }
    }

    private int calc(int w) {
        if (mode.equals(Mode.Right)) {
            return w;
        } else {
            return width - w;
        }
    }

    public static int RADIUS = 4; // 圆角
    public static int INNERHIGHT = 10; // 上拉高度
    public static int CORNERWIDTHDP = 5 + RADIUS;
    public static int CORNERHEIGHTDP = 5;

    public int dp2px(float dpValue) {
        return AndTools.dp2px(mContext, dpValue);
    }
}
