package org.snail.rocket.server.handler;

import io.netty.channel.Channel;
import io.netty.channel.socket.SocketChannel;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ${DESCRIPTION}
 *
 * @author shouchen<shouchen21647@hundsun.com>
 * @create 2018-02-06 16:32
 */

public class NettyChannelMap {
    private static Map<String,SocketChannel> map=new ConcurrentHashMap<String, SocketChannel>();
    private static Map<String,String> authMap=new ConcurrentHashMap<>();
    public static void add(String clientId,SocketChannel socketChannel){
        map.put(clientId,socketChannel);
    }
    public static void addAuth(String clientId,String auth){
        authMap.put(clientId,auth);
    }
    public static Channel get(String clientId){
        return map.get(clientId);
    }
    public static void remove(SocketChannel socketChannel){
        for (Map.Entry entry:map.entrySet()){
            if (entry.getValue()==socketChannel){
                map.remove(entry.getKey());
            }
        }
    }
    public static Set<String> getKeys(){
        return map.keySet();
    }
    public static String getAuth(String clientId){
        return authMap.get(clientId);
    }
    public static void removeAuth(String clientId){
        authMap.remove(clientId);
    }
    public static void removeChannel(String clientId){
       map.remove(clientId);
    }
}
