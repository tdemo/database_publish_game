package cn.sdu.online.findteam.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ActionMenuView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.sdu.online.findteam.R;

public class WriteActivity extends Activity implements View.OnClickListener {

    private String getsign;
    private TextView titleTv;
    private EditText teamMsgEt;
    private Button back;
    private LinearLayout finishBtn;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setActionBarLayout(R.layout.writeactivity_actionbar);
        getsign = WriteActivity.this.getIntent().getExtras().getString("sign");
        titleTv = (TextView) findViewById(R.id.writeactivity_ab_text);
        handle();
        setContentView(R.layout.writeactivity_layout);

        initView();
    }

    private void initView() {
        back = (Button) findViewById(R.id.writeactivity_backbt);
        finishBtn = (LinearLayout) findViewById(R.id.write_finish_bt);
        teamMsgEt = (EditText) findViewById(R.id.writeat_edit_text);

        back.setOnClickListener(this);
        finishBtn.setOnClickListener(this);
    }

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

    private void handle() {
        if (getsign.equals("编辑队伍信息")) {
            titleTv.setText("编辑队伍信息");
        } else if (getsign.equals("写日志")) {
            titleTv.setText("写日志");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.writeactivity_backbt:
                WriteActivity.this.finish();
                break;
            case R.id.write_finish_bt:
                if (getsign.equals("编辑队伍信息")) {
                    editMsgFinish();
                } else if (getsign.equals("写日志")) {
                    writeLogFinish();
                }
                break;
        }
    }

    private void editMsgFinish() {
        if (teamMsgEt.getText().toString().length() != 0) {
            intent = WriteActivity.this.getIntent();
            intent.putExtra("teaminfor", teamMsgEt.getText().toString());
            WriteActivity.this.setResult(1, intent);
            WriteActivity.this.finish();
        }
        else {
            Toast.makeText(WriteActivity.this, "。您还未填写队伍信息！" , Toast.LENGTH_SHORT).show();
        }
    }

    private void writeLogFinish(){
        if (teamMsgEt.getText().toString().trim().length() != 0){
            intent = WriteActivity.this.getIntent();
            intent.putExtra("teamlog", teamMsgEt.getText().toString());
            WriteActivity.this.setResult(2, intent);
            WriteActivity.this.finish();
        }
        else {
            Toast.makeText(WriteActivity.this, "您还未填写日志！", Toast.LENGTH_SHORT).show();
        }
    }
}
