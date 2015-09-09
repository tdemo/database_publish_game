package cn.sdu.online.findteam.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ActionMenuView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import cn.sdu.online.findteam.R;

public class TeamLogActivity extends Activity implements View.OnClickListener {

    private Button back/*addview,*/, push;
    private LinearLayout discuss_item;
    private EditText editText;
    private ScrollView scrollView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBarLayout(R.layout.teamlog_actionbar);
        setContentView(R.layout.teamlog_content_layout);

        /*addview = (Button) findViewById(R.id.add_view);*/
        back = (Button) findViewById(R.id.teamlog_back_bt);
        discuss_item = (LinearLayout) findViewById(R.id.discuss_item_linearlayout);
        editText = (EditText) findViewById(R.id.write_discuss);
        push = (Button) findViewById(R.id.push_discuss);
        scrollView = (ScrollView) findViewById(R.id.teamlog_scrollview);

        back.setOnClickListener(this);
        push.setOnClickListener(this);

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
        switch (v.getId()) {
            case R.id.push_discuss:
                if (editText.getText().toString().trim().length() != 0) {
                    String disc = editText.getText().toString();

                    LayoutInflater inflater = LayoutInflater.from(TeamLogActivity.this);
                    View view = inflater.inflate(R.layout.teamlog_content_discuss, null);
                    TextView textView = (TextView) view.findViewById(R.id.log_discuss_tv);
                    textView.setText(disc);

                    discuss_item.addView(view);
                  /*  TeamLogActivity.this.setContentView(R.layout.teamlog_content_layout);*/
                    Handler handler = new Handler();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                        }
                    });
                }
                editText.setText("");
                break;

            case R.id.teamlog_back_bt:
                TeamLogActivity.this.finish();
                break;
     /*       case R.id.write_discuss:
                editText.setFocusableInTouchMode(true);
                editText.requestFocus();
                break;*/
            default:
                break;
        }

    }
}
