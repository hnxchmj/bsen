package com.nbcb.myron.bsen.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint(value = "/webSocket/{nickname}") //webSocket连接点映射
@Component
public class WebSocket {
    @Autowired
    private JdbcTemplate jdbc;
    private static JdbcTemplate jdbcs;
    //存储每个客户端对应的MyWebSocket对象
    private static CopyOnWriteArraySet<WebSocket> webSocketSets = new CopyOnWriteArraySet<WebSocket>();
    private Session session; //当前会话的session
    private static String nickname;

    //解决spring注入不上问题
    @PostConstruct
    public void init() {
        jdbcs = jdbc;
    }

    /**
     * 成功建立连接的方法
     */

    @OnOpen
    public void onOpen(Session session, @PathParam("nickname") String nickname) {
        createUser(nickname);
        this.nickname = nickname;
        this.session = session;
        webSocketSets.add(this);
        System.err.println(this);
        System.out.println(session);
//this.session.getAsyncRemote().sendText(nickname+"上线了"+"当前在线人数为"+webSocketSet.size()+"人");
        for (WebSocket myWebSocket : webSocketSets) {
            if (myWebSocket == this) {
                session.getAsyncRemote().sendText(nickname + "连接成功" + "当前在线人数为" + webSocketSets.size() + "人");
            } else
                session.getAsyncRemote().sendText(nickname + "上线了" + "当前在线人数为" + webSocketSets.size() + "人");
        }
    }

    private void createUser(String nickname2) {
        String uuid = UUID.randomUUID().toString();
        this.jdbcs.update("insert into demo(id,name)values(?,?)", new Object[]{uuid, nickname2});
    }

    /**
     * 连接关闭的方法
     *     
     */
    @OnClose
    public void onClose(Session session) {
        webSocketSets.remove(this);
    }

    /**
     * 接收客户端消息后调用的方法
     */
    @OnMessage
    public void onMessage(String message, Session session, @PathParam("nickname") String nickname) {
        broadcast(message, nickname);
    }

    private void broadcast(String message, String nickname) {
        for (WebSocket myWebSocket : webSocketSets) {
            if (myWebSocket == this) {
                session.getAsyncRemote().sendText("<span style=\"color:red;float:right\">" + message + ":" + nickname + "</span>");
            } else {
                session.getAsyncRemote().sendText(nickname + ":" + message);
            }
        }
    }

    /**
     * 发生错误的方法
     */
    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println("发生错误");
        error.printStackTrace();
    }
}