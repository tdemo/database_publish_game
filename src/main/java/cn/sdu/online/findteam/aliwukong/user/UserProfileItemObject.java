package cn.sdu.online.findteam.aliwukong.user;

import android.view.View;

import java.util.List;

public class UserProfileItemObject {

    public UserProfileItemObject(Type t) {
        this.type = t;
    }

    public enum Type {
        Avatar, // 头像区域
        Header, // 蓝色的头部
        BoldTextContent, // 粗边框的内容
        TextContent, // 细边框的内容
        NONE_DIVIDER_CONTENT,// 没有divider的内容
        MyAvatar,//个人详情里面的头像
        TextContentRight,//内容靠右对齐
        TextContentRight_NO_DIVIVDER,//内容靠右对齐，没有下分割线
        Header_TEMP,
        Remark;
    };

    public Type type;

    public Boolean isActive = true;
    public boolean first = false;
    public String title;
    public String content;
    public Integer imageResId;
    public String mediaId;
    public View.OnClickListener onclick;
    public List<String> labels;
    public String gender;//"F" or "M"
}
