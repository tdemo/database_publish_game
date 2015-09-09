package cn.sdu.online.findteam.aliwukong.imkit.route;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import cn.sdu.online.findteam.aliwukong.imkit.base.DisplayListItem;
import cn.sdu.online.findteam.aliwukong.imkit.base.ViewHolder;
import cn.sdu.online.findteam.aliwukong.imkit.base.ViewHolderCreator;

class RouteViewBridge {

    public static final int DEFAULT_VIEW_TYPE = 0;

    private Router mRouter;

    private String category;

    private RouteViewBridge() {
    }

    public ViewHolder createViewHolder(Object model) {
        ViewHolder viewHolder = null;
        try {
            ViewHolderCreator viewHolderCreator = mRouter.generate().newInstance();
            viewHolder = viewHolderCreator.onCreate(mRouter.value(), model);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return viewHolder;
    }

    public void buildViewTypeMap(HashMap<String, Map<String, Integer>> viewMap) {
        Class<? extends ViewHolder>[] clazz = mRouter.value();
        Map<String, Integer> viewTypeIndex;
        if (viewMap.containsKey(category)) {
            viewTypeIndex = viewMap.get(category);
            addViewTypeMaps(clazz, viewTypeIndex);
        } else {
            viewTypeIndex = new HashMap<String, Integer>();
            addViewTypeMaps(clazz, viewTypeIndex);
            viewMap.put(category, viewTypeIndex);
        }
    }

    private void addViewTypeMaps(Class<? extends ViewHolder>[] clazzs, Map<String, Integer> viewTypeIndexMap) {
        for (int i = 0; i < clazzs.length; i++) {
            String key = buildViewHolder(i, clazzs[i]);
            if (!viewTypeIndexMap.containsKey(key)) {
                viewTypeIndexMap.put(key, viewTypeIndexMap.size());
            }
        }
    }

    private String buildViewHolder(int index, Class<? extends ViewHolder> clazz) {
        return String.format("%s#%d", clazz.toString(), index);
    }

    public int getViewType(DisplayListItem<?> item, HashMap<String, Map<String, Integer>> map) {
        Class<? extends ViewHolder> clazz = mRouter.value()[item.getViewType()];
        Map<String, Integer> viewTypeIndexMap = map.get(category);
        if (viewTypeIndexMap == null) {
            return DEFAULT_VIEW_TYPE;
        }
        String key = buildViewHolder(item.getViewType(), clazz);
        Integer index = viewTypeIndexMap.get(key);
        return index == null ? DEFAULT_VIEW_TYPE : index.intValue();

    }

    public static int getViewCount(String category, HashMap<String, Map<String, Integer>> map) {
        Map<String, Integer> viewTypeMap = map.get(category);
        return viewTypeMap == null ? DEFAULT_VIEW_TYPE : viewTypeMap.size();
    }

    public static RouteViewBridge createRouteViewBridge(Class<?> domainClazz, String category) {
        if (!isAttachRouter(domainClazz)) {
            return null;
        }
        RouteViewBridge routeViewBridge = new RouteViewBridge();
        routeViewBridge.mRouter = domainClazz.getAnnotation(Router.class);
        routeViewBridge.category = category;
        return routeViewBridge;
    }

    private static boolean isAttachRouter(Class<?> controller) {
        if (controller == null) {
            return false;
        }
        return controller.isAnnotationPresent(Router.class);
    }


}
