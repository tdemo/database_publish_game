package cn.sdu.online.findteam.mob;

public class InviteMemListItem {
    public String title;
    public String info;
    public int img;
    public boolean isSelected;

    public InviteMemListItem(String title, String info, int img, boolean isSelected){
        this.title = title;
        this.info = info;
        this.img = img;
        this.isSelected = isSelected;
    }
}
