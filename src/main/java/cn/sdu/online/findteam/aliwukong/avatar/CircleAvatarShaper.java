package cn.sdu.online.findteam.aliwukong.avatar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;

import cn.sdu.online.findteam.aliwukong.avatar.AvatarShaper;
import cn.sdu.online.findteam.util.AndTools;

public class CircleAvatarShaper implements AvatarShaper {
    //    private CircleAvatarMask mAvatarMask;
    private static final int RADIUS = 25;
    private static final int DIAMETER = 50;

    public CircleAvatarShaper() {
//        mAvatarMask = new CircleAvatarMask();
    }

    /**
     * 修改图像形状
     *
     * @param src_avatar 原始avatar
     * @return 修改形状后的avatar
     */
    @Override
    public Bitmap ShapeAvatar(Bitmap src_avatar) {
        if(src_avatar == null) {
            return null;
        }

        //取最短边做边长作为正方形的边长
        int width = src_avatar.getWidth();
        int height = src_avatar.getHeight();

        if(width == 0 || height == 0){
            return null;
        }

        int d = 0;
        float r = 0;
        Rect rectSrc;

        if(width > height){
            d = height;
            r = d >> 1;
            rectSrc = new Rect( (width-d) >> 1, 0, d, d );
        }else{
            d = width;
            r = d >> 1;
            rectSrc = new Rect( 0, (height-d) >> 1,d, d );
        }

//        if(r == 0  || width == 0 || height == 0){
//        if(r == 0){
//            return null;
//        }

        //new一个Canvas在bitmap上面画图
        Bitmap output = Bitmap.createBitmap(d,d,Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        //设置边缘光滑，去掉锯齿,相交模式为取交集
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(false);

        canvas.drawCircle(r,r,r,paint);

        //设置两个图形相交模式为取交集，多余的将被去掉
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        //canvas将bitmap画在backgroundBmp上
        RectF rectDst = new RectF(0,0,d,d);
        canvas.drawBitmap(src_avatar, rectSrc, rectDst, paint);

        return output;
    }

    public Bitmap defaultAvatar(Context context){
        int radius = AndTools.dp2px(context, RADIUS);
        int diameter = radius << 1;
        //new一个Canvas在bitmap上面画图
        Bitmap output = Bitmap.createBitmap(diameter,diameter, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        //设置边缘光滑，去掉锯齿,相交模式为取交集
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.GRAY);
        paint.setFilterBitmap(false);
        paint.setStyle(Paint.Style.FILL);

        canvas.drawCircle( radius,radius,radius,paint);
        return output;
    }

    public Bitmap ShapeAvatar1(Bitmap src){
        int radius = src.getWidth() / 2;
        BitmapShader bitmapShader = new BitmapShader(src, Shader.TileMode.REPEAT,
                Shader.TileMode.REPEAT);
        Bitmap dest = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(dest);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(bitmapShader);
        c.drawCircle(radius,radius, radius, paint);
        return dest;
    }

    public Bitmap toRoundBitmap(Context context,Bitmap bitmap) {
        //取最短边做边长作为正方形的边长
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int r = width > height ? height : width;

        //构建一个bitmap
        Bitmap backgroundBmp = Bitmap.createBitmap(r,r, Bitmap.Config.ARGB_8888);
        //new一个Canvas，在backgroundBmp上画图
        Canvas canvas = new Canvas(backgroundBmp);
//        canvas.drawColor(Color.TRANSPARENT);

        //设置边缘光滑，去掉锯齿
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setAntiAlias(true);
//        paint.setStyle(Paint.Style.FILL);
        paint.setFilterBitmap(false);
//        paint.setColor(Color.TRANSPARENT);

        Paint mBGPaint = new Paint();
        mBGPaint.setStyle(Paint.Style.FILL);
        mBGPaint.setColor(Color.TRANSPARENT);

        Paint maskPaint = new Paint();
        maskPaint.setFilterBitmap(true);
        maskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));

        //宽高相等，即正方形
        RectF rect = new RectF(0, 0, r, r);

//        int sc = canvas.saveLayer(rect, null,
//                Canvas.MATRIX_SAVE_FLAG |
//                        Canvas.CLIP_SAVE_FLAG |
//                        Canvas.HAS_ALPHA_LAYER_SAVE_FLAG |
//                        Canvas.FULL_COLOR_LAYER_SAVE_FLAG |
//                        Canvas.CLIP_TO_LAYER_SAVE_FLAG);


        //canvas将bitmap画在backgroundBmp上
        Rect srcRect = new Rect(0, 0, r, r);
        canvas.drawBitmap(bitmap, srcRect, rect, paint);

        //设置两个图形相交模式为取交集，多余的将被去掉
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));

        //通过制定的rect画一个圆角矩形，当圆角rx=ry=r/2时，画出来的圆角矩形就是圆形
//        canvas.drawRoundRect(rect, r/2, r/2, paint);
        Bitmap srcBmp = makeMask(r);
        canvas.drawBitmap(srcBmp,0,0,paint);
//        canvas.drawCircle(r/2,r/2,r/2,paint);
//        canvas.restoreToCount(sc);

        //返回已经绘画好的backgroundBmp
        return backgroundBmp;
    }

    public Bitmap makeMask(int d){
        //new一个Canvas在bitmap上面画图
        Bitmap output = Bitmap.createBitmap(d,d, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        //设置边缘光滑，去掉锯齿,相交模式为取交集
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.GRAY);
        paint.setStyle(Paint.Style.FILL);

        canvas.drawCircle( d/2,d/2,d/2,paint);
        return output;
    }

    /**
     * 把图片变成圆角
     *
     * @param bitmap 需要修改的图片
     * @param pixels 圆角的弧度
     * @return 圆角图片
     */
    public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }
}
