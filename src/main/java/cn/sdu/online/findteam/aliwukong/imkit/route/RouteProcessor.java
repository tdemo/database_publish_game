package cn.sdu.online.findteam.aliwukong.imkit.route;

/**
 * Created by wn on 2015/8/14.
 */

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import cn.sdu.online.findteam.aliwukong.imkit.base.DisplayListItem;
import cn.sdu.online.findteam.aliwukong.imkit.base.ViewHolder;

/**
 * Created by zijunlzj on 14/11/25.
 * Modified by zhongqian.wzq:
 * SparseArray.indexOfValue：compares values using {@code ==} rather than {@code equals}.
 * 由于"="比较的是内存，indexOfValue(String)会始终返回-1,除非参数是同一个字符串
 */
public class RouteProcessor {


    private static class ClassHolder {
        private static RouteProcessor instance = new RouteProcessor();
    }

    private RouteProcessor() {
    }

    private static RouteProcessor getInstance() {
        return ClassHolder.instance;
    }

    private HashMap<String,Map<String,Integer>> mViewHolderMap = new HashMap<String, Map<String,Integer>>();

    private void buildRouter(Class<? extends DisplayListItem> modelClazz,String category) {
        RouteViewBridge routeViewBridge = RouteViewBridge.createRouteViewBridge(modelClazz,
                category);
        if (routeViewBridge == null) {
            return;
        }
        routeViewBridge.buildViewTypeMap(mViewHolderMap);
    }

    public synchronized static ViewHolder route(DisplayListItem<?> model,String category) {
        Class<?> clazz = model.getClass();
        Log.v("hehehehehehheheh" , clazz+"");
        RouteViewBridge routeViewBridge =  RouteViewBridge.createRouteViewBridge(clazz, category);
        if (routeViewBridge != null) {
            return routeViewBridge.createViewHolder(model);
        }
        return null;
    }

    public static int getViewType(DisplayListItem<?> model,String category) {
        Class<?> clazz = model.getClass();
        RouteViewBridge routeViewBridge = RouteViewBridge.createRouteViewBridge(clazz, category);
        if (routeViewBridge == null) {
            return RouteViewBridge.DEFAULT_VIEW_TYPE;
        }
        return routeViewBridge.getViewType(model,getInstance().mViewHolderMap);
    }

    public static int getViewCount(String category) {
        return RouteViewBridge.getViewCount(category, getInstance().mViewHolderMap);
    }

    public static void registRouter(Class<? extends DisplayListItem> modelClazz,String category) {
        RouteProcessor.getInstance().buildRouter(modelClazz,category);
    }

}
