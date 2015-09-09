package cn.sdu.online.findteam.mob;

/**
 * Created by wn on 2015/8/2.
 */
public class ChatActivityListItem {
    private int headimg;
    private String message;
    private int type;

    public int getImg() {
        return headimg;
    }

    public void setImg(int headimg) {
        this.headimg = headimg;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
