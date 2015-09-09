package cn.sdu.online.findteam.mob;

public class ChatListItem {
    public String title;
    public String info;
    public int img;
    public boolean seeornot;
    public int num;

    public ChatListItem(String title, String info, int img,
                        boolean seeornot, int num) {
        this.title = title;
        this.info = info;
        this.img = img;
        this.seeornot = seeornot;
        this.num = num;
    }

    public String getTitle(){
        return title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public boolean getSeeornot() {
        return seeornot;
    }

    public void setSeeornot(boolean seeornot) {
        this.seeornot = seeornot;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getNum() {
        return num;
    }
}
