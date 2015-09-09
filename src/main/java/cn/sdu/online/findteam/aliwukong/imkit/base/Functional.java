package cn.sdu.online.findteam.aliwukong.imkit.base;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wn on 2015/8/14.
 */
public class Functional {

    public interface Func<TFrom, TTo> {
        TTo func(TFrom source);
    }

    public interface Action<T> {
        void action(T t);
    }


    public interface Predicate<T> {
        boolean predicate(T t);
    }

    public static <TFrom, TTo> List<TTo> each(List<TFrom> sources, Func<TFrom, TTo> func) {
        List<TTo> list = new ArrayList<TTo>();
        for (TFrom item : sources) {
            TTo model = func.func(item);
            if (model != null) {
                list.add(model);
            }
        }
        return list;
    }

}

