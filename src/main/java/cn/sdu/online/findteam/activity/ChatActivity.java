package cn.sdu.online.findteam.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ActionMenuView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.sdu.online.findteam.R;
import cn.sdu.online.findteam.mob.ChatActivityListItem;
import cn.sdu.online.findteam.resource.RoundImageView;

public class ChatActivity extends Activity implements View.OnClickListener,View.OnTouchListener {
    private ListView mListView;
    private List<ChatActivityListItem> list;
    private int TYPE_COUNT = 2;
    private int LEFT = 0;
    private int RIGHT = 1;
    private LayoutInflater mInflater;
    private MyAdapter adapter;
    private EditText editText;
    private Button button,backBtn;
    private RelativeLayout linearLayout;
    private TextView chatPerson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBarLayout(R.layout.chatactivity_actionbar);
        chatPerson = (TextView) findViewById(R.id.chat_person);
        chatPerson.setText(getIntent().getExtras().getString("chatperson"));
        setContentView(R.layout.chatactivity_layout);
        initView();

        mInflater = LayoutInflater.from(this);
        initData();
        adapter = new MyAdapter();
        mListView.setAdapter(adapter);
    }

    private void initView() {
        mListView = (ListView) findViewById(R.id.chat_listview);
        editText = (EditText) findViewById(R.id.write_chatmsg);
        button = (Button) findViewById(R.id.push_chatmsg);
        linearLayout = (RelativeLayout) findViewById(R.id.chatactivity_layout);
        backBtn = (Button) findViewById(R.id.chat_back_btn);
        backBtn.setOnClickListener(this);
        button.setOnClickListener(this);
        linearLayout.setOnTouchListener(this);
        mListView.setOnTouchListener(this);
    }

    private void initData() {
        list = new ArrayList<ChatActivityListItem>();
/*        for (int i = 0; i < 13; i++) {
            ChatActivityListItem item = new ChatActivityListItem();
            if (i % 3 == 0) {
                item.setMessage("呵呵又是你齐天大圣孙悟空，身如玄铁，火眼金睛，长生不老还有七十二变设计从来不需要高大上，更不需要多么复杂，用心表达，就足以。");
                item.setImg(R.drawable.otherteam_headtmp);
                item.setType(0);
            } else {
                item.setMessage("有是我怎么着");
                item.setImg(R.drawable.singlecompetition_itemimg);
                item.setType(1);
            }
            list.add(item);
        }*/
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.push_chatmsg:
                if (editText.getText().toString().trim().length() != 0) {
                    ChatActivityListItem chatActivityListItem1 = new ChatActivityListItem();
                    chatActivityListItem1.setType(1);
                    chatActivityListItem1.setMessage(editText.getText().toString());
                    list.add(chatActivityListItem1);
                    adapter.notifyDataSetChanged();
                }
                editText.setText("");
                break;

            case R.id.chat_back_btn:
                finish();

            default:
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        return imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            if (list.get(position).getType() == 0) {
                return LEFT;
            }
            return RIGHT;
        }

        @Override
        public int getViewTypeCount() {
            return TYPE_COUNT;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder mHolder;
            ChatActivityListItem listItem = list.get(position);
            if (getItemViewType(position) == LEFT) {
                if (convertView == null) {
                    mHolder = new ViewHolder();
                    convertView = mInflater.inflate(R.layout.chat_item_left, null);
                    mHolder.img = (RoundImageView) convertView.findViewById(R.id.img_userimgleft);
                    mHolder.message = (TextView) convertView.findViewById(R.id.tv_messageleft);
                    mHolder.arrow = (ImageView) convertView.findViewById(R.id.arrow_left);
                    convertView.setTag(mHolder);
                } else {
                    mHolder = (ViewHolder) convertView.getTag();
                }
                mHolder.img.setImageResource(R.drawable.otherteam_headtmp);
                mHolder.message.setText(listItem.getMessage());
            } else {
                if (convertView == null) {
                    mHolder = new ViewHolder();
                    convertView = mInflater.inflate(R.layout.chat_item_right, null);
                    mHolder.img = (RoundImageView) convertView.findViewById(R.id.img_userimgright);
                    mHolder.message = (TextView) convertView.findViewById(R.id.tv_messageright);
                    mHolder.arrow = (ImageView) convertView.findViewById(R.id.arrow_right);
                    convertView.setTag(mHolder);
                } else {
                    mHolder = (ViewHolder) convertView.getTag();
                }

                mHolder.img.setImageResource(R.drawable.singlecompetition_itemimg);
                mHolder.message.setText(listItem.getMessage());
            }
            return convertView;
        }

        private class ViewHolder {
            RoundImageView img;
            TextView message;
            ImageView arrow;
        }
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
}
