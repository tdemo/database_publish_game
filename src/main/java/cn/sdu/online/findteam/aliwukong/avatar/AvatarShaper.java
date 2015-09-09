package cn.sdu.online.findteam.aliwukong.avatar;

/**
 * Created by wn on 2015/8/13.
 */

import android.content.Context;
import android.graphics.Bitmap;

/**
 * 头像塑形器，用于修改头像的bitmap形状。塑形完成后，应用全局的头像都被修改。
 * Created by bokui on 14/12/3.
 */
public interface AvatarShaper {
    /**
     * 修改图像形状
     * @param src_avatar    原始avatar
     * @return  修改形状后的avatar
     */
    public Bitmap ShapeAvatar(Bitmap src_avatar);

    /**
     * 创建默认Avatar，头像加载完成前以及用户未头像时显示的默认图像
     * @return 默认Avatar
     */
    public Bitmap defaultAvatar(Context context);
}

