package cn.sdu.online.findteam.aliwukong.avatar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

import cn.sdu.online.findteam.aliwukong.avatar.AvatarShaper;
import cn.sdu.online.findteam.util.AndTools;

/**
 * Created by wn on 2015/8/14.
 */
public class RoundRectAvatarShaper implements AvatarShaper {
    //    private static final float rect_radius = 24f;
    private static final int RADIUS = 25;
    private static final int DIAMETER = 50;

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

        if(r == 0  || width == 0 || height == 0){
            return null;
        }

        //new一个Canvas在bitmap上面画图
        Bitmap output = Bitmap.createBitmap(d,d,Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        //设置边缘光滑，去掉锯齿,相交模式为取交集
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        //宽高相等，即正方形
//        RectF rect = new RectF(width/2,height/2, r, r);
        //通过制定的rect画一个圆角矩形，当圆角rx=ry=r/2时，画出来的圆角矩形就是圆形
//        canvas.drawRoundRect(rect, r/2, r/2, paint);
        RectF rectDst = new RectF(0,0,d,d);
        float rect_radius = (d >> 3);
        canvas.drawRoundRect(rectDst, rect_radius, rect_radius, paint);

        //设置两个图形相交模式为取交集，多余的将被去掉
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        //canvas将bitmap画在backgroundBmp上
        canvas.drawBitmap(src_avatar, rectSrc, rectDst, paint);

        return output;

//        //取最短边做边长作为正方形的边长
//        int width = src_avatar.getWidth();
//        int height = src_avatar.getHeight();
//        int r = width > height ? height : width;
//
//        //new一个Canvas在bitmap上面画图
//        Bitmap output = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(output);
//
//        //设置边缘光滑，去掉锯齿,相交模式为取交集
//        Paint paint = new Paint();
//        paint.setAntiAlias(true);
//
//        //宽高相等，即正方形
//        RectF rect = new RectF(0, 0, r, r);
//        //通过制定的rect画一个圆角矩形，当圆角rx=ry=r/2时，画出来的圆角矩形就是圆形
//        canvas.drawRoundRect(rect, rect_radius, rect_radius, paint);
//
//        //设置两个图形相交模式为取交集，多余的将被去掉
//        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
//        //canvas将bitmap画在backgroundBmp上
//        canvas.drawBitmap(src_avatar, null, rect, paint);
//
////        mAvatarMask.drawMask(canvas,0xff424242);
//        return output;
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
        paint.setStyle(Paint.Style.FILL);

        //宽高相等，即正方形
        float rect_radius = (diameter >> 3);
        RectF rect = new RectF(0, 0, radius, radius);
        canvas.drawRoundRect(rect, rect_radius, rect_radius, paint);

        return output;
    }
}
