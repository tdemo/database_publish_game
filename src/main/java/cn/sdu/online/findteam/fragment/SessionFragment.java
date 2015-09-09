package cn.sdu.online.findteam.fragment;

import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.os.*;
import android.os.Process;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.wukong.Callback;

import javax.inject.Inject;

import cn.sdu.online.findteam.aliwukong.imkit.base.Functional.Action;
import cn.sdu.online.findteam.R;
import cn.sdu.online.findteam.aliwukong.imkit.base.ListAdapter;
import cn.sdu.online.findteam.aliwukong.imkit.base.ListFragment;
import cn.sdu.online.findteam.aliwukong.imkit.session.model.Session;
import cn.sdu.online.findteam.aliwukong.imkit.session.SessionAdapter;
import cn.sdu.online.findteam.aliwukong.imkit.business.SessionServiceFacade;
import cn.sdu.online.findteam.resource.DialogDefine;

public class SessionFragment extends ListFragment {

    @Inject
    SessionServiceFacade mService;

    private TextView mListEmptyView;
    private SessionAdapter mSessionAdapter;

    @Override
    public int getLayoutResId() {
        return R.layout.session_list;
    }

    @Override
    public ListView findListView(View fragmentView) {
        return (ListView) fragmentView.findViewById(R.id.session_list);
    }

    @Override
    public ListAdapter buildAdapter(Activity activity) {
        mSessionAdapter = new SessionAdapter(activity);
        return mSessionAdapter;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadData();
        mService.onRemoved(onSessionRemoved)
//                    .onReceiveMessage(onReceiveMessage)
                .onUnreadCountChange(onUnreadChange)
                .onContentChange(onContentChange)
                .onCreated(onCreateSession)
                .onTopChange(onTopChange);
    }

    @Override
    protected void onInitViews(View parent) {
        mListEmptyView = (TextView) parent.findViewById(R.id.list_empty);
        mListView.setEmptyView(mListEmptyView);
    }

    private void loadData() {
        mListEmptyView.setVisibility(View.VISIBLE);
//        mListEmptyView.setText(String.format(getString(R.string.load_data), "0"));
        mListEmptyView.setText(getString(R.string.load_data1));
        mService.listSessions(Integer.MAX_VALUE, new Callback<List<Session>>() {
            @Override
            public void onSuccess(List<Session> sessions) {
                mSessionAdapter.setList(sessions);
                mListEmptyView.setVisibility(View.GONE);
            }

            @Override
            public void onException(String s, String s2) {
                mListEmptyView.setText(s2);
            }

            @Override
            public void onProgress(List<Session> sessions, int progress) {
//                mListEmptyView.setText(String.format(getString(R.string.load_data), progress + ""));
            }
        });
    }

    private Action<List<Session>> onCreateSession = new Action<List<Session>>() {
        @Override
        public void action(List<Session> sessions) {
            mSessionAdapter.addSession(0, sessions);
            mSessionAdapter.sort();
            mSessionAdapter.notifyDataSetChanged();
        }
    };

    private Action<String> onSessionRemoved = new Action<String>() {
        @Override
        public void action(String id) {
            mSessionAdapter.removeSession(id);
        }
    };

    private Action<List<Session>> onReceiveMessage = new Action<List<Session>>() {
        @Override
        public void action(List<Session> session) {
//            mSessionAdapter.sort();
//            mSessionAdapter.notifyDataSetChanged();
        }
    };

    private Action<List<Session>> onContentChange = new Action<List<Session>>() {
        @Override
        public void action(List<Session> sessions) {
            mSessionAdapter.sort();
            mSessionAdapter.notifyDataSetChanged();
//            mSessionAdapter.notifyDataSetChanged(sessions, "content");
        }
    };


    private Action<List<Session>> onUnreadChange = new Action<List<Session>>() {
        @Override
        public void action(List<Session> sessions) {
            mSessionAdapter.notifyDataSetChanged(sessions, "unread");
        }
    };

    private Action<List<Session>> onTopChange = new Action<List<Session>>() {
        @Override
        public void action(List<Session> sessions) {
            mSessionAdapter.sort();
            mSessionAdapter.notifyDataSetChanged();
        }
    };
}
