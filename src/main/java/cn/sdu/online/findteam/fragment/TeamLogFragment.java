package cn.sdu.online.findteam.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

import java.util.ArrayList;
import java.util.List;

import cn.sdu.online.findteam.R;
import cn.sdu.online.findteam.activity.MySingleTeamActivity;
import cn.sdu.online.findteam.activity.OtherTeamActivity;
import cn.sdu.online.findteam.activity.TeamLogActivity;
import cn.sdu.online.findteam.activity.WriteActivity;
import cn.sdu.online.findteam.adapter.TeamLogListViewAdapter;
import cn.sdu.online.findteam.mob.TeamLogListViewItem;
import cn.sdu.online.findteam.share.MyApplication;
import cn.sdu.online.findteam.view.SingleCompetitionListView;

public class TeamLogFragment extends Fragment implements View.OnClickListener {

    private View view;
    List<TeamLogListViewItem> listViewItems;
    SingleCompetitionListView listView;
    String name[];
    String time[];
    String content;
    TeamLogListViewAdapter teamLogListViewAdapter;
    private Button writelog;
    private PopupWindow popupWindow;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (TeamLogFragment.this.getActivity().equals(MySingleTeamActivity.mContext)) {
            if (MyApplication.IDENTITY.equals("队长")) {
                view = inflater.inflate(R.layout.myteam_teamlog_layout, null);
                teamHeaderLog();
            } else {
                view = inflater.inflate(R.layout.myteam_teamlog_layout, null);
                teamMemLog();
            }
        }
        else {
            view = inflater.inflate(R.layout.other_teamlog_layout,null);
            teamOtherLog();
        }

        return view;
    }

    private void initListView(ListView listView) {
        listViewItems = new ArrayList<TeamLogListViewItem>();
        name = new String[]{"大师兄", "二师弟", "沙师弟"};
        time = new String[]{"2015年7月23日 10:45", "2015年7月23日 11:45", "2015年7月23日 12:45"};
        content = "我是严肃的日志~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~";

        for (int i = 0; i < name.length; i++) {
            listViewItems.add(new TeamLogListViewItem(R.drawable.teammember_header,
                    name[i], time[i], content));
        }

        teamLogListViewAdapter = new TeamLogListViewAdapter(TeamLogFragment.this.getActivity().getApplicationContext(), listViewItems);
        listView.setAdapter(teamLogListViewAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                if (TeamLogFragment.this.getActivity().equals(OtherTeamActivity.getContext())) {
                    intent.setClass(OtherTeamActivity.getContext(), TeamLogActivity.class);
                } else if (TeamLogFragment.this.getActivity().equals(MySingleTeamActivity.mContext)) {
                    intent.setClass(MySingleTeamActivity.mContext, TeamLogActivity.class);
                }
                TeamLogFragment.this.getActivity().startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.teammem_writelog:
                Intent intent = new Intent();
                intent.setClass(TeamLogFragment.this.getActivity(), WriteActivity.class);
                intent.putExtra("sign", "写日志");
                TeamLogFragment.this.getActivity().startActivityForResult(intent, 1);
                break;
        }
    }

    /**
     * 别人来的时候调用
     */
    private void teamOtherLog() {
        listView = (SingleCompetitionListView) view.findViewById(R.id.teamlog_listview);
        initListView(listView);
    }

    /**
     * 队员来的时候调用
     */
    private void teamMemLog() {
        writelog = (Button) view.findViewById(R.id.teammem_writelog);
        writelog.setOnClickListener(TeamLogFragment.this);
        listView = (SingleCompetitionListView) view.findViewById(R.id.teammem_log_list);
        initListView(listView);
    }

    /**
     * 队长来的时候调用
     */
    private void teamHeaderLog() {
        writelog = (Button) view.findViewById(R.id.teammem_writelog);
        writelog.setOnClickListener(TeamLogFragment.this);
        listView = (SingleCompetitionListView) view.findViewById(R.id.teammem_log_list);
        initListView(listView);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showPopupWindow(position, view);
                return false;
            }
        });
    }

    private void showPopupWindow(final int position, View view) {

        // 一个自定义的布局，作为显示的内容
        View contentView = LayoutInflater.from(TeamLogFragment.this.getActivity()).inflate(
                R.layout.delete_pop_window, null);
        // 设置按钮的点击事件
        Button delete = (Button) contentView.findViewById(R.id.pop_delete_btn);

        popupWindow = new PopupWindow(contentView,
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setWidth(400);
        popupWindow.setHeight(150);
        popupWindow.setAnimationStyle(R.style.popwindow_anim);
        popupWindow.setTouchable(true);
        backgroundAlpha(0.5f);
        popupWindow.setOnDismissListener(new poponDismissListener());

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listViewItems.remove(position);
                teamLogListViewAdapter.notifyDataSetChanged();
                popupWindow.dismiss();
            }
        });

        popupWindow.setTouchInterceptor(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
            }
        });

        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        popupWindow.setBackgroundDrawable(getResources().getDrawable(
                R.color.textcolor_gray));

        // 设置好参数之后再show
        popupWindow.showAtLocation(TeamLogFragment.this.getView(), Gravity.CENTER, 0, 0);
    }

    /**
     * 设置添加屏幕的背景透明度
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = TeamLogFragment.this.getActivity().getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        TeamLogFragment.this.getActivity().getWindow().setAttributes(lp);
    }

    /**
     * 添加新笔记时弹出的popWin关闭的事件，主要是为了将背景透明度改回来
     *
     * @author cg
     */
    class poponDismissListener implements PopupWindow.OnDismissListener {

        @Override
        public void onDismiss() {
            // TODO Auto-generated method stub
            //Log.v("List_noteTypeActivity:", "我是关闭事件");
            backgroundAlpha(1f);
        }
    }

    public void addListItem(int headbmp, String name, String time, String content){
        listViewItems.add(new TeamLogListViewItem(headbmp, name, time, content));
        teamLogListViewAdapter.notifyDataSetChanged();
    }
}


