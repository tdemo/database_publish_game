package cn.sdu.online.findteam.aliwukong.imkit.chat.controller;

/**
 * Created by wn on 2015/8/14.
 */
/**
 * 聊天窗口的管理器，主要是管理当前在运行的会话是哪个等等
 *
 */
public class ChatWindowManager {

    private static ChatWindowManager sChatWindowManager;

    private String mCurrentCid = null;

    public synchronized static ChatWindowManager getInstance() {
        if (sChatWindowManager == null) {
            sChatWindowManager = new ChatWindowManager();
        }
        return sChatWindowManager;
    }

    public final String getCurrentChatCid() {
        return this.mCurrentCid;
    }

    /**
     * 设置当前聊天窗口对应的cid，进入聊天窗口的时候调用
     * @param cid
     */
    public void setCurrentChatCid(String cid) {
        this.mCurrentCid = cid;
    }

    /**
     * 退出聊天窗口的时候调用
     * @param windowCid
     */
    public void exitChatWindow(String windowCid) {
        if(windowCid != null && windowCid.equals(mCurrentCid)) {
            this.mCurrentCid = null;
        }
    }

    private boolean isWindowPause = false;
    public void resumeWindow() {
        this.isWindowPause = false;
    }

    public void pauseWindow() {
        this.isWindowPause = true;
    }

    public boolean isWindowPause() {
        return this.isWindowPause;
    }

    /**
     * 判断要显示错误信息的cid与当前窗口的会话id相比，是否已经要提示错误信息，主要用于后台的task进行错误消息的提示
     * @param cid
     * @return
     */
    public boolean isShoudShowErrorOnWindow(String cid) {
        return this.mCurrentCid != null && this.mCurrentCid.equals(cid);
    }
}
