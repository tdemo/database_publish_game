package cn.sdu.online.findteam.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ActionMenuView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.sdu.online.findteam.R;
import cn.sdu.online.findteam.adapter.SingleCompetitionListAdapter;
import cn.sdu.online.findteam.mob.SingleCompetitionListItem;
import cn.sdu.online.findteam.net.NetCore;
import cn.sdu.online.findteam.resource.DialogDefine;
import cn.sdu.online.findteam.share.MyApplication;
import cn.sdu.online.findteam.util.AndTools;
import cn.sdu.online.findteam.view.SingleCompetitionListView;

/**
 * 具体的单个比赛信息界面
 */
public class SingleCompetitionActivity extends Activity implements View.OnClickListener {

    public static Context SingleCompetitionContext;
    private ListView singlelistView;
    private TextView introduce, gameName_tv, actionbar_tv;
    private LinearLayout detail;

    private List<SingleCompetitionListItem> listItems;

    private String gameID = "-1";

    /**
     * ActionBar上的返回 Button
     */
    private Button returnButton;


    Dialog dialog;
    View contentView;
    SingleCompetitionListAdapter Adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBarLayout(R.layout.singlecompetition_actionbar_layout);
        dialog = DialogDefine.createLoadingDialog(this,
                "加载中...");
        dialog.show();

        if (!AndTools.isNetworkAvailable(MyApplication.getInstance())) {
            if (dialog != null) {
                dialog.dismiss();
            }
            AndTools.showToast(this, "当前网络不可用！");
            return;
        }
        initView();
        initListView();
        setIntentData();

        Thread thread = new Thread(new loadGamesTeam());
        thread.start();
    }

    private void initView() {
        contentView = View.inflate(SingleCompetitionActivity.this, R.layout.singlecompetition_layout, null);
        SingleCompetitionContext = SingleCompetitionActivity.this;
        introduce = (TextView) contentView.findViewById(R.id.singleGame_introduce);
        singlelistView = (SingleCompetitionListView) contentView.findViewById(R.id.singlecplist);
        gameName_tv = (TextView) contentView.findViewById(R.id.competition_name);
        actionbar_tv = (TextView) findViewById(R.id.singleGame_actionbar_tv);
        returnButton = (Button) findViewById(R.id.singlecp_return_bt);
        returnButton.setOnClickListener(this);
        detail = (LinearLayout) contentView.findViewById(R.id.game_detail_infor);
        detail.setOnClickListener(this);
    }

    public void initListView() {
        listItems = new ArrayList<SingleCompetitionListItem>();
        Adapter = new SingleCompetitionListAdapter(SingleCompetitionActivity.this,
                listItems);
        singlelistView.setAdapter(Adapter);
    }

    private void setIntentData() {
        if (getIntent().getExtras() != null) {
            introduce.setText(getIntent().getExtras().getString("gameIntroduce"));
            gameID = getIntent().getExtras().getString("gameID");
            String gamename = getIntent().getExtras().getString("gameName");
            actionbar_tv.setText(gamename);
            gameName_tv.setText(gamename);
        }
    }

    class loadGamesTeam implements Runnable {

        @Override
        public void run() {
            try {
                if (gameID.equals("-1")) {
                    getGamesInfo.sendEmptyMessage(-1);
                    return;
                }

                String teamInfo = new NetCore().getGamesTeam(gameID);
                JSONArray teamlist = new JSONArray(teamInfo);
                for (int i = 0; i < teamlist.length(); i++) {
                    JSONObject team = (JSONObject) teamlist.get(i);
                    int maxNum = team.getInt("maxNum");
                    String teamName = team.getString("name");
                    String introduction = team.getString("introduce");
                    String teamID = team.getString("id");
                    Log.v("hehehehehe", teamName);
                    Log.v("hehehehehe", introduction);
                    listItems.add(new SingleCompetitionListItem(R.id.singlecp_item_img, teamName,
                            maxNum, R.id.singlecp_item_line1,
                            introduction, R.id.singlecp_item_line2,
                            R.id.singlecp_item_look, R.id.singlecp_item_join,
                            teamID));
                }
                getGamesInfo.sendEmptyMessage(0);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    Handler getGamesInfo = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case -1:
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    setContentView(contentView);
                    AndTools.showToast(SingleCompetitionActivity.this, "加载失败！");
                    break;

                case 0:
                    setContentView(contentView);
                    Adapter.notifyDataSetChanged();
                    if (dialog != null){
                        dialog.dismiss();
                    }
                    break;
            }
        }
    };

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

    public static Context getContext() {
        return SingleCompetitionContext;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.singlecp_return_bt:
                SingleCompetitionActivity.this.finish();
                break;

            case R.id.game_detail_infor:

                break;
        }
    }
}