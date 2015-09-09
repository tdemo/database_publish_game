package cn.sdu.online.findteam.aliwukong.avatar;

import android.content.Context;
import android.widget.AbsListView;
import android.widget.ImageView;

import java.util.List;

import cn.sdu.online.findteam.aliwukong.imkit.widget.CustomGridView;

/**
 * 头像魔法师，用于用户和会话的头像显示。<br>
 * IM应用中需要将用户头像显示在消息或者会话的icon中。{@link com.alibaba.wukong.demo.base.avatar.AvatarMagician AvatarMagician}
 * 为应用开发提供了快速将一个openid的用户头像显示在view上。会话头像采用宫格显示的方式，当传入会话头像的openid数量为1个时，显示模式同用户头像。
 * 当openid数量小于等于4大于1时，采用4宫格显示。当小于等于9大于4时，采用9宫格显示。以此类推。<br>
 * <br>
 * 头像的形状：View上绑定时采用云端默认的形状，应用可以通过两种方式修改头像的形状<br>
 * 1、全局处理：该处理采用修改bitmap的方式处理头像，修改完成缓存中将存储处理后的头像。
 * 优点：性能快，直接修改头像bitmap，降低了重复绘制的消耗。缺点：不支持一个app内多种形状头像显示。
 * 一般一个app内部头像的显示的风格一致务必采用该方案。通过{@link #setAvatarShape(int) setAvatarShape}
 * 和{@link #setAvatarShape(AvatarShaper) setAvatarShape} 接口方法设置全局的avatar形状修改接口。
 * 建议在处理drawable图像形状时，采用透明作为背景色。例如当处理为圆形时，将圆形以外部分处理为透明色。这样可以让头像显示在不同背景颜色下。<br>
 * 2、绘制前处理：该处理采用在头像绘制到view时处理，应用可以重载View的onDraw方法，在头像绘制时修改头像的形状。
 * 优点：app内部可使用不同的风格显示头像，例如好友和消息界面可以采用不同风格。缺点：性能上差于全局处理。该处理应用可以自行实现，
 * 也可以使用{@link #getAvatarMask(int)}  getAvatarMask}提供的默认形状。获取AvatarMask实例后，在view onDraw方式中，
 * 在super.onDraw方法后调用AvatarMask的drawMask方法。<br>
 * Created by bokui on 14/12/1.
 */
public interface AvatarMagician {
    /**
     * 圆形头像
     */
    public static final int CIRCLE_AVATAR_SHAPE = 1;
    /**
     * 圆角矩形头像
     */
    public static final int ROUNDRECT_AVATAR_SHAPE = 2;

    public void init(Context context);

    /**
     * 设置用户头像
     * @param view      显示头像的ImageView
     * @param openid    用户id
     */
    public void setUserAvatar(ImageView view,long openid,AbsListView listView);

    /**
     * 设置会话icon
     * @param gridView   会话由多个用户头像组成，viewMap的键为用户id，值为显示该用户的ImageView
     */
//    public void setConversationAvatar(Map<Long,ImageView> viewMap);
//    public void setConversationAvatar(ImageView view,Long... openids);
    public void setConversationAvatar(CustomGridView gridView,List<Long> openids,AbsListView listView);

    /**
     * 设置Avatar的形状，设置完成后，应用内所有头像将使用该形状。
     * @param shape 形状类型，{@link #CIRCLE_AVATAR_SHAPE}圆形，
     * {@link #ROUNDRECT_AVATAR_SHAPE}圆角矩形
     */
    public void setAvatarShape(int shape);

    /**
     * 是指Avatar的形状修改器，设置完成后，应用内所有头像将使用该形状。
     * @param shaper 形状修改器。
     */
    public void setAvatarShape(AvatarShaper shaper);

    /**
     * 获取Avatar头像遮盖，当view onDraw方式时，在super.onDraw方法后调用AvatarMask的drawMask方法。
     * @param shape 形状类型，{@link #CIRCLE_AVATAR_SHAPE}圆形， {@link #ROUNDRECT_AVATAR_SHAPE}圆角矩形
     * @return  AvatarMask实例，如果未曾调用setAvatarShape则返回null。
     */
    public AvatarMask getAvatarMask(int shape) throws Throwable;
}
