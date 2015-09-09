package cn.sdu.online.findteam.aliwukong.imkit.session;

import android.content.Context;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import cn.sdu.online.findteam.aliwukong.imkit.base.ListAdapter;
import cn.sdu.online.findteam.aliwukong.imkit.session.model.Session;

/**
 * Created by wn on 2015/8/14.
 */
public class SessionAdapter extends ListAdapter<Session> {

    public SessionAdapter(Context context) {
        super(context);
    }

    public void removeSession(String id) {
        for (int i = 0; i < mList.size(); i++) {
            Session session = mList.get(i);
            if (session.getId().equals(id)) {
                mList.remove(session);
                break;
            }
        }
        notifyDataSetChanged();
    }

    public void sort(){
        Collections.sort(mList, new Comparator<Session>() {
            @Override
            public int compare(Session lhs, Session rhs) {
                return lhs.compareTo(rhs);
            }
        });
    }


    public void addSession(int location,List<Session> list){//需要去重
        mList.addAll(location,list);
        removeSameElements(mList);
    }

    private void removeSameElements(List<Session> sessions) {
        try {
            Set<Session> set = new HashSet<Session>();
            List<Session> newList = new ArrayList<Session>();
            Set<String> idsSet = new HashSet<String>();

            for (Iterator<Session> iter = this.mList.iterator(); iter.hasNext();) {

                Session element = iter.next();
                String _id = element.getId();
                if (TextUtils.isEmpty(_id)) {
                    if (!idsSet.add(_id)) {
                        continue;
                    }
                }
                if (set.add(element)) {
                    newList.add(element);
                }
            }

            this.mList.clear();
            this.mList.addAll(newList);
            newList.clear();
        } catch (Throwable tr) {
        }
    }

    @Override
    protected String getDomainCategory() {
        return Session.DOMAIN_CATEGORY;
    }
}

