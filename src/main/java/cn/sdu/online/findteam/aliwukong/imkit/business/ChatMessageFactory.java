package cn.sdu.online.findteam.aliwukong.imkit.business;

import android.util.SparseArray;

import com.alibaba.wukong.im.Message;
import com.alibaba.wukong.im.MessageContent;

import java.util.List;

import cn.sdu.online.findteam.aliwukong.imkit.base.Functional;
import cn.sdu.online.findteam.aliwukong.imkit.chat.model.ChatMessage;

/**
 * Created by wn on 2015/8/14.
 */
public class ChatMessageFactory {

    private SparseArray<Functional.Func<Message, ChatMessage>> createMap
            = new SparseArray<Functional.Func<Message, ChatMessage>>();

    public ChatMessageFactory() {
        init();
    }

    private void reg(int type, Functional.Func<Message, ChatMessage> func) {
        createMap.put(type, func);
    }

    private void init() {
        reg(MessageContent.MessageContentType.TEXT,
                new Functional.Func<Message, ChatMessage>() {
                    @Override
                    public ChatMessage func(Message source) {
                        return new TextMessageCreator().onCreate(source);
                    }
                });
        reg(MessageContent.MessageContentType.IMAGE,
                new Functional.Func<Message, ChatMessage>() {
                    @Override
                    public ChatMessage func(Message source) {
                        return new ImageMessageCreator().onCreate(source);
                    }
                });
        reg(MessageContent.MessageContentType.AUDIO,
                new Functional.Func<Message, ChatMessage>() {
                    @Override
                    public ChatMessage func(Message source) {
                        return new AudioMessageCreator().onCreate(source);
                    }
                });
    }

    public ChatMessage create(Message message){
        int contentType=message.messageContent().type();
        Functional.Func<Message,ChatMessage> func= createMap.get(contentType);
        if(func==null){
            return null;
        }
        return func.func(message);
    }

    public List<ChatMessage> createList(List<Message> list){
        return Functional.each(list, new Functional.Func<Message, ChatMessage>() {

            @Override
            public ChatMessage func(Message source) {
                return create(source);
            }
        });
    }
}

