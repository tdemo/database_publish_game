package cn.sdu.online.findteam.mob;


/**
 * Created by wn on 2015/8/29.
 */
public class MyTeamListItem {
    public int header;
    public String teamName;
    public String introduce;
    public String parent;

    public MyTeamListItem(int header, String teamName, String introduce, String parent){
        this.header = header;
        this.teamName = teamName;
        this.introduce = introduce;
        this.parent = parent;
    }
}
