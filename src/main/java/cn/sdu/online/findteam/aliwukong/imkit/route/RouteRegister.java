package cn.sdu.online.findteam.aliwukong.imkit.route;

import cn.sdu.online.findteam.aliwukong.imkit.base.DisplayListItem;
import cn.sdu.online.findteam.aliwukong.imkit.chat.model.AudioReceiveMessage;
import cn.sdu.online.findteam.aliwukong.imkit.chat.model.AudioSendMessage;
import cn.sdu.online.findteam.aliwukong.imkit.chat.model.ChatMessage;
import cn.sdu.online.findteam.aliwukong.imkit.chat.model.ImageReceiveMessage;
import cn.sdu.online.findteam.aliwukong.imkit.chat.model.ImageSendMessage;
import cn.sdu.online.findteam.aliwukong.imkit.chat.model.SysmsgMessage;
import cn.sdu.online.findteam.aliwukong.imkit.chat.model.TextReceiveMessage;
import cn.sdu.online.findteam.aliwukong.imkit.chat.model.TextSendMessage;
import cn.sdu.online.findteam.aliwukong.imkit.session.model.GroupSession;
import cn.sdu.online.findteam.aliwukong.imkit.session.model.Session;
import cn.sdu.online.findteam.aliwukong.imkit.session.model.SingleSession;

public class RouteRegister {

    public static void bootwrapped() {
        reg(GroupSession.class, Session.DOMAIN_CATEGORY);
        reg(SingleSession.class, Session.DOMAIN_CATEGORY);
        reg(TextReceiveMessage.class, ChatMessage.DOMAIN_CATEGORY);
        reg(TextSendMessage.class, ChatMessage.DOMAIN_CATEGORY);
        reg(SysmsgMessage.class, ChatMessage.DOMAIN_CATEGORY);
        reg(ImageSendMessage.class, ChatMessage.DOMAIN_CATEGORY);
        reg(ImageReceiveMessage.class, ChatMessage.DOMAIN_CATEGORY);
        reg(AudioSendMessage.class, ChatMessage.DOMAIN_CATEGORY);
        reg(AudioReceiveMessage.class, ChatMessage.DOMAIN_CATEGORY);
    }


    private static void reg(Class<? extends DisplayListItem> domain, String category) {
        RouteProcessor.registRouter(domain, category);
    }

}

