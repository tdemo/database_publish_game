package cn.sdu.online.findteam.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cn.sdu.online.findteam.R;
import cn.sdu.online.findteam.activity.MySingleTeamActivity;
import cn.sdu.online.findteam.activity.MyTeamActivity;
import cn.sdu.online.findteam.mob.MyTeamListItem;
import cn.sdu.online.findteam.resource.RoundImageView;

/**
 * Created by wn on 2015/8/29.
 */
public class MyTeamListAdapter extends BaseExpandableListAdapter {

    LayoutInflater inflater;
    List<List<MyTeamListItem>> listItems;
    private String[] groupName = new String[]{"已加入", "申请还未加入", "已拒绝"};

    public MyTeamListAdapter(Context mContext, List<List<MyTeamListItem>> listItems) {
        inflater = LayoutInflater.from(mContext);
        this.listItems = listItems;
    }

    @Override
    public int getGroupCount() {
        return groupName.length;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return listItems.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return listItems.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return listItems.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.myteam_listgroup_item, null);
        TextView groupTv = (TextView) convertView.findViewById(R.id.myteam_group_tv);
        groupTv.setText(groupName[groupPosition] + " (" + listItems.get(groupPosition).size() + ")");
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        MyViewHolder myViewHolder;
        if (convertView == null){
            myViewHolder = new MyViewHolder();
            convertView = inflater.inflate(R.layout.myteam_list_item, null);
            myViewHolder.teamName = (TextView) convertView.findViewById(R.id.myteam_teamName);
            myViewHolder.parentName = (TextView) convertView.findViewById(R.id.myteam_parentName);
            myViewHolder.teamHeader = (RoundImageView) convertView.findViewById(R.id.myteam_teamHeader);
            myViewHolder.teamIntroduce = (TextView) convertView.findViewById(R.id.myteam_teamIntroduce);
            myViewHolder.parentLayout = (LinearLayout) convertView.findViewById(R.id.myteam_parent_layout);
            convertView.setTag(myViewHolder);
        }
        else {
            myViewHolder = (MyViewHolder) convertView.getTag();
        }
        myViewHolder.teamIntroduce.setText(listItems.get(groupPosition).get(childPosition).introduce);
        myViewHolder.teamName.setText(listItems.get(groupPosition).get(childPosition).teamName);
        myViewHolder.teamHeader.setImageResource(listItems.get(groupPosition).get(childPosition).header);
        myViewHolder.parentName.setText(listItems.get(groupPosition).get(childPosition).parent);
        switch (childPosition % 4){
            case 0:
                myViewHolder.parentLayout.setBackgroundColor(Color.parseColor("#f3a1ab"));
                break;
            case 1:
                myViewHolder.parentLayout.setBackgroundColor(Color.parseColor("#f1e6a9"));
                break;
            case 2:
                myViewHolder.parentLayout.setBackgroundColor(Color.parseColor("#91dacb"));
                break;
            case 3:
                myViewHolder.parentLayout.setBackgroundColor(Color.parseColor("#9fc7fe"));
                break;
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyTeamActivity.getInstance(), MySingleTeamActivity.class);
                MyTeamActivity.getInstance().startActivity(intent);
            }
        });
        return convertView;
    }

    class MyViewHolder{
        public TextView teamName;
        public TextView teamIntroduce;
        public RoundImageView teamHeader;
        public TextView parentName;
        public LinearLayout parentLayout;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
