package cn.sdu.online.findteam.aliwukong.imkit.base;

import com.alibaba.wukong.auth.AuthService;
import com.alibaba.wukong.im.ConversationService;
import com.alibaba.wukong.im.IMEngine;
import com.alibaba.wukong.im.MessageBuilder;
import com.alibaba.wukong.im.MessageService;
import com.alibaba.wukong.im.UserService;

import javax.inject.Singleton;

import cn.sdu.online.findteam.aliwukong.imkit.business.ChatMessageFactory;
import cn.sdu.online.findteam.aliwukong.imkit.chat.controller.ChatFragment;
import cn.sdu.online.findteam.aliwukong.imkit.chat.controller.ChatMessageTransmitter;
import cn.sdu.online.findteam.fragment.SessionFragment;
import dagger.Module;
import dagger.Provides;

/**
 * Created by wn on 2015/8/14.
 */
@Module(library = true, injects = {
        SessionFragment.class, ChatFragment.class,ChatMessageTransmitter.class
})
public class ImKitModule {


    @Provides
    @Singleton
    ChatMessageFactory provideChatFactory() {
        return new ChatMessageFactory();
    }

    @Provides
    ConversationService provideConversationService() {
        return IMEngine.getIMService(ConversationService.class);
    }

    @Provides
    AuthService provideAuthService(){
        return  IMEngine.getIMService(AuthService.class);
    }

    @Provides
    MessageService provideMessageService(){
        return IMEngine.getIMService(MessageService.class);
    }

    @Provides
    UserService provideUserService(){
        return IMEngine.getIMService(UserService.class);
    }

    @Provides
    MessageBuilder provideMessageBuilder(){
        return IMEngine.getIMService(MessageBuilder.class);
    }
}
