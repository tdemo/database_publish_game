package cn.sdu.online.findteam.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ActionMenuView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import cn.sdu.online.findteam.R;
import cn.sdu.online.findteam.adapter.InviteMemListAdapter;
import cn.sdu.online.findteam.mob.InviteMemListItem;

public class InviteNewMemActivity extends Activity implements View.OnClickListener{

    private ListView inviteMemList;
    private Button finish, action_search, search_Btn, back_Btn;
    private List<InviteMemListItem> list;
    private InviteMemListAdapter inviteMemListAdapter;
    private LinearLayout search_Layout;

    // 搜索框的状态
    private boolean search_State;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBarLayout(R.layout.invitemem_actionbar);
        setContentView(R.layout.invitemem_activity);
        init();
        initList();
    }

    private void init(){
        search_State = false;
        inviteMemList = (ListView) findViewById(R.id.invite_activity_listview);
        search_Layout = (LinearLayout) findViewById(R.id.invitemem_search_layout);
        finish = (Button) findViewById(R.id.invite_finish_btn);
        finish.setOnClickListener(this);
        action_search = (Button) findViewById(R.id.invitemem_actionsearch);
        action_search.setOnClickListener(this);
        back_Btn = (Button) findViewById(R.id.invitemem_back_btn);
        back_Btn.setOnClickListener(this);
    }

    private void initList(){
        list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(new InviteMemListItem("同学甲", "我是个人介绍我是个人介绍我是个人介绍",
                    R.drawable.frienda, false));
        }
        inviteMemListAdapter = new InviteMemListAdapter(list, InviteNewMemActivity.this);
        inviteMemList.setAdapter(inviteMemListAdapter);
        inviteMemListAdapter.setTextCallback(new InviteMemListAdapter.TextCallback() {
            public void onListener(int count) {
                finish.setText("完成" + "(" + count + ")");
            }
        });
        inviteMemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                inviteMemListAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * @param layoutId 布局Id
     */
    public void setActionBarLayout(int layoutId) {
        ActionBar actionBar = getActionBar();
        if (null != actionBar) {
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);
            LayoutInflater inflator = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflator.inflate(layoutId, null);
            ActionBar.LayoutParams layout = new ActionBar.LayoutParams(ActionMenuView.LayoutParams.FILL_PARENT, ActionMenuView.LayoutParams.FILL_PARENT);
            actionBar.setCustomView(v, layout);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.invitemem_actionsearch:
                search_State = !search_State;
                if (search_State){
                    search_Layout.setVisibility(View.VISIBLE);
                }
                else {
                    search_Layout.setVisibility(View.GONE);
                }
                break;

            case R.id.invite_finish_btn:

                break;

            case R.id.invitemem_back_btn:
                InviteNewMemActivity.this.finish();
        }
    }
}
