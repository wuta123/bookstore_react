package com.bookstore.www.utils;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/websocket/transfer/{userId}")
@Component
public class SocketServer {
    private static final ConcurrentHashMap<String, Session> SESSIONS =
            new ConcurrentHashMap<>();
    public boolean sendMessage(Session toSession, String message){
        if(toSession != null){
            try{
                toSession.getBasicRemote().sendText(message);
            }catch(IOException e){
                e.printStackTrace();
                return false;
            }
            System.out.println("信息已发送");
            return true;
        }else {
            System.out.println("收信方不在线");
            return false;
        }
    }

    public void sendMessageToUser(String user_id, String order_id) throws InterruptedException {
        System.out.println("尝试发信给用户，UUID为：" + user_id);
        for(int i = 0; i < 6; i++){
            System.out.println("第"+i+"次发送。");
            Session toSession = SESSIONS.get(user_id);
            boolean res = sendMessage(toSession, order_id);
            if(res == true)
                break;
            Thread.sleep(4000);
        }
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId){
        if(SESSIONS.get(userId) != null)
            return;
        SESSIONS.put(userId, session);
        System.out.println("用户：" + userId + "上线了");
    }

    @OnMessage
    public void onMessage(String message){
        System.out.println("服务器收到信息: " + message);
    }


    @OnClose
    public void onClose(@PathParam("userId") String userId){
        SESSIONS.remove(userId);
        System.out.println("用户：" + userId + "下线了");
    }

    @OnError
    public void onError(Session session, Throwable throwable){
        System.out.println("出现错误");
        throwable.printStackTrace();
    }
}
