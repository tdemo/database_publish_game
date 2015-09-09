package cn.sdu.online.findteam.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import cn.sdu.online.findteam.R;
import cn.sdu.online.findteam.activity.OtherTeamActivity;
import cn.sdu.online.findteam.activity.SingleCompetitionActivity;
import cn.sdu.online.findteam.mob.SingleCompetitionListItem;
import cn.sdu.online.findteam.net.NetCore;
import cn.sdu.online.findteam.share.MyApplication;
import cn.sdu.online.findteam.util.AndTools;

public class SingleCompetitionListAdapter extends BaseAdapter {

    LayoutInflater inflater;
    List<SingleCompetitionListItem> listItems;

    public SingleCompetitionListAdapter(Context mContext, List<SingleCompetitionListItem> listItems) {
        inflater = LayoutInflater.from(mContext);
        this.listItems = listItems;
    }

    @Override
    public int getCount() {
        return listItems.size();
    }

    @Override
    public Object getItem(int position) {
        return listItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.singlecompetition_item_layout, null);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.singlecp_item_img);
            viewHolder.teamname = (TextView) convertView.findViewById(R.id.singlecp_item_teamname);
            viewHolder.personnum = (TextView) convertView.findViewById(R.id.singlecp_item_personnum);
            viewHolder.line1 = convertView.findViewById(R.id.singlecp_item_line1);
            viewHolder.content = (TextView) convertView.findViewById(R.id.singlecp_item_content);
            viewHolder.line2 = convertView.findViewById(R.id.singlecp_item_line2);
            viewHolder.look = (Button) convertView.findViewById(R.id.singlecp_item_look);
            viewHolder.join = (Button) convertView.findViewById(R.id.singlecp_item_join);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.teamname.setText(listItems.get(position).teamname);
        viewHolder.personnum.setText("缺" + listItems.get(position).personnum + "人");
        viewHolder.content.setText(listItems.get(position).content);
        viewHolder.look.setTag(position);
        viewHolder.join.setTag(position);

        viewHolder.look.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(SingleCompetitionActivity.getContext(), OtherTeamActivity.class);
                SingleCompetitionActivity.getContext().startActivity(intent);
            }
        });

        viewHolder.join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AndTools.isNetworkAvailable(MyApplication.getInstance())){
                    AndTools.showToast(SingleCompetitionActivity.getContext(), "当前网络不可用！");
                    return;
                }

                new Thread(){
                    @Override
                    public void run() {
                        try {
                            String jsonData = new NetCore().joinTeam(listItems.get(position).teamID);
                            JSONObject jsonObject = new JSONObject(jsonData);
                            Bundle bundle = new Bundle();
                            bundle.putInt("code", jsonObject.getInt("code"));
                            bundle.putString("msg", jsonObject.getString("msg"));
                            Message message = new Message();
                            message.setData(bundle);
                            loadteamHander.sendMessage(message);

                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });

        return convertView;
    }

    public class ViewHolder {
        ImageView imageView;
        TextView teamname;
        TextView personnum;
        View line1;
        TextView content;
        View line2;
        Button look;
        Button join;
    }

    Handler loadteamHander = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String jsonMsg = bundle.getString("msg", "出现异常错误！");
            AndTools.showToast(SingleCompetitionActivity.getContext(), jsonMsg);
        }
    };
}