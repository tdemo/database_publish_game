package cn.sdu.online.findteam.mob;

/**
 * 这个类是 SingCompetitionListAdapter 中的数据类
 */
public class SingleCompetitionListItem {
    public int imageView;
    public String teamname;
    public int personnum;
    public int line1;
    public String content;
    public int line2;
    public int look;
    public int join;
    public String teamID;

    public SingleCompetitionListItem(int imageView, String teamname,
                                     int personnum, int line1,
                                     String content, int line2,
                                     int look, int join,
                                     String teamID){
        this.imageView = imageView;
        this.teamname = teamname;
        this.personnum = personnum;
        this.line1 = line1;
        this.content = content;
        this.line2 = line2;
        this.look = look;
        this.join = join;
        this.teamID = teamID;
    }
}
