package cn.sdu.online.findteam.fragment;


import java.util.ArrayList;
import java.util.List;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import cn.sdu.online.findteam.R;
import cn.sdu.online.findteam.adapter.MyMessageListViewAdapter;
import cn.sdu.online.findteam.mob.ChatListItem;

public class FriendMainFragment extends ListFragment {

    private ListView list;
    private List<ChatListItem> data;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.mymessage_listview, container, false);

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //list = (ListView)findViewById(android.R.id.list);
        data = getData();
        MyMessageListViewAdapter adapter = new MyMessageListViewAdapter(getActivity(), data);
        setListAdapter(adapter);
    }

    private List<ChatListItem> getData() {
        List<ChatListItem> list = new ArrayList<ChatListItem>();
        for (int i = 0; i < 10; i++) {
            list.add(new ChatListItem("同学甲", "我是个人介绍我是个人我是个人我是个人",
                    R.drawable.frienda, false, 0));
        }
        return list;
    }
}
